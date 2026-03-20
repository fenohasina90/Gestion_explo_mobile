import apiService from './api.service';
import type { JournalEntry, JournalFilterRequest } from '../types';

/**
 * Service pour la gestion du journal d'audit
 */
class JournalService {
  private readonly baseUrl = '/api/journal';
  private readonly queryUrl = '/api/journal-query/entries';

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
    const params = new URLSearchParams();

    if (filter.dateDebut) {
      params.append('dateDebut', filter.dateDebut);
    }
    if (filter.dateFin) {
      params.append('dateFin', filter.dateFin);
    }
    if (filter.utilisateurId) {
      params.append('utilisateurId', filter.utilisateurId.toString());
    }
    if (filter.searchText) {
      params.append('searchText', filter.searchText);
    }

    const query = params.toString();
    const url = query ? `${this.queryUrl}?${query}` : this.queryUrl;
    return apiService.get<JournalEntry[]>(url);
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
