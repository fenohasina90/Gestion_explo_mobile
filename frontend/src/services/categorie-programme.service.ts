import apiService from './api.service';
import type { 
  CategorieProgramme, 
  CreateCategorieProgrammeRequest, 
  UpdateCategorieProgrammeRequest 
} from '../types';

/**
 * Service pour la gestion des catégories de programme
 */
class CategorieProgrammeService {
  private readonly baseUrl = '/api/categories-programme';

  /**
   * Créer une nouvelle catégorie de programme
   */
  async createCategorie(data: CreateCategorieProgrammeRequest): Promise<CategorieProgramme> {
    return apiService.post<CategorieProgramme>(this.baseUrl, data);
  }

  /**
   * Récupérer toutes les catégories de programme
   */
  async getAllCategories(): Promise<CategorieProgramme[]> {
    return apiService.get<CategorieProgramme[]>(this.baseUrl);
  }

  /**
   * Récupérer une catégorie par ID
   */
  async getCategorieById(id: number): Promise<CategorieProgramme> {
    return apiService.get<CategorieProgramme>(`${this.baseUrl}/${id}`);
  }

  /**
   * Mettre à jour une catégorie
   */
  async updateCategorie(id: number, data: UpdateCategorieProgrammeRequest): Promise<CategorieProgramme> {
    return apiService.put<CategorieProgramme>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Supprimer une catégorie
   */
  async deleteCategorie(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Compter les programmes par catégorie
   */
  async countProgrammesByCategorie(id: number): Promise<number> {
    return apiService.get<number>(`${this.baseUrl}/${id}/count`);
  }
}

export default new CategorieProgrammeService();
