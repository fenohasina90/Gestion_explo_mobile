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
  anneeExerciceId: number;
  anneeExercice: string;
}

export interface User {
  id: number;
  username: string;
  role: string;
  active: boolean;
  anneeExerciceId: number;
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
  etat: number; // 1 = actif, 11 = supprimé
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

/**
 * Types pour les parents
 */
export interface Parent {
  id: number;
  nom: string;
  prenom: string;
  adresse: string;
  telephone: string;
  createdAt: string;
  updatedAt: string;
}

export interface ParentSuggestion {
  id: number;
  nom: string;
  prenom: string;
  nomComplet: string;
  telephone: string;
  adresse: string;
}

export interface CreateParentRequest {
  nom: string;
  prenom: string;
  adresse: string;
  telephone: string;
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
  adresse: string;
  parentId: number;
  parentNom: string;
  parentPrenom: string;
  bapteme?: string;
  createdAt: string;
  updatedAt: string;
}

export interface EnfantSuggestion {
  id: number;
  nom: string;
  prenom: string;
  nomComplet: string;
  genre: string;
  dateNaissance: string;
  age: number;
  parentNom: string;
  parentPrenom: string;
  parentNomComplet: string;
  classeId?: number;
  classeNom?: string;
}

export interface CreateEnfantRequest {
  nom: string;
  prenom: string;
  genre: string;
  dateNaissance: string;
  adresse: string;
  parentId: number;
  bapteme?: string;
}

/**
 * Types pour les classes
 */
export interface Classe {
  id: number;
  nom: string;
  age: number;
  ageMin: number;
  ageMax: number;
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
  parentId: number;
  parentNom: string;
  parentPrenom: string;
  parentTelephone: string;
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
 * Types pour la pagination
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
 * Types pour les budgets globaux
 */
export interface BudgetStatus {
  id: number;
  nom: string;
}

export interface BudgetGlobalResponse {
  id: number;
  anneeExerciceId: number;
  anneeExercice: string;
  statusId: number;
  status: string;
  montant: number;
  nombreActivites: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateBudgetGlobalRequest {
  anneeExerciceId: number;
  statusId: number;
}

export interface UpdateBudgetGlobalRequest {
  statusId: number;
}

/**
 * Types pour les activités
 */
export interface ActiviteStatus {
  id: number;
  nom: string;
}

export interface DetailActiviteDto {
  details: string;
  montant: number;
}

export interface ActiviteResponse {
  id: number;
  nom: string;
  description?: string;
  dateDebut: string;
  dateFin: string;
  budgetGlobalId: number;
  anneeExercice: string;
  statusId: number;
  status: string;
  montant: number;
  details: DetailActiviteDto[];
  createdAt: string;
}

export interface CreateActiviteRequest {
  nom: string;
  description?: string;
  dateDebut?: string;
  dateFin?: string;
  budgetGlobalId: number;
  statusId: number;
  details: DetailActiviteDto[];
}

export interface UpdateActiviteRequest {
  nom?: string;
  description?: string;
  dateDebut?: string;
  dateFin?: string;
  statusId?: number;
  details?: DetailActiviteDto[];
}

/**
 * Types pour les colonnes d'export PDF budget
 */
export interface ExportColumnsDto {
  includeDate: boolean;
  includeNomActivite: boolean;
  includeCoutActivite: boolean;
  includeDescriptionActivite: boolean;
  includeDetailsActivite: boolean;
  includeCoutDetails: boolean;
  includeStatutActivite: boolean;
}

/**
 * Types pour la participation aux activités
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
  role: string;
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

/**
 * Types pour la présence aux classes progressives (CP)
 */
export interface EnregistrerPresenceCpRequest {
  classeProgressiveId: number;
  enfantsPresents: number[];
  staffPresents: number[];
}

/**
 * Types pour les catégories de programme
 */
export interface CategorieProgramme {
  id: number;
  nom: string;
  nombreProgrammes: number;
}

export interface CreateCategorieProgrammeRequest {
  nom: string;
}

export interface UpdateCategorieProgrammeRequest {
  nom: string;
}

/**
 * Types pour les programmes
 */
export interface Programme {
  id: number;
  nom: string;
  description?: string;
  categorieId: number;
  categorieNom: string;
  classeId: number;
  classeNom: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateProgrammeRequest {
  nom: string;
  description?: string;
  categorieId: number;
  classeId: number;
}

export interface UpdateProgrammeRequest {
  nom: string;
  description?: string;
  categorieId: number;
  classeId: number;
}

/**
 * Types pour les statuts de programme
 */
export interface ProgrammeStatus {
  id: number;
  nom: string;
}

export interface ProgrammeStatusResponse {
  id: number;
  programmeId: number;
  programmeNom: string;
  classeProgressiveId: number;
  classeProgressiveDate: string;
  statusId: number;
  statusNom: string;
  dateChangement: string;
}

export interface UpdateProgrammeStatusRequest {
  statusId: number;
}

export interface ChangeProgrammeStatusRequest {
  programmeId: number;
  classeProgressiveId: number;
  newStatusId: number;
}

/**
 * Types pour les classes progressives (CP)
 */
export interface ClasseProgressive {
  id: number;
  dateCp: string;
  heureDebut: string;
  heureFin: string;
  niveau?: string;
  etat?: number; // 0 = ouverte, 1 = clôturée
  anneeExerciceId: number;
  anneeExercice: string;
  nombreProgrammes: number;
}

export interface CreateClasseProgressiveRequest {
  dateCp: string;
  heureDebut: string;
  heureFin: string;
  niveau?: string;
  anneeExerciceId: number;
}

export interface UpdateClasseProgressiveRequest {
  dateCp: string;
  heureDebut: string;
  heureFin: string;
  niveau?: string;
  anneeExerciceId: number;
}

export interface ClasseProgressiveFilterRequest {
  dateDebut?: string;
  dateFin?: string;
  anneeExerciceId?: number;
}

/**
 * Types pour les détails de CP (affectation programmes)
 */
export interface CpDetailsInstructeurDto {
  id: number;
  nom: string;
  prenom: string;
  totem?: string;
}

export interface CpDetails {
  id: number;
  classeProgressiveId: number;
  classeProgressiveDate: string;
  programmeId?: number;
  programmeNom?: string;
  programmeDescription?: string;
  description?: string; // Pour activité libre
  classeId?: number;
  classeNom?: string;
  statusId?: number;
  statusNom?: string;
  instructeurs: CpDetailsInstructeurDto[];
}

export interface AddProgrammeToCpRequest {
  classeProgressiveId: number;
  programmeId?: number;
  description?: string;
  instructeurIds: number[];
}

export interface UpdateCpDetailsInstructeurRequest {
  instructeurIds: number[];
}

/**
 * Types pour l'historique des programmes
 */
export interface HistoriqueProgramme {
  id: number;
  programmeId: number;
  programmeNom: string;
  classeProgressiveId: number;
  classeProgressiveDate: string;
  ancienStatusId: number;
  ancienStatusNom: string;
  nouveauStatusId: number;
  nouveauStatusNom: string;
  dateChangement: string;
  utilisateurId: number;
  username: string;
}

