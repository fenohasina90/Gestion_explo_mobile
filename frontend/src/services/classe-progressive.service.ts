import apiService from './api.service';
import type { 
  ClasseProgressive, 
  CreateClasseProgressiveRequest, 
  UpdateClasseProgressiveRequest 
} from '../types';

/**
 * Service pour la gestion des Classes Progressives (CP)
 */
class ClasseProgressiveService {
  private readonly baseUrl = '/api/classe-progressive';

  /**
   * Créer une nouvelle CP
   */
  async createCP(data: CreateClasseProgressiveRequest): Promise<ClasseProgressive> {
    return apiService.post<ClasseProgressive>(this.baseUrl, data);
  }

  /**
   * Récupérer toutes les CP
   */
  async getAllCP(): Promise<ClasseProgressive[]> {
    return apiService.get<ClasseProgressive[]>(this.baseUrl);
  }

  /**
   * Filtrer les CP par dates
   */
  async filterCPByDates(params: {
    dateDebut?: string;
    dateFin?: string;
    anneeExerciceId?: number;
  }): Promise<ClasseProgressive[]> {
    return apiService.get<ClasseProgressive[]>(`${this.baseUrl}/filter`, { params });
  }

  /**
   * Récupérer une CP par ID
   */
  async getCPById(id: number): Promise<ClasseProgressive> {
    return apiService.get<ClasseProgressive>(`${this.baseUrl}/${id}`);
  }

  /**
   * Mettre à jour une CP
   */
  async updateCP(id: number, data: UpdateClasseProgressiveRequest): Promise<ClasseProgressive> {
    return apiService.put<ClasseProgressive>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Supprimer une CP
   */
  async deleteCP(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Récupérer les CP par année d'exercice
   */
  async getCPByAnneeExercice(anneeExerciceId: number): Promise<ClasseProgressive[]> {
    return apiService.get<ClasseProgressive[]>(`${this.baseUrl}/annee/${anneeExerciceId}`);
  }

  /**
   * Clôturer une CP (met etat à 1)
   * Une fois clôturée, les présences et changements de statuts sont interdits
   */
  async cloturerCP(id: number): Promise<ClasseProgressive> {
    return apiService.put<ClasseProgressive>(`${this.baseUrl}/${id}/cloturer`, {});
  }
}

export default new ClasseProgressiveService();
