import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import cpDetailsService from '../services/cp-details.service';
import classeProgressiveService from '../services/classe-progressive.service';
import programmeService from '../services/programme.service';
import instructeurService from '../services/instructeur.service';
import categorieProgrammeService from '../services/categorie-programme.service';
import classeService from '../services/classe.service';
import programmeStatusService from '../services/programme-status.service';
import { CPPresenceModal } from '../components/CPPresenceModal';
import { useAuth } from '../contexts/AuthContext';
import type { 
  CpDetailsResponse, 
  AddProgrammeToCpRequest,
  UpdateCpDetailsInstructeurRequest,
  ClasseProgressive,
  Programme,
  InstructeurSuggestion,
  CategorieProgramme,
  Classe
} from '../types';
import './CPDetailsPage.css';

export function CPDetailsPage() {
  const { cpId } = useParams<{ cpId: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  
  const [cpDetails, setCpDetails] = useState<CpDetailsResponse[]>([]);
  const [cp, setCP] = useState<ClasseProgressive | null>(null);
  const [programmes, setProgrammes] = useState<Programme[]>([]);
  const [allProgrammes, setAllProgrammes] = useState<Programme[]>([]);
  const [categories, setCategories] = useState<CategorieProgramme[]>([]);
  const [classes, setClasses] = useState<Classe[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showPresenceModal, setShowPresenceModal] = useState(false);
  const [editingDetail, setEditingDetail] = useState<CpDetailsResponse | null>(null);
  
  // Filtres de programme
  const [selectedCategorieId, setSelectedCategorieId] = useState<number | ''>('');
  const [selectedClasseId, setSelectedClasseId] = useState<number | ''>('');
  
  // Auto-complétion instructeurs
  const [searchQuery, setSearchQuery] = useState('');
  const [suggestions, setSuggestions] = useState<InstructeurSuggestion[]>([]);
  const [selectedInstructeurs, setSelectedInstructeurs] = useState<InstructeurSuggestion[]>([]);
  const [editSelectedInstructeurs, setEditSelectedInstructeurs] = useState<InstructeurSuggestion[]>([]);

  const [addFormData, setAddFormData] = useState<AddProgrammeToCpRequest>({
    classeProgressiveId: Number(cpId),
    programmeId: undefined,
    description: '',
    instructeurIds: [],
  });

  const [activityType, setActivityType] = useState<'programme' | 'libre'>('programme');

  useEffect(() => {
    if (cpId) {
      loadData();
    }
  }, [cpId]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [detailsData, cpData, progData, catData, classData] = await Promise.all([
        cpDetailsService.getCPDetails(Number(cpId)),
        classeProgressiveService.getCPById(Number(cpId)),
        programmeService.getAllProgrammes(),
        categorieProgrammeService.getAllCategories(),
        classeService.getAllClasses(),
      ]);
      setCpDetails(detailsData);
      setCP(cpData);
      setProgrammes(progData);
      setAllProgrammes(progData);
      setCategories(catData);
      setClasses(classData);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement des données');
    } finally {
      setLoading(false);
    }
  };
  
  // Filtrer les programmes selon la catégorie et classe sélectionnées
  useEffect(() => {
    let filtered = allProgrammes;
    
    if (selectedCategorieId) {
      filtered = filtered.filter(p => p.categorieId === selectedCategorieId);
    }
    
    if (selectedClasseId) {
      filtered = filtered.filter(p => p.classeId === selectedClasseId);
    }
    
    setProgrammes(filtered);
  }, [selectedCategorieId, selectedClasseId, allProgrammes]);

  const handleAddActivity = () => {
    setActivityType('programme');
    setSelectedCategorieId('');
    setSelectedClasseId('');
    setSearchQuery('');
    setSuggestions([]);
    setSelectedInstructeurs([]);
    setAddFormData({
      classeProgressiveId: Number(cpId),
      programmeId: undefined,
      description: '',
      instructeurIds: [],
    });
    setShowAddModal(true);
  };

  const handleActivityTypeChange = (type: 'programme' | 'libre') => {
    setActivityType(type);
    if (type === 'libre') {
      setAddFormData({
        ...addFormData,
        programmeId: undefined,
        description: '',
      });
    } else {
      setAddFormData({
        ...addFormData,
        programmeId: undefined,
        description: '',
      });
    }
  };
  
  // Gestion de l'auto-complétion pour les instructeurs (mode ajout)
  const handleSearchChange = async (value: string) => {
    setSearchQuery(value);
    
    if (value.trim().length >= 2) {
      try {
        const results = await instructeurService.searchInstructeurs(value);
        // Filtrer les instructeurs déjà sélectionnés
        const filtered = results.filter(
          r => !selectedInstructeurs.some(s => s.id === r.id)
        );
        setSuggestions(filtered);
      } catch (err) {
        console.error('Erreur de recherche:', err);
      }
    } else {
      setSuggestions([]);
    }
  };
  
  const handleSelectInstructeur = (instructeur: InstructeurSuggestion) => {
    setSelectedInstructeurs([...selectedInstructeurs, instructeur]);
    setSearchQuery('');
    setSuggestions([]);
  };
  
  const handleRemoveInstructeur = (instructeurId: number) => {
    setSelectedInstructeurs(selectedInstructeurs.filter(i => i.id !== instructeurId));
  };
  
  // Gestion de l'auto-complétion pour les instructeurs (mode édition)
  const handleEditSearchChange = async (value: string) => {
    setSearchQuery(value);
    
    if (value.trim().length >= 2) {
      try {
        const results = await instructeurService.searchInstructeurs(value);
        // Filtrer les instructeurs déjà sélectionnés
        const filtered = results.filter(
          r => !editSelectedInstructeurs.some(s => s.id === r.id)
        );
        setSuggestions(filtered);
      } catch (err) {
        console.error('Erreur de recherche:', err);
      }
    } else {
      setSuggestions([]);
    }
  };
  
  const handleEditSelectInstructeur = (instructeur: InstructeurSuggestion) => {
    setEditSelectedInstructeurs([...editSelectedInstructeurs, instructeur]);
    setSearchQuery('');
    setSuggestions([]);
  };
  
  const handleEditRemoveInstructeur = (instructeurId: number) => {
    setEditSelectedInstructeurs(editSelectedInstructeurs.filter(i => i.id !== instructeurId));
  };

  const handleSubmitAdd = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (activityType === 'programme' && !addFormData.programmeId) {
      setError('Veuillez sélectionner un programme');
      return;
    }

    if (activityType === 'libre' && !addFormData.description?.trim()) {
      setError('Veuillez entrer une description pour l\'activité libre');
      return;
    }

    if (selectedInstructeurs.length === 0) {
      setError('Veuillez sélectionner au moins un instructeur');
      return;
    }

    try {
      const dataToSend: AddProgrammeToCpRequest = {
        classeProgressiveId: Number(cpId),
        instructeurIds: selectedInstructeurs.map(i => i.id),
      };

      if (activityType === 'programme') {
        dataToSend.programmeId = addFormData.programmeId;
      } else {
        dataToSend.description = addFormData.description;
      }

      await cpDetailsService.addProgrammeToCP(dataToSend);
      setSuccess(`${activityType === 'programme' ? 'Programme' : 'Activité libre'} ajouté(e) avec succès !`);
      setShowAddModal(false);
      setError(null);
      setTimeout(() => setSuccess(null), 3000);
      loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de l\'ajout');
    }
  };

  const handleEditInstructeurs = (detail: CpDetailsResponse) => {
    setEditingDetail(detail);
    // Convertir les instructeurs du détail en InstructeurSuggestion
    const instructeursToEdit = detail.instructeurs.map(i => ({
      id: i.id,
      nomComplet: i.nomComplet,
      nom: '',
      prenom: ''
    }));
    setEditSelectedInstructeurs(instructeursToEdit);
    setSearchQuery('');
    setSuggestions([]);
    setShowEditModal(true);
  };

  const handleChangeStatus = async (detail: CpDetailsResponse, newStatusId: number, newStatusName: string) => {
    // Vérifier si la CP est clôturée
    if (cp?.etat === 1) {
      setError('Impossible de modifier le statut : la CP est clôturée');
      return;
    }

    // Vérifier si le programme est déjà terminé
    if (detail.statusNom === 'Terminé') {
      setError('Impossible de modifier le statut : le programme est déjà terminé');
      return;
    }

    if (!detail.programmeId) {
      setError('Impossible de changer le statut d\'une activité libre');
      return;
    }

    if (window.confirm(`Confirmer le passage au statut "${newStatusName}" ?`)) {
      try {
        await programmeStatusService.changeProgrammeStatus({
          programmeId: detail.programmeId,
          classeProgressiveId: Number(cpId),
          newStatusId
        });
        setSuccess(`Statut changé avec succès : ${newStatusName}`);
        setError(null);
        setTimeout(() => setSuccess(null), 3000);
        loadData();
      } catch (err: any) {
        setError(err.response?.data?.message || 'Erreur lors du changement de statut');
      }
    }
  };

  const handleSubmitEdit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (editSelectedInstructeurs.length === 0) {
      setError('Veuillez sélectionner au moins un instructeur');
      return;
    }

    try {
      const updateData: UpdateCpDetailsInstructeurRequest = {
        instructeurIds: editSelectedInstructeurs.map(i => i.id),
      };
      await cpDetailsService.updateInstructeurs(editingDetail!.id, updateData);
      setSuccess('Instructeurs mis à jour avec succès !');
      setShowEditModal(false);
      setError(null);
      setTimeout(() => setSuccess(null), 3000);
      loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de la mise à jour');
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Êtes-vous sûr de vouloir retirer cette activité de la CP ?')) {
      try {
        await cpDetailsService.deleteCPDetail(id);
        setSuccess('Activité retirée avec succès !');
        setError(null);
        setTimeout(() => setSuccess(null), 3000);
        loadData();
      } catch (err: any) {
        setError(err.response?.data?.message || 'Erreur lors de la suppression');
      }
    }
  };

  if (loading && !cp) return <div className="loading">Chargement...</div>;

  return (
    <div className="cp-details-page">
      <div className="page-header">
        <div>
          <button className="btn btn-secondary" onClick={() => navigate('/cp')}>
            ← Retour aux CP
          </button>
          <h1>📋 Programmes et Activités de la CP</h1>
          {cp && (
            <p className="cp-info">
              {new Date(cp.dateCp).toLocaleDateString('fr-FR', {
                weekday: 'long',
                year: 'numeric',
                month: 'long',
                day: 'numeric'
              })} - {cp.heureDebut} à {cp.heureFin}
              {cp.niveau && <span className="badge badge-primary">{cp.niveau}</span>}
            </p>
          )}
        </div>
        <div style={{ display: 'flex', gap: '12px' }}>
          <button className="btn btn-info" onClick={() => setShowPresenceModal(true)}>
            👥 Gérer présence
          </button>
          <button className="btn btn-primary" onClick={handleAddActivity}>
            + Ajouter une activité
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

      <div className="activities-grid">
        {cpDetails.length === 0 ? (
          <div className="empty-state">
            <p>Aucune activité planifiée pour cette CP</p>
            <p className="text-muted">Cliquez sur "Ajouter une activité" pour commencer</p>
          </div>
        ) : (
          cpDetails.map(detail => (
            <div key={detail.id} className="activity-card">
              <div className="activity-header">
                <div>
                  {detail.programmeId ? (
                    <>
                      <h3>{detail.programmeName}</h3>
                      <div className="activity-meta">
                        <span className="badge badge-info">{detail.categorieName}</span>
                        {detail.statusNom && (
                          <span className={`badge badge-status badge-${detail.statusNom.toLowerCase().replace(' ', '-')}`}>
                            {detail.statusNom}
                          </span>
                        )}
                      </div>
                    </>
                  ) : (
                    <>
                      <h3>🎨 Activité libre</h3>
                      <p className="activity-description">{detail.description}</p>
                    </>
                  )}
                </div>
              </div>
              
              <div className="instructeurs-section">
                <h4>👥 Instructeurs ({detail.instructeurs.length})</h4>
                <div className="instructeurs-list">
                  {detail.instructeurs.map(instr => (
                    <span key={instr.id} className="instructeur-badge">
                      {instr.nomComplet}
                    </span>
                  ))}
                </div>
              </div>

              <div className="activity-actions">
                {/* Boutons de changement de statut (uniquement pour Directeur/Co-Directeur et programmes) */}
                {detail.programmeId && (user?.role === 'Directeur' || user?.role === 'Co_Directeur') && cp?.etat !== 1 && (
                  <>
                    {detail.statusNom === 'En attente' && (
                      <button
                        className="btn btn-sm btn-primary"
                        onClick={() => handleChangeStatus(detail, 2, 'En cours')}
                        title="Passer en cours"
                      >
                        ▶️ En cours
                      </button>
                    )}
                    {detail.statusNom === 'En cours' && (
                      <button
                        className="btn btn-sm btn-success"
                        onClick={() => handleChangeStatus(detail, 3, 'Terminé')}
                        title="Marquer comme terminé"
                      >
                        ✅ Terminé
                      </button>
                    )}
                  </>
                )}
                
                <button
                  className="btn btn-sm btn-info"
                  onClick={() => handleEditInstructeurs(detail)}
                  title="Modifier les instructeurs"
                >
                  ✏️ Instructeurs
                </button>
                <button
                  className="btn btn-sm btn-danger"
                  onClick={() => handleDelete(detail.id)}
                  title="Retirer de la CP"
                  disabled={detail.statusNom === 'Terminé'}
                >
                  🗑️ Retirer
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Modal Ajout */}
      {showAddModal && (
        <div className="modal-overlay" onClick={() => setShowAddModal(false)}>
          <div className="modal-content modal-large" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Ajouter une activité à la CP</h2>
              <button className="close-btn" onClick={() => setShowAddModal(false)}>×</button>
            </div>
            <form onSubmit={handleSubmitAdd}>
              <div className="activity-type-selector">
                <label>
                  <input
                    type="radio"
                    name="activityType"
                    value="programme"
                    checked={activityType === 'programme'}
                    onChange={() => handleActivityTypeChange('programme')}
                  />
                  <span>📖 Programme officiel</span>
                </label>
                <label>
                  <input
                    type="radio"
                    name="activityType"
                    value="libre"
                    checked={activityType === 'libre'}
                    onChange={() => handleActivityTypeChange('libre')}
                  />
                  <span>🎨 Activité libre</span>
                </label>
              </div>

              {activityType === 'programme' ? (
                <>
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="categorieFilter">Catégorie</label>
                      <select
                        id="categorieFilter"
                        className="form-control"
                        value={selectedCategorieId}
                        onChange={(e) => setSelectedCategorieId(e.target.value ? Number(e.target.value) : '')}
                      >
                        <option value="">Toutes les catégories</option>
                        {categories.map(cat => (
                          <option key={cat.id} value={cat.id}>
                            {cat.nom}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div className="form-group">
                      <label htmlFor="classeFilter">Classe</label>
                      <select
                        id="classeFilter"
                        className="form-control"
                        value={selectedClasseId}
                        onChange={(e) => setSelectedClasseId(e.target.value ? Number(e.target.value) : '')}
                      >
                        <option value="">Toutes les classes</option>
                        {classes.map(cls => (
                          <option key={cls.id} value={cls.id}>
                            {cls.nom}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>
                  <div className="form-group">
                    <label htmlFor="programmeId">Programme *</label>
                    <select
                      id="programmeId"
                      className="form-control"
                      value={addFormData.programmeId || ''}
                      onChange={(e) => setAddFormData({ ...addFormData, programmeId: Number(e.target.value) })}
                      required
                    >
                      <option value="">Sélectionner un programme</option>
                      {programmes.map(prog => (
                        <option key={prog.id} value={prog.id}>
                          {prog.nom} - {prog.categorieNom} ({prog.classeNom})
                        </option>
                      ))}
                    </select>
                    {programmes.length === 0 && (
                      <small className="text-muted">
                        Aucun programme trouvé avec ces filtres
                      </small>
                    )}
                  </div>
                </>
              ) : (
                <div className="form-group">
                  <label htmlFor="description">Description de l'activité *</label>
                  <textarea
                    id="description"
                    className="form-control"
                    rows={3}
                    value={addFormData.description}
                    onChange={(e) => setAddFormData({ ...addFormData, description: e.target.value })}
                    placeholder="Ex: Atelier de bricolage pour la fête des pères"
                    required={activityType === 'libre'}
                  />
                </div>
              )}

              <div className="form-group">
                <label>Instructeurs * (au moins 1)</label>
                
                {/* Auto-complétion */}
                <div className="autocomplete-container">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Rechercher un instructeur..."
                    value={searchQuery}
                    onChange={(e) => handleSearchChange(e.target.value)}
                    autoComplete="off"
                  />
                  {suggestions.length > 0 && (
                    <ul className="suggestions-list">
                      {suggestions.map(instr => (
                        <li
                          key={instr.id}
                          onClick={() => handleSelectInstructeur(instr)}
                          className="suggestion-item"
                        >
                          {instr.nomComplet}
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
                
                {/* Liste des instructeurs sélectionnés */}
                {selectedInstructeurs.length > 0 && (
                  <div className="selected-instructeurs">
                    <label className="text-muted">Instructeurs sélectionnés:</label>
                    <div className="instructeurs-chips">
                      {selectedInstructeurs.map(instr => (
                        <span key={instr.id} className="instructeur-chip">
                          {instr.nomComplet}
                          <button
                            type="button"
                            onClick={() => handleRemoveInstructeur(instr.id)}
                            className="chip-remove"
                            title="Retirer"
                          >
                            ×
                          </button>
                        </span>
                      ))}
                    </div>
                  </div>
                )}
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowAddModal(false)}>
                  Annuler
                </button>
                <button type="submit" className="btn btn-primary">
                  Ajouter
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Modification */}
      {showEditModal && editingDetail && (
        <div className="modal-overlay" onClick={() => setShowEditModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Modifier les instructeurs</h2>
              <button className="close-btn" onClick={() => setShowEditModal(false)}>×</button>
            </div>
            <form onSubmit={handleSubmitEdit}>
              <div className="form-group">
                <label>
                  {editingDetail.programmeId ? editingDetail.programmeName : editingDetail.description}
                </label>
                
                {/* Auto-complétion */}
                <div className="autocomplete-container">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Rechercher un instructeur..."
                    value={searchQuery}
                    onChange={(e) => handleEditSearchChange(e.target.value)}
                    autoComplete="off"
                  />
                  {suggestions.length > 0 && (
                    <ul className="suggestions-list">
                      {suggestions.map(instr => (
                        <li
                          key={instr.id}
                          onClick={() => handleEditSelectInstructeur(instr)}
                          className="suggestion-item"
                        >
                          {instr.nomComplet}
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
                
                {/* Liste des instructeurs sélectionnés */}
                {editSelectedInstructeurs.length > 0 && (
                  <div className="selected-instructeurs">
                    <label className="text-muted">Instructeurs sélectionnés:</label>
                    <div className="instructeurs-chips">
                      {editSelectedInstructeurs.map(instr => (
                        <span key={instr.id} className="instructeur-chip">
                          {instr.nomComplet}
                          <button
                            type="button"
                            onClick={() => handleEditRemoveInstructeur(instr.id)}
                            className="chip-remove"
                            title="Retirer"
                          >
                            ×
                          </button>
                        </span>
                      ))}
                    </div>
                  </div>
                )}
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowEditModal(false)}>
                  Annuler
                </button>
                <button type="submit" className="btn btn-primary">
                  Enregistrer
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal de gestion de présence */}
      {showPresenceModal && cp && (
        <CPPresenceModal
          classeProgressiveId={Number(cpId)}
          cpDate={cp.dateCp}
          onClose={() => setShowPresenceModal(false)}
          onSuccess={() => {
            setSuccess('Présence enregistrée avec succès');
            setTimeout(() => setSuccess(null), 3000);
          }}
        />
      )}
    </div>
  );
}
