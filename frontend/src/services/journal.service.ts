import apiService from './api.service';
import type { JournalEntry, JournalFilterRequest } from '../types';

/**
 * Service pour la gestion du journal d'audit
 */
class JournalService {
  private readonly baseUrl = '/api/journal';

  /**
   * Récupérer toutes les entrées du journal
   */
  async getAllJournal(): Promise<JournalEntry[]> {
    return apiService.get<JournalEntry[]>(this.baseUrl);
  }

  /**
   * Filtrer les entrées du journal avec critères multiples
   */
  async filterJournal(filter: JournalFilterRequest): Promise<JournalEntry[]> {
    return apiService.post<JournalEntry[]>(`${this.baseUrl}/filter`, filter);
  }

  /**
   * Récupérer les entrées par période
   */
  async getJournalByPeriod(dateDebut: string, dateFin: string): Promise<JournalEntry[]> {
    return apiService.get<JournalEntry[]>(
      `${this.baseUrl}/period?dateDebut=${dateDebut}&dateFin=${dateFin}`
    );
  }

  /**
   * Récupérer les entrées par utilisateur
   */
  async getJournalByUtilisateur(utilisateurId: number): Promise<JournalEntry[]> {
    return apiService.get<JournalEntry[]>(`${this.baseUrl}/utilisateur/${utilisateurId}`);
  }
}

export default new JournalService();
