/**
 * Configuration de l'API
 */
export const API_CONFIG = {
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  timeout: Number(import.meta.env.VITE_API_TIMEOUT) || 30000,
  headers: {
    'Content-Type': 'application/json',
  },
};

/**
 * Configuration du stockage local
 */
export const STORAGE_CONFIG = {
  name: 'explorateurs_db',
  storeName: 'explorateurs_store',
  description: 'Base de données locale pour Gestion Explorateurs',
};

/**
 * Endpoints de l'API
 */
export const API_ENDPOINTS = {
  // Authentification
  login: '/api/auth/login',
  logout: '/api/auth/logout',
  
  // Utilisateurs
  userMe: '/api/utilisateur/me',
  users: '/api/utilisateur',
  
  // Année d'exercice
  anneeExercice: '/api/annee-exercice',
  anneeExerciceActive: '/api/annee-exercice/active',
  anneeExerciceAll: '/api/annee-exercice/all',
  
  // Enfants
  enfants: '/api/enfants',
  enfantsSearch: '/api/enfants/search',
  
  // Activités
  activites: '/api/activites',
  activitesInscriptions: '/api/activites/inscriptions',
  
  // Inscriptions
  inscriptions: '/api/inscriptions',
  
  // Parents
  parents: '/api/parents',
  
  // Staff
  staff: '/api/staff',
  instructeurs: '/api/instructeurs',
  
  // Budget
  budget: '/api/budget',
  budgetCategories: '/api/budget/categories',
  
  // Rapports
  rapports: '/api/rapports',
  rapportsExport: '/api/rapports/export',
};

/**
 * Clés du stockage local
 */
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'auth_token',
  AUTH_USER: 'auth_user',
  LAST_SYNC: 'last_sync',
  PENDING_PREFIX: 'pending_',
};
