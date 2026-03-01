/**
 * Type générique pour les réponses paginées
 */
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}

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
  utilisateurId: number | null;
  timestamp: string;
}

export interface JournalFilterRequest {
  dateDebut?: string;
  dateFin?: string;
  utilisateurId?: number;
  searchText?: string;
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
  createdAt: string;
  updatedAt: string;
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
export interface Staff {
  id: number;
  instructeurId: number;
  instructeurNom: string;
  instructeurPrenom: string;
  instructeurGenre: string;
  instructeurTotem?: string;
  instructeurTelephone?: string;
  instructeurEstChefGuide: boolean;
  role: string;
  roleId: number;
  anneeExerciceId: number;
  anneeExercice: string;
  etat: number; // 1 = actif, 11 = supprimé
  createdAt: string;
  updatedAt: string;
}

export interface CreateStaffRequest {
  instructeurId: number;
  roleId: number;
  anneeExerciceId: number;
}

export interface UpdateStaffRequest {
  roleId?: number;
  // Informations de l'instructeur (optionnelles)
  nom?: string;
  prenom?: string;
  genre?: string;
  totem?: string;
  telephone?: string;
  estChefGuide?: boolean;
}

/**
 * Types pour les parents
 */
export interface Parent {
  id: number;
  nom: string;
  prenom: string;
  adresse?: string;
  telephone?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ParentSuggestion {
  id: number;
  nom: string;
  prenom: string;
  telephone?: string;
  adresse?: string;
  nomComplet: string;
}

export interface CreateParentRequest {
  nom: string;
  prenom: string;
  adresse?: string;
  telephone?: string;
}

/**
 * Types pour les enfants
 */
export interface EnfantResponse {
  id: number;
  nom: string;
  prenom: string;
  genre: string;
  dateNaissance: string;
  age: number;
  adresse?: string;
  parentId?: number;
  parentNom?: string;
  parentPrenom?: string;
  bapteme?: string;
  createdAt: string;
  updatedAt: string;
}

export interface EnfantSuggestion {
  id: number;
  nom: string;
  prenom: string;
  genre: string;
  dateNaissance: string;
  age: number;
  parentNom?: string;
  parentPrenom?: string;
  nomComplet: string;
  parentNomComplet: string;
}

export interface CreateEnfantRequest {
  nom: string;
  prenom: string;
  genre: string;
  dateNaissance: string;
  adresse?: string;
  parentId: number;
  bapteme?: string;
}

/**
 * Types pour les inscriptions
 */
export interface InscriptionResponse {
  id: number;
  enfantId: number;
  enfantNom: string;
  enfantPrenom: string;
  enfantGenre: string;
  enfantDateNaissance: string;
  enfantAge: number;
  parentId?: number;
  parentNom?: string;
  parentPrenom?: string;
  parentTelephone?: string;
  anneeExerciceId: number;
  anneeExercice: string;
  classeId: number;
  classeNom: string;
  estAssurance: boolean;
  createdAt: string;
}

export interface CreateInscriptionRequest {
  enfantId: number;
  anneeExerciceId: number;
  classeId: number;
  estAssurance?: boolean;
}

/**
 * Types pour les classes
 */
export interface Classe {
  id: number;
  nom: string;
  logo?: string;
  age?: number;
}

/**
 * Types pour le budget et les activités
 */
export interface DetailActiviteDto {
  details: string;
  montant: number;
}

export interface DetailActiviteResponse {
  id: number;
  details: string;
  montant: number;
  createdAt: string;
}

export interface CreateActiviteRequest {
  nom: string;
  description?: string;
  dateDebut?: string;
  dateFin?: string;
  budgetGlobalId: number;
  statusId?: number;
  details: DetailActiviteDto[];
}

export interface UpdateActiviteRequest {
  nom: string;
  description?: string;
  dateDebut?: string;
  dateFin?: string;
  statusId: number;
  details: DetailActiviteDto[];
}

export interface UpdateBudgetStatusRequest {
  statusId: number;
}

export interface ExportBudgetPdfRequest {
  anneeExerciceId: number;
  includeDate?: boolean;
  includeNomActivite?: boolean;
  includeCoutActivite?: boolean;
  includeDescriptionActivite?: boolean;
  includeDetailsActivite?: boolean;
  includeCoutDetails?: boolean;
  includeStatutActivite?: boolean;
}

export interface ActiviteResponse {
  id: number;
  nom: string;
  description?: string;
  dateDebut: string;
  dateFin: string;
  montant: number;
  budgetGlobalId: number;
  anneeExercice: string;
  status: string;
  statusId: number;
  details: DetailActiviteResponse[];
  createdAt: string;
}

export interface ActiviteStatusResponse {
  id: number;
  status: string;
}

export interface BudgetGlobalResponse {
  id: number;
  anneeExercice: string;
  anneeExerciceId: number;
  montant: number;
  status: string;
  statusId: number;
  nombreActivites: number;
  createdAt: string;
}

/**
 * Types pour la gestion de présence aux activités
 */
export interface ParticipantEnfantDto {
  inscriptionId: number;
  enfantId: number;
  nom: string;
  prenom: string;
  genre: string;
  classeId?: number;
  classeNom?: string;
}

export interface ParticipantStaffDto {
  staffId: number;
  instructeurId: number;
  nom: string;
  prenom: string;
  totem?: string;
  role?: string;
}

export interface PersonnesDisponiblesResponse {
  enfants: ParticipantEnfantDto[];
  staff: ParticipantStaffDto[];
}

export interface EnregistrerPresenceRequest {
  activiteId: number;
  enfantsPresents: number[];
  staffPresents: number[];
}

export interface ParticipantsResponse {
  enfants?: ParticipantEnfantDto[];
  staff?: ParticipantStaffDto[];
}
