import axios from 'axios';
import apiService from './api.service';
import type {
  MouvementBudgetaire,
  CreateMouvementBudgetaireRequest,
  UpdateMouvementBudgetaireRequest,
  MouvementBudgetaireFilterRequest,
  EtatCaisse,
  TypeMouvement,
  PageResponse,
} from '@/types';

/**
 * Service pour la gestion des mouvements budgétaires
 */
class MouvementBudgetaireService {
  private readonly baseUrl = '/api/budget-mouvements';
  private readonly readUrl = '/api/budget-mouvements';
  private readonly allUrl = '/api/budget-mouvements/all';
  private readonly typesUrl = '/api/types-mouvement';
  private readonly betaBaseUrl = import.meta.env.VITE_BETA_API_URL || '';

  private getReadUrl(path: string): string {
    if (!this.betaBaseUrl) {
      return path;
    }

    const base = this.betaBaseUrl.endsWith('/')
      ? this.betaBaseUrl.slice(0, -1)
      : this.betaBaseUrl;
    return `${base}${path}`;
  }

  private async betaGet<T>(path: string): Promise<T> {
    const url = this.getReadUrl(path);

    if (!this.betaBaseUrl) {
      return apiService.get<T>(url);
    }

    const response = await axios.get<T>(url, {
      headers: {
        'Content-Type': 'application/json',
      },
      timeout: 30000,
    });

    return response.data;
  }

  /**
   * Créer un nouveau mouvement budgétaire
   */
  async createMouvement(data: CreateMouvementBudgetaireRequest): Promise<MouvementBudgetaire> {
    return apiService.post<MouvementBudgetaire>(this.baseUrl, data);
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

    const url = `${this.readUrl}?${params.toString()}`;
    return this.betaGet<PageResponse<MouvementBudgetaire>>(url);
  }

  /**
   * Récupérer tous les mouvements budgétaires avec filtres
   */
  async getMouvementsWithFilters(filters?: MouvementBudgetaireFilterRequest): Promise<MouvementBudgetaire[]> {
    const params = new URLSearchParams();
    if (filters?.recherche) params.append('recherche', filters.recherche);
    if (filters?.dateDebut) params.append('dateDebut', filters.dateDebut);
    if (filters?.dateFin) params.append('dateFin', filters.dateFin);
    if (filters?.typeId) params.append('typeId', filters.typeId.toString());
    if (filters?.anneeExerciceId) params.append('anneeExerciceId', filters.anneeExerciceId.toString());

    const query = params.toString();
    const url = query ? `${this.allUrl}?${query}` : this.allUrl;
    return this.betaGet<MouvementBudgetaire[]>(url);
  }

  /**
   * Récupérer un mouvement par ID
   */
  async getMouvementById(id: number): Promise<MouvementBudgetaire> {
    return this.betaGet<MouvementBudgetaire>(`${this.readUrl}/${id}`);
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
      ? `${this.readUrl}/etat-caisse?anneeExerciceId=${anneeExerciceId}`
      : `${this.readUrl}/etat-caisse`;
    return this.betaGet<EtatCaisse>(url);
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
