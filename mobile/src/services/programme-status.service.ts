import api from './api.service';
import type {
  ProgrammeStatus,
  ProgrammeStatusResponse,
  UpdateProgrammeStatusRequest,
  HistoriqueProgramme
} from '@/types';

/**
 * Service pour la gestion des statuts de programme
 */
class ProgrammeStatusService {
  private readonly BASE_URL = '/api/programme-status';

  /**
   * Récupérer tous les statuts disponibles
   */
  async getAllStatuts(): Promise<ProgrammeStatus[]> {
    const response = await api.get<ProgrammeStatus[]>(`${this.BASE_URL}/statuts`);
    return response.data;
  }

  /**
   * Récupérer le statut d'un programme dans une CP
   */
  async getStatusByProgrammeAndCP(programmeId: number, classeProgressiveId: number): Promise<ProgrammeStatusResponse> {
    const response = await api.get<ProgrammeStatusResponse>(
      `${this.BASE_URL}/programme/${programmeId}/cp/${classeProgressiveId}`
    );
    return response.data;
  }

  /**
   * Modifier le statut d'un programme dans une CP
   */
  async updateStatus(
    programmeId: number,
    classeProgressiveId: number,
    request: UpdateProgrammeStatusRequest
  ): Promise<ProgrammeStatusResponse> {
    const response = await api.put<ProgrammeStatusResponse>(
      `${this.BASE_URL}/programme/${programmeId}/cp/${classeProgressiveId}`,
      request
    );
    return response.data;
  }

  /**
   * Récupérer l'historique d'un programme
   */
  async getHistoriqueByProgramme(programmeId: number): Promise<HistoriqueProgramme[]> {
    const response = await api.get<HistoriqueProgramme[]>(`${this.BASE_URL}/programme/${programmeId}/historique`);
    return response.data;
  }

  /**
   * Récupérer tout l'historique
   */
  async getAllHistorique(): Promise<HistoriqueProgramme[]> {
    const response = await api.get<HistoriqueProgramme[]>(`${this.BASE_URL}/historique`);
    return response.data;
  }
}

export default new ProgrammeStatusService();
