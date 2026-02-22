import localforage from 'localforage'

// Configuration de localforage pour le stockage offline
const offlineStore = localforage.createInstance({
  name: 'explorateurDB',
  storeName: 'explorateur_data',
  description: 'Stockage local pour le Club des Explorateurs',
})

// Stockage des données avec horodatage pour la synchronisation
export const saveOfflineData = async (key: string, data: any) => {
  try {
    await offlineStore.setItem(key, {
      data,
      timestamp: new Date().toISOString(),
      synced: false,
    })
    return true
  } catch (error) {
    console.error('Erreur lors de la sauvegarde offline:', error)
    return false
  }
}

// Récupération des données offline
export const getOfflineData = async (key: string) => {
  try {
    const result = await offlineStore.getItem(key)
    return result
  } catch (error) {
    console.error('Erreur lors de la récupération des données offline:', error)
    return null
  }
}

// Suppression des données offline
export const removeOfflineData = async (key: string) => {
  try {
    await offlineStore.removeItem(key)
    return true
  } catch (error) {
    console.error('Erreur lors de la suppression des données offline:', error)
    return false
  }
}

// Récupération de toutes les clés non synchronisées
export const getUnsyncedKeys = async () => {
  try {
    const keys = await offlineStore.keys()
    const unsyncedKeys: string[] = []
    
    for (const key of keys) {
      const item: any = await offlineStore.getItem(key)
      if (item && !item.synced) {
        unsyncedKeys.push(key)
      }
    }
    
    return unsyncedKeys
  } catch (error) {
    console.error('Erreur lors de la récupération des clés non synchronisées:', error)
    return []
  }
}

// Marquer les données comme synchronisées
export const markAsSynced = async (key: string) => {
  try {
    const item: any = await offlineStore.getItem(key)
    if (item) {
      item.synced = true
      await offlineStore.setItem(key, item)
    }
    return true
  } catch (error) {
    console.error('Erreur lors du marquage comme synchronisé:', error)
    return false
  }
}

// Vider tout le stockage offline
export const clearOfflineStorage = async () => {
  try {
    await offlineStore.clear()
    return true
  } catch (error) {
    console.error('Erreur lors de la suppression du stockage offline:', error)
    return false
  }
}

export default offlineStore
