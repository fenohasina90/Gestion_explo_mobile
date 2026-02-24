import apiService from './api.service';
import type {
  Utilisateur,
  CreateUtilisateurRequest,
  UpdateUtilisateurRequest,
  Role,
} from '../types';

/**
 * Service pour la gestion des utilisateurs
 */
class UtilisateurService {
  private readonly baseUrl = '/api/utilisateur';

  /**
   * Récupérer l'utilisateur connecté
   */
  async getCurrentUser(): Promise<Utilisateur> {
    return apiService.get<Utilisateur>(`${this.baseUrl}/me`);
  }

  /**
   * Créer un nouvel utilisateur
   */
  async createUtilisateur(data: CreateUtilisateurRequest): Promise<Utilisateur> {
    return apiService.post<Utilisateur>(this.baseUrl, data);
  }

  /**
   * Mettre à jour un utilisateur
   */
  async updateUtilisateur(id: number, data: UpdateUtilisateurRequest): Promise<Utilisateur> {
    return apiService.put<Utilisateur>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Supprimer un utilisateur
   */
  async deleteUtilisateur(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Récupérer tous les utilisateurs
   */
  async getAllUtilisateurs(): Promise<Utilisateur[]> {
    return apiService.get<Utilisateur[]>(this.baseUrl);
  }

  /**
   * Récupérer un utilisateur par ID
   */
  async getUtilisateurById(id: number): Promise<Utilisateur> {
    return apiService.get<Utilisateur>(`${this.baseUrl}/${id}`);
  }

  /**
   * Récupérer les utilisateurs par année d'exercice
   */
  async getUtilisateursByAnnee(anneeId: number): Promise<Utilisateur[]> {
    return apiService.get<Utilisateur[]>(`${this.baseUrl}/annee/${anneeId}`);
  }

  /**
   * Récupérer les utilisateurs actifs
   */
  async getUtilisateursActifs(): Promise<Utilisateur[]> {
    return apiService.get<Utilisateur[]>(`${this.baseUrl}/actifs`);
  }

  /**
   * Récupérer tous les rôles  disponibles
   */
  async getAllRoles(): Promise<Role[]> {
    return apiService.get<Role[]>(`${this.baseUrl}/roles`);
  }
}

export default new UtilisateurService();
