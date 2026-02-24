/**
 * Configuration de l'API
 */

// URL de base de l'API selon l'environnement
export const API_CONFIG = {
  // API locale (développement)
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  
  // Timeout des requêtes (30 secondes)
  timeout: 30000,
  
  // Headers par défaut
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
};

// Configuration de stockage offline
export const STORAGE_CONFIG = {
  // Nom de la base de données locale
  name: 'explorateurs_db',
  
  // Driver préféré (localforage utilise IndexedDB par défaut)
  driverOrder: ['indexeddb', 'websql', 'localstorage']
};

// Configuration de synchronisation
export const SYNC_CONFIG = {
  // Intervalle de synchronisation automatique (5 minutes)
  autoSyncInterval: 5 * 60 * 1000,
  
  // Nombre maximum de tentatives de sync en cas d'échec
  maxRetries: 3,
  
  // Délai entre les tentatives (en millisecondes)
  retryDelay: 5000
};

// Endpoints de l'API
export const API_ENDPOINTS = {
  // Authentification
  login: '/api/auth/login',
  
  // Utilisateurs
  currentUser: '/api/utilisateur/me',
  utilisateurs: '/api/utilisateur',
  utilisateurById: (id: number) => `/api/utilisateur/${id}`,
  utilisateursByAnnee: (anneeId: number) => `/api/utilisateur/annee/${anneeId}`,
  utilisateursActifs: '/api/utilisateur/actifs',
  roles: '/api/utilisateur/roles',
  
  // Années d'exercice
  anneesExercice: '/api/annee-exercice',
  anneeExerciceById: (id: number) => `/api/annee-exercice/${id}`,
  currentYear: '/api/annee-exercice/courante',
  recentYear: '/api/annee-exercice/recente',
  
  // Journal d'audit
  journal: '/api/journal',
  journalFilter: '/api/journal/filter',
  journalByPeriod: '/api/journal/period'
};
