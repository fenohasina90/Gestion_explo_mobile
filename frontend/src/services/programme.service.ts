import apiService from './api.service';
import type { 
  Programme, 
  CreateProgrammeRequest, 
  UpdateProgrammeRequest,
  HistoriqueProgramme,
  UpdateProgrammeStatusRequest
} from '../types';

/**
 * Service pour la gestion des programmes
 */
class ProgrammeService {
  private readonly baseUrl = '/api/programmes';

  /**
   * Créer un nouveau programme
   */
  async createProgramme(data: CreateProgrammeRequest): Promise<Programme> {
    return apiService.post<Programme>(this.baseUrl, data);
  }

  /**
   * Récupérer tous les programmes
   */
  async getAllProgrammes(): Promise<Programme[]> {
    return apiService.get<Programme[]>(this.baseUrl);
  }

  /**
   * Rechercher des programmes par filtres
   */
  async searchProgrammes(params: {
    categorieId?: number;
    classeId?: number;
    nom?: string;
  }): Promise<Programme[]> {
    return apiService.get<Programme[]>(`${this.baseUrl}/search`, { params });
  }

  /**
   * Récupérer un programme par ID
   */
  async getProgrammeById(id: number): Promise<Programme> {
    return apiService.get<Programme>(`${this.baseUrl}/${id}`);
  }

  /**
   * Mettre à jour un programme
   */
  async updateProgramme(id: number, data: UpdateProgrammeRequest): Promise<Programme> {
    return apiService.put<Programme>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Supprimer un programme
   */
  async deleteProgramme(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Récupérer l'historique d'un programme
   */
  async getHistoriqueProgramme(programmeId: number, cpId: number): Promise<HistoriqueProgramme[]> {
    return apiService.get<HistoriqueProgramme[]>(
      `${this.baseUrl}/${programmeId}/cp/${cpId}/historique`
    );
  }

  /**
   * Mettre à jour le statut d'un programme dans une CP
   */
  async updateProgrammeStatus(
    programmeId: number, 
    cpId: number, 
    data: UpdateProgrammeStatusRequest
  ): Promise<void> {
    return apiService.put<void>(
      `${this.baseUrl}/${programmeId}/cp/${cpId}/statut`,
      data
    );
  }
}

export default new ProgrammeService();
