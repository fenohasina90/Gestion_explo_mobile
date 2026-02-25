import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import { API_CONFIG } from '@/config/api.config';
import { useAuthStore } from '@/stores/auth.store';

/**
 * Service API avec intercepteurs pour gérer l'authentification
 * et les erreurs de manière centralisée
 */
class ApiService {
  private axiosInstance: AxiosInstance;

  constructor() {
    this.axiosInstance = axios.create({
      baseURL: API_CONFIG.baseURL,
      timeout: API_CONFIG.timeout,
      headers: API_CONFIG.headers
    });

    this.setupInterceptors();
  }

  /**
   * Configure les intercepteurs pour les requêtes et réponses
   */
  private setupInterceptors(): void {
    // Intercepteur de requête - Ajoute le token JWT
    this.axiosInstance.interceptors.request.use(
      (config) => {
        const authStore = useAuthStore();
        const token = authStore.token;

        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Intercepteur de réponse - Gère les erreurs
    this.axiosInstance.interceptors.response.use(
      (response) => response,
      async (error: AxiosError) => {
        const authStore = useAuthStore();

        // Si erreur 401 (non autorisé), déconnecter l'utilisateur
        if (error.response?.status === 401) {
          await authStore.logout();
        }

        return Promise.reject(error);
      }
    );
  }

  /**
   * Effectue une requête GET
   */
  async get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.axiosInstance.get(url, config);
    return response.data;
  }

  /**
   * Effectue une requête POST
   */
  async post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.axiosInstance.post(url, data, config);
    return response.data;
  }

  /**
   * Effectue une requête PUT
   */
  async put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.axiosInstance.put(url, data, config);
    return response.data;
  }

  /**
   * Effectue une requête DELETE
   */
  async delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.axiosInstance.delete(url, config);
    return response.data;
  }

  /**
   * Effectue une requête PATCH
   */
  async patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.axiosInstance.patch(url, data, config);
    return response.data;
  }

  /**
   * Vérifie si le serveur est accessible
   */
  async checkConnection(): Promise<boolean> {
    try {
      await this.axiosInstance.get('/actuator/health', { timeout: 5000 });
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Retourne l'instance Axios pour des cas d'usage avancés
   */
  getAxiosInstance(): AxiosInstance {
    return this.axiosInstance;
  }
}

export default new ApiService();
