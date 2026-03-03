import apiService from './api.service';
import type {
  PersonnesDisponiblesResponse,
  EnregistrerPresenceCpRequest,
  ParticipantsResponse,
} from '@/types';

/**
 * Service pour la gestion de la présence aux classes progressives (CP)
 */
class CpPresenceService {
  private readonly baseUrl = '/api/cp-presence';

  /**
   * Récupérer les personnes disponibles pour faire la présence à une CP
   */
  async getPersonnesDisponibles(classeProgressiveId: number): Promise<PersonnesDisponiblesResponse> {
    return apiService.get<PersonnesDisponiblesResponse>(`${this.baseUrl}/disponibles/${classeProgressiveId}`);
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
  ): Promise<ParticipantsResponse> {
    const params: any = {
      filtreEnfant,
      filtreStaff,
    };
    
    if (classeId) {
      params.classeId = classeId;
    }

    return apiService.get<ParticipantsResponse>(`${this.baseUrl}/${classeProgressiveId}`, { params });
  }
}

export default new CpPresenceService();
