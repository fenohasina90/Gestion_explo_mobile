import apiService from './api.service';
import type { 
  StatistiqueEnfant,
  StatistiqueStaff,
  StatistiqueFilterRequest
} from '../types';

/**
 * Service pour la gestion des statistiques des enfants et des staffs
 */
class StatistiqueService {
  private readonly baseUrl = '/api/statistiques';

  /**
   * Récupérer les statistiques de tous les enfants avec filtres
   */
  async getStatistiquesEnfants(filters?: StatistiqueFilterRequest): Promise<StatistiqueEnfant[]> {
    const params = new URLSearchParams();
    
    if (filters?.anneeExerciceId) {
      params.append('anneeExerciceId', filters.anneeExerciceId.toString());
    }
    if (filters?.classeId) {
      params.append('classeId', filters.classeId.toString());
    }
    if (filters?.genre) {
      params.append('genre', filters.genre);
    }

    const queryString = params.toString();
    const url = queryString ? `${this.baseUrl}/enfants?${queryString}` : `${this.baseUrl}/enfants`;
    
    return apiService.get<StatistiqueEnfant[]>(url);
  }

  /**
   * Récupérer les statistiques d'un enfant spécifique
   */
  async getStatistiqueEnfant(inscriptionId: number): Promise<StatistiqueEnfant> {
    return apiService.get<StatistiqueEnfant>(`${this.baseUrl}/enfants/${inscriptionId}`);
  }

  /**
   * Récupérer les statistiques de tous les staffs avec filtres
   */
  async getStatistiquesStaffs(filters?: StatistiqueFilterRequest): Promise<StatistiqueStaff[]> {
    const params = new URLSearchParams();
    
    if (filters?.anneeExerciceId) {
      params.append('anneeExerciceId', filters.anneeExerciceId.toString());
    }

    const queryString = params.toString();
    const url = queryString ? `${this.baseUrl}/staffs?${queryString}` : `${this.baseUrl}/staffs`;
    
    return apiService.get<StatistiqueStaff[]>(url);
  }

  /**
   * Récupérer les statistiques d'un staff spécifique
   */
  async getStatistiqueStaff(staffId: number, anneeExerciceId?: number): Promise<StatistiqueStaff> {
    const params = new URLSearchParams();
    
    if (anneeExerciceId) {
      params.append('anneeExerciceId', anneeExerciceId.toString());
    }

    const queryString = params.toString();
    const url = queryString 
      ? `${this.baseUrl}/staffs/${staffId}?${queryString}` 
      : `${this.baseUrl}/staffs/${staffId}`;
    
    return apiService.get<StatistiqueStaff>(url);
  }
}

export default new StatistiqueService();
