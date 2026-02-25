import apiService from './api.service';
import type {
  EnfantResponse,
  EnfantSuggestion,
  CreateEnfantRequest,
  PageResponse,
} from '../types';

/**
 * Service pour la gestion des enfants
 */
class EnfantService {
  private readonly baseUrl = '/api/enfants';

  /**
   * Rechercher des enfants pour l'auto-complétion (10-15 ans uniquement)
   */
  async searchEnfants(query: string, anneeExerciceId: number): Promise<EnfantSuggestion[]> {
    return apiService.get<EnfantSuggestion[]>(`${this.baseUrl}/search`, {
      params: { query, anneeExerciceId },
    });
  }

  /**
   * Créer un nouvel enfant
   */
  async createEnfant(data: CreateEnfantRequest, anneeExerciceId: number): Promise<EnfantResponse> {
    return apiService.post<EnfantResponse>(this.baseUrl, data, {
      params: { anneeExerciceId },
    });
  }

  /**
   * Récupérer tous les enfants
   */
  async getAllEnfants(): Promise<EnfantResponse[]> {
    const response = await apiService.get<PageResponse<EnfantResponse>>(this.baseUrl, {
      params: { page: 0, size: 1000, sort: 'nom', direction: 'asc' },
    });
    return response.content;
  }

  /**
   * Récupérer tous les enfants avec pagination
   */
  async getAllEnfantsPaginated(
    page: number = 0,
    size: number = 10,
    sort: string = 'nom',
    direction: 'asc' | 'desc' = 'asc'
  ): Promise<PageResponse<EnfantResponse>> {
    return apiService.get<PageResponse<EnfantResponse>>(this.baseUrl, {
      params: { page, size, sort, direction },
    });
  }

  /**
   * Récupérer un enfant par ID
   */
  async getEnfantById(id: number): Promise<EnfantResponse> {
    return apiService.get<EnfantResponse>(`${this.baseUrl}/${id}`);
  }

  /**
   * Récupérer les enfants d'un parent
   */
  async getEnfantsByParentId(parentId: number): Promise<EnfantResponse[]> {
    return apiService.get<EnfantResponse[]>(`${this.baseUrl}/parent/${parentId}`);
  }

  /**
   * Supprimer un enfant
   */
  async deleteEnfant(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }
}

export default new EnfantService();
