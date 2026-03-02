import apiService from './api.service';
import type { 
  CpDetailsResponse, 
  AddProgrammeToCpRequest, 
  UpdateCpDetailsInstructeurRequest 
} from '../types';

/**
 * Service pour la gestion des CP Details (affectation des programmes à une CP)
 */
class CpDetailsService {
  private readonly baseUrl = '/api/cp-details';

  /**
   * Ajouter un programme ou une activité libre à une CP
   */
  async addProgrammeToCP(data: AddProgrammeToCpRequest): Promise<CpDetailsResponse> {
    return apiService.post<CpDetailsResponse>(this.baseUrl, data);
  }

  /**
   * Récupérer tous les programmes d'une CP
   */
  async getCPDetails(cpId: number): Promise<CpDetailsResponse[]> {
    return apiService.get<CpDetailsResponse[]>(`${this.baseUrl}/cp/${cpId}`);
  }

  /**
   * Récupérer un CP Detail par ID
   */
  async getCPDetailById(id: number): Promise<CpDetailsResponse> {
    return apiService.get<CpDetailsResponse>(`${this.baseUrl}/${id}`);
  }

  /**
   * Mettre à jour les instructeurs d'un CP Detail
   */
  async updateInstructeurs(
    cpDetailsId: number, 
    data: UpdateCpDetailsInstructeurRequest
  ): Promise<CpDetailsResponse> {
    return apiService.put<CpDetailsResponse>(
      `${this.baseUrl}/${cpDetailsId}/instructeur`,
      data
    );
  }

  /**
   * Supprimer un programme d'une CP
   */
  async deleteCPDetail(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }
}

export default new CpDetailsService();
