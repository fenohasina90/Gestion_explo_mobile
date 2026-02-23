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
 * Types pour les enfants
 */
export interface Enfant {
  id: number;
  nom: string;
  prenom: string;
  dateNaissance: string;
  sexe: string;
  adresse?: string;
  telephone?: string;
  email?: string;
  actif: boolean;
}

/**
 * Types pour les activités
 */
export interface Activite {
  id: number;
  nom: string;
  description?: string;
  dateDebut: string;
  dateFin: string;
  lieu?: string;
  prix?: number;
  capaciteMax?: number;
  statut: string;
}

/**
 * Types pour les inscriptions
 */
export interface Inscription {
  id: number;
  enfantId: number;
  activiteId: number;
  dateInscription: string;
  statut: string;
  montantPaye?: number;
  remarques?: string;
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
  createdAt: string;
}

export interface CreateAnneeExerciceRequest {
  annee: string;
}
