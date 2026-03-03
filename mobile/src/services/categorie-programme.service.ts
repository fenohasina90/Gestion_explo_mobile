import api from './api.service';
import type {
  CategorieProgramme,
  CreateCategorieProgrammeRequest,
  UpdateCategorieProgrammeRequest
} from '@/types';

/**
 * Service pour la gestion des catégories de programme
 */
class CategorieProgrammeService {
  private readonly BASE_URL = '/api/categories-programme';

  /**
   * Récupérer toutes les catégories
   */
  async getAllCategories(): Promise<CategorieProgramme[]> {
    return await api.get<CategorieProgramme[]>(this.BASE_URL);
  }

  /**
   * Récupérer une catégorie par ID
   */
  async getCategorieById(id: number): Promise<CategorieProgramme> {
    return await api.get<CategorieProgramme>(`${this.BASE_URL}/${id}`);
  }

  /**
   * Créer une nouvelle catégorie
   */
  async createCategorie(request: CreateCategorieProgrammeRequest): Promise<CategorieProgramme> {
    return await api.post<CategorieProgramme>(this.BASE_URL, request);
  }

  /**
   * Modifier une catégorie
   */
  async updateCategorie(id: number, request: UpdateCategorieProgrammeRequest): Promise<CategorieProgramme> {
    return await api.put<CategorieProgramme>(`${this.BASE_URL}/${id}`, request);
  }

  /**
   * Supprimer une catégorie
   */
  async deleteCategorie(id: number): Promise<void> {
    await api.delete(`${this.BASE_URL}/${id}`);
  }

  /**
   * Compter les programmes par catégorie
   */
  async countProgrammesByCategorie(categorieId: number): Promise<number> {
    return await api.get<number>(`${this.BASE_URL}/${categorieId}/count`);
  }
}

export default new CategorieProgrammeService();
