import apiService from './api.service';
import type {
  Instructeur,
  InstructeurSuggestion,
  CreateInstructeurRequest,
} from '@/types';

/**
 * Service pour la gestion des instructeurs
 */
class InstructeurService {
  private readonly baseUrl = '/api/instructeur';

  /**
   * Créer un nouvel instructeur
   */
  async createInstructeur(data: CreateInstructeurRequest): Promise<Instructeur> {
    return apiService.post<Instructeur>(this.baseUrl, data);
  }

  /**
   * Mettre à jour un instructeur
   */
  async updateInstructeur(id: number, data: CreateInstructeurRequest): Promise<Instructeur> {
    return apiService.put<Instructeur>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Supprimer un instructeur
   */
  async deleteInstructeur(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Récupérer tous les instructeurs
   */
  async getAllInstructeurs(): Promise<Instructeur[]> {
    return apiService.get<Instructeur[]>(this.baseUrl);
  }

  /**
   * Récupérer un instructeur par ID
   */
  async getInstructeurById(id: number): Promise<Instructeur> {
    return apiService.get<Instructeur>(`${this.baseUrl}/${id}`);
  }

  /**
   * Rechercher des instructeurs (auto-complétion)
   */
  async searchInstructeurs(query: string): Promise<InstructeurSuggestion[]> {
    return apiService.get<InstructeurSuggestion[]>(`${this.baseUrl}/search`, {
      params: { query }
    });
  }
}

export default new InstructeurService();
