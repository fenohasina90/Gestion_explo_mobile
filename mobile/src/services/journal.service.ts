import apiService from './api.service';
import { API_ENDPOINTS } from '@/config/api.config';
import type { JournalEntry, JournalFilterRequest } from '@/types';

/**
 * Service pour la gestion du journal d'audit
 */
class JournalService {
  /**
   * Récupère toutes les entrées du journal
   */
  async getAllJournal(): Promise<JournalEntry[]> {
    return apiService.get<JournalEntry[]>(API_ENDPOINTS.journal);
  }

  /**
   * Filtre les entrées du journal selon les critères
   */
  async filterJournal(filters: JournalFilterRequest): Promise<JournalEntry[]> {
    return apiService.post<JournalEntry[]>(API_ENDPOINTS.journalFilter, filters);
  }

  /**
   * Récupère les entrées du journal pour une période donnée
   */
  async getJournalByPeriod(dateDebut: string, dateFin: string): Promise<JournalEntry[]> {
    return apiService.get<JournalEntry[]>(
      `${API_ENDPOINTS.journalByPeriod}?dateDebut=${dateDebut}&dateFin=${dateFin}`
    );
  }
}

export default new JournalService();
