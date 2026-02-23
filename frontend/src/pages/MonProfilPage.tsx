import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import utilisateurService from '../services/utilisateur.service';
import type { Utilisateur, UpdateUtilisateurRequest } from '../types';
import './MonProfilPage.css';

export function MonProfilPage() {
  const { user: currentUser, logout } = useAuth();
  const navigate = useNavigate();
  const [utilisateur, setUtilisateur] = useState<Utilisateur | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    confirmPassword: '',
  });

  useEffect(() => {
    loadUserData();
  }, []);

  const loadUserData = async () => {
    try {
      setLoading(true);
      const data = await utilisateurService.getCurrentUser();
      setUtilisateur(data);
      setFormData({
        username: data.username,
        password: '',
        confirmPassword: '',
      });
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement des données');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    // Validation du mot de passe
    if (formData.password && formData.password !== formData.confirmPassword) {
      setError('Les mots de passe ne correspondent pas');
      return;
    }

    if (!utilisateur) return;

    try {
      const updateData: UpdateUtilisateurRequest = {};
      let usernameChanged = false;
      
      // Ajouter username seulement s'il a changé
      if (formData.username !== utilisateur.username) {
        updateData.username = formData.username;
        usernameChanged = true;
      }
      
      // Ajouter password seulement s'il est renseigné
      if (formData.password) {
        updateData.password = formData.password;
      }

      // Si aucun changement, ne rien faire
      if (Object.keys(updateData).length === 0) {
        setError('Aucune modification détectée');
        return;
      }

      await utilisateurService.updateUtilisateur(utilisateur.id, updateData);
      
      // Si le username a changé, déconnecter l'utilisateur
      if (usernameChanged) {
        setSuccess('Nom d\'utilisateur modifié avec succès. Vous allez être déconnecté...');
        setTimeout(async () => {
          await logout();
          navigate('/login');
        }, 2000);
      } else {
        // Sinon, afficher le message de succès et réinitialiser le formulaire
        setSuccess('Profil mis à jour avec succès');
        
        // Réinitialiser les champs de mot de passe
        setFormData(prev => ({
          ...prev,
          password: '',
          confirmPassword: '',
        }));
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de la mise à jour');
    }
  };

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div className="mon-profil-page">
      <div className="page-header">
        <h1>Mon Profil</h1>
      </div>

      {error && (
        <div className="alert alert-danger">
          {error}
          <button onClick={() => setError(null)}>×</button>
        </div>
      )}

      {success && (
        <div className="alert alert-success">
          {success}
          <button onClick={() => setSuccess(null)}>×</button>
        </div>
      )}

      <div className="profile-container">
        <div className="profile-info-card">
          <h2>Informations du compte</h2>
          <div className="info-row">
            <span className="info-label">Rôle :</span>
            <span className="info-value">
              <span className={`badge badge-${utilisateur?.role.toLowerCase()}`}>
                {utilisateur?.role}
              </span>
            </span>
          </div>
          <div className="info-row">
            <span className="info-label">Année d'exercice :</span>
            <span className="info-value">
              {utilisateur && new Date(utilisateur.anneeExercice).getFullYear()}
            </span>
          </div>
          <div className="info-row">
            <span className="info-label">Statut :</span>
            <span className="info-value">
              <span className={`status-badge ${utilisateur?.active ? 'active' : 'inactive'}`}>
                {utilisateur?.active ? 'Actif' : 'Inactif'}
              </span>
            </span>
          </div>
          <div className="info-row">
            <span className="info-label">Compte créé le :</span>
            <span className="info-value">
              {utilisateur && new Date(utilisateur.createdAt).toLocaleDateString()}
            </span>
          </div>
          <div className="info-note">
            <small>ℹ️ Ces informations ne peuvent être modifiées que par un Directeur</small>
          </div>
        </div>

        <div className="profile-edit-card">
          <h2>Modifier mes informations</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Nom d'utilisateur</label>
              <input
                type="text"
                className="form-control"
                value={formData.username}
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label>Nouveau mot de passe (laisser vide pour ne pas changer)</label>
              <input
                type="password"
                className="form-control"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                placeholder="••••••••"
              />
            </div>

            <div className="form-group">
              <label>Confirmer le nouveau mot de passe</label>
              <input
                type="password"
                className="form-control"
                value={formData.confirmPassword}
                onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                placeholder="••••••••"
                disabled={!formData.password}
              />
            </div>

            <div className="form-actions">
              <button type="submit" className="btn btn-primary">
                Enregistrer les modifications
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
