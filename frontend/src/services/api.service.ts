import axios from 'axios';
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { API_CONFIG, STORAGE_KEYS } from '../config/api.config';
import offlineStorage from './offline-storage.service';

/**
 * Service API avec intercepteurs pour JWT
 */
class ApiService {
  private axiosInstance: AxiosInstance;

  constructor() {
    this.axiosInstance = axios.create({
      baseURL: API_CONFIG.baseURL,
      timeout: API_CONFIG.timeout,
      headers: API_CONFIG.headers,
    });

    this.setupInterceptors();
  }

  /**
   * Configure les intercepteurs de requêtes et réponses
   */
  private setupInterceptors(): void {
    // Intercepteur de requête - ajoute le token JWT
    this.axiosInstance.interceptors.request.use(
      async (config) => {
        const token = await offlineStorage.get<string>(STORAGE_KEYS.AUTH_TOKEN);
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Intercepteur de réponse - gère l'expiration du token
    this.axiosInstance.interceptors.response.use(
      (response) => response,
      async (error) => {
        if (error.response?.status === 401) {
          // Token expiré ou invalide - déconnecter l'utilisateur
          await offlineStorage.remove(STORAGE_KEYS.AUTH_TOKEN);
          await offlineStorage.remove(STORAGE_KEYS.AUTH_USER);
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  /**
   * Requête GET
   */
  async get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.axiosInstance.get(url, config);
    return response.data;
  }

  /**
   * Requête POST
   */
  async post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.axiosInstance.post(url, data, config);
    return response.data;
  }

  /**
   * Requête PUT
   */
  async put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.axiosInstance.put(url, data, config);
    return response.data;
  }

  /**
   * Requête DELETE
   */
  async delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.axiosInstance.delete(url, config);
    return response.data;
  }

  /**
   * Requête PATCH
   */
  async patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.axiosInstance.patch(url, data, config);
    return response.data;
  }

  /**
   * Vérifie la connexion au serveur
   */
  async checkConnection(): Promise<boolean> {
    try {
      await this.axiosInstance.get('/actuator/health', { timeout: 5000 });
      return true;
    } catch (error) {
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
