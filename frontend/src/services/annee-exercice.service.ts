import apiService from './api.service';
import type { AnneeExercice, CreateAnneeExerciceRequest } from '../types';

/**
 * Service pour la gestion des années d'exercice
 */
class AnneeExerciceService {
  private readonly baseUrl = '/api/annee-exercice';

  /**
   * Créer une nouvelle année d'exercice
   */
  async createAnneeExercice(data: CreateAnneeExerciceRequest): Promise<AnneeExercice> {
    return apiService.post<AnneeExercice>(this.baseUrl, data);
  }

  /**
   * Récupérer toutes les années d'exercice
   */
  async getAllAnneesExercice(): Promise<AnneeExercice[]> {
    return apiService.get<AnneeExercice[]>(this.baseUrl);
  }

  /**
   * Récupérer une année d'exercice par ID
   */
  async getAnneeExerciceById(id: number): Promise<AnneeExercice> {
    return apiService.get<AnneeExercice>(`${this.baseUrl}/${id}`);
  }

  /**
   * Récupérer l'année d'exercice la plus récente
   */
  async getLatestAnneeExercice(): Promise<AnneeExercice> {
    return apiService.get<AnneeExercice>(`${this.baseUrl}/latest`);
  }

  /**
   * Supprimer une année d'exercice
   */
  async deleteAnneeExercice(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }
}

export default new AnneeExerciceService();
