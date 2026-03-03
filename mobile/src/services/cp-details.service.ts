import api from './api.service';
import type {
  CpDetails,
  AddProgrammeToCpRequest,
  UpdateCpDetailsInstructeurRequest
} from '@/types';

/**
 * Service pour la gestion des affectations de programmes aux CP
 */
class CpDetailsService {
  private readonly BASE_URL = '/api/cp-details';

  /**
   * Récupérer les programmes d'une CP
   */
  async getProgrammesByCP(classeProgressiveId: number): Promise<CpDetails[]> {
    return await api.get<CpDetails[]>(`${this.BASE_URL}/cp/${classeProgressiveId}`);
  }

  /**
   * Récupérer un détail de CP par ID
   */
  async getCpDetailsById(id: number): Promise<CpDetails> {
    return await api.get<CpDetails>(`${this.BASE_URL}/${id}`);
  }

  /**
   * Ajouter un programme ou activité libre à une CP
   */
  async addProgrammeToCP(request: AddProgrammeToCpRequest): Promise<CpDetails> {
    return await api.post<CpDetails>(this.BASE_URL, request);
  }

  /**
   * Modifier les instructeurs d'un programme dans une CP
   */
  async updateInstructeur(cpDetailsId: number, request: UpdateCpDetailsInstructeurRequest): Promise<CpDetails> {
    return await api.put<CpDetails>(`${this.BASE_URL}/${cpDetailsId}/instructeur`, request);
  }

  /**
   * Supprimer un programme d'une CP
   */
  async removeProgrammeFromCP(cpDetailsId: number): Promise<void> {
    await api.delete(`${this.BASE_URL}/${cpDetailsId}`);
  }
}

export default new CpDetailsService();
