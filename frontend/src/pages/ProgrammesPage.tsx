import { useState, useEffect } from 'react';
import programmeService from '../services/programme.service';
import categorieProgrammeService from '../services/categorie-programme.service';
import classeService from '../services/classe.service';
import type { 
  Programme, 
  CreateProgrammeRequest,
  CategorieProgramme,
  Classe
} from '../types';
import './ProgrammesPage.css';

export function ProgrammesPage() {
  const [programmes, setProgrammes] = useState<Programme[]>([]);
  const [categories, setCategories] = useState<CategorieProgramme[]>([]);
  const [classes, setClasses] = useState<Classe[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  
  // Filtres
  const [filters, setFilters] = useState({
    categorieId: '',
    classeId: '',
    nom: '',
  });

  const [formData, setFormData] = useState<CreateProgrammeRequest>({
    nom: '',
    description: '',
    categorieId: 0,
    classeId: 0,
  });

  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      const [progData, catData, classeData] = await Promise.all([
        programmeService.getAllProgrammes(),
        categorieProgrammeService.getAllCategories(),
        classeService.getAllClasses(),
      ]);
      setProgrammes(progData);
      setCategories(catData);
      setClasses(classeData);
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
      if (filters.categorieId) params.categorieId = Number(filters.categorieId);
      if (filters.classeId) params.classeId = Number(filters.classeId);
      if (filters.nom) params.nom = filters.nom;

      const data = await programmeService.searchProgrammes(params);
      setProgrammes(data);
    } catch (err: any) {
      setError(err.message || 'Erreur lors de la recherche');
    } finally {
      setLoading(false);
    }
  };

  const handleResetFilters = async () => {
    setFilters({ categorieId: '', classeId: '', nom: '' });
    try {
      setLoading(true);
      const data = await programmeService.getAllProgrammes();
      setProgrammes(data);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingId(null);
    setFormData({
      nom: '',
      description: '',
      categorieId: categories.length > 0 ? categories[0].id : 0,
      classeId: classes.length > 0 ? classes[0].id : 0,
    });
    setShowModal(true);
  };

  const handleEdit = (programme: Programme) => {
    setEditingId(programme.id);
    setFormData({
      nom: programme.nom,
      description: programme.description || '',
      categorieId: programme.categorieId,
      classeId: programme.classeId,
    });
    setShowModal(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingId) {
        await programmeService.updateProgramme(editingId, formData);
        setSuccess('Programme modifié avec succès !');
      } else {
        await programmeService.createProgramme(formData);
        setSuccess('Programme créé avec succès !');
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
    if (window.confirm('Êtes-vous sûr de vouloir supprimer ce programme ?')) {
      try {
        await programmeService.deleteProgramme(id);
        setSuccess('Programme supprimé avec succès !');
        setError(null);
        setTimeout(() => setSuccess(null), 3000);
        handleResetFilters();
      } catch (err: any) {
        setError(err.response?.data?.message || 'Erreur lors de la suppression');
      }
    }
  };

  if (loading && programmes.length === 0) return <div className="loading">Chargement...</div>;

  return (
    <div className="programmes-page">
      <div className="page-header">
        <h1>📖 Gestion des Programmes</h1>
        <button className="btn btn-primary" onClick={handleCreate}>
          + Nouveau Programme
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
        <h3>🔍 Filtres de recherche</h3>
        <div className="filters-grid">
          <div className="form-group">
            <label>Catégorie</label>
            <select
              className="form-control"
              value={filters.categorieId}
              onChange={(e) => setFilters({ ...filters, categorieId: e.target.value })}
            >
              <option value="">Toutes les catégories</option>
              {categories.map(cat => (
                <option key={cat.id} value={cat.id}>{cat.nom}</option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>Classe</label>
            <select
              className="form-control"
              value={filters.classeId}
              onChange={(e) => setFilters({ ...filters, classeId: e.target.value })}
            >
              <option value="">Toutes les classes</option>
              {classes.map(classe => (
                <option key={classe.id} value={classe.id}>{classe.nom}</option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>Nom du programme</label>
            <input
              type="text"
              className="form-control"
              value={filters.nom}
              onChange={(e) => setFilters({ ...filters, nom: e.target.value })}
              placeholder="Rechercher..."
            />
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
              <th>Nom</th>
              <th>Description</th>
              <th>Catégorie</th>
              <th>Classe</th>
              <th>Date de création</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {programmes.length === 0 ? (
              <tr>
                <td colSpan={6} className="text-center">Aucun programme trouvé</td>
              </tr>
            ) : (
              programmes.map(programme => (
                <tr key={programme.id}>
                  <td><strong>{programme.nom}</strong></td>
                  <td>{programme.description || '-'}</td>
                  <td>
                    <span className="badge badge-info">{programme.categorieNom}</span>
                  </td>
                  <td>
                    <span className="badge badge-primary">{programme.classeNom}</span>
                  </td>
                  <td>{new Date(programme.createdAt).toLocaleDateString('fr-FR')}</td>
                  <td className="actions">
                    <button
                      className="btn btn-sm btn-info"
                      onClick={() => handleEdit(programme)}
                      title="Modifier"
                    >
                      ✏️
                    </button>
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDelete(programme.id)}
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
              <h2>{editingId ? 'Modifier le programme' : 'Nouveau programme'}</h2>
              <button className="close-btn" onClick={() => setShowModal(false)}>×</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="nom">Nom *</label>
                <input
                  id="nom"
                  type="text"
                  className="form-control"
                  value={formData.nom}
                  onChange={(e) => setFormData({ ...formData, nom: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="description">Description</label>
                <textarea
                  id="description"
                  className="form-control"
                  rows={3}
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label htmlFor="categorieId">Catégorie *</label>
                <select
                  id="categorieId"
                  className="form-control"
                  value={formData.categorieId}
                  onChange={(e) => setFormData({ ...formData, categorieId: Number(e.target.value) })}
                  required
                >
                  <option value="">Sélectionner une catégorie</option>
                  {categories.map(cat => (
                    <option key={cat.id} value={cat.id}>{cat.nom}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label htmlFor="classeId">Classe *</label>
                <select
                  id="classeId"
                  className="form-control"
                  value={formData.classeId}
                  onChange={(e) => setFormData({ ...formData, classeId: Number(e.target.value) })}
                  required
                >
                  <option value="">Sélectionner une classe</option>
                  {classes.map(classe => (
                    <option key={classe.id} value={classe.id}>{classe.nom}</option>
                  ))}
                </select>
              </div>
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
