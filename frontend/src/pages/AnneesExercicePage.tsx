import { useState, useEffect } from 'react';
import anneeExerciceService from '../services/annee-exercice.service';
import historiqueProgrammeService from '../services/historique-programme.service';
import { useAuth } from '../contexts/AuthContext';
import type { AnneeExercice, CreateAnneeExerciceRequest } from '../types';
import './AnneesExercicePage.css';

export function AnneesExercicePage() {
  const { user } = useAuth();
  const [anneesExercice, setAnneesExercice] = useState<AnneeExercice[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [loadingInit, setLoadingInit] = useState<number | null>(null);
  const [formData, setFormData] = useState<CreateAnneeExerciceRequest>({
    annee: new Date().toISOString().split('T')[0],
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const data = await anneeExerciceService.getAllAnneesExercice();
      setAnneesExercice(data);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement des données');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setFormData({
      annee: new Date(new Date().getFullYear() + 1, 0, 1).toISOString().split('T')[0],
    });
    setShowModal(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await anneeExerciceService.createAnneeExercice(formData);
      setShowModal(false);
      setSuccess('Année d\'exercice créée avec succès !');
      setError(null);
      setTimeout(() => setSuccess(null), 3000);
      loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de la création');
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cette année d\'exercice ?')) {
      try {
        await anneeExerciceService.deleteAnneeExercice(id);
        setSuccess('Année d\'exercice supprimée avec succès !');
        setError(null);
        setTimeout(() => setSuccess(null), 3000);
        loadData();
      } catch (err: any) {
        setError(err.response?.data?.message || 'Erreur lors de la suppression');
      }
    }
  };

  const handleInitialiserStatuts = async (annee: AnneeExercice) => {
    if (annee.statutsInitialises) {
      setError('Les statuts ont déjà été initialisés pour cette année');
      setTimeout(() => setError(null), 3000);
      return;
    }

    const anneeNum = new Date(annee.annee).getFullYear();
    if (!window.confirm(
      `Initialiser tous les programmes au statut "EN ATTENTE" pour l'année ${anneeNum} ?\n\n` +
      `Cette action ne peut être effectuée qu'une seule fois par année.`
    )) {
      return;
    }

    try {
      setLoadingInit(annee.id);
      const response = await historiqueProgrammeService.initialiserAnnee(annee.id);
      setSuccess(`Initialisation réussie ! ${response.nombreProgrammes} programmes initialisés à "EN ATTENTE"`);
      setError(null);
      setTimeout(() => setSuccess(null), 5000);
      loadData(); // Recharger pour mettre à jour le statut
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de l\'initialisation des statuts');
      setTimeout(() => setError(null), 5000);
    } finally {
      setLoadingInit(null);
    }
  };

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div className="annees-exercice-page">
      <div className="page-header">
        <h1>Gestion des Années d'Exercice</h1>
        <button className="btn btn-primary" onClick={handleCreate}>
          + Nouvelle Année
        </button>
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

      <div className="cards-container">
        {anneesExercice.map(annee => (
          <div key={annee.id} className="annee-card">
            <div className="annee-card-header">
              <h3>{new Date(annee.annee).getFullYear()}</h3>
              <button
                className="btn btn-sm btn-danger"
                onClick={() => handleDelete(annee.id)}
                title="Supprimer"
              >
                🗑️
              </button>
            </div>
            <div className="annee-card-body">
              <p><strong>Période:</strong> {new Date(annee.annee).toLocaleDateString()} - {new Date(annee.dateFin).toLocaleDateString()}</p>
              <p><strong>Créée le:</strong> {new Date(annee.createdAt).toLocaleDateString()}</p>
              
              {/* Statut d'initialisation */}
              <div className="init-status">
                {annee.statutsInitialises ? (
                  <p className="status-badge initialized">🔒 Statuts initialisés</p>
                ) : (
                  <p className="status-badge not-initialized">⚠️ Statuts non initialisés</p>
                )}
              </div>

              {/* Bouton d'initialisation (uniquement pour Directeur et année non initialisée) */}
              {user?.role === 'Directeur' && !annee.statutsInitialises && (
                <button
                  className="btn btn-block btn-warning"
                  onClick={() => handleInitialiserStatuts(annee)}
                  disabled={loadingInit === annee.id}
                  title="Initialiser tous les programmes à 'EN ATTENTE'"
                >
                  {loadingInit === annee.id ? (
                    '⌛ Initialisation...'
                  ) : (
                    '🚀 Initialiser les statuts'
                  )}
                </button>
              )}

              {new Date(annee.dateFin) < new Date() && (
                <p className="status-badge expired"><strong>⚠️ Année expirée</strong></p>
              )}
            </div>
          </div>
        ))}
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Nouvelle année d'exercice</h2>
              <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Date de début (1er janvier)</label>
                <input
                  type="date"
                  className="form-control"
                  value={formData.annee}
                  onChange={(e) => setFormData({ annee: e.target.value })}
                  required
                />
                <small className="form-text">L'année d'exercice commence le 1er janvier</small>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Annuler
                </button>
                <button type="submit" className="btn btn-primary">
                  Créer
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
