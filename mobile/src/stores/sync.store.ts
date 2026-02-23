import { defineStore } from 'pinia';
import { ref } from 'vue';
import syncService from '@/services/sync.service';
import offlineStorage from '@/services/offline-storage.service';

export const useSyncStore = defineStore('sync', () => {
  // État
  const isSyncing = ref(false);
  const lastSyncTime = ref<number | null>(null);
  const syncError = ref<string | null>(null);
  const pendingChanges = ref(0);

  /**
   * Initialise le store
   */
  async function initialize() {
    lastSyncTime.value = await syncService.getLastSyncTimestamp();
    await updatePendingChanges();
  }

  /**
   * Lance une synchronisation manuelle
   */
  async function manualSync(): Promise<boolean> {
    isSyncing.value = true;
    syncError.value = null;

    try {
      const success = await syncService.sync();
      
      if (success) {
        lastSyncTime.value = Date.now();
        await updatePendingChanges();
      } else {
        syncError.value = 'La synchronisation a échoué';
      }

      return success;
    } catch (error: any) {
      syncError.value = error.message || 'Erreur de synchronisation';
      return false;
    } finally {
      isSyncing.value = false;
    }
  }

  /**
   * Met à jour le nombre de modifications en attente
   */
  async function updatePendingChanges() {
    const keys = await offlineStorage.keys();
    pendingChanges.value = keys.filter(key => key.startsWith('pending_')).length;
  }

  /**
   * Obtient le status de la synchronisation
   */
  function getSyncStatus() {
    return syncService.getSyncStatus();
  }

  return {
    // État
    isSyncing,
    lastSyncTime,
    syncError,
    pendingChanges,
    
    // Actions
    initialize,
    manualSync,
    updatePendingChanges,
    getSyncStatus
  };
});
