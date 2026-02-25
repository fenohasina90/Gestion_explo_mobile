import apiService from './api.service';
import type { Classe } from '../types';

/**
 * Service pour la gestion des classes
 */
class ClasseService {
  private readonly baseUrl = '/api/classes';

  /**
   * Récupérer toutes les classes
   */
  async getAllClasses(): Promise<Classe[]> {
    return apiService.get<Classe[]>(this.baseUrl);
  }

  /**
   * Récupérer une classe par ID
   */
  async getClasseById(id: number): Promise<Classe> {
    return apiService.get<Classe>(`${this.baseUrl}/${id}`);
  }
}

export default new ClasseService();
