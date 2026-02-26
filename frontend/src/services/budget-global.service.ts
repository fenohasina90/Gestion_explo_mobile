import apiService from './api.service';
import offlineStorage from './offline-storage.service';
import { STORAGE_KEYS } from '../config/api.config';
import type { BudgetGlobalResponse, UpdateBudgetStatusRequest, ExportBudgetPdfRequest } from '../types';

/**
 * Service pour la gestion du budget global
 */
class BudgetGlobalService {
  private readonly baseUrl = '/api/budget-global';

  /**
   * Récupérer ou créer le budget pour une année d'exercice
   */
  async getBudgetByAnneeExercice(anneeExerciceId: number): Promise<BudgetGlobalResponse> {
    return apiService.get<BudgetGlobalResponse>(`${this.baseUrl}/annee/${anneeExerciceId}`);
  }

  /**
   * Récupérer tous les budgets
   */
  async getAllBudgets(): Promise<BudgetGlobalResponse[]> {
    return apiService.get<BudgetGlobalResponse[]>(this.baseUrl);
  }

  /**
   * Modifier le statut d'un budget
   */
  async updateBudgetStatus(budgetId: number, request: UpdateBudgetStatusRequest): Promise<BudgetGlobalResponse> {
    return apiService.put<BudgetGlobalResponse>(`${this.baseUrl}/${budgetId}/status`, request);
  }

  /**
   * Exporter le budget en PDF
   */
  async exportBudgetPdf(request: ExportBudgetPdfRequest): Promise<Blob> {
    const token = await offlineStorage.get<string>(STORAGE_KEYS.AUTH_TOKEN);
    const response = await fetch(`${import.meta.env.VITE_API_URL || 'http://localhost:8080'}${this.baseUrl}/export-pdf`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(request)
    });
    
    if (!response.ok) {
      throw new Error('Erreur lors de l\'export PDF');
    }
    
    return response.blob();
  }
}

export default new BudgetGlobalService();
