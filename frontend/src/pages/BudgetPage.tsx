import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import budgetGlobalService from '../services/budget-global.service';
import activiteService from '../services/activite.service';
import anneeExerciceService from '../services/annee-exercice.service';
import type {
  BudgetGlobalResponse,
  ActiviteResponse,
  CreateActiviteRequest,
  UpdateActiviteRequest,
  DetailActiviteDto,
  ActiviteStatusResponse,
  AnneeExercice,
} from '../types';
import './BudgetPage.css';

export const BudgetPage = () => {
  const { user: currentUser } = useAuth();
  
  // États
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [budget, setBudget] = useState<BudgetGlobalResponse | null>(null);
  const [activites, setActivites] = useState<ActiviteResponse[]>([]);
  const [statuts, setStatuts] = useState<ActiviteStatusResponse[]>([]);
  const [anneesExercice, setAnneesExercice] = useState<AnneeExercice[]>([]);
  const [selectedAnneeId, setSelectedAnneeId] = useState<number | null>(null);
  
  // États pour le modal
  const [showModal, setShowModal] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [selectedActivite, setSelectedActivite] = useState<ActiviteResponse | null>(null);
  const [formData, setFormData] = useState<CreateActiviteRequest | UpdateActiviteRequest>({
    nom: '',
    description: '',
    dateDebut: '',
    dateFin: '',
    budgetGlobalId: 0,
    statusId: 1,
    details: [{ details: '', montant: 0 }],
  });

  // États pour le modal d'export PDF
  const [showExportModal, setShowExportModal] = useState(false);
  const [exportColumns, setExportColumns] = useState({
    includeDate: true,
    includeNomActivite: true,
    includeCoutActivite: true,
    includeDescriptionActivite: false,
    includeDetailsActivite: true,
    includeCoutDetails: true,
    includeStatutActivite: true,
  });

  // Charger les données initiales
  useEffect(() => {
    loadInitialData();
  }, []);

  // Charger budget et activités quand l'année change
  useEffect(() => {
    if (selectedAnneeId) {
      loadBudgetAndActivites();
    }
  }, [selectedAnneeId]);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      setError(null);

      // Charger les années d'exercice
      const anneesData = await anneeExerciceService.getAllAnneesExercice();
      setAnneesExercice(anneesData);
      console.log('Années chargées:', anneesData);

      // Charger les statuts
      const statutsData = await activiteService.getAllStatuts();
      setStatuts(statutsData);
      console.log('Statuts chargés:', statutsData);

      // Sélectionner l'année de l'utilisateur connecté par défaut
      if (currentUser?.anneeExercice && anneesData.length > 0) {
        const userAnnee = anneesData.find(a => a.annee === currentUser.anneeExercice);
        if (userAnnee) {
          setSelectedAnneeId(userAnnee.id);
          console.log('Année sélectionnée (utilisateur):', userAnnee);
        } else {
          setSelectedAnneeId(anneesData[0].id);
          console.log('Année sélectionnée (première):', anneesData[0]);
        }
      } else if (anneesData.length > 0) {
        setSelectedAnneeId(anneesData[0].id);
        console.log('Année sélectionnée (première):', anneesData[0]);
      } else {
        setError('Aucune année d\'exercice trouvée. Veuillez créer une année d\'exercice.');
      }
    } catch (err: any) {
      console.error('Erreur lors du chargement des données:', err);
      setError(err.response?.data?.message || 'Erreur lors du chargement des données');
    } finally {
      setLoading(false);
    }
  };

  const loadBudgetAndActivites = async () => {
    if (!selectedAnneeId) return;

    try {
      setLoading(true);
      setError(null);

      // Charger le budget (créé automatiquement si n'existe pas)
      const budgetData = await budgetGlobalService.getBudgetByAnneeExercice(selectedAnneeId);
      setBudget(budgetData);
      console.log('Budget chargé:', budgetData);

      // Charger les activités
      const activitesData = await activiteService.getActivitesByAnneeExercice(selectedAnneeId);
      setActivites(activitesData);
      console.log('Activités chargées:', activitesData);
    } catch (err: any) {
      console.error('Erreur lors du chargement du budget:', err);
      setError(err.response?.data?.message || 'Erreur lors du chargement du budget');
    } finally {
      setLoading(false);
    }
  };

  const openCreateModal = () => {
    console.log('openCreateModal appelé');
    console.log('Budget:', budget);
    console.log('canManageBudget:', canManageBudget());
    console.log('isBudgetEditable:', isBudgetEditable());
    
    if (!budget) {
      setError('Aucun budget trouvé. Sélectionnez une année d\'exercice.');
      return;
    }

    if (!isBudgetEditable()) {
      setError('Le budget ne peut plus être modifié (statut: ' + budget.status + ')');
      return;
    }

    setEditMode(false);
    setSelectedActivite(null);
    setFormData({
      nom: '',
      description: '',
      dateDebut: '',
      dateFin: '',
      budgetGlobalId: budget.id,
      statusId: 1, // "En attente" par défaut
      details: [{ details: '', montant: 0 }],
    });
    setShowModal(true);
    setError(null);
    setSuccess(null);
  };

  const openEditModal = (activite: ActiviteResponse) => {
    if (!isBudgetEditable()) {
      setError('Le budget ne peut plus être modifié (statut: ' + budget?.status + ')');
      return;
    }

    if (activite.status === 'Terminé') {
      setError('Impossible de modifier une activité dont le statut est "Terminé"');
      return;
    }

    setEditMode(true);
    setSelectedActivite(activite);
    setFormData({
      nom: activite.nom,
      description: activite.description || '',
      dateDebut: activite.dateDebut,
      dateFin: activite.dateFin,
      statusId: activite.statusId,
      details: activite.details.map(d => ({ details: d.details, montant: d.montant })),
    });
    setShowModal(true);
    setError(null);
    setSuccess(null);
  };

  const closeModal = () => {
    setShowModal(false);
    setEditMode(false);
    setSelectedActivite(null);
    setFormData({
      nom: '',
      description: '',
      dateDebut: '',
      dateFin: '',
      budgetGlobalId: 0,
      statusId: 1,
      details: [{ details: '', montant: 0 }],
    });
  };

  const handleAddDetail = () => {
    setFormData({
      ...formData,
      details: [...formData.details, { details: '', montant: 0 }],
    });
  };

  const handleRemoveDetail = (index: number) => {
    if (formData.details.length <= 1) {
      setError('Au moins un détail est requis');
      return;
    }
    const newDetails = formData.details.filter((_, i) => i !== index);
    setFormData({ ...formData, details: newDetails });
  };

  const handleDetailChange = (index: number, field: keyof DetailActiviteDto, value: any) => {
    const newDetails = [...formData.details];
    newDetails[index] = { ...newDetails[index], [field]: value };
    setFormData({ ...formData, details: newDetails });
  };

  const calculateTotal = () => {
    return formData.details.reduce((sum, detail) => sum + (Number(detail.montant) || 0), 0);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validation
    if (!formData.nom.trim()) {
      setError('Le nom de l\'activité est requis');
      return;
    }
    if (formData.details.length === 0) {
      setError('Au moins un détail est requis');
      return;
    }
    for (const detail of formData.details) {
      if (!detail.details.trim()) {
        setError('Tous les détails doivent avoir une description');
        return;
      }
      if (detail.montant <= 0) {
        setError('Tous les montants doivent être positifs');
        return;
      }
    }

    try {
      setLoading(true);
      setError(null);
      
      if (editMode && selectedActivite) {
        // Modification
        await activiteService.updateActivite(selectedActivite.id, formData as UpdateActiviteRequest);
        setSuccess('Activité modifiée avec succès');
      } else {
        // Création
        await activiteService.createActivite(formData as CreateActiviteRequest);
        setSuccess('Activité créée avec succès');
      }
      
      closeModal();
      loadBudgetAndActivites(); // Recharger pour voir les changements
    } catch (err: any) {
      console.error('Erreur lors de l\'enregistrement:', err);
      setError(err.response?.data?.message || 'Erreur lors de l\'enregistrement de l\'activité');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (activiteId: number, nom: string) => {
    if (!isBudgetEditable()) {
      setError('Le budget ne peut plus être modifié (statut: ' + budget?.status + ')');
      return;
    }

    if (!window.confirm(`Êtes-vous sûr de vouloir supprimer l'activité "${nom}" ?`)) {
      return;
    }

    try {
      setLoading(true);
      setError(null);
      
      await activiteService.deleteActivite(activiteId);
      
      setSuccess('Activité supprimée avec succès');
      loadBudgetAndActivites();
    } catch (err: any) {
      console.error('Erreur lors de la suppression:', err);
      setError(err.response?.data?.message || 'Erreur lors de la suppression de l\'activité');
    } finally {
      setLoading(false);
    }
  };

  const handleBudgetStatusChange = async (newStatusId: number) => {
    if (!budget) return;

    try {
      setLoading(true);
      setError(null);
      
      const updatedBudget = await budgetGlobalService.updateBudgetStatus(budget.id, { statusId: newStatusId });
      
      setBudget(updatedBudget);
      setSuccess('Statut du budget modifié avec succès');
    } catch (err: any) {
      console.error('Erreur lors de la modification du statut:', err);
      setError(err.response?.data?.message || 'Erreur lors de la modification du statut');
    } finally {
      setLoading(false);
    }
  };

  const canManageBudget = () => {
    return currentUser?.role === 'Directeur' || currentUser?.role === 'Co_Directeur';
  };

  const isBudgetEditable = () => {
    return budget && budget.status === 'Créé';
  };

  const formatMontant = (montant: number) => {
    return montant.toLocaleString('fr-FR') + ' Ar';
  };

  const formatDate = (dateStr?: string) => {
    if (!dateStr) return 'Non défini';
    return new Date(dateStr).toLocaleDateString('fr-FR');
  };

  // Fonctions pour l'export PDF
  const handleExportPdf = async () => {
    if (!selectedAnneeId) {
      setError('Veuillez sélectionner une année d\'exercice');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      
      const blob = await budgetGlobalService.exportBudgetPdf({
        anneeExerciceId: selectedAnneeId,
        ...exportColumns,
      });
      
      // Télécharger le fichier
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `budget_${selectedAnneeId}.pdf`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      setSuccess('PDF exporté avec succès');
      setShowExportModal(false);
    } catch (err: any) {
      console.error('Erreur lors de l\'export PDF:', err);
      setError('Erreur lors de l\'export du PDF');
    } finally {
      setLoading(false);
    }
  };

  const toggleExportColumn = (column: keyof typeof exportColumns) => {
    setExportColumns(prev => ({
      ...prev,
      [column]: !prev[column],
    }));
  };

  if (loading && !budget) {
    return (
      <div className="budget-page">
        <div className="loading">Chargement...</div>
      </div>
    );
  }

  return (
    <div className="budget-page">
      <div className="page-header">
        <h1>Gestion du Budget</h1>
        <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
          {!canManageBudget() && (
            <span className="info-text">Vous devez être Directeur ou Co-Directeur pour créer des activités</span>
          )}
          {canManageBudget() && !budget && (
            <span className="info-text">Sélectionnez une année pour créer des activités</span>
          )}
          {canManageBudget() && budget && !isBudgetEditable() && (
            <span className="info-text">Budget approuvé - Lecture seule</span>
          )}
          {budget && (
            <button className="btn btn-secondary" onClick={() => setShowExportModal(true)}>
              📄 Exporter PDF
            </button>
          )}
          {canManageBudget() && isBudgetEditable() && (
            <button className="btn btn-primary" onClick={openCreateModal}>
              + Nouvelle Activité
            </button>
          )}
        </div>
      </div>

      {/* Messages */}
      {error && (
        <div className="alert alert-error">
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

      {/* Filtre par année */}
      <div className="filters">
        <div className="filter-group">
          <label>Année d'exercice:</label>
          <select
            value={selectedAnneeId || ''}
            onChange={(e) => setSelectedAnneeId(Number(e.target.value))}
          >
            <option value="">Sélectionner une année</option>
            {anneesExercice.map((annee) => (
              <option key={annee.id} value={annee.id}>
                {new Date(annee.annee).getFullYear()}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Résumé du budget */}
      {budget && (
        <div className="budget-summary">
          <div className="budget-header">
            <h2>Budget {budget.anneeExercice}</h2>
            {canManageBudget() && (
              <div className="budget-status-selector">
                <label htmlFor="budgetStatus">Statut:</label>
                <select
                  id="budgetStatus"
                  value={budget.statusId}
                  onChange={(e) => handleBudgetStatusChange(Number(e.target.value))}
                  disabled={loading || budget.status === 'Approuvé comité'}
                >
                  <option value={1}>Créé</option>
                  <option value={2}>Approuvé comité</option>
                </select>
              </div>
            )}
          </div>
          <div className="summary-cards">
            <div className="summary-card">
              <div className="card-label">Montant Total</div>
              <div className="card-value">{formatMontant(budget.montant)}</div>
            </div>
            <div className="summary-card">
              <div className="card-label">Nombre d'Activités</div>
              <div className="card-value">{budget.nombreActivites}</div>
            </div>
            <div className="summary-card">
              <div className="card-label">Statut</div>
              <div className="card-value">{budget.status}</div>
            </div>
          </div>
          {!isBudgetEditable() && (
            <div className="budget-notice">
              ⚠️ Ce budget a été approuvé et ne peut plus être modifié
            </div>
          )}
        </div>
      )}

      {/* Liste des activités */}
      <div className="activites-section">
        <h2>Activités</h2>
        {activites.length === 0 ? (
          <div className="empty-state">
            Aucune activité pour cette année d'exercice
          </div>
        ) : (
          <div className="activites-list">
            {activites.map((activite) => (
              <div key={activite.id} className="activite-card">
                <div className="activite-header">
                  <h3>{activite.nom}</h3>
                  <div className="activite-header-right">
                    <span className={`status-badge status-${activite.status.toLowerCase().replace(/\s/g, '-')}`}>
                      {activite.status}
                    </span>
                    {canManageBudget() && isBudgetEditable() && (
                      <div className="activite-actions">
                        <button
                          className="btn btn-sm btn-secondary"
                          onClick={() => openEditModal(activite)}
                          title="Modifier"
                        >
                          ✏️
                        </button>
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => handleDelete(activite.id, activite.nom)}
                          title="Supprimer"
                        >
                          🗑️
                        </button>
                      </div>
                    )}
                  </div>
                </div>
                {activite.description && (
                  <p className="activite-description">{activite.description}</p>
                )}
                {(activite.dateDebut || activite.dateFin) && (
                  <div className="activite-dates">
                    <span>📅 {formatDate(activite.dateDebut)} - {formatDate(activite.dateFin)}</span>
                  </div>
                )}
                <div className="activite-details">
                  <h4>Détails:</h4>
                  <table>
                    <thead>
                      <tr>
                        <th>Description</th>
                        <th>Montant</th>
                      </tr>
                    </thead>
                    <tbody>
                      {activite.details.map((detail) => (
                        <tr key={detail.id}>
                          <td>{detail.details}</td>
                          <td className="montant">{formatMontant(detail.montant)}</td>
                        </tr>
                      ))}
                    </tbody>
                    <tfoot>
                      <tr>
                        <td><strong>Total:</strong></td>
                        <td className="montant"><strong>{formatMontant(activite.montant)}</strong></td>
                      </tr>
                    </tfoot>
                  </table>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Modal de création / modification */}
      {showModal && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editMode ? 'Modifier l\'activité' : 'Nouvelle Activité'}</h2>
              <button className="close-btn" onClick={closeModal}>×</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="nom">Nom de l'activité *</label>
                <input
                  id="nom"
                  type="text"
                  value={formData.nom}
                  onChange={(e) => setFormData({ ...formData, nom: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="description">Description</label>
                <textarea
                  id="description"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  rows={3}
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="dateDebut">Date de début</label>
                  <input
                    id="dateDebut"
                    type="date"
                    value={formData.dateDebut || ''}
                    onChange={(e) => setFormData({ ...formData, dateDebut: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="dateFin">Date de fin</label>
                  <input
                    id="dateFin"
                    type="date"
                    value={formData.dateFin || ''}
                    onChange={(e) => setFormData({ ...formData, dateFin: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="statusId">Statut</label>
                <select
                  id="statusId"
                  value={formData.statusId}
                  onChange={(e) => setFormData({ ...formData, statusId: Number(e.target.value) })}
                >
                  {statuts.map((statut) => (
                    <option key={statut.id} value={statut.id}>
                      {statut.status}
                    </option>
                  ))}
                </select>
              </div>

              <div className="details-section">
                <div className="details-header">
                  <h3>Détails du budget *</h3>
                  <button type="button" className="btn btn-secondary" onClick={handleAddDetail}>
                    + Ajouter une ligne
                  </button>
                </div>

                {formData.details.map((detail, index) => (
                  <div key={index} className="detail-row">
                    <div className="form-group flex-grow">
                      <input
                        type="text"
                        placeholder="Description"
                        value={detail.details}
                        onChange={(e) => handleDetailChange(index, 'details', e.target.value)}
                        required
                      />
                    </div>
                    <div className="form-group">
                      <input
                        type="number"
                        placeholder="Montant"
                        value={detail.montant || ''}
                        onChange={(e) => handleDetailChange(index, 'montant', Number(e.target.value))}
                        min="0"
                        step="1"
                        required
                      />
                    </div>
                    <button
                      type="button"
                      className="btn btn-danger btn-icon"
                      onClick={() => handleRemoveDetail(index)}
                      disabled={formData.details.length === 1}
                    >
                      🗑️
                    </button>
                  </div>
                ))}

                <div className="total-row">
                  <strong>Total estimé:</strong>
                  <strong>{formatMontant(calculateTotal())}</strong>
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={closeModal}>
                  Annuler
                </button>
                <button type="submit" className="btn btn-primary" disabled={loading}>
                  {loading 
                    ? (editMode ? 'Modification...' : 'Création...') 
                    : (editMode ? 'Modifier l\'activité' : 'Créer l\'activité')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Export PDF */}
      {showExportModal && (
        <div className="modal-overlay" onClick={() => setShowExportModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Exporter le budget en PDF</h2>
              <button className="close-btn" onClick={() => setShowExportModal(false)}>×</button>
            </div>
            
            <div className="modal-body">
              <p className="export-info">
                <strong>Colonnes obligatoires :</strong> Statut du budget, Montant total
              </p>
              <p className="export-info" style={{ marginBottom: '20px' }}>
                Sélectionnez les colonnes optionnelles à inclure dans le PDF :
              </p>

              <div className="export-columns">
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={exportColumns.includeDate}
                    onChange={() => toggleExportColumn('includeDate')}
                  />
                  <span>Date activité (début - fin)</span>
                </label>

                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={exportColumns.includeNomActivite}
                    onChange={() => toggleExportColumn('includeNomActivite')}
                  />
                  <span>Nom activité</span>
                </label>

                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={exportColumns.includeCoutActivite}
                    onChange={() => toggleExportColumn('includeCoutActivite')}
                  />
                  <span>Coût activité</span>
                </label>

                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={exportColumns.includeDescriptionActivite}
                    onChange={() => toggleExportColumn('includeDescriptionActivite')}
                  />
                  <span>Description activité</span>
                </label>

                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={exportColumns.includeDetailsActivite}
                    onChange={() => toggleExportColumn('includeDetailsActivite')}
                  />
                  <span>Détails activité</span>
                </label>

                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={exportColumns.includeCoutDetails}
                    onChange={() => toggleExportColumn('includeCoutDetails')}
                  />
                  <span>Coût détails activité</span>
                </label>

                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={exportColumns.includeStatutActivite}
                    onChange={() => toggleExportColumn('includeStatutActivite')}
                  />
                  <span>Statut activité</span>
                </label>
              </div>
            </div>

            <div className="modal-footer">
              <button 
                type="button" 
                className="btn btn-secondary" 
                onClick={() => setShowExportModal(false)}
              >
                Annuler
              </button>
              <button 
                type="button" 
                className="btn btn-primary" 
                onClick={handleExportPdf}
                disabled={loading}
              >
                {loading ? 'Export en cours...' : '📥 Télécharger PDF'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
