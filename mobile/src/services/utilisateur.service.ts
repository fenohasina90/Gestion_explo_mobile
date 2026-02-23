import apiService from './api.service';
import { API_ENDPOINTS } from '@/config/api.config';
import type {
  Utilisateur,
  CreateUtilisateurRequest,
  UpdateUtilisateurRequest,
  Role,
  UserInfoResponse
} from '@/types';

/**
 * Service pour la gestion des utilisateurs
 */
class UtilisateurService {
  /**
   * Récupère les informations de l'utilisateur connecté
   */
  async getCurrentUser(): Promise<UserInfoResponse> {
    return apiService.get<UserInfoResponse>(API_ENDPOINTS.currentUser);
  }

  /**
   * Crée un nouvel utilisateur (Directeur uniquement)
   */
  async createUtilisateur(request: CreateUtilisateurRequest): Promise<Utilisateur> {
    return apiService.post<Utilisateur>(API_ENDPOINTS.utilisateurs, request);
  }

  /**
   * Met à jour un utilisateur
   */
  async updateUtilisateur(id: number, request: UpdateUtilisateurRequest): Promise<Utilisateur> {
    return apiService.put<Utilisateur>(API_ENDPOINTS.utilisateurById(id), request);
  }

  /**
   * Supprime un utilisateur (Directeur uniquement)
   */
  async deleteUtilisateur(id: number): Promise<void> {
    return apiService.delete<void>(API_ENDPOINTS.utilisateurById(id));
  }

  /**
   * Récupère tous les utilisateurs
   */
  async getAllUtilisateurs(): Promise<Utilisateur[]> {
    return apiService.get<Utilisateur[]>(API_ENDPOINTS.utilisateurs);
  }

  /**
   * Récupère un utilisateur par son ID
   */
  async getUtilisateurById(id: number): Promise<Utilisateur> {
    return apiService.get<Utilisateur>(API_ENDPOINTS.utilisateurById(id));
  }

  /**
   * Récupère les utilisateurs par année d'exercice
   */
  async getUtilisateursByAnnee(anneeId: number): Promise<Utilisateur[]> {
    return apiService.get<Utilisateur[]>(API_ENDPOINTS.utilisateursByAnnee(anneeId));
  }

  /**
   * Récupère les utilisateurs actifs
   */
  async getUtilisateursActifs(): Promise<Utilisateur[]> {
    return apiService.get<Utilisateur[]>(API_ENDPOINTS.utilisateursActifs);
  }

  /**
   * Récupère tous les rôles disponibles
   */
  async getAllRoles(): Promise<Role[]> {
    return apiService.get<Role[]>(API_ENDPOINTS.roles);
  }
}

export default new UtilisateurService();
