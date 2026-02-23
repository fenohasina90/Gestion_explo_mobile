import { Network } from '@capacitor/network';
import offlineStorage from './offline-storage.service';
import apiService from './api.service';
import { SYNC_CONFIG } from '@/config/api.config';

/**
 * Service de synchronisation des données entre le serveur et le stockage local
 */
class SyncService {
  private syncInterval: number | null = null;
  private isSyncing = false;

  /**
   * Démarre la synchronisation automatique
   */
  startAutoSync(): void {
    if (this.syncInterval) {
      console.log('La synchronisation automatique est déjà active');
      return;
    }

    console.log('Démarrage de la synchronisation automatique');
    
    // Synchronisation initiale
    this.sync();

    // Synchronisation automatique à intervalle régulier
    this.syncInterval = window.setInterval(() => {
      this.sync();
    }, SYNC_CONFIG.autoSyncInterval);

    // Écouter les changements de connexion réseau
    Network.addListener('networkStatusChange', (status) => {
      if (status.connected) {
        console.log('Connexion réseau détectée, synchronisation...');
        this.sync();
      }
    });
  }

  /**
   * Arrête la synchronisation automatique
   */
  stopAutoSync(): void {
    if (this.syncInterval) {
      clearInterval(this.syncInterval);
      this.syncInterval = null;
      console.log('Synchronisation automatique arrêtée');
    }
  }

  /**
   * Synchronise les données avec le serveur
   */
  async sync(): Promise<boolean> {
    if (this.isSyncing) {
      console.log('Synchronisation déjà en cours...');
      return false;
    }

    this.isSyncing = true;
    console.log('Début de la synchronisation...');

    try {
      // Vérifier la connexion réseau
      const networkStatus = await Network.getStatus();
      if (!networkStatus.connected) {
        console.log('Pas de connexion réseau, synchronisation annulée');
        return false;
      }

      // Vérifier la connexion au serveur
      const serverReachable = await apiService.checkConnection();
      if (!serverReachable) {
        console.log('Serveur inaccessible, synchronisation annulée');
        return false;
      }

      // Synchroniser les données pendantes (à implémenter selon vos besoins)
      await this.syncPendingData();

      // Mettre à jour les données locales depuis le serveur
      await this.fetchLatestData();

      console.log('Synchronisation réussie');
      
      // Sauvegarder l'horodatage de la dernière synchronisation
      await offlineStorage.set('lastSyncTimestamp', Date.now());
      
      return true;
    } catch (error) {
      console.error('Erreur lors de la synchronisation:', error);
      return false;
    } finally {
      this.isSyncing = false;
    }
  }

  /**
   * Envoie les données en attente au serveur
   */
  private async syncPendingData(): Promise<void> {
    const pendingKeys = await offlineStorage.keys();
    const pendingDataKeys = pendingKeys.filter(key => key.startsWith('pending_'));

    for (const key of pendingDataKeys) {
      try {
        const data = await offlineStorage.get(key);
        if (data) {
          // Envoyer les données au serveur (à adapter selon votre logique)
          console.log(`Envoi des données en attente: ${key}`, data);
          
          // Supprimer après envoi réussi
          await offlineStorage.remove(key);
        }
      } catch (error) {
        console.error(`Erreur lors de l'envoi de ${key}:`, error);
      }
    }
  }

  /**
   * Récupère les dernières données du serveur
   */
  private async fetchLatestData(): Promise<void> {
    try {
      // Récupérer les données essentielles (à adapter selon vos besoins)
      // Exemple: années d'exercice, classes, etc.
      console.log('Récupération des dernières données du serveur...');
      
      // Implémenter la logique de récupération selon vos endpoints
    } catch (error) {
      console.error('Erreur lors de la récupération des données:', error);
      throw error;
    }
  }

  /**
   * Sauvegarde des données pour synchronisation ultérieure
   */
  async savePendingData(key: string, data: any): Promise<void> {
    const pendingKey = `pending_${key}_${Date.now()}`;
    await offlineStorage.set(pendingKey, data);
    console.log(`Données sauvegardées pour synchronisation: ${pendingKey}`);
  }

  /**
   * Obtient l'état de la synchronisation
   */
  getSyncStatus(): { isSyncing: boolean; autoSyncActive: boolean } {
    return {
      isSyncing: this.isSyncing,
      autoSyncActive: this.syncInterval !== null
    };
  }

  /**
   * Obtient l'horodatage de la dernière synchronisation
   */
  async getLastSyncTimestamp(): Promise<number | null> {
    return await offlineStorage.get<number>('lastSyncTimestamp');
  }
}

export default new SyncService();
