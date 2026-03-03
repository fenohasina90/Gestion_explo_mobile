import api from './api.service';
import type {
  Programme,
  CreateProgrammeRequest,
  UpdateProgrammeRequest
} from '@/types';

/**
 * Service pour la gestion des programmes
 */
class ProgrammeService {
  private readonly BASE_URL = '/api/programmes';

  /**
   * Récupérer tous les programmes
   */
  async getAllProgrammes(): Promise<Programme[]> {
    return await api.get<Programme[]>(this.BASE_URL);
  }

  /**
   * Récupérer un programme par ID
   */
  async getProgrammeById(id: number): Promise<Programme> {
    return await api.get<Programme>(`${this.BASE_URL}/${id}`);
  }

  /**
   * Récupérer les programmes par catégorie
   */
  async getProgrammesByCategorie(categorieId: number): Promise<Programme[]> {
    return await api.get<Programme[]>(`${this.BASE_URL}/categorie/${categorieId}`);
  }

  /**
   * Récupérer les programmes par classe
   */
  async getProgrammesByClasse(classeId: number): Promise<Programme[]> {
    return await api.get<Programme[]>(`${this.BASE_URL}/classe/${classeId}`);
  }

  /**
   * Filtrer les programmes
   */
  async filterProgrammes(categorieId?: number, classeId?: number): Promise<Programme[]> {
    const params: any = {};
    if (categorieId) params.categorieId = categorieId;
    if (classeId) params.classeId = classeId;
    
    return await api.get<Programme[]>(`${this.BASE_URL}/filter`, { params });
  }

  /**
   * Créer un nouveau programme
   */
  async createProgramme(request: CreateProgrammeRequest): Promise<Programme> {
    return await api.post<Programme>(this.BASE_URL, request);
  }

  /**
   * Modifier un programme
   */
  async updateProgramme(id: number, request: UpdateProgrammeRequest): Promise<Programme> {
    return await api.put<Programme>(`${this.BASE_URL}/${id}`, request);
  }

  /**
   * Supprimer un programme
   */
  async deleteProgramme(id: number): Promise<void> {
    await api.delete(`${this.BASE_URL}/${id}`);
  }
}

export default new ProgrammeService();
