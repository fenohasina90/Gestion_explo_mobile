import apiService from './api.service';
import type {
  BudgetGlobalResponse,
  CreateBudgetGlobalRequest,
  UpdateBudgetGlobalRequest,
  BudgetStatus,
} from '@/types';

/**
 * Service pour la gestion des budgets globaux
 */
class BudgetGlobalService {
  private readonly baseUrl = '/api/budget-global';

  /**
   * Créer un nouveau budget global
   */
  async createBudgetGlobal(data: CreateBudgetGlobalRequest): Promise<BudgetGlobalResponse> {
    return apiService.post<BudgetGlobalResponse>(this.baseUrl, data);
  }

  /**
   * Récupérer tous les budgets globaux
   */
  async getAllBudgetsGlobaux(): Promise<BudgetGlobalResponse[]> {
    return apiService.get<BudgetGlobalResponse[]>(this.baseUrl);
  }

  /**
   * Récupérer un budget global par ID
   */
  async getBudgetGlobalById(id: number): Promise<BudgetGlobalResponse> {
    return apiService.get<BudgetGlobalResponse>(`${this.baseUrl}/${id}`);
  }

  /**
   * Récupérer le budget global d'une année d'exercice
   */
  async getBudgetByAnneeExercice(anneeExerciceId: number): Promise<BudgetGlobalResponse> {
    return apiService.get<BudgetGlobalResponse>(`${this.baseUrl}/annee/${anneeExerciceId}`);
  }

  /**
   * Mettre à jour le statut d'un budget global
   */
  async updateBudgetStatus(id: number, data: UpdateBudgetGlobalRequest): Promise<BudgetGlobalResponse> {
    return apiService.put<BudgetGlobalResponse>(`${this.baseUrl}/${id}/status`, data);
  }

  /**
   * Supprimer un budget global
   */
  async deleteBudgetGlobal(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Récupérer tous les statuts de budget disponibles
   */
  async getAllStatuts(): Promise<BudgetStatus[]> {
    return apiService.get<BudgetStatus[]>(`${this.baseUrl}/statuts`);
  }

  /**
   * Exporter le budget en PDF
   */
  async exportBudgetToPdf(anneeExerciceId: number, columns: {
    includeDate: boolean;
    includeNomActivite: boolean;
    includeCoutActivite: boolean;
    includeDescriptionActivite: boolean;
    includeDetailsActivite: boolean;
    includeCoutDetails: boolean;
    includeStatutActivite: boolean;
  }): Promise<void> {
    try {
      const axiosInstance = apiService.getAxiosInstance();
      
      const response = await axiosInstance.post(
        `${this.baseUrl}/export-pdf`,
        {
          anneeExerciceId,
          ...columns
        },
        {
          responseType: 'blob',
        }
      );

      // Créer un lien de téléchargement
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      
      const contentDisposition = response.headers['content-disposition'];
      let filename = `budget_${anneeExerciceId}.pdf`;
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

export default new BudgetGlobalService();
