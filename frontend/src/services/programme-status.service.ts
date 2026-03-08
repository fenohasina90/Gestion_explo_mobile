import apiService from './api.service';
import type { ChangeProgrammeStatusRequest, ProgrammeStatus, HistoriqueProgrammesResponse } from '../types';

/**
 * Service pour la gestion des statuts de programme
 */
class ProgrammeStatusService {
  private readonly baseUrl = '/api/programme-status';

  /**
   * Récupérer tous les statuts disponibles
   */
  async getAllStatuts(): Promise<ProgrammeStatus[]> {
    return apiService.get<ProgrammeStatus[]>(`${this.baseUrl}/statuts`);
  }

  /**
   * Changer le statut d'un programme dans une CP
   */
  async changeProgrammeStatus(data: ChangeProgrammeStatusRequest): Promise<HistoriqueProgrammesResponse> {
    return apiService.post<HistoriqueProgrammesResponse>(`${this.baseUrl}/change`, data);
  }
}

export default new ProgrammeStatusService();
