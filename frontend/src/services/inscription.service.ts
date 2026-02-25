import apiService from './api.service';
import type {
  InscriptionResponse,
  CreateInscriptionRequest,
} from '../types';

/**
 * Service pour la gestion des inscriptions
 */
class InscriptionService {
  private readonly baseUrl = '/api/inscriptions';

  /**
   * Créer une nouvelle inscription
   */
  async createInscription(data: CreateInscriptionRequest): Promise<InscriptionResponse> {
    return apiService.post<InscriptionResponse>(this.baseUrl, data);
  }

  /**
   * Récupérer toutes les inscriptions
   */
  async getAllInscriptions(): Promise<InscriptionResponse[]> {
    return apiService.get<InscriptionResponse[]>(this.baseUrl);
  }

  /**
   * Récupérer une inscription par ID
   */
  async getInscriptionById(id: number): Promise<InscriptionResponse> {
    return apiService.get<InscriptionResponse>(`${this.baseUrl}/${id}`);
  }

  /**
   * Filtrer les inscriptions
   */
  async filterInscriptions(
    anneeExerciceId?: number,
    classeId?: number,
    genre?: string
  ): Promise<InscriptionResponse[]> {
    const params: Record<string, any> = {};
    if (anneeExerciceId) params.anneeExerciceId = anneeExerciceId;
    if (classeId) params.classeId = classeId;
    if (genre) params.genre = genre;

    return apiService.get<InscriptionResponse[]>(`${this.baseUrl}/filter`, { params });
  }

  /**
   * Mettre à jour le statut d'assurance
   */
  async updateAssurance(id: number, estAssurance: boolean): Promise<InscriptionResponse> {
    return apiService.patch<InscriptionResponse>(`${this.baseUrl}/${id}/assurance`, null, {
      params: { estAssurance },
    });
  }

  /**
   * Supprimer une inscription
   */
  async deleteInscription(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Exporter les inscriptions en PDF
   */
  async exportToPdf(
    anneeExerciceId?: number,
    classeId?: number,
    genre?: string,
    estAssurance?: boolean
  ): Promise<void> {
    const params: Record<string, any> = {};
    if (anneeExerciceId) params.anneeExerciceId = anneeExerciceId;
    if (classeId) params.classeId = classeId;
    if (genre) params.genre = genre;
    if (estAssurance !== undefined) params.estAssurance = estAssurance;

    try {
      // Utiliser l'instance axios du service pour bénéficier des intercepteurs
      const axiosInstance = apiService.getAxiosInstance();
      
      const response = await axiosInstance.get(`${this.baseUrl}/export/pdf`, {
        params,
        responseType: 'blob',
      });

      // Créer un lien de téléchargement
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      
      // Extraire le nom de fichier depuis les en-têtes ou utiliser un nom par défaut
      const contentDisposition = response.headers['content-disposition'];
      let filename = 'liste_explorateur.pdf';
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="?(.+)"?/);
        if (filenameMatch && filenameMatch[1]) {
          filename = filenameMatch[1];
        }
      }
      
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Erreur lors de l\'export PDF:', error);
      throw error;
    }
  }
}

export default new InscriptionService();
