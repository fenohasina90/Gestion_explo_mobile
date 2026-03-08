import api from './api.service';
import type {
  ClasseProgressive,
  CreateClasseProgressiveRequest,
  UpdateClasseProgressiveRequest,
  ClasseProgressiveFilterRequest
} from '@/types';

/**
 * Service pour la gestion des classes progressives (CP)
 */
class ClasseProgressiveService {
  private readonly BASE_URL = '/api/classe-progressive';

  /**
   * Récupérer toutes les CPs
   */
  async getAllCP(): Promise<ClasseProgressive[]> {
    return await api.get<ClasseProgressive[]>(this.BASE_URL);
  }

  /**
   * Récupérer une CP par ID
   */
  async getCPById(id: number): Promise<ClasseProgressive> {
    return await api.get<ClasseProgressive>(`${this.BASE_URL}/${id}`);
  }

  /**
   * Filtrer les CPs
   */
  async filterCP(filters: ClasseProgressiveFilterRequest): Promise<ClasseProgressive[]> {
    const params: any = {};
    if (filters.dateDebut) params.dateDebut = filters.dateDebut;
    if (filters.dateFin) params.dateFin = filters.dateFin;
    if (filters.anneeExerciceId) params.anneeExerciceId = filters.anneeExerciceId;
    
    return await api.get<ClasseProgressive[]>(`${this.BASE_URL}/filter`, { params });
  }

  /**
   * Créer une nouvelle CP
   */
  async createCP(request: CreateClasseProgressiveRequest): Promise<ClasseProgressive> {
    return await api.post<ClasseProgressive>(this.BASE_URL, request);
  }

  /**
   * Modifier une CP
   */
  async updateCP(id: number, request: UpdateClasseProgressiveRequest): Promise<ClasseProgressive> {
    return await api.put<ClasseProgressive>(`${this.BASE_URL}/${id}`, request);
  }

  /**
   * Supprimer une CP
   */
  async deleteCP(id: number): Promise<void> {
    await api.delete(`${this.BASE_URL}/${id}`);
  }

  /**
   * Clôturer une CP
   */
  async cloturerCP(id: number): Promise<ClasseProgressive> {
    return await api.put<ClasseProgressive>(`${this.BASE_URL}/${id}/cloturer`);
  }
}

export default new ClasseProgressiveService();
