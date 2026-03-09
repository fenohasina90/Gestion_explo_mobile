import apiService from './api.service';
import type { 
  StatistiquesAnnuelles,
  ProgressionAnnuelle,
  ProgrammeAvancement
} from '@/types';

/**
 * Service pour gérer l'historique des programmes
 */
class HistoriqueProgrammeService {
  private readonly BASE_URL = '/api/historique-programmes';

  /**
   * Récupère les statistiques annuelles des programmes
   * @param anneeExerciceId - ID de l'année d'exercice (optionnel)
   */
  async getStatistiquesAnnuelles(anneeExerciceId?: number): Promise<StatistiquesAnnuelles[]> {
    try {
      const params: any = {};
      if (anneeExerciceId) {
        params.anneeExerciceId = anneeExerciceId;
      }
      
      const response = await apiService.get<StatistiquesAnnuelles[]>(
        `${this.BASE_URL}/statistiques`,
        { params }
      );
      return response;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Erreur lors de la récupération des statistiques');
    }
  }

  /**
   * Récupère la progression annuelle des programmes
   * @param anneeExerciceId - ID de l'année d'exercice (optionnel)
   */
  async getProgressionAnnuelle(anneeExerciceId?: number): Promise<ProgressionAnnuelle[]> {
    try {
      const params: any = {};
      if (anneeExerciceId) {
        params.anneeExerciceId = anneeExerciceId;
      }
      
      const response = await apiService.get<ProgressionAnnuelle[]>(
        `${this.BASE_URL}/progression`,
        { params }
      );
      return response;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Erreur lors de la récupération de la progression');
    }
  }

  /**
   * Récupère l'avancement détaillé des programmes avec historique
   * @param anneeExerciceId - ID de l'année d'exercice (optionnel)
   */
  async getAvancementProgrammes(anneeExerciceId?: number): Promise<ProgrammeAvancement[]> {
    try {
      const params: any = {};
      if (anneeExerciceId) {
        params.anneeExerciceId = anneeExerciceId;
      }
      
      const response = await apiService.get<ProgrammeAvancement[]>(
        `${this.BASE_URL}/avancement`,
        { params }
      );
      return response;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Erreur lors de la récupération de l\'avancement');
    }
  }
}

export default new HistoriqueProgrammeService();
