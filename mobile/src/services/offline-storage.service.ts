import localforage from 'localforage';
import { STORAGE_CONFIG } from '@/config/api.config';

/**
 * Service de stockage offline utilisant LocalForage (IndexedDB)
 */
class OfflineStorageService {
  private storage: LocalForage;

  constructor() {
    this.storage = localforage.createInstance({
      name: STORAGE_CONFIG.name,
      driver: [
        localforage.INDEXEDDB,
        localforage.WEBSQL,
        localforage.LOCALSTORAGE
      ]
    });
  }

  /**
   * Sauvegarde une valeur
   */
  async set<T>(key: string, value: T): Promise<T> {
    try {
      await this.storage.setItem(key, value);
      return value;
    } catch (error) {
      console.error(`Erreur lors de la sauvegarde de ${key}:`, error);
      throw error;
    }
  }

  /**
   * Récupère une valeur
   */
  async get<T>(key: string): Promise<T | null> {
    try {
      return await this.storage.getItem<T>(key);
    } catch (error) {
      console.error(`Erreur lors de la récupération de ${key}:`, error);
      return null;
    }
  }

  /**
   * Supprime une valeur
   */
  async remove(key: string): Promise<void> {
    try {
      await this.storage.removeItem(key);
    } catch (error) {
      console.error(`Erreur lors de la suppression de ${key}:`, error);
      throw error;
    }
  }

  /**
   * Vide tout le stockage
   */
  async clear(): Promise<void> {
    try {
      await this.storage.clear();
    } catch (error) {
      console.error('Erreur lors du vidage du stockage:', error);
      throw error;
    }
  }

  /**
   * Récupère toutes les clés
   */
  async keys(): Promise<string[]> {
    try {
      return await this.storage.keys();
    } catch (error) {
      console.error('Erreur lors de la récupération des clés:', error);
      return [];
    }
  }

  /**
   * Vérifie si une clé existe
   */
  async has(key: string): Promise<boolean> {
    try {
      const keys = await this.storage.keys();
      return keys.includes(key);
    } catch (error) {
      console.error(`Erreur lors de la vérification de ${key}:`, error);
      return false;
    }
  }

  /**
   * Sauvegarde des données avec horodatage
   */
  async setWithTimestamp<T>(key: string, value: T): Promise<void> {
    const data = {
      value,
      timestamp: Date.now()
    };
    await this.set(key, data);
  }

  /**
   * Récupère des données avec horodatage
   */
  async getWithTimestamp<T>(key: string): Promise<{ value: T; timestamp: number } | null> {
    return await this.get<{ value: T; timestamp: number }>(key);
  }

  /**
   * Vérifie si les données sont périmées (plus anciennes que maxAge en ms)
   */
  async isStale(key: string, maxAge: number): Promise<boolean> {
    const data = await this.getWithTimestamp(key);
    if (!data) return true;
    
    const age = Date.now() - data.timestamp;
    return age > maxAge;
  }
}

export default new OfflineStorageService();
