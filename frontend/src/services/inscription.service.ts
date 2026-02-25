import apiService from './api.service';
import type {
  InscriptionResponse,
  CreateInscriptionRequest,
} from '../types';

/**
 * Service pour la gestion des inscriptions
 */
class InscriptionService {
  private readonly baseUrl = '/api/inscriptions';

  /**
   * Créer une nouvelle inscription
   */
  async createInscription(data: CreateInscriptionRequest): Promise<InscriptionResponse> {
    return apiService.post<InscriptionResponse>(this.baseUrl, data);
  }

  /**
   * Récupérer toutes les inscriptions
   */
  async getAllInscriptions(): Promise<InscriptionResponse[]> {
    return apiService.get<InscriptionResponse[]>(this.baseUrl);
  }

  /**
   * Récupérer une inscription par ID
   */
  async getInscriptionById(id: number): Promise<InscriptionResponse> {
    return apiService.get<InscriptionResponse>(`${this.baseUrl}/${id}`);
  }

  /**
   * Filtrer les inscriptions
   */
  async filterInscriptions(
    anneeExerciceId?: number,
    classeId?: number,
    genre?: string
  ): Promise<InscriptionResponse[]> {
    const params: Record<string, any> = {};
    if (anneeExerciceId) params.anneeExerciceId = anneeExerciceId;
    if (classeId) params.classeId = classeId;
    if (genre) params.genre = genre;

    return apiService.get<InscriptionResponse[]>(`${this.baseUrl}/filter`, { params });
  }

  /**
   * Mettre à jour le statut d'assurance
   */
  async updateAssurance(id: number, estAssurance: boolean): Promise<InscriptionResponse> {
    return apiService.patch<InscriptionResponse>(`${this.baseUrl}/${id}/assurance`, null, {
      params: { estAssurance },
    });
  }

  /**
   * Supprimer une inscription
   */
  async deleteInscription(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }
}

export default new InscriptionService();
