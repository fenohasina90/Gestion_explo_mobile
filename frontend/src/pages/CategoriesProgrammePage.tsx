import { useState, useEffect } from 'react';
import categorieProgrammeService from '../services/categorie-programme.service';
import type { CategorieProgramme, CreateCategorieProgrammeRequest } from '../types';
import './CategoriesProgrammePage.css';

export function CategoriesProgrammePage() {
  const [categories, setCategories] = useState<CategorieProgramme[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState<CreateCategorieProgrammeRequest>({
    nom: '',
    description: '',
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const data = await categorieProgrammeService.getAllCategories();
      setCategories(data);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement des données');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingId(null);
    setFormData({
      nom: '',
      description: '',
    });
    setShowModal(true);
  };

  const handleEdit = (categorie: CategorieProgramme) => {
    setEditingId(categorie.id);
    setFormData({
      nom: categorie.nom,
      description: categorie.description || '',
    });
    setShowModal(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingId) {
        await categorieProgrammeService.updateCategorie(editingId, formData);
        setSuccess('Catégorie modifiée avec succès !');
      } else {
        await categorieProgrammeService.createCategorie(formData);
        setSuccess('Catégorie créée avec succès !');
      }
      setShowModal(false);
      setError(null);
      setTimeout(() => setSuccess(null), 3000);
      loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de l\'enregistrement');
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cette catégorie ?')) {
      try {
        await categorieProgrammeService.deleteCategorie(id);
        setSuccess('Catégorie supprimée avec succès !');
        setError(null);
        setTimeout(() => setSuccess(null), 3000);
        loadData();
      } catch (err: any) {
        setError(err.response?.data?.message || 'Erreur lors de la suppression');
      }
    }
  };

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div className="categories-programme-page">
      <div className="page-header">
        <h1>📚 Gestion des Catégories de Programme</h1>
        <button className="btn btn-primary" onClick={handleCreate}>
          + Nouvelle Catégorie
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

      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Nom</th>
              <th>Description</th>
              <th>Date de création</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {categories.length === 0 ? (
              <tr>
                <td colSpan={4} className="text-center">Aucune catégorie trouvée</td>
              </tr>
            ) : (
              categories.map(categorie => (
                <tr key={categorie.id}>
                  <td><strong>{categorie.nom}</strong></td>
                  <td>{categorie.description || '-'}</td>
                  <td>{new Date(categorie.createdAt).toLocaleDateString('fr-FR')}</td>
                  <td className="actions">
                    <button
                      className="btn btn-sm btn-info"
                      onClick={() => handleEdit(categorie)}
                      title="Modifier"
                    >
                      ✏️
                    </button>
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDelete(categorie.id)}
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
              <h2>{editingId ? 'Modifier la catégorie' : 'Nouvelle catégorie'}</h2>
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
