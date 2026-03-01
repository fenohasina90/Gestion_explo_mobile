import apiService from './api.service';
import type {
  ActiviteResponse,
  CreateActiviteRequest,
  UpdateActiviteRequest,
  ActiviteStatus,
  ExportColumnsDto,
} from '@/types';

/**
 * Service pour la gestion des activités
 */
class ActiviteService {
  private readonly baseUrl = '/api/activites';

  /**
   * Créer une nouvelle activité
   */
  async createActivite(data: CreateActiviteRequest): Promise<ActiviteResponse> {
    return apiService.post<ActiviteResponse>(this.baseUrl, data);
  }

  /**
   * Récupérer toutes les activités
   */
  async getAllActivites(): Promise<ActiviteResponse[]> {
    return apiService.get<ActiviteResponse[]>(this.baseUrl);
  }

  /**
   * Récupérer une activité par ID
   */
  async getActiviteById(id: number): Promise<ActiviteResponse> {
    return apiService.get<ActiviteResponse>(`${this.baseUrl}/${id}`);
  }

  /**
   * Récupérer les activités d'un budget
   */
  async getActivitesByBudgetId(budgetId: number): Promise<ActiviteResponse[]> {
    return apiService.get<ActiviteResponse[]>(`${this.baseUrl}/budget/${budgetId}`);
  }

  /**
   * Récupérer les activités d'une année d'exercice
   */
  async getActivitesByAnneeExercice(anneeExerciceId: number): Promise<ActiviteResponse[]> {
    return apiService.get<ActiviteResponse[]>(`${this.baseUrl}/annee/${anneeExerciceId}`);
  }

  /**
   * Mettre à jour une activité
   */
  async updateActivite(id: number, data: UpdateActiviteRequest): Promise<ActiviteResponse> {
    return apiService.put<ActiviteResponse>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Supprimer une activité
   */
  async deleteActivite(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Annuler une activité
   */
  async annulerActivite(id: number): Promise<ActiviteResponse> {
    return apiService.put<ActiviteResponse>(`${this.baseUrl}/${id}/annuler`, {});
  }

  /**
   * Récupérer tous les statuts d'activité disponibles
   */
  async getAllStatuts(): Promise<ActiviteStatus[]> {
    return apiService.get<ActiviteStatus[]>(`${this.baseUrl}/statuts`);
  }

  /**
   * Exporter le budget en PDF
   */
  async exportBudgetToPdf(budgetId: number, columns: ExportColumnsDto): Promise<void> {
    try {
      const axiosInstance = apiService.getAxiosInstance();
      
      const response = await axiosInstance.post(
        `${this.baseUrl}/budget/${budgetId}/export/pdf`,
        columns,
        {
          responseType: 'blob',
        }
      );

      // Créer un lien de téléchargement
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      
      const contentDisposition = response.headers['content-disposition'];
      let filename = 'budget.pdf';
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="?(.+)"?/);
        if (filenameMatch && filenameMatch[1]) {
          filename = filenameMatch[1];
        }
      }
      
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Erreur lors de l\'export PDF:', error);
      throw error;
    }
  }
}

export default new ActiviteService();
