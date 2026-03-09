import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import inscriptionService from '../services/inscription.service';
import enfantService from '../services/enfant.service';
import parentService from '../services/parent.service';
import classeService from '../services/classe.service';
import anneeExerciceService from '../services/annee-exercice.service';
import type {
  InscriptionResponse,
  EnfantResponse,
  EnfantSuggestion,
  Parent,
  ParentSuggestion,
  CreateEnfantRequest,
  CreateParentRequest,
  CreateInscriptionRequest,
  Classe,
  AnneeExercice,
} from '../types';
import './EnfantsPage.css';

type TabType = 'enfants' | 'parents';

export const EnfantsPage = () => {
  const { user: currentUser } = useAuth();
  const [activeTab, setActiveTab] = useState<TabType>('enfants');
  
  // États communs
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [classes, setClasses] = useState<Classe[]>([]);
  const [anneesExercice, setAnneesExercice] = useState<AnneeExercice[]>([]);
  
  // États pour les inscriptions (onglet Enfants)
  const [inscriptions, setInscriptions] = useState<InscriptionResponse[]>([]);
  const [filteredInscriptions, setFilteredInscriptions] = useState<InscriptionResponse[]>([]);
  const [filterAnneeId, setFilterAnneeId] = useState<number | null>(null);
  const [filterClasseId, setFilterClasseId] = useState<number | null>(null);
  const [filterGenre, setFilterGenre] = useState<string>('');
  const [filterAssurance, setFilterAssurance] = useState<string>('');
  
  // États pour les parents (onglet Parents)
  const [parents, setParents] = useState<Parent[]>([]);
  const [filteredParents, setFilteredParents] = useState<Parent[]>([]);
  const [parentEnfants, setParentEnfants] = useState<Record<number, EnfantResponse[]>>({});
  const [filterParentAnneeId, setFilterParentAnneeId] = useState<number | null>(null);
  const [filterParentClasseId, setFilterParentClasseId] = useState<number | null>(null);
  
  // États pour les modals
  const [showInscriptionModal, setShowInscriptionModal] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [selectedInscription, setSelectedInscription] = useState<InscriptionResponse | null>(null);
  
  // États pour l'auto-complétion enfant
  const [enfantSearchQuery, setEnfantSearchQuery] = useState('');
  const [enfantSuggestions, setEnfantSuggestions] = useState<EnfantSuggestion[]>([]);
  const [selectedEnfant, setSelectedEnfant] = useState<EnfantSuggestion | null>(null);
  const [showEnfantForm, setShowEnfantForm] = useState(false);
  
  // États pour l'auto-complétion parent
  const [parentSearchQuery, setParentSearchQuery] = useState('');
  const [parentSuggestions, setParentSuggestions] = useState<ParentSuggestion[]>([]);
  const [selectedParent, setSelectedParent] = useState<ParentSuggestion | null>(null);
  const [showParentForm, setShowParentForm] = useState(false);
  
  // États pour les formulaires
  const [inscriptionFormData, setInscriptionFormData] = useState<CreateInscriptionRequest>({
    enfantId: 0,
    anneeExerciceId: 0,
    classeId: 0,
    estAssurance: false,
  });
  
  const [enfantFormData, setEnfantFormData] = useState<CreateEnfantRequest>({
    nom: '',
    prenom: '',
    genre: 'GARCON',
    dateNaissance: '',
    adresse: '',
    parentId: 0,
    bapteme: '',
  });
  
  const [parentFormData, setParentFormData] = useState<CreateParentRequest>({
    nom: '',
    prenom: '',
    adresse: '',
    telephone: '',
  });
  
  useEffect(() => {
    loadData();
  }, []);
  
  useEffect(() => {
    applyFilters();
  }, [inscriptions, filterAnneeId, filterClasseId, filterGenre, filterAssurance]);
  
  useEffect(() => {
    applyParentFilters();
  }, [parents, parentEnfants, filterParentAnneeId, filterParentClasseId]);
  
  useEffect(() => {
    // Auto-complétion enfant avec debounce
    const timer = setTimeout(() => {
      if (enfantSearchQuery.length >= 1 && inscriptionFormData.anneeExerciceId) {
        searchEnfants();
      } else {
        setEnfantSuggestions([]);
        setShowEnfantForm(false);
      }
    }, 300);
    return () => clearTimeout(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [enfantSearchQuery, inscriptionFormData.anneeExerciceId]);
  
  useEffect(() => {
    // Auto-complétion parent avec debounce
    const timer = setTimeout(() => {
      if (parentSearchQuery.length >= 1) {
        searchParents();
      } else {
        setParentSuggestions([]);
        setShowParentForm(false);
      }
    }, 300);
    return () => clearTimeout(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [parentSearchQuery]);
  
  const loadData = async () => {
    try {
      setLoading(true);
      const [inscriptionsData, parentsData, classesData, anneesData] = await Promise.all([
        inscriptionService.getAllInscriptions(),
        parentService.getAllParents(),
        classeService.getAllClasses(),
        anneeExerciceService.getAllAnneesExercice(),
      ]);
      
      setInscriptions(inscriptionsData);
      setParents(parentsData);
      setClasses(classesData);
      setAnneesExercice(anneesData);
      
      // Charger les enfants de chaque parent
      const enfantsMap: Record<number, EnfantResponse[]> = {};
      await Promise.all(
        parentsData.map(async (parent) => {
          const enfants = await enfantService.getEnfantsByParentId(parent.id);
          enfantsMap[parent.id] = enfants;
        })
      );
      setParentEnfants(enfantsMap);
      
      setError(null);
    } catch (err: any) {
      console.error('Erreur lors du chargement des données:', err);
      setError(err.response?.data?.message || 'Erreur lors du chargement des données');
    } finally {
      setLoading(false);
    }
  };
  
  const applyFilters = () => {
    let filtered = [...inscriptions];
    
    if (filterAnneeId) {
      filtered = filtered.filter(i => i.anneeExerciceId === filterAnneeId);
    }
    
    if (filterClasseId) {
      filtered = filtered.filter(i => i.classeId === filterClasseId);
    }
    
    if (filterGenre) {
      filtered = filtered.filter(i => i.enfantGenre === filterGenre);
    }
    
    if (filterAssurance) {
      const hasAssurance = filterAssurance === 'OUI';
      filtered = filtered.filter(i => i.estAssurance === hasAssurance);
    }
    
    setFilteredInscriptions(filtered);
  };
  
  // Fonction pour obtenir la couleur du badge selon la classe
  const getClasseBadgeColor = (classeNom: string): string => {
    const nom = classeNom.toLowerCase();
    if (nom.includes('ami')) return 'badge-ami';
    if (nom.includes('compagnon')) return 'badge-compagnon';
    if (nom.includes('eclaireur') || nom.includes('éclaireur')) return 'badge-eclaireur';
    if (nom.includes('pionnier')) return 'badge-pionnier';
    if (nom.includes('voyageur')) return 'badge-voyageur';
    if (nom.includes('guide')) return 'badge-guide';
    return 'badge-success'; // fallback
  };
  
  const applyParentFilters = () => {
    let filtered = [...parents];
    
    // Filtrer par année d'exercice (parents ayant des enfants inscrits cette année)
    if (filterParentAnneeId) {
      filtered = filtered.filter(parent => {
        const enfants = parentEnfants[parent.id] || [];
        return enfants.some(enfant => {
          return inscriptions.some(i => 
            i.enfantId === enfant.id && i.anneeExerciceId === filterParentAnneeId
          );
        });
      });
    }
    
    // Filtrer par classe (parents ayant des enfants dans cette classe)
    if (filterParentClasseId) {
      filtered = filtered.filter(parent => {
        const enfants = parentEnfants[parent.id] || [];
        return enfants.some(enfant => {
          return inscriptions.some(i => 
            i.enfantId === enfant.id && i.classeId === filterParentClasseId
          );
        });
      });
    }
    
    setFilteredParents(filtered);
  };
  
  const handleExportPdf = async () => {
    try {
      setLoading(true);
      
      // Convertir les filtres pour l'API
      const estAssurance = filterAssurance === 'OUI' ? true : filterAssurance === 'NON' ? false : undefined;
      const genre = filterGenre || undefined;
      
      await inscriptionService.exportToPdf(
        filterAnneeId || undefined,
        filterClasseId || undefined,
        genre,
        estAssurance
      );
      
      setSuccess('Export PDF réussi !');
      setTimeout(() => setSuccess(null), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de l\'export PDF');
      setTimeout(() => setError(null), 5000);
    } finally {
      setLoading(false);
    }
  };
  
  const searchEnfants = async () => {
    if (!enfantSearchQuery || enfantSearchQuery.length < 1 || !inscriptionFormData.anneeExerciceId) {
      return;
    }
    try {
      console.log('Recherche enfants - query:', enfantSearchQuery, 'anneeId:', inscriptionFormData.anneeExerciceId);
      const results = await enfantService.searchEnfants(
        enfantSearchQuery,
        inscriptionFormData.anneeExerciceId
      );
      console.log('Résultats trouvés:', results.length, results);
      setEnfantSuggestions(results);
      setShowEnfantForm(results.length === 0);
      console.log('État enfantSuggestions après setEnfantSuggestions');
    } catch (err: any) {
      console.error('Erreur lors de la recherche d\'enfants:', err);
    }
  };
  
  const searchParents = async () => {
    if (!parentSearchQuery || parentSearchQuery.length < 1) {
      return;
    }
    try {
      console.log('Recherche parents - query:', parentSearchQuery);
      const results = await parentService.searchParents(parentSearchQuery);
      console.log('Résultats parents trouvés:', results.length);
      setParentSuggestions(results);
      setShowParentForm(results.length === 0);
    } catch (err: any) {
      console.error('Erreur lors de la recherche de parents:', err);
    }
  };
  
  const handleOpenInscriptionModal = () => {
    setShowInscriptionModal(true);
    setEditMode(false);
    setSelectedInscription(null);
    resetInscriptionForm();
  };
  
  const handleCloseInscriptionModal = () => {
    setShowInscriptionModal(false);
    resetInscriptionForm();
  };
  
  const resetInscriptionForm = () => {
    // Définir automatiquement l'année d'exercice de l'utilisateur connecté
    let userAnneeId = 0;
    
    // Essayer d'abord avec anneeExerciceId directement depuis l'utilisateur
    if (currentUser?.anneeExerciceId) {
      userAnneeId = currentUser.anneeExerciceId;
    } 
    // Sinon chercher l'année active
    else if (anneesExercice.length > 0) {
      const anneeActive = anneesExercice.find(annee => annee.estActif);
      if (anneeActive) {
        userAnneeId = anneeActive.id;
      } else {
        // Prendre la première année si aucune n'est active
        userAnneeId = anneesExercice[0].id;
      }
    }
    
    console.log('Reset inscription form - anneeExerciceId:', userAnneeId);
    
    setInscriptionFormData({
      enfantId: 0,
      anneeExerciceId: userAnneeId,
      classeId: 0,
      estAssurance: false,
    });
    setEnfantSearchQuery('');
    setSelectedEnfant(null);
    setEnfantSuggestions([]);
    setShowEnfantForm(false);
    setParentSearchQuery('');
    setSelectedParent(null);
    setParentSuggestions([]);
    setShowParentForm(false);
    setEnfantFormData({
      nom: '',
      prenom: '',
      genre: 'GARCON',
      dateNaissance: '',
      adresse: '',
      parentId: 0,
      bapteme: '',
    });
    setParentFormData({
      nom: '',
      prenom: '',
      adresse: '',
      telephone: '',
    });
  };
  
  const handleSelectEnfant = (enfant: EnfantSuggestion) => {
    setSelectedEnfant(enfant);
    setEnfantSearchQuery(enfant.nomComplet);
    setInscriptionFormData(prev => ({ ...prev, enfantId: enfant.id }));
    setEnfantSuggestions([]);
  };
  
  const handleSelectParent = (parent: ParentSuggestion) => {
    setSelectedParent(parent);
    setParentSearchQuery(parent.nomComplet);
    setEnfantFormData(prev => ({ ...prev, parentId: parent.id }));
    setParentSuggestions([]);
  };
  
  const handleCreateParent = async () => {
    try {
      const newParent = await parentService.createParent(parentFormData);
      setSelectedParent({
        id: newParent.id,
        nom: newParent.nom,
        prenom: newParent.prenom,
        telephone: newParent.telephone,
        adresse: newParent.adresse,
        nomComplet: `${newParent.nom} ${newParent.prenom}`,
      });
      setParentSearchQuery(`${newParent.nom} ${newParent.prenom}`);
      setEnfantFormData(prev => ({ ...prev, parentId: newParent.id }));
      setShowParentForm(false);
      setSuccess('Parent créé avec succès');
      setTimeout(() => setSuccess(null), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de la création du parent');
      setTimeout(() => setError(null), 5000);
    }
  };
  
  const handleCreateEnfant = async () => {
    if (!selectedParent || enfantFormData.parentId === 0) {
      setError('Veuillez sélectionner ou créer un parent');
      return;
    }
    
    try {
      const newEnfant = await enfantService.createEnfant(
        enfantFormData,
        inscriptionFormData.anneeExerciceId
      );
      setSelectedEnfant({
        id: newEnfant.id,
        nom: newEnfant.nom,
        prenom: newEnfant.prenom,
        genre: newEnfant.genre,
        dateNaissance: newEnfant.dateNaissance,
        age: newEnfant.age,
        parentNom: newEnfant.parentNom,
        parentPrenom: newEnfant.parentPrenom,
        nomComplet: `${newEnfant.nom} ${newEnfant.prenom}`,
        parentNomComplet: `${newEnfant.parentNom} ${newEnfant.parentPrenom}`,
      });
      setEnfantSearchQuery(`${newEnfant.nom} ${newEnfant.prenom}`);
      setInscriptionFormData(prev => ({ ...prev, enfantId: newEnfant.id }));
      setShowEnfantForm(false);
      setSuccess('Enfant créé avec succès');
      setTimeout(() => setSuccess(null), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de la création de l\'enfant');
      setTimeout(() => setError(null), 5000);
    }
  };
  
  const handleSubmitInscription = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Vérifier que l'utilisateur peut créer pour cette année
    if (currentUser?.role === 'Directeur' || currentUser?.role === 'Co_Directeur') {
      const selectedAnnee = anneesExercice.find(a => a.id === inscriptionFormData.anneeExerciceId);
      if (selectedAnnee && selectedAnnee.annee !== currentUser.anneeExercice) {
        setError('Vous ne pouvez créer des inscriptions que pour votre année d\'exercice');
        return;
      }
    }
    
    try {
      if (editMode && selectedInscription) {
        // Mode édition - mise à jour assurance
        await inscriptionService.updateAssurance(
          selectedInscription.id,
          inscriptionFormData.estAssurance || false
        );
        setSuccess('Assurance mise à jour avec succès');
      } else {
        // Mode création
        await inscriptionService.createInscription(inscriptionFormData);
        setSuccess('Inscription créée avec succès');
      }
      
      handleCloseInscriptionModal();
      loadData();
      setTimeout(() => setSuccess(null), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de l\'enregistrement');
      setTimeout(() => setError(null), 5000);
    }
  };
  
  const handleEditInscription = (inscription: InscriptionResponse) => {
    setEditMode(true);
    setSelectedInscription(inscription);
    setInscriptionFormData({
      enfantId: inscription.enfantId,
      anneeExerciceId: inscription.anneeExerciceId,
      classeId: inscription.classeId,
      estAssurance: inscription.estAssurance,
    });
    setEnfantSearchQuery(`${inscription.enfantNom} ${inscription.enfantPrenom}`);
    setShowInscriptionModal(true);
  };
  
  const handleDeleteInscription = async (id: number) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer cette inscription ?')) {
      return;
    }
    
    try {
      await inscriptionService.deleteInscription(id);
      setSuccess('Inscription supprimée avec succès');
      loadData();
      setTimeout(() => setSuccess(null), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de la suppression');
      setTimeout(() => setError(null), 5000);
    }
  };
  
  const handleDeleteParent = async (id: number) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer ce parent ?')) {
      return;
    }
    
    try {
      await parentService.deleteParent(id);
      setSuccess('Parent supprimé avec succès');
      loadData();
      setTimeout(() => setSuccess(null), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de la suppression');
      setTimeout(() => setError(null), 5000);
    }
  };
  
  const canModify = () => {
    return currentUser?.role === 'Directeur' || currentUser?.role === 'Co_Directeur';
  };
  
  const canDelete = () => {
    return currentUser?.role === 'Directeur';
  };
  
  const formatAnnee = (annee: string) => {
    // Extrait l'année de début (ex: "2023-2024" => "2023")
    return annee.split('-')[0];
  };
  
  const parseAnnee = (annee: string): number => {
    // Extrait l'année de début comme nombre (ex: "2023-2024" => 2023)
    return parseInt(annee.split('-')[0], 10);
  };
  
  if (loading) {
    return (
      <div className="page-container">
        <div className="loading">Chargement...</div>
      </div>
    );
  }
  
  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Gestion des Enfants & Parents</h1>
        {canModify() && (
          <button className="primary-btn" onClick={handleOpenInscriptionModal}>
            ➕ Nouvelle inscription
          </button>
        )}
      </div>
      
      {error && (
        <div className="alert alert-error">
          <span>⚠️ {error}</span>
          <button onClick={() => setError(null)}>✕</button>
        </div>
      )}
      
      {success && (
        <div className="alert alert-success">
          <span>✓ {success}</span>
          <button onClick={() => setSuccess(null)}>✕</button>
        </div>
      )}
      
      {/* Tabs avec slide transition */}
      <div className="tabs-container">
        <div className="tabs-header">
          <button
            className={`tab-btn ${activeTab === 'enfants' ? 'active' : ''}`}
            onClick={() => setActiveTab('enfants')}
          >
            👶 Enfants inscrits
          </button>
          <button
            className={`tab-btn ${activeTab === 'parents' ? 'active' : ''}`}
            onClick={() => setActiveTab('parents')}
          >
            👨‍👩‍👧‍👦 Parents
          </button>
        </div>
        
        <div className={`tabs-content ${activeTab}`}>
          {/* Onglet Enfants */}
          {activeTab === 'enfants' && (
            <div className="tab-pane enfants-tab">
              <div className="filters-section">
                <h3>Filtres</h3>
                <div className="filters-row">
                  <div className="filter-item">
                    <label>Année d'exercice</label>
                    <select
                      value={filterAnneeId || ''}
                      onChange={(e) => setFilterAnneeId(e.target.value ? Number(e.target.value) : null)}
                    >
                      <option value="">Toutes</option>
                      {anneesExercice.map(annee => (
                        <option key={annee.id} value={annee.id}>
                          {formatAnnee(annee.annee)}
                        </option>
                      ))}
                    </select>
                  </div>
                  
                  <div className="filter-item">
                    <label>Classe</label>
                    <select
                      value={filterClasseId || ''}
                      onChange={(e) => setFilterClasseId(e.target.value ? Number(e.target.value) : null)}
                    >
                      <option value="">Toutes</option>
                      {classes.map(classe => (
                        <option key={classe.id} value={classe.id}>
                          {classe.nom}
                        </option>
                      ))}
                    </select>
                  </div>
                  
                  <div className="filter-item">
                    <label>Genre</label>
                    <select
                      value={filterGenre}
                      onChange={(e) => setFilterGenre(e.target.value)}
                    >
                      <option value="">Tous</option>
                      <option value="GARCON">Garçon</option>
                      <option value="FILLE">Fille</option>
                    </select>
                  </div>
                  
                  <div className="filter-item">
                    <label>Assurance</label>
                    <select
                      value={filterAssurance}
                      onChange={(e) => setFilterAssurance(e.target.value)}
                    >
                      <option value="">Toutes</option>
                      <option value="OUI">Oui</option>
                      <option value="NON">Non</option>
                    </select>
                  </div>
                  
                  <button
                    className="secondary-btn"
                    onClick={() => {
                      setFilterAnneeId(null);
                      setFilterClasseId(null);
                      setFilterGenre('');
                      setFilterAssurance('');
                    }}
                  >
                    Réinitialiser
                  </button>
                  
                  <button
                    className="primary-btn"
                    onClick={handleExportPdf}
                    disabled={loading || filteredInscriptions.length === 0}
                    title="Exporter la liste en PDF"
                  >
                    📄 Exporter PDF
                  </button>
                </div>
              </div>
              
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Nom & Prénom</th>
                      {/* <th>Genre</th> */}
                      {/* <th>Âge</th> */}
                      <th>Classe</th>
                      <th>Parent</th>
                      <th>Téléphone</th>
                      <th>Assurance</th>
                      <th>Année</th>
                      {(canModify() || canDelete()) && <th>Actions</th>}
                    </tr>
                  </thead>
                  <tbody>
                    {filteredInscriptions.length === 0 ? (
                      <tr>
                        <td colSpan={9} className="empty-state">
                          Aucune inscription trouvée
                        </td>
                      </tr>
                    ) : (
                      filteredInscriptions.map(inscription => (
                        <tr key={inscription.id}>
                          <td>
                            <strong>{inscription.enfantNom} {inscription.enfantPrenom}</strong>
                          </td>
                          {/* <td>
                            <span className={`badge badge-${inscription.enfantGenre === 'GARCON' ? 'primary' : 'danger'}`}>
                              {inscription.enfantGenre === 'GARCON' ? 'Garçon' : 'Fille'}
                            </span>
                          </td> */}
                          {/* <td>{inscription.enfantAge} ans</td> */}
                          <td>
                            <span className={`badge ${getClasseBadgeColor(inscription.classeNom)}`}>
                              {inscription.classeNom}
                            </span>
                          </td>
                          <td>{inscription.parentNom} {inscription.parentPrenom}</td>
                          <td>{inscription.parentTelephone || '-'}</td>
                          <td>
                            {inscription.estAssurance ? (
                              <span className="badge badge-success">Oui</span>
                            ) : (
                              <span className="badge badge-secondary">Non</span>
                            )}
                          </td>
                          <td>{formatAnnee(inscription.anneeExercice)}</td>
                          {(canModify() || canDelete()) && (
                            <td className="actions-cell">
                              {canModify() && (
                                <button
                                  className="icon-btn edit-btn"
                                  onClick={() => handleEditInscription(inscription)}
                                  title="Modifier l'assurance"
                                >
                                  ✏️
                                </button>
                              )}
                              {canDelete() && (
                                <button
                                  className="icon-btn delete-btn"
                                  onClick={() => handleDeleteInscription(inscription.id)}
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
            </div>
          )}
          
          {/* Onglet Parents */}
          {activeTab === 'parents' && (
            <div className="tab-pane parents-tab">
              <div className="filters-section">
                <h3>Filtres</h3>
                <div className="filters-row">
                  <div className="filter-item">
                    <label>Année d'exercice</label>
                    <select
                      value={filterParentAnneeId || ''}
                      onChange={(e) => setFilterParentAnneeId(e.target.value ? Number(e.target.value) : null)}
                    >
                      <option value="">Toutes</option>
                      {anneesExercice.map(annee => (
                        <option key={annee.id} value={annee.id}>
                          {formatAnnee(annee.annee)}
                        </option>
                      ))}
                    </select>
                  </div>
                  
                  <div className="filter-item">
                    <label>Classe de l'enfant</label>
                    <select
                      value={filterParentClasseId || ''}
                      onChange={(e) => setFilterParentClasseId(e.target.value ? Number(e.target.value) : null)}
                    >
                      <option value="">Toutes</option>
                      {classes.map(classe => (
                        <option key={classe.id} value={classe.id}>
                          {classe.nom}
                        </option>
                      ))}
                    </select>
                  </div>
                  
                  <button
                    className="secondary-btn"
                    onClick={() => {
                      setFilterParentAnneeId(null);
                      setFilterParentClasseId(null);
                    }}
                  >
                    Réinitialiser
                  </button>
                </div>
              </div>
              
              <div className="parents-list">
                {filteredParents.length === 0 ? (
                  <div className="empty-state">Aucun parent trouvé</div>
                ) : (
                  filteredParents.map(parent => (
                    <div key={parent.id} className="parent-card">
                      <div className="parent-header">
                        <div className="parent-info">
                          <h3>{parent.nom} {parent.prenom}</h3>
                          <div className="parent-details">
                            {parent.telephone && (
                              <span className="detail-item">📞 {parent.telephone}</span>
                            )}
                            {parent.adresse && (
                              <span className="detail-item">📍 {parent.adresse}</span>
                            )}
                          </div>
                        </div>
                        {canDelete() && (
                          <button
                            className="icon-btn delete-btn"
                            onClick={() => handleDeleteParent(parent.id)}
                            title="Supprimer"
                          >
                            🗑️
                          </button>
                        )}
                      </div>
                      
                      <div className="enfants-list">
                        <h4>Enfants:</h4>
                        {parentEnfants[parent.id]?.length > 0 ? (
                          <ul>
                            {parentEnfants[parent.id].map(enfant => {
                              const inscription = inscriptions.find(i => i.enfantId === enfant.id);
                              return (
                                <li key={enfant.id}>
                                  <span className="enfant-name">
                                    {/* {enfant.nom}  */}
                                    {enfant.prenom}
                                  </span>
                                  <span className={`badge badge-${enfant.genre === 'GARCON' ? 'primary' : 'danger'}`}>
                                    {enfant.genre === 'GARCON' ? 'Garçon' : 'Fille'}
                                  </span>
                                  <span className="enfant-age">{enfant.age} ans</span>
                                  {inscription && (
                                    <span className={`badge ${getClasseBadgeColor(inscription.classeNom)}`}>
                                      {inscription.classeNom}
                                    </span>
                                  )}
                                </li>
                              );
                            })}
                          </ul>
                        ) : (
                          <p className="no-enfants">Aucun enfant</p>
                        )}
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>
      </div>
      
      {/* Modal de création d'inscription */}
      {showInscriptionModal && (
        <div className="modal-overlay" onClick={handleCloseInscriptionModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editMode ? 'Modifier l\'assurance' : 'Nouvelle inscription'}</h2>
              <button className="close-btn" onClick={handleCloseInscriptionModal}>✕</button>
            </div>
            
            <form onSubmit={handleSubmitInscription}>
              <div className="modal-body">
                {!editMode && (
                  <>
                    {/* L'année d'exercice est automatiquement celle de l'utilisateur connecté */}
                    <div className="form-group" style={{ padding: '10px', backgroundColor: '#e3f2fd', border: '1px solid #2196f3', borderRadius: '4px', marginBottom: '15px' }}>
                      <small style={{ color: '#1976d2' }}>
                        📅 Année d'exercice : <strong>{anneesExercice.find(a => a.id === inscriptionFormData.anneeExerciceId)?.annee.split('-')[0] || 'Non définie'}</strong>
                      </small>
                    </div>
                    
                    {/* Auto-complétion enfant */}
                    {inscriptionFormData.anneeExerciceId > 0 && (
                      <>
                        <div className="form-group">
                          <label>Rechercher un enfant (10-15 ans) *</label>
                          <input
                            type="text"
                            value={enfantSearchQuery}
                            onChange={(e) => setEnfantSearchQuery(e.target.value)}
                            placeholder="Nom ou prénom..."
                            disabled={!!selectedEnfant}
                          />
                          {selectedEnfant && (
                            <button
                              type="button"
                              className="clear-btn"
                              onClick={() => {
                                setSelectedEnfant(null);
                                setEnfantSearchQuery('');
                                setInscriptionFormData(prev => ({ ...prev, enfantId: 0 }));
                              }}
                            >
                              Changer
                            </button>
                          )}
                        </div>
                        
                        {/* Logs debug */}
                        {console.log('RENDER - enfantSuggestions.length:', enfantSuggestions.length, 'selectedEnfant:', selectedEnfant, 'condition:', enfantSuggestions.length > 0 && !selectedEnfant)}
                        
                        {enfantSuggestions.length > 0 && !selectedEnfant && (
                          <div 
                            className="suggestions-list" 
                            style={{ 
                              border: '2px solid red', 
                              padding: '10px',
                              backgroundColor: 'white',
                              position: 'relative',
                              zIndex: 9999,
                              marginTop: '5px',
                              boxShadow: '0 4px 12px rgba(0,0,0,0.3)'
                            }}
                          >
                            <div style={{ color: 'green', fontWeight: 'bold', marginBottom: '10px' }}>
                              ✅ SUGGESTIONS VISIBLE - {enfantSuggestions.length} résultats
                            </div>
                            {enfantSuggestions.map(enfant => (
                              <div
                                key={enfant.id}
                                className="suggestion-item"
                                onClick={() => handleSelectEnfant(enfant)}
                              >
                                <div>
                                  <strong>{enfant.nomComplet}</strong>
                                  <span className={`badge badge-${enfant.genre === 'GARCON' ? 'primary' : 'danger'}`}>
                                    {enfant.genre}
                                  </span>
                                  <span className="age-badge">{enfant.age} ans</span>
                                </div>
                                <div className="parent-info-small">
                                  Parent: {enfant.parentNomComplet}
                                </div>
                              </div>
                            ))}
                          </div>
                        )}
                        
                        {/* Formulaire création enfant */}
                        {showEnfantForm && (
                          <div className="nested-form">
                            <h4>Créer un nouvel enfant</h4>
                            
                            {/* Auto-complétion parent */}
                            <div className="form-group">
                              <label>Rechercher un parent *</label>
                              <input
                                type="text"
                                value={parentSearchQuery}
                                onChange={(e) => setParentSearchQuery(e.target.value)}
                                placeholder="Nom ou prénom du parent..."
                                disabled={!!selectedParent}
                              />
                              {selectedParent && (
                                <button
                                  type="button"
                                  className="clear-btn"
                                  onClick={() => {
                                    setSelectedParent(null);
                                    setParentSearchQuery('');
                                    setEnfantFormData(prev => ({ ...prev, parentId: 0 }));
                                  }}
                                >
                                  Changer
                                </button>
                              )}
                            </div>
                            
                            {parentSuggestions.length > 0 && !selectedParent && (
                              <div className="suggestions-list">
                                {parentSuggestions.map(parent => (
                                  <div
                                    key={parent.id}
                                    className="suggestion-item"
                                    onClick={() => handleSelectParent(parent)}
                                  >
                                    <strong>{parent.nomComplet}</strong>
                                    <div className="small-text">
                                      {parent.telephone} | {parent.adresse}
                                    </div>
                                  </div>
                                ))}
                              </div>
                            )}
                            
                            {/* Formulaire création parent */}
                            {showParentForm && (
                              <div className="nested-form">
                                <h5>Créer un nouveau parent</h5>
                                <div className="form-row">
                                  <div className="form-group">
                                    <label>Nom *</label>
                                    <input
                                      type="text"
                                      value={parentFormData.nom}
                                      onChange={(e) => setParentFormData(prev => ({
                                        ...prev,
                                        nom: e.target.value
                                      }))}
                                      required
                                    />
                                  </div>
                                  <div className="form-group">
                                    <label>Prénom *</label>
                                    <input
                                      type="text"
                                      value={parentFormData.prenom}
                                      onChange={(e) => setParentFormData(prev => ({
                                        ...prev,
                                        prenom: e.target.value
                                      }))}
                                      required
                                    />
                                  </div>
                                </div>
                                <div className="form-group">
                                  <label>Téléphone</label>
                                  <input
                                    type="tel"
                                    value={parentFormData.telephone}
                                    onChange={(e) => setParentFormData(prev => ({
                                      ...prev,
                                      telephone: e.target.value
                                    }))}
                                  />
                                </div>
                                <div className="form-group">
                                  <label>Adresse</label>
                                  <input
                                    type="text"
                                    value={parentFormData.adresse}
                                    onChange={(e) => setParentFormData(prev => ({
                                      ...prev,
                                      adresse: e.target.value
                                    }))}
                                  />
                                </div>
                                <button
                                  type="button"
                                  className="secondary-btn"
                                  onClick={handleCreateParent}
                                >
                                  Créer le parent
                                </button>
                              </div>
                            )}
                            
                            {/* Informations enfant */}
                            {selectedParent && (
                              <>
                                <div className="form-row">
                                  <div className="form-group">
                                    <label>Nom *</label>
                                    <input
                                      type="text"
                                      value={enfantFormData.nom}
                                      onChange={(e) => setEnfantFormData(prev => ({
                                        ...prev,
                                        nom: e.target.value
                                      }))}
                                      required
                                    />
                                  </div>
                                  <div className="form-group">
                                    <label>Prénom *</label>
                                    <input
                                      type="text"
                                      value={enfantFormData.prenom}
                                      onChange={(e) => setEnfantFormData(prev => ({
                                        ...prev,
                                        prenom: e.target.value
                                      }))}
                                      required
                                    />
                                  </div>
                                </div>
                                <div className="form-row">
                                  <div className="form-group">
                                    <label>Genre *</label>
                                    <select
                                      value={enfantFormData.genre}
                                      onChange={(e) => setEnfantFormData(prev => ({
                                        ...prev,
                                        genre: e.target.value
                                      }))}
                                      required
                                    >
                                      <option value="GARCON">Garçon</option>
                                      <option value="FILLE">Fille</option>
                                    </select>
                                  </div>
                                  <div className="form-group">
                                    <label>Date de naissance *</label>
                                    <input
                                      type="date"
                                      value={enfantFormData.dateNaissance}
                                      onChange={(e) => {
                                        setEnfantFormData(prev => ({
                                          ...prev,
                                          dateNaissance: e.target.value
                                        }));
                                      }}
                                      required
                                    />
                                    {enfantFormData.dateNaissance && inscriptionFormData.anneeExerciceId && (() => {
                                      // Calculer l'âge pour l'année d'exercice
                                      const anneeExercice = anneesExercice.find(a => a.id === inscriptionFormData.anneeExerciceId);
                                      if (!anneeExercice) return null;
                                      
                                      const anneeEx = parseAnnee(anneeExercice.annee);
                                      const anneeNaissance = new Date(enfantFormData.dateNaissance).getFullYear();
                                      const age = anneeEx - anneeNaissance;
                                      
                                      // Trouver la classe appropriée
                                      const classeAppropriee = classes.find(c => c.age === age);
                                      
                                      if (age < 10 || age > 15) {
                                        return (
                                          <div className="classe-warning">
                                            <p className="warning-message">
                                              ⚠️ Âge en {anneeEx}: {age} ans - Hors limites (10-15 ans requis)
                                            </p>
                                          </div>
                                        );
                                      }
                                      
                                      return classeAppropriee ? (
                                        <div className="classe-info">
                                          <p className="info-message">
                                            ℹ️ Âge en {anneeEx}: {age} ans → Classe <strong>{classeAppropriee.nom}</strong>
                                          </p>
                                        </div>
                                      ) : (
                                        <div className="classe-warning">
                                          <p className="warning-message">
                                            ⚠️ Âge en {anneeEx}: {age} ans - Aucune classe disponible
                                          </p>
                                        </div>
                                      );
                                    })()}
                                  </div>
                                </div>
                                <button
                                  type="button"
                                  className="secondary-btn"
                                  onClick={handleCreateEnfant}
                                >
                                  Créer l'enfant
                                </button>
                              </>
                            )}
                          </div>
                        )}
                      </>
                    )}
                    
                    {/* Sélection classe */}
                    {selectedEnfant && (
                      <>
                        <div className="form-group">
                          <label>Classe *</label>
                          {(() => {
                            // Filtrer pour trouver la classe correspondant à l'âge de l'enfant
                            const classeAppropriee = classes.find(c => c.age === selectedEnfant.age);
                            
                            // Pré-sélectionner automatiquement la classe appropriée
                            if (classeAppropriee && inscriptionFormData.classeId === 0) {
                              setTimeout(() => {
                                setInscriptionFormData(prev => ({
                                  ...prev,
                                  classeId: classeAppropriee.id
                                }));
                              }, 0);
                            }
                            
                            return (
                              <>
                                {classeAppropriee ? (
                                  <div className="classe-info">
                                    <p className="info-message">
                                      ℹ️ {selectedEnfant.prenom} a {selectedEnfant.age} ans, 
                                      la classe appropriée est <strong>{classeAppropriee.nom}</strong>
                                    </p>
                                  </div>
                                ) : (
                                  <div className="classe-warning">
                                    <p className="warning-message">
                                      ⚠️ Aucune classe disponible pour un enfant de {selectedEnfant.age} ans
                                    </p>
                                  </div>
                                )}
                                <select
                                  value={inscriptionFormData.classeId}
                                  onChange={(e) => setInscriptionFormData(prev => ({
                                    ...prev,
                                    classeId: Number(e.target.value)
                                  }))}
                                  required
                                  disabled={!classeAppropriee}
                                >
                                  <option value="">Sélectionner</option>
                                  {classes
                                    .filter(c => c.age === selectedEnfant.age)
                                    .map(classe => (
                                      <option key={classe.id} value={classe.id}>
                                        {classe.nom} ({classe.age} ans)
                                      </option>
                                    ))}
                                </select>
                              </>
                            );
                          })()}
                        </div>
                      </>
                    )}
                  </>
                )}
                
                {/* Assurance (création et modification) */}
                <div className="form-group">
                  <label className="checkbox-label">
                    <input
                      type="checkbox"
                      checked={inscriptionFormData.estAssurance}
                      onChange={(e) => setInscriptionFormData(prev => ({
                        ...prev,
                        estAssurance: e.target.checked
                      }))}
                    />
                    <span>Assurance souscrite</span>
                  </label>
                </div>
              </div>
              
              <div className="modal-footer">
                <button type="button" className="secondary-btn" onClick={handleCloseInscriptionModal}>
                  Annuler
                </button>
                <button
                  type="submit"
                  className="primary-btn"
                  disabled={!editMode && (!selectedEnfant || !inscriptionFormData.classeId || !inscriptionFormData.anneeExerciceId)}
                >
                  {editMode ? 'Mettre à jour' : 'Créer l\'inscription'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
