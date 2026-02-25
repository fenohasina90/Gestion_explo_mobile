import apiService from './api.service';
import type {
  Parent,
  ParentSuggestion,
  CreateParentRequest,
  PageResponse,
} from '../types';

/**
 * Service pour la gestion des parents
 */
class ParentService {
  private readonly baseUrl = '/api/parents';

  /**
   * Rechercher des parents pour l'auto-complétion
   */
  async searchParents(query: string): Promise<ParentSuggestion[]> {
    return apiService.get<ParentSuggestion[]>(`${this.baseUrl}/search`, {
      params: { query },
    });
  }

  /**
   * Créer un nouveau parent
   */
  async createParent(data: CreateParentRequest): Promise<Parent> {
    return apiService.post<Parent>(this.baseUrl, data);
  }

  /**
   * Récupérer tous les parents
   */
  async getAllParents(): Promise<Parent[]> {
    // Appeler l'endpoint paginé avec une grande taille pour récupérer tous les parents
    const response = await apiService.get<PageResponse<Parent>>(this.baseUrl, {
      params: { page: 0, size: 1000, sort: 'nom', direction: 'asc' },
    });
    return response.content;
  }

  /**
   * Récupérer tous les parents avec pagination
   */
  async getAllParentsPaginated(
    page: number = 0,
    size: number = 10,
    sort: string = 'nom',
    direction: 'asc' | 'desc' = 'asc'
  ): Promise<PageResponse<Parent>> {
    return apiService.get<PageResponse<Parent>>(this.baseUrl, {
      params: { page, size, sort, direction },
    });
  }

  /**
   * Récupérer un parent par ID
   */
  async getParentById(id: number): Promise<Parent> {
    return apiService.get<Parent>(`${this.baseUrl}/${id}`);
  }

  /**
   * Supprimer un parent
   */
  async deleteParent(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }
}

export default new ParentService();
