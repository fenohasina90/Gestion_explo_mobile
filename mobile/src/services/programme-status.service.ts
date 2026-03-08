import api from './api.service';
import type {
  ProgrammeStatus,
  ProgrammeStatusResponse,
  UpdateProgrammeStatusRequest,
  ChangeProgrammeStatusRequest,
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
    return await api.get<ProgrammeStatus[]>(`${this.BASE_URL}/statuts`);
  }

  /**
   * Récupérer le statut d'un programme dans une CP
   */
  async getStatusByProgrammeAndCP(programmeId: number, classeProgressiveId: number): Promise<ProgrammeStatusResponse> {
    return await api.get<ProgrammeStatusResponse>(
      `${this.BASE_URL}/programme/${programmeId}/cp/${classeProgressiveId}`
    );
  }

  /**
   * Modifier le statut d'un programme dans une CP
   */
  async updateStatus(
    programmeId: number,
    classeProgressiveId: number,
    request: UpdateProgrammeStatusRequest
  ): Promise<ProgrammeStatusResponse> {
    return await api.put<ProgrammeStatusResponse>(
      `${this.BASE_URL}/programme/${programmeId}/cp/${classeProgressiveId}`,
      request
    );
  }

  /**
   * Changer le statut d'un programme dans une CP (nouveau endpoint)
   */
  async changeProgrammeStatus(request: ChangeProgrammeStatusRequest): Promise<any> {
    return await api.post<any>(`${this.BASE_URL}/change`, request);
  }

  /**
   * Récupérer l'historique d'un programme
   */
  async getHistoriqueByProgramme(programmeId: number): Promise<HistoriqueProgramme[]> {
    return await api.get<HistoriqueProgramme[]>(`${this.BASE_URL}/programme/${programmeId}/historique`);
  }

  /**
   * Récupérer tout l'historique
   */
  async getAllHistorique(): Promise<HistoriqueProgramme[]> {
    return await api.get<HistoriqueProgramme[]>(`${this.BASE_URL}/historique`);
  }
}

export default new ProgrammeStatusService();
