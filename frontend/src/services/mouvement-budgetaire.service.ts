import apiService from './api.service';
import type { 
  MouvementBudgetaire, 
  CreateMouvementBudgetaireRequest, 
  UpdateMouvementBudgetaireRequest,
  MouvementBudgetaireFilterRequest,
  EtatCaisse,
  TypeMouvement,
  PageResponse
} from '../types';

/**
 * Service pour la gestion des mouvements budgétaires
 */
class MouvementBudgetaireService {
  private readonly baseUrl = '/api/mouvements-budgetaires';
  private readonly typesUrl = '/api/types-mouvement';

  /**
   * Créer un nouveau mouvement budgétaire
   */
  async createMouvement(data: CreateMouvementBudgetaireRequest): Promise<MouvementBudgetaire> {
    return apiService.post<MouvementBudgetaire>(this.baseUrl, data);
  }

  /**
   * Récupérer tous les mouvements budgétaires avec filtres
   */
  async getMouvementsWithFilters(filters?: MouvementBudgetaireFilterRequest): Promise<MouvementBudgetaire[]> {
    const params = new URLSearchParams();
    
    if (filters?.recherche) {
      params.append('recherche', filters.recherche);
    }
    if (filters?.dateDebut) {
      params.append('dateDebut', filters.dateDebut);
    }
    if (filters?.dateFin) {
      params.append('dateFin', filters.dateFin);
    }
    if (filters?.typeId) {
      params.append('typeId', filters.typeId.toString());
    }
    if (filters?.anneeExerciceId) {
      params.append('anneeExerciceId', filters.anneeExerciceId.toString());
    }

    const queryString = params.toString();
    const url = queryString ? `${this.baseUrl}?${queryString}` : this.baseUrl;
    
    return apiService.get<MouvementBudgetaire[]>(url);
  }

  /**
   * Récupérer tous les mouvements budgétaires avec filtres et pagination
   */
  async getMouvementsWithFiltersPaginated(
    filters?: MouvementBudgetaireFilterRequest,
    page: number = 0,
    size: number = 10,
    sort: string = 'createdAt',
    direction: 'asc' | 'desc' = 'desc'
  ): Promise<PageResponse<MouvementBudgetaire>> {
    const params = new URLSearchParams();
    
    // Ajout des filtres
    if (filters?.recherche) {
      params.append('recherche', filters.recherche);
    }
    if (filters?.dateDebut) {
      params.append('dateDebut', filters.dateDebut);
    }
    if (filters?.dateFin) {
      params.append('dateFin', filters.dateFin);
    }
    if (filters?.typeId) {
      params.append('typeId', filters.typeId.toString());
    }
    if (filters?.anneeExerciceId) {
      params.append('anneeExerciceId', filters.anneeExerciceId.toString());
    }
    
    // Ajout des paramètres de pagination
    params.append('page', page.toString());
    params.append('size', size.toString());
    params.append('sort', sort);
    params.append('direction', direction);

    const url = `${this.baseUrl}?${params.toString()}`;
    
    return apiService.get<PageResponse<MouvementBudgetaire>>(url);
  }

  /**
   * Récupérer un mouvement par ID
   */
  async getMouvementById(id: number): Promise<MouvementBudgetaire> {
    return apiService.get<MouvementBudgetaire>(`${this.baseUrl}/${id}`);
  }

  /**
   * Mettre à jour un mouvement
   */
  async updateMouvement(id: number, data: UpdateMouvementBudgetaireRequest): Promise<MouvementBudgetaire> {
    return apiService.put<MouvementBudgetaire>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Supprimer un mouvement
   */
  async deleteMouvement(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Obtenir l'état de caisse
   */
  async getEtatCaisse(anneeExerciceId?: number): Promise<EtatCaisse> {
    const url = anneeExerciceId 
      ? `${this.baseUrl}/etat-caisse?anneeExerciceId=${anneeExerciceId}`
      : `${this.baseUrl}/etat-caisse`;
    return apiService.get<EtatCaisse>(url);
  }

  /**
   * Récupérer tous les types de mouvements
   */
  async getAllTypes(): Promise<TypeMouvement[]> {
    return apiService.get<TypeMouvement[]>(this.typesUrl);
  }

  /**
   * Récupérer un type par ID
   */
  async getTypeById(id: number): Promise<TypeMouvement> {
    return apiService.get<TypeMouvement>(`${this.typesUrl}/${id}`);
  }
}

export default new MouvementBudgetaireService();
