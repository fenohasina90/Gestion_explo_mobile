import apiService from './api.service';
import { API_ENDPOINTS } from '@/config/api.config';
import type { AnneeExercice, CreateAnneeExerciceRequest } from '@/types';

/**
 * Service pour la gestion des années d'exercice
 */
class AnneeExerciceService {
  /**
   * Crée une nouvelle année d'exercice
   */
  async createAnneeExercice(request: CreateAnneeExerciceRequest): Promise<AnneeExercice> {
    return apiService.post<AnneeExercice>(API_ENDPOINTS.anneesExercice, request);
  }

  /**
   * Récupère toutes les années d'exercice
   */
  async getAllAnneesExercice(): Promise<AnneeExercice[]> {
    return apiService.get<AnneeExercice[]>(API_ENDPOINTS.anneesExercice);
  }

  /**
   * Récupère une année d'exercice par son ID
   */
  async getAnneeExerciceById(id: number): Promise<AnneeExercice> {
    return apiService.get<AnneeExercice>(API_ENDPOINTS.anneeExerciceById(id));
  }

  /**
   * Récupère l'année d'exercice en cours
   */
  async getCurrentAnneeExercice(): Promise<AnneeExercice> {
    return apiService.get<AnneeExercice>(API_ENDPOINTS.currentYear);
  }

  /**
   * Récupère l'année d'exercice la plus récente
   */
  async getRecentAnneeExercice(): Promise<AnneeExercice> {
    return apiService.get<AnneeExercice>(API_ENDPOINTS.recentYear);
  }

  /**
   * Supprime une année d'exercice
   */
  async deleteAnneeExercice(id: number): Promise<void> {
    return apiService.delete<void>(API_ENDPOINTS.anneeExerciceById(id));
  }
}

export default new AnneeExerciceService();
