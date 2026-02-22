import apiClient from './api'
import { getUnsyncedKeys, getOfflineData, markAsSynced, removeOfflineData } from './offlineStorage'

// Vérification de la connexion réseau
export const isOnline = (): boolean => {
  return navigator.onLine
}

// Synchronisation des données locales avec le serveur
export const syncWithServer = async () => {
  if (!isOnline()) {
    console.log('Pas de connexion réseau disponible')
    return { success: false, message: 'Pas de connexion réseau' }
  }

  try {
    const unsyncedKeys = await getUnsyncedKeys()
    
    if (unsyncedKeys.length === 0) {
      return { success: true, message: 'Aucune donnée à synchroniser' }
    }

    let syncedCount = 0
    let failedCount = 0

    for (const key of unsyncedKeys) {
      try {
        const offlineItem: any = await getOfflineData(key)
        
        if (offlineItem && offlineItem.data) {
          // Extraire l'endpoint et la méthode du key (format: method_endpoint_id)
          const [method, ...endpointParts] = key.split('_')
          const endpoint = endpointParts.join('/')

          // Envoyer les données au serveur
          if (method === 'POST') {
            await apiClient.post(`/${endpoint}`, offlineItem.data)
          } else if (method === 'PUT') {
            await apiClient.put(`/${endpoint}`, offlineItem.data)
          } else if (method === 'DELETE') {
            await apiClient.delete(`/${endpoint}`)
          }

          // Marquer comme synchronisé
          await markAsSynced(key)
          syncedCount++
        }
      } catch (error) {
        console.error(`Erreur lors de la synchronisation de ${key}:`, error)
        failedCount++
      }
    }

    return {
      success: true,
      message: `${syncedCount} éléments synchronisés, ${failedCount} échecs`,
      synced: syncedCount,
      failed: failedCount,
    }
  } catch (error) {
    console.error('Erreur lors de la synchronisation:', error)
    return { success: false, message: 'Erreur lors de la synchronisation' }
  }
}

// Configuration de la synchronisation automatique lors de la reconnexion
export const setupAutoSync = () => {
  window.addEventListener('online', async () => {
    console.log('Connexion rétablie, synchronisation en cours...')
    const result = await syncWithServer()
    console.log('Résultat de la synchronisation:', result)
  })

  window.addEventListener('offline', () => {
    console.log('Connexion perdue, passage en mode offline')
  })
}
