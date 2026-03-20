import { useState, useEffect } from 'react';
import mouvementBudgetaireService from '../services/mouvement-budgetaire.service';
import anneeExerciceService from '../services/annee-exercice.service';
import Pagination from '../components/Pagination';
import type { 
  MouvementBudgetaire, 
  CreateMouvementBudgetaireRequest,
  UpdateMouvementBudgetaireRequest,
  MouvementBudgetaireFilterRequest,
  EtatCaisse,
  TypeMouvement,
  AnneeExercice,
  PageResponse
} from '../types';
import { useAuth } from '../contexts/AuthContext';
import './MouvementsBudgetairesPage.css';

export function MouvementsBudgetairesPage() {
  const { user } = useAuth();
  const [mouvements, setMouvements] = useState<MouvementBudgetaire[]>([]);
  const [mouvementsPage, setMouvementsPage] = useState<PageResponse<MouvementBudgetaire> | null>(null);
  const [etatCaisse, setEtatCaisse] = useState<EtatCaisse | null>(null);
  const [types, setTypes] = useState<TypeMouvement[]>([]);
  const [annees, setAnnees] = useState<AnneeExercice[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [showFilters, setShowFilters] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  
  // Pagination
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  
  const [filters, setFilters] = useState<MouvementBudgetaireFilterRequest>({
    recherche: '',
    dateDebut: '',
    dateFin: '',
    typeId: undefined,
    anneeExerciceId: undefined,
  });

  const [formData, setFormData] = useState<CreateMouvementBudgetaireRequest>({
    typeId: 0,
    montant: 0,
    description: '',
  });

  const isDirecteur = user?.role === 'Directeur';
  const isDirecteurOrCo =
    user?.role === 'Directeur' ||
    user?.role === 'Co-Directeur' ||
    user?.role === 'Co_Directeur';

  useEffect(() => {
    loadData();
    loadTypes();
    loadAnnees();
  }, [currentPage, pageSize, filters]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [mouvementsData, etatData] = await Promise.all([
        mouvementBudgetaireService.getMouvementsWithFiltersPaginated(filters, currentPage, pageSize, 'createdAt', 'desc'),
        mouvementBudgetaireService.getEtatCaisse(filters.anneeExerciceId),
      ]);
      setMouvementsPage(mouvementsData);
      setMouvements(mouvementsData.content);
      setEtatCaisse(etatData);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement des données');
    } finally {
      setLoading(false);
    }
  };

  const loadTypes = async () => {
    try {
      const data = await mouvementBudgetaireService.getAllTypes();
      setTypes(data);
    } catch (err: any) {
      console.error('Erreur lors du chargement des types', err);
    }
  };

  const loadAnnees = async () => {
    try {
      const data = await anneeExerciceService.getAllAnneesExercice();
      setAnnees(data);
    } catch (err: any) {
      console.error('Erreur lors du chargement des années', err);
    }
  };

  const handleFilter = async (e: React.FormEvent) => {
    e.preventDefault();
    setCurrentPage(0); // Retour à la première page lors d'un nouveau filtrage
    loadData();
  };

  const handleResetFilters = async () => {
    setFilters({
      recherche: '',
      dateDebut: '',
      dateFin: '',
      typeId: undefined,
      anneeExerciceId: undefined,
    });
    setCurrentPage(0); // Retour à la première page
    await loadData();
  };

  const handleCreate = () => {
    setEditingId(null);
    setFormData({
      typeId: 0,
      montant: 0,
      description: '',
    });
    setShowModal(true);
  };

  const handleEdit = (mouvement: MouvementBudgetaire) => {
    setEditingId(mouvement.id);
    setFormData({
      typeId: mouvement.type.id,
      montant: mouvement.montant,
      description: mouvement.description || '',
    });
    setShowModal(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingId) {
        const updateData: UpdateMouvementBudgetaireRequest = {
          typeId: formData.typeId,
          montant: formData.montant,
          description: formData.description,
        };
        await mouvementBudgetaireService.updateMouvement(editingId, updateData);
        setSuccess('Mouvement budgétaire modifié avec succès !');
      } else {
        await mouvementBudgetaireService.createMouvement(formData);
        setSuccess('Mouvement budgétaire créé avec succès !');
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
    if (window.confirm('Êtes-vous sûr de vouloir supprimer ce mouvement budgétaire ?')) {
      try {
        await mouvementBudgetaireService.deleteMouvement(id);
        setSuccess('Mouvement budgétaire supprimé avec succès !');
        setError(null);
        setTimeout(() => setSuccess(null), 3000);
        loadData();
      } catch (err: any) {
        setError(err.response?.data?.message || 'Erreur lors de la suppression');
      }
    }
  };

  const formatMontant = (montant: number) => {
    return new Intl.NumberFormat('fr-MG', {
      style: 'currency',
      currency: 'MGA',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(montant);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div className="mouvements-budgetaires-page">
      <div className="page-header">
        <h1>💰 Gestion Budgétaire</h1>
        <div className="header-actions">
          {isDirecteur && (
            <button className="btn btn-primary" onClick={handleCreate}>
              + Nouveau Mouvement
            </button>
          )}
          <button
            className="btn btn-secondary"
            onClick={() => setShowFilters(!showFilters)}
          >
            {showFilters ? '🔽 Masquer les filtres' : '🔼 Afficher les filtres'}
          </button>
        </div>
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

      {/* État de caisse */}
      {etatCaisse && (
        <div className="etat-caisse">
          <div className="etat-card recettes">
            <div className="etat-icon">💵</div>
            <div className="etat-content">
              <div className="etat-label">Total Recettes</div>
              <div className="etat-value">{formatMontant(etatCaisse.totalRecettes)}</div>
            </div>
          </div>
          <div className="etat-card depenses">
            <div className="etat-icon">💸</div>
            <div className="etat-content">
              <div className="etat-label">Total Dépenses</div>
              <div className="etat-value">{formatMontant(etatCaisse.totalDepenses)}</div>
            </div>
          </div>
          <div className={`etat-card solde ${etatCaisse.solde >= 0 ? 'positif' : 'negatif'}`}>
            <div className="etat-icon">{etatCaisse.solde >= 0 ? '✅' : '⚠️'}</div>
            <div className="etat-content">
              <div className="etat-label">Solde</div>
              <div className="etat-value">{formatMontant(etatCaisse.solde)}</div>
            </div>
          </div>
        </div>
      )}

      {/* Filtres */}
      {showFilters && (
        <div className="filters-section">
          <form onSubmit={handleFilter} className="filters-form">
            <div className="filters-grid">
              <div className="filter-group">
                <label htmlFor="recherche">Recherche</label>
                <input
                  type="text"
                  id="recherche"
                  placeholder="Description..."
                  value={filters.recherche}
                  onChange={(e) => setFilters({ ...filters, recherche: e.target.value })}
                />
              </div>

              <div className="filter-group">
                <label htmlFor="dateDebut">Date de début</label>
                <input
                  type="date"
                  id="dateDebut"
                  value={filters.dateDebut}
                  onChange={(e) => setFilters({ ...filters, dateDebut: e.target.value })}
                />
              </div>

              <div className="filter-group">
                <label htmlFor="dateFin">Date de fin</label>
                <input
                  type="date"
                  id="dateFin"
                  value={filters.dateFin}
                  onChange={(e) => setFilters({ ...filters, dateFin: e.target.value })}
                />
              </div>

              <div className="filter-group">
                <label htmlFor="typeId">Type de mouvement</label>
                <select
                  id="typeId"
                  value={filters.typeId || ''}
                  onChange={(e) => setFilters({ ...filters, typeId: e.target.value ? Number(e.target.value) : undefined })}
                >
                  <option value="">Tous les types</option>
                  {types.map(type => (
                    <option key={type.id} value={type.id}>{type.type}</option>
                  ))}
                </select>
              </div>

              <div className="filter-group">
                <label htmlFor="anneeExerciceId">Année d'exercice</label>
                <select
                  id="anneeExerciceId"
                  value={filters.anneeExerciceId || ''}
                  onChange={(e) => setFilters({ ...filters, anneeExerciceId: e.target.value ? Number(e.target.value) : undefined })}
                >
                  <option value="">Toutes les années</option>
                  {annees.map(annee => (
                    <option key={annee.id} value={annee.id}>
                      {new Date(annee.annee).getFullYear()}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="filters-actions">
              <button type="submit" className="btn btn-primary">
                Rechercher
              </button>
              <button type="button" className="btn btn-secondary" onClick={handleResetFilters}>
                Réinitialiser
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Liste des mouvements */}
      <div className="table-container">
        <p className="results-count">
          <strong>{mouvementsPage?.totalElements || 0}</strong> mouvement{(mouvementsPage?.totalElements || 0) !== 1 ? 's' : ''} trouvé{(mouvementsPage?.totalElements || 0) !== 1 ? 's' : ''}
        </p>
        
        <table className="data-table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Type</th>
              <th>Description</th>
              <th>Montant</th>
              <th>Année</th>
              {isDirecteurOrCo && <th>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {mouvements.length === 0 ? (
              <tr>
                <td colSpan={isDirecteurOrCo ? 6 : 5} className="text-center">
                  Aucun mouvement budgétaire trouvé
                </td>
              </tr>
            ) : (
              mouvements.map(mouvement => (
                <tr key={mouvement.id}>
                  <td>{formatDate(mouvement.createdAt)}</td>
                  <td>
                    <span className={`badge badge-${mouvement.type.type === 'RECETTE' ? 'success' : 'danger'}`}>
                      {mouvement.type.type === 'RECETTE' ? '💵 RECETTE' : '💸 DÉPENSE'}
                    </span>
                  </td>
                  <td>{mouvement.description || '-'}</td>
                  <td className={mouvement.type.type === 'RECETTE' ? 'text-success' : 'text-danger'}>
                    <strong>{formatMontant(mouvement.montant)}</strong>
                  </td>
                  <td>{new Date(mouvement.anneeExercice.annee).getFullYear()}</td>
                  {isDirecteurOrCo && (
                    <td className="actions">
                      {isDirecteurOrCo && (
                        <button
                          className="btn btn-sm btn-info"
                          onClick={() => handleEdit(mouvement)}
                          title="Modifier"
                        >
                          ✏️
                        </button>
                      )}
                      {isDirecteur && (
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => handleDelete(mouvement.id)}
                          title="Supprimer"
                        >
                          🗑️
                        </button>
                      )}
                    </td>
                  )}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      {mouvementsPage && mouvementsPage.totalElements > 0 && (
        <Pagination
          currentPage={mouvementsPage.page}
          totalPages={mouvementsPage.totalPages}
          totalElements={mouvementsPage.totalElements}
          pageSize={mouvementsPage.size}
          onPageChange={setCurrentPage}
          onSizeChange={(size) => {
            setPageSize(size);
            setCurrentPage(0);
          }}
        />
      )}

      {/* Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editingId ? 'Modifier le mouvement' : 'Nouveau mouvement budgétaire'}</h2>
              <button className="close-btn" onClick={() => setShowModal(false)}>×</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="typeId">Type de mouvement *</label>
                <select
                  id="typeId"
                  className="form-control"
                  value={formData.typeId}
                  onChange={(e) => setFormData({ ...formData, typeId: Number(e.target.value) })}
                  required
                >
                  <option value="0">Sélectionner un type</option>
                  {types.map(type => (
                    <option key={type.id} value={type.id}>{type.type}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="montant">Montant (Ar) *</label>
                <input
                  id="montant"
                  type="number"
                  className="form-control"
                  value={formData.montant}
                  onChange={(e) => setFormData({ ...formData, montant: Number(e.target.value) })}
                  min="0"
                  step="0.01"
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
                  placeholder="Description du mouvement budgétaire..."
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
