import React, { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import apiService from '../services/api.service';
import offlineStorage from '../services/offline-storage.service';
import { API_ENDPOINTS, STORAGE_KEYS } from '../config/api.config';
import type { User, LoginRequest, LoginResponse } from '../types';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (credentials: LoginRequest) => Promise<boolean>;
  logout: () => Promise<void>;
  checkAuth: () => Promise<boolean>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  /**
   * Initialise l'authentification depuis le stockage local
   */
  useEffect(() => {
    const initialize = async () => {
      try {
        const savedToken = await offlineStorage.get<string>(STORAGE_KEYS.AUTH_TOKEN);
        const savedUser = await offlineStorage.get<User>(STORAGE_KEYS.AUTH_USER);

        if (savedToken && savedUser) {
          setToken(savedToken);
          setUser(savedUser);
          setIsAuthenticated(true);
        }
      } catch (err) {
        console.error('Erreur lors de l\'initialisation:', err);
      } finally {
        setIsLoading(false);
      }
    };

    initialize();
  }, []);

  /**
   * Connexion de l'utilisateur
   */
  const login = async (credentials: LoginRequest): Promise<boolean> => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await apiService.post<LoginResponse>(
        API_ENDPOINTS.login,
        credentials
      );

      // Créer un objet plain pour le stockage (pas de propriétés réactives)
      const plainUser: User = {
        id: response.userId,
        username: response.username,
        role: response.role,
        active: true,
        anneeExercice: response.anneeExercice,
        anneeExerciceId: response.anneeExerciceId,
      };

      // Mettre à jour l'état
      setToken(response.token);
      setUser(plainUser);
      setIsAuthenticated(true);

      // Persister dans le stockage local
      await offlineStorage.set(STORAGE_KEYS.AUTH_TOKEN, response.token);
      await offlineStorage.set(STORAGE_KEYS.AUTH_USER, plainUser);

      return true;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Erreur de connexion';
      setError(errorMessage);
      console.error('Erreur de connexion:', err);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * Déconnexion de l'utilisateur
   */
  const logout = async () => {
    // Réinitialiser l'état
    setToken(null);
    setUser(null);
    setIsAuthenticated(false);
    setError(null);

    // Nettoyer le stockage local
    await offlineStorage.remove(STORAGE_KEYS.AUTH_TOKEN);
    await offlineStorage.remove(STORAGE_KEYS.AUTH_USER);
  };

  /**
   * Vérifie l'authentification
   */
  const checkAuth = async (): Promise<boolean> => {
    try {
      const savedToken = await offlineStorage.get<string>(STORAGE_KEYS.AUTH_TOKEN);
      if (!savedToken) {
        setIsAuthenticated(false);
        return false;
      }

      // Optionnel : vérifier le token avec le backend
      await apiService.get(API_ENDPOINTS.userMe);
      setIsAuthenticated(true);
      return true;
    } catch (err) {
      setIsAuthenticated(false);
      await logout();
      return false;
    }
  };

  const value: AuthContextType = {
    user,
    token,
    isAuthenticated,
    isLoading,
    error,
    login,
    logout,
    checkAuth,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

/**
 * Hook pour utiliser le contexte d'authentification
 */
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth doit être utilisé à l\'intérieur d\'un AuthProvider');
  }
  return context;
};
