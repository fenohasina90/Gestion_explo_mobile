import axios from 'axios';
import apiService from './api.service';
import { API_ENDPOINTS } from '@/config/api.config';
import type { JournalEntry, JournalFilterRequest } from '@/types';

/**
 * Service pour la gestion du journal d'audit
 */
class JournalService {
  private readonly betaBaseUrl = import.meta.env.VITE_BETA_API_URL || '';
  private readonly baseUrl = '/api/journal';

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
   * Récupère toutes les entrées du journal
   */
  async getAllJournal(): Promise<JournalEntry[]> {
    return this.betaGet<JournalEntry[]>(this.baseUrl);
  }

  /**
   * Filtre les entrées du journal selon les critères
   */
  async filterJournal(filters: JournalFilterRequest): Promise<JournalEntry[]> {
    const params = new URLSearchParams();

    if (filters.dateDebut) {
      params.append('dateDebut', filters.dateDebut);
    }
    if (filters.dateFin) {
      params.append('dateFin', filters.dateFin);
    }
    if (filters.searchText) {
      params.append('searchText', filters.searchText);
    }

    const query = params.toString();
    const url = query ? `${this.baseUrl}/filter?${query}` : `${this.baseUrl}/filter`;

    return this.betaGet<JournalEntry[]>(url);
  }

  /**
   * Récupère les entrées du journal pour une période donnée
   */
  async getJournalByPeriod(dateDebut: string, dateFin: string): Promise<JournalEntry[]> {
    return this.betaGet<JournalEntry[]>(`${this.baseUrl}/filter?dateDebut=${dateDebut}&dateFin=${dateFin}`);
  }
}

export default new JournalService();
