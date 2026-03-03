import apiService from './api.service';
import type {
  CpPersonnesDisponiblesResponse,
  EnregistrerPresenceCpRequest,
  CpParticipantsResponse,
} from '../types';

/**
 * Service pour la gestion de la présence aux classes progressives
 */
class CpPresenceService {
  private readonly baseUrl = '/api/cp-presence';

  /**
   * Récupérer les personnes disponibles pour faire la présence
   */
  async getPersonnesDisponibles(classeProgressiveId: number): Promise<CpPersonnesDisponiblesResponse> {
    return apiService.get<CpPersonnesDisponiblesResponse>(`${this.baseUrl}/disponibles/${classeProgressiveId}`);
  }

  /**
   * Enregistrer la présence à une classe progressive
   */
  async enregistrerPresence(request: EnregistrerPresenceCpRequest): Promise<string> {
    return apiService.post<string>(`${this.baseUrl}/presence`, request);
  }

  /**
   * Consulter les participants d'une classe progressive avec filtres
   */
  async getParticipants(
    classeProgressiveId: number,
    filtreEnfant: boolean = true,
    filtreStaff: boolean = true,
    classeId?: number
  ): Promise<CpParticipantsResponse> {
    const params: any = {
      filtreEnfant,
      filtreStaff,
    };
    
    if (classeId) {
      params.classeId = classeId;
    }

    return apiService.get<CpParticipantsResponse>(`${this.baseUrl}/${classeProgressiveId}`, { params });
  }
}

export default new CpPresenceService();
