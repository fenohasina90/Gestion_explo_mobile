/**
 * Types pour l'authentification
 */
export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  userId: number;
  username: string;
  role: string;
  anneeExercice: string;
}

export interface User {
  id: number;
  username: string;
  role: string;
  active: boolean;
  anneeExercice: string;
}

/**
 * Types pour les utilisateurs
 */
export interface Utilisateur {
  id: number;
  username: string;
  role: string;
  active: boolean;
  anneeExercice: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateUtilisateurRequest {
  username: string;
  password: string;
  roleId: number;
  anneeExerciceId: number;
}

export interface UpdateUtilisateurRequest {
  username?: string;
  password?: string;
  roleId?: number;
  active?: boolean;
  anneeExerciceId?: number;
}

export interface Role {
  id: number;
  roleName: string;
}

/**
 * Types pour les années d'exercice
 */
export interface AnneeExercice {
  id: number;
  annee: string;
  dateFin: string;
  createdAt: string;
}

export interface CreateAnneeExerciceRequest {
  annee: string;
}

/**
 * Types pour le journal d'audit
 */
export interface JournalEntry {
  id: number;
  action: string;
  username: string;
  utilisateurId: number;
  timestamp: string;
}

export interface JournalFilterRequest {
  dateDebut?: string;
  dateFin?: string;
  searchText?: string;
}

/**
 * Types pour les réponses API
 */
export interface UserInfoResponse {
  id: number;
  username: string;
  role: string;
  active: boolean;
  anneeExercice: string;
}

/**
 * Types pour les instructeurs
 */
export interface Instructeur {
  id: number;
  nom: string;
  prenom: string;
  genre: string;
  totem?: string;
  telephone?: string;
  estChefGuide: boolean;
}

export interface InstructeurSuggestion {
  id: number;
  nom: string;
  prenom: string;
  nomComplet: string;
}

export interface CreateInstructeurRequest {
  nom: string;
  prenom: string;
  genre: string;
  totem?: string;
  telephone?: string;
  estChefGuide?: boolean;
}

/**
 * Types pour les staffs
 */
export interface RoleStaff {
  id: number;
  roleName: string;
}

export interface Staff {
  id: number;
  instructeurId: number;
  instructeurNom: string;
  instructeurPrenom: string;
  instructeurGenre: string;
  instructeurTotem?: string;
  instructeurTelephone?: string;
  instructeurEstChefGuide: boolean;
  roleId: number;
  role: string;
  anneeExerciceId: number;
  anneeExercice: string;
}

export interface CreateStaffRequest {
  instructeurId: number;
  roleId: number;
  anneeExerciceId: number;
}

export interface UpdateStaffRequest {
  roleId?: number;
  nom?: string;
  prenom?: string;
  genre?: string;
  totem?: string;
  telephone?: string;
  estChefGuide?: boolean;
}

/**
 * Types pour les erreurs
 */
export interface ApiError {
  message: string;
  status?: number;
  code?: string;
}
