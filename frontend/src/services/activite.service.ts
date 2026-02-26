import apiService from './api.service';
import type { 
  CreateActiviteRequest,
  UpdateActiviteRequest,
  ActiviteResponse, 
  ActiviteStatusResponse 
} from '../types';

/**
 * Service pour la gestion des activités
 */
class ActiviteService {
  private readonly baseUrl = '/api/activites';

  /**
   * Créer une nouvelle activité
   */
  async createActivite(request: CreateActiviteRequest): Promise<ActiviteResponse> {
    return apiService.post<ActiviteResponse>(this.baseUrl, request);
  }

  /**
   * Modifier une activité existante
   */
  async updateActivite(id: number, request: UpdateActiviteRequest): Promise<ActiviteResponse> {
    return apiService.put<ActiviteResponse>(`${this.baseUrl}/${id}`, request);
  }

  /**
   * Supprimer une activité
   */
  async deleteActivite(id: number): Promise<void> {
    return apiService.delete(`${this.baseUrl}/${id}`);
  }

  /**
   * Récupérer les activités par année d'exercice
   */
  async getActivitesByAnneeExercice(anneeExerciceId: number): Promise<ActiviteResponse[]> {
    return apiService.get<ActiviteResponse[]>(`${this.baseUrl}/annee/${anneeExerciceId}`);
  }

  /**
   * Récupérer une activité par ID
   */
  async getActiviteById(id: number): Promise<ActiviteResponse> {
    return apiService.get<ActiviteResponse>(`${this.baseUrl}/${id}`);
  }

  /**
   * Récupérer tous les statuts disponibles
   */
  async getAllStatuts(): Promise<ActiviteStatusResponse[]> {
    return apiService.get<ActiviteStatusResponse[]>(`${this.baseUrl}/statuts`);
  }
}

export default new ActiviteService();
