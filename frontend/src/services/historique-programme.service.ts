import apiService from './api.service';
import type { 
  HistoriqueProgramme,
  ProgressionAnnuelle,
  StatistiquesAnnuelles,
  ProgrammeAvancement
} from '../types';

/**
 * Service pour la gestion de l'historique des programmes
 */
class HistoriqueProgrammeService {
  private readonly baseUrl = '/api/historique-programmes';

  /**
   * Récupérer l'historique complet d'un programme
   */
  async getHistoriqueProgramme(programmeId: number): Promise<HistoriqueProgramme[]> {
    return apiService.get<HistoriqueProgramme[]>(`${this.baseUrl}/programme/${programmeId}`);
  }

  /**
   * Récupérer l'historique d'un programme pour une année spécifique
   */
  async getHistoriqueProgrammeParAnnee(
    programmeId: number, 
    anneeId: number
  ): Promise<HistoriqueProgramme[]> {
    return apiService.get<HistoriqueProgramme[]>(
      `${this.baseUrl}/programme/${programmeId}/annee/${anneeId}`
    );
  }

  /**
   * Récupérer l'historique de tous les programmes d'une CP
   */
  async getHistoriqueCp(cpId: number): Promise<HistoriqueProgramme[]> {
    return apiService.get<HistoriqueProgramme[]>(`${this.baseUrl}/cp/${cpId}`);
  }

  /**
   * Récupérer la progression annuelle de tous les programmes
   */
  async getProgressionAnnuelle(anneeId?: number): Promise<ProgressionAnnuelle[]> {
    const url = anneeId 
      ? `${this.baseUrl}/progression?anneeExerciceId=${anneeId}`
      : `${this.baseUrl}/progression`;
    return apiService.get<ProgressionAnnuelle[]>(url);
  }

  /**
   * Récupérer les statistiques annuelles
   */
  async getStatistiquesAnnuelles(anneeId?: number): Promise<StatistiquesAnnuelles[]> {
    const url = anneeId 
      ? `${this.baseUrl}/statistiques?anneeExerciceId=${anneeId}`
      : `${this.baseUrl}/statistiques`;
    return apiService.get<StatistiquesAnnuelles[]>(url);
  }

  /**
   * Récupérer l'avancement détaillé de tous les programmes
   */
  async getAvancementProgrammes(anneeId?: number): Promise<ProgrammeAvancement[]> {
    const url = anneeId 
      ? `${this.baseUrl}/avancement?anneeExerciceId=${anneeId}`
      : `${this.baseUrl}/avancement`;
    return apiService.get<ProgrammeAvancement[]>(url);
  }

  /**
   * Initialiser les statuts pour une nouvelle année
   */
  async initialiserAnnee(anneeId: number): Promise<{ message: string; nombreProgrammes: number }> {
    return apiService.post<{ message: string; nombreProgrammes: number }>(
      `${this.baseUrl}/initialiser-annee/${anneeId}`,
      {}
    );
  }
}

export default new HistoriqueProgrammeService();
