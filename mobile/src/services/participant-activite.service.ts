import apiService from './api.service';
import type {
  PersonnesDisponiblesResponse,
  EnregistrerPresenceRequest,
  ParticipantsResponse,
} from '@/types';

/**
 * Service pour la gestion de la présence aux activités
 */
class ParticipantActiviteService {
  private readonly baseUrl = '/api/participants';

  /**
   * Récupérer les personnes disponibles pour faire la présence
   */
  async getPersonnesDisponibles(activiteId: number): Promise<PersonnesDisponiblesResponse> {
    return apiService.get<PersonnesDisponiblesResponse>(`${this.baseUrl}/disponibles/${activiteId}`);
  }

  /**
   * Enregistrer la présence à une activité
   */
  async enregistrerPresence(request: EnregistrerPresenceRequest): Promise<string> {
    return apiService.post<string>(`${this.baseUrl}/presence`, request);
  }

  /**
   * Consulter les participants d'une activité avec filtres
   */
  async getParticipants(
    activiteId: number,
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

    return apiService.get<ParticipantsResponse>(`${this.baseUrl}/${activiteId}`, { params });
  }

  /**
   * Annuler une activité
   */
  async annulerActivite(activiteId: number): Promise<any> {
    return apiService.put<any>(`/api/activites/${activiteId}/annuler`, {});
  }
}

export default new ParticipantActiviteService();
