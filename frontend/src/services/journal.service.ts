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
    try {
      return await apiService.get<JournalEntry[]>(url);
    } catch (error: any) {
      if (error?.response?.status !== 403) {
        throw error;
      }

      const entries = await this.getAllJournal();
      return entries.filter((entry) => {
        const entryDate = new Date(entry.timestamp);
        const afterStart = filter.dateDebut ? entryDate >= new Date(filter.dateDebut) : true;
        const beforeEnd = filter.dateFin ? entryDate <= new Date(filter.dateFin) : true;
        const byUser = filter.utilisateurId ? entry.utilisateurId === filter.utilisateurId : true;
        const byText = filter.searchText
          ? entry.action.toLowerCase().includes(filter.searchText.toLowerCase()) ||
            entry.username.toLowerCase().includes(filter.searchText.toLowerCase())
          : true;

        return afterStart && beforeEnd && byUser && byText;
      });
    }
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
