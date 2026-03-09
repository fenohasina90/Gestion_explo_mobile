import apiService from './api.service';
import type {
  StatistiqueEnfant,
  StatistiqueStaff,
  StatistiqueFilterRequest,
} from '@/types';

/**
 * Service pour la gestion des statistiques
 */
class StatistiqueService {
  private readonly baseUrl = '/api/statistiques';

  /**
   * Récupérer les statistiques des enfants avec filtres optionnels
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
    
    const url = params.toString() 
      ? `${this.baseUrl}/enfants?${params}` 
      : `${this.baseUrl}/enfants`;
    
    return apiService.get<StatistiqueEnfant[]>(url);
  }

  /**
   * Récupérer les statistiques d'un enfant spécifique
   */
  async getStatistiqueEnfant(inscriptionId: number): Promise<StatistiqueEnfant> {
    return apiService.get<StatistiqueEnfant>(`${this.baseUrl}/enfants/${inscriptionId}`);
  }

  /**
   * Récupérer les statistiques des staffs avec filtres optionnels
   */
  async getStatistiquesStaffs(filters?: StatistiqueFilterRequest): Promise<StatistiqueStaff[]> {
    const params = new URLSearchParams();
    
    if (filters?.anneeExerciceId) {
      params.append('anneeExerciceId', filters.anneeExerciceId.toString());
    }
    
    const url = params.toString() 
      ? `${this.baseUrl}/staffs?${params}` 
      : `${this.baseUrl}/staffs`;
    
    return apiService.get<StatistiqueStaff[]>(url);
  }

  /**
   * Récupérer les statistiques d'un staff spécifique
   */
  async getStatistiqueStaff(staffId: number, anneeExerciceId?: number): Promise<StatistiqueStaff> {
    const params = anneeExerciceId ? { anneeExerciceId: anneeExerciceId.toString() } : {};
    return apiService.get<StatistiqueStaff>(`${this.baseUrl}/staffs/${staffId}`, { params });
  }
}

export default new StatistiqueService();
