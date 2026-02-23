import localforage from 'localforage';
import { STORAGE_CONFIG } from '../config/api.config';

/**
 * Service de stockage hors ligne utilisant LocalForage (IndexedDB)
 */
class OfflineStorageService {
  private storage: LocalForage;

  constructor() {
    this.storage = localforage.createInstance({
      name: STORAGE_CONFIG.name,
      storeName: STORAGE_CONFIG.storeName,
      description: STORAGE_CONFIG.description,
      driver: [localforage.INDEXEDDB, localforage.LOCALSTORAGE],
    });
  }

  /**
   * Sauvegarde une valeur
   */
  async set<T>(key: string, value: T): Promise<T> {
    try {
      return await this.storage.setItem(key, value);
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
    }
  }

  /**
   * Vide tout le stockage
   */
  async clear(): Promise<void> {
    try {
      await this.storage.clear();
    } catch (error) {
      console.error('Erreur lors du nettoyage du stockage:', error);
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
   * Sauvegarde avec un timestamp
   */
  async setWithTimestamp<T>(key: string, value: T): Promise<void> {
    const data = {
      value,
      timestamp: Date.now(),
    };
    await this.set(key, data);
  }

  /**
   * Récupère une valeur avec son timestamp
   */
  async getWithTimestamp<T>(key: string): Promise<{ value: T; timestamp: number } | null> {
    return await this.get<{ value: T; timestamp: number }>(key);
  }

  /**
   * Vérifie si une donnée est obsolète (expirée)
   */
  async isStale(key: string, maxAge: number): Promise<boolean> {
    const data = await this.getWithTimestamp(key);
    if (!data) return true;
    return Date.now() - data.timestamp > maxAge;
  }
}

export default new OfflineStorageService();
