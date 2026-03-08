import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import classeProgressiveService from '../services/classe-progressive.service';
import anneeExerciceService from '../services/annee-exercice.service';
import type { 
  ClasseProgressive, 
  CreateClasseProgressiveRequest,
  AnneeExercice
} from '../types';
import './ClassesProgressivesPage.css';

export function ClassesProgressivesPage() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [classesProgressives, setClassesProgressives] = useState<ClasseProgressive[]>([]);
  const [anneesExercice, setAnneesExercice] = useState<AnneeExercice[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);

  // Filtres
  const [filters, setFilters] = useState({
    dateDebut: '',
    dateFin: '',
    anneeExerciceId: '',
  });

  const [formData, setFormData] = useState<CreateClasseProgressiveRequest>({
    dateCp: '',
    heureDebut: '',
    heureFin: '',
    niveau: '',
    anneeExerciceId: 0,
  });

  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      const [cpData, anneeData] = await Promise.all([
        classeProgressiveService.getAllCP(),
        anneeExerciceService.getAllAnneesExercice(),
      ]);
      setClassesProgressives(cpData);
      setAnneesExercice(anneeData);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement des données');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    try {
      setLoading(true);
      const params: any = {};
      if (filters.dateDebut) params.dateDebut = filters.dateDebut;
      if (filters.dateFin) params.dateFin = filters.dateFin;
      if (filters.anneeExerciceId) params.anneeExerciceId = Number(filters.anneeExerciceId);

      const data = await classeProgressiveService.filterCPByDates(params);
      setClassesProgressives(data);
    } catch (err: any) {
      setError(err.message || 'Erreur lors de la recherche');
    } finally {
      setLoading(false);
    }
  };

  const handleResetFilters = async () => {
    setFilters({ dateDebut: '', dateFin: '', anneeExerciceId: '' });
    try {
      setLoading(true);
      const data = await classeProgressiveService.getAllCP();
      setClassesProgressives(data);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingId(null);
    setFormData({
      dateCp: new Date().toISOString().split('T')[0],
      heureDebut: '14:00',
      heureFin: '17:00',
      niveau: '',
      anneeExerciceId: user?.anneeExerciceId || 0,
    });
    setShowModal(true);
  };

  const handleEdit = (cp: ClasseProgressive) => {
    setEditingId(cp.id);
    setFormData({
      dateCp: cp.dateCp,
      heureDebut: cp.heureDebut,
      heureFin: cp.heureFin,
      niveau: cp.niveau,
      anneeExerciceId: cp.anneeExerciceId,
    });
    setShowModal(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validation
    if (formData.heureDebut >= formData.heureFin) {
      setError('L\'heure de début doit être avant l\'heure de fin');
      return;
    }

    try {
      if (editingId) {
        await classeProgressiveService.updateCP(editingId, {
          dateCp: formData.dateCp,
          heureDebut: formData.heureDebut,
          heureFin: formData.heureFin,
          niveau: formData.niveau,
        });
        setSuccess('CP modifiée avec succès !');
      } else {
        await classeProgressiveService.createCP(formData);
        setSuccess('CP créée avec succès !');
      }
      setShowModal(false);
      setError(null);
      setTimeout(() => setSuccess(null), 3000);
      handleResetFilters();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de l\'enregistrement');
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cette CP ? Toutes les affectations de programmes seront également supprimées.')) {
      try {
        await classeProgressiveService.deleteCP(id);
        setSuccess('CP supprimée avec succès !');
        setError(null);
        setTimeout(() => setSuccess(null), 3000);
        handleResetFilters();
      } catch (err: any) {
        setError(err.response?.data?.message || 'Erreur lors de la suppression');
      }
    }
  };

  const handleCloturer = async (cp: ClasseProgressive) => {
    if (cp.etat === 1) {
      setError('Cette CP est déjà clôturée.');
      return;
    }

    if (window.confirm('Voulez-vous clôturer cette CP ? Une fois clôturée, vous ne pourrez plus modifier les présences ni les statuts des programmes.')) {
      try {
        await classeProgressiveService.cloturerCP(cp.id);
        setSuccess('CP clôturée avec succès !');
        setError(null);
        setTimeout(() => setSuccess(null), 3000);
        handleResetFilters();
      } catch (err: any) {
        setError(err.response?.data?.message || 'Erreur lors de la clôture');
      }
    }
  };

  const handleViewDetails = (cpId: number) => {
    navigate(`/cp/${cpId}/programmes`);
  };

  if (loading && classesProgressives.length === 0) return <div className="loading">Chargement...</div>;

  return (
    <div className="classes-progressives-page">
      <div className="page-header">
        <h1>🎯 Classes Progressives (CP)</h1>
        <button className="btn btn-primary" onClick={handleCreate}>
          + Nouvelle CP
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

      {/* Filtres */}
      <div className="filters-card">
        <h3>🔍 Filtres</h3>
        <div className="filters-grid">
          <div className="form-group">
            <label>Date début</label>
            <input
              type="date"
              className="form-control"
              value={filters.dateDebut}
              onChange={(e) => setFilters({ ...filters, dateDebut: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label>Date fin</label>
            <input
              type="date"
              className="form-control"
              value={filters.dateFin}
              onChange={(e) => setFilters({ ...filters, dateFin: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label>Année d'exercice</label>
            <select
              className="form-control"
              value={filters.anneeExerciceId}
              onChange={(e) => setFilters({ ...filters, anneeExerciceId: e.target.value })}
            >
              <option value="">Toutes les années</option>
              {anneesExercice.map(annee => (
                <option key={annee.id} value={annee.id}>
                  {new Date(annee.annee).getFullYear()}
                </option>
              ))}
            </select>
          </div>
          <div className="form-group filter-actions">
            <button className="btn btn-primary" onClick={handleSearch}>
              Rechercher
            </button>
            <button className="btn btn-secondary" onClick={handleResetFilters}>
              Réinitialiser
            </button>
          </div>
        </div>
      </div>

      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Date CP</th>
              <th>Horaires</th>
              <th>Niveau</th>
              <th>Année d'exercice</th>
              <th>État</th>
              <th>Date de création</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {classesProgressives.length === 0 ? (
              <tr>
                <td colSpan={7} className="text-center">Aucune CP trouvée</td>
              </tr>
            ) : (
              classesProgressives.map(cp => (
                <tr key={cp.id}>
                  <td>
                    <strong>{new Date(cp.dateCp).toLocaleDateString('fr-FR', {
                      weekday: 'long',
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric'
                    })}</strong>
                  </td>
                  <td>{cp.heureDebut} - {cp.heureFin}</td>
                  <td>
                    <span className="badge badge-primary">{cp.niveau || '-'}</span>
                  </td>
                  <td>{new Date(cp.anneeExercice).getFullYear()}</td>
                  <td>
                    {cp.etat === 1 ? (
                      <span className="badge badge-danger">🔒 Clôturée</span>
                    ) : (
                      <span className="badge badge-success">🔓 Ouverte</span>
                    )}
                  </td>
                  <td>{new Date(cp.createdAt).toLocaleDateString('fr-FR')}</td>
                  <td className="actions">
                    <button
                      className="btn btn-sm btn-success"
                      onClick={() => handleViewDetails(cp.id)}
                      title="Voir les programmes"
                    >
                      📋
                    </button>
                    <button
                      className="btn btn-sm btn-info"
                      onClick={() => handleEdit(cp)}
                      title="Modifier"
                    >
                      ✏️
                    </button>
                    {cp.etat !== 1 && (user?.role === 'Directeur' || user?.role === 'Co_Directeur') && (
                      <button
                        className="btn btn-sm btn-warning"
                        onClick={() => handleCloturer(cp)}
                        title="Clôturer cette CP"
                      >
                        🔒
                      </button>
                    )}
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDelete(cp.id)}
                      title="Supprimer"
                    >
                      🗑️
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editingId ? 'Modifier la CP' : 'Nouvelle CP'}</h2>
              <button className="close-btn" onClick={() => setShowModal(false)}>×</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="dateCp">Date de la CP *</label>
                <input
                  id="dateCp"
                  type="date"
                  className="form-control"
                  value={formData.dateCp}
                  onChange={(e) => setFormData({ ...formData, dateCp: e.target.value })}
                  required
                />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="heureDebut">Heure de début *</label>
                  <input
                    id="heureDebut"
                    type="time"
                    className="form-control"
                    value={formData.heureDebut}
                    onChange={(e) => setFormData({ ...formData, heureDebut: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="heureFin">Heure de fin *</label>
                  <input
                    id="heureFin"
                    type="time"
                    className="form-control"
                    value={formData.heureFin}
                    onChange={(e) => setFormData({ ...formData, heureFin: e.target.value })}
                    required
                  />
                </div>
              </div>
              <div className="form-group">
                <label htmlFor="niveau">Niveau</label>
                <input
                  id="niveau"
                  type="text"
                  className="form-control"
                  value={formData.niveau}
                  onChange={(e) => setFormData({ ...formData, niveau: e.target.value })}
                  placeholder="Ex: CP1, CP2, etc."
                />
              </div>
              {!editingId && (
                <div className="form-group">
                  <label htmlFor="anneeExercice">Année d'exercice</label>
                  <input
                    id="anneeExercice"
                    type="text"
                    className="form-control"
                    value={user?.anneeExercice ? new Date(user.anneeExercice).getFullYear() : ''}
                    disabled
                    style={{ backgroundColor: '#e9ecef', cursor: 'not-allowed' }}
                  />
                  <small className="text-muted">
                    Vous créez cette CP pour votre année d'exercice
                  </small>
                </div>
              )}
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Annuler
                </button>
                <button type="submit" className="btn btn-primary">
                  {editingId ? 'Modifier' : 'Créer'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
