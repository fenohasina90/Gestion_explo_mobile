import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import apiService from '@/services/api.service';
import offlineStorage from '@/services/offline-storage.service';
import syncService from '@/services/sync.service';
import { API_ENDPOINTS } from '@/config/api.config';
import type { LoginRequest, LoginResponse, User } from '@/types';

export const useAuthStore = defineStore('auth', () => {
  // État
  const token = ref<string | null>(null);
  const user = ref<User | null>(null);
  const isAuthenticated = ref(false);
  const isLoading = ref(false);
  const error = ref<string | null>(null);

  // Getters computed
  const userRole = computed(() => user.value?.role || null);
  const isDirecteur = computed(() => user.value?.role === 'Directeur');
  const isCoDirecteur = computed(() => user.value?.role === 'Co-Directeur' || user.value?.role === 'Co_Directeur');

  /**
   * Initialise le store depuis le stockage local
   */
  async function initialize() {
    try {
      const savedToken = await offlineStorage.get<string>('auth_token');
      const savedUser = await offlineStorage.get<User>('auth_user');

      if (savedToken && savedUser) {
        token.value = savedToken;
        user.value = savedUser;
        isAuthenticated.value = true;
      }
    } catch (err) {
      console.error('Erreur lors de l\'initialisation de l\'auth:', err);
    }
  }

  /**
   * Connexion de l'utilisateur
   */
  async function login(credentials: LoginRequest): Promise<boolean> {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await apiService.post<LoginResponse>(
        API_ENDPOINTS.login,
        credentials
      );

      // Créer un objet plain (non-réactif) pour le stockage
      const plainUser: User = {
        id: response.userId,
        username: response.username,
        role: response.role,
        active: true,
        anneeExerciceId: response.anneeExerciceId,
        anneeExercice: response.anneeExercice
      };

      // Sauvegarder le token et les infos utilisateur
      token.value = response.token;
      user.value = plainUser;
      isAuthenticated.value = true;

      // Persister dans le stockage local (objet plain, pas réactif)
      await offlineStorage.set('auth_token', response.token);
      await offlineStorage.set('auth_user', plainUser);

      // Démarrer la synchronisation automatique
      syncService.startAutoSync();

      return true;
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Erreur de connexion';
      console.error('Erreur de connexion:', err);
      return false;
    } finally {
      isLoading.value = false;
    }
  }

  /**
   * Déconnexion de l'utilisateur
   */
  async function logout() {
    // Arrêter la synchronisation
    syncService.stopAutoSync();

    // Réinitialiser l'état
    token.value = null;
    user.value = null;
    isAuthenticated.value = false;
    error.value = null;

    // Nettoyer le stockage local
    await offlineStorage.remove('auth_token');
    await offlineStorage.remove('auth_user');
  }

  /**
   * Récupère les informations de l'utilisateur connecté
   */
  async function fetchCurrentUser(): Promise<boolean> {
    if (!isAuthenticated.value) return false;

    try {
      const userData = await apiService.get<User>(API_ENDPOINTS.currentUser);
      user.value = userData;
      await offlineStorage.set('auth_user', userData);
      return true;
    } catch (err) {
      console.error('Erreur lors de la récupération de l\'utilisateur:', err);
      return false;
    }
  }

  /**
   * Vérifie si le token est toujours valide
   */
  async function checkAuth(): Promise<boolean> {
    if (!token.value) {
      isAuthenticated.value = false;
      return false;
    }

    try {
      await fetchCurrentUser();
      return true;
    } catch {
      await logout();
      return false;
    }
  }

  return {
    // État
    token,
    user,
    isAuthenticated,
    isLoading,
    error,
    
    // Getters
    userRole,
    isDirecteur,
    isCoDirecteur,
    
    // Actions
    initialize,
    login,
    logout,
    fetchCurrentUser,
    checkAuth
  };
});
