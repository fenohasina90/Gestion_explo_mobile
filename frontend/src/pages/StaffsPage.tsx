import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import staffService from '../services/staff.service';
import instructeurService from '../services/instructeur.service';
import utilisateurService from '../services/utilisateur.service';
import anneeExerciceService from '../services/annee-exercice.service';
import type {
  Staff,
  InstructeurSuggestion,
  CreateInstructeurRequest,
  CreateStaffRequest,
  UpdateStaffRequest,
  Role,
  AnneeExercice,
} from '../types';
import './StaffsPage.css';

export function StaffsPage() {
  const { user: currentUser } = useAuth();
  const [staffs, setStaffs] = useState<Staff[]>([]);
  const [roles, setRoles] = useState<Role[]>([]);
  const [anneesExercice, setAnneesExercice] = useState<AnneeExercice[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [selectedStaff, setSelectedStaff] = useState<Staff | null>(null);

  // États pour les filtres
  const [filterAnneeId, setFilterAnneeId] = useState<number | null>(null);
  const [filterRoleId, setFilterRoleId] = useState<number | null>(null);
  const [filterGenre, setFilterGenre] = useState<string>('');
  const [filterEstChefGuide, setFilterEstChefGuide] = useState<string>(''); // '' | 'true' | 'false'

  // États pour l'auto-complétion
  const [searchQuery, setSearchQuery] = useState('');
  const [suggestions, setSuggestions] = useState<InstructeurSuggestion[]>([]);
  const [selectedInstructeur, setSelectedInstructeur] = useState<InstructeurSuggestion | null>(null);
  const [showInstructeurForm, setShowInstructeurForm] = useState(false);

  // États pour le formulaire de staff
  const [staffFormData, setStaffFormData] = useState<CreateStaffRequest>({
    instructeurId: 0,
    roleId: 0,
    anneeExerciceId: 0,
  });

  // États pour le formulaire d'instructeur
  const [instructeurFormData, setInstructeurFormData] = useState<CreateInstructeurRequest>({
    nom: '',
    prenom: '',
    genre: 'HOMME',
    totem: '',
    telephone: '',
    estChefGuide: false,
  });

  useEffect(() => {
    loadData();
  }, []);

  useEffect(() => {
    // Filtrer les staffs localement
    filterStaffs();
  }, [filterAnneeId, filterRoleId, filterGenre, filterEstChefGuide]);

  useEffect(() => {
    // Auto-complétion avec debounce
    const timer = setTimeout(() => {
      if (searchQuery.length >= 1) {
        searchInstructeurs();
      } else {
        setSuggestions([]);
        setShowInstructeurForm(false);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [searchQuery]);

  // Fonction helper pour obtenir la classe CSS du badge selon le rôle
  const getRoleBadgeClass = (roleName: string): string => {
    const roleClasses: Record<string, string> = {
      'Directeur': 'badge-danger',
      'Co_Directeur': 'badge-warning',
      'Secrétaire': 'badge-primary',
      'Instructeur': 'badge-secondary'
    };
    return roleClasses[roleName] || 'badge-secondary';
  };

  const loadData = async () => {
    try {
      setLoading(true);
      const [staffsData, rolesData, anneesData] = await Promise.all([
        staffService.getAllStaffs(),
        utilisateurService.getAllRoles(),
        anneeExerciceService.getAllAnneesExercice(),
      ]);
      setStaffs(staffsData);
      setRoles(rolesData);
      setAnneesExercice(anneesData);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement des données');
    } finally {
      setLoading(false);
    }
  };

  const searchInstructeurs = async () => {
    try {
      const results = await instructeurService.searchInstructeurs(searchQuery);
      
      // Filtrer les instructeurs déjà assignés comme staff actifs pour l'année sélectionnée
      const anneeExerciceId = staffFormData.anneeExerciceId;
      const assignedInstructeurIds = staffs
        .filter(staff => 
          staff.anneeExerciceId === anneeExerciceId && 
          staff.etat !== 11 // Exclure seulement les staffs actifs
        )
        .map(staff => staff.instructeurId);
      
      const filteredResults = results.filter(
        instructeur => !assignedInstructeurIds.includes(instructeur.id)
      );
      
      setSuggestions(filteredResults);
      // Afficher le formulaire automatiquement si aucun résultat
      setShowInstructeurForm(filteredResults.length === 0);
    } catch (err: any) {
      console.error('Erreur lors de la recherche:', err);
      setSuggestions([]);
      setShowInstructeurForm(true);
    }
  };

  const filterStaffs = () => {
    // La filtration se fait côté serveur, on recharge les données
    if (filterAnneeId || filterRoleId || filterEstChefGuide) {
      const estChefGuideParam = filterEstChefGuide === 'true' ? true : filterEstChefGuide === 'false' ? false : undefined;
      staffService
        .getStaffsWithFilters(filterAnneeId || undefined, filterRoleId || undefined, estChefGuideParam)
        .then(setStaffs)
        .catch((err) => console.error('Erreur filtrage:', err));
    } else {
      loadData();
    }
  };

  const handleCreate = () => {
    setEditMode(false);
    setSelectedStaff(null);
    setSearchQuery('');
    setSuggestions([]);
    setSelectedInstructeur(null);
    setShowInstructeurForm(false);
    
    // Définir automatiquement l'année d'exercice de l'utilisateur connecté
    let userAnneeId = anneesExercice[0]?.id || 0;
    if (currentUser?.anneeExercice) {
      const userAnnee = anneesExercice.find(
        annee => annee.annee === currentUser.anneeExercice
      );
      if (userAnnee) {
        userAnneeId = userAnnee.id;
      }
    }
    
    setStaffFormData({
      instructeurId: 0,
      roleId: roles[0]?.id || 0,
      anneeExerciceId: userAnneeId,
    });
    setInstructeurFormData({
      nom: '',
      prenom: '',
      genre: 'HOMME',
      totem: '',
      telephone: '',
      estChefGuide: false,
    });
    setShowModal(true);
  };

  const handleEdit = (staff: Staff) => {
    setEditMode(true);
    setSelectedStaff(staff);
    setStaffFormData({
      instructeurId: staff.instructeurId,
      roleId: staff.roleId,
      anneeExerciceId: staff.anneeExerciceId,
    });
    // Pré-remplir l'instructeur sélectionné
    setSelectedInstructeur({
      id: staff.instructeurId,
      nom: staff.instructeurNom,
      prenom: staff.instructeurPrenom,
      nomComplet: `${staff.instructeurNom} ${staff.instructeurPrenom}`,
    });
    // Pré-remplir les données de l'instructeur pour l'édition
    setInstructeurFormData({
      nom: staff.instructeurNom,
      prenom: staff.instructeurPrenom,
      genre: staff.instructeurGenre,
      totem: staff.instructeurTotem || '',
      telephone: staff.instructeurTelephone || '',
      estChefGuide: staff.instructeurEstChefGuide,
    });
    setSearchQuery(`${staff.instructeurNom} ${staff.instructeurPrenom}`);
    setShowModal(true);
  };

  const handleSelectInstructeur = (instructeur: InstructeurSuggestion) => {
    setSelectedInstructeur(instructeur);
    setSearchQuery(instructeur.nomComplet);
    setSuggestions([]);
    setShowInstructeurForm(false);
    setStaffFormData({ ...staffFormData, instructeurId: instructeur.id });
  };

  const handleCreateInstructeur = async () => {
    try {
      // Valider les champs obligatoires
      if (!instructeurFormData.nom || !instructeurFormData.prenom || !instructeurFormData.genre) {
        setError('Nom, prénom et genre sont obligatoires');
        return;
      }

      const newInstructeur = await instructeurService.createInstructeur(instructeurFormData);
      setSelectedInstructeur({
        id: newInstructeur.id,
        nom: newInstructeur.nom,
        prenom: newInstructeur.prenom,
        nomComplet: `${newInstructeur.nom} ${newInstructeur.prenom}`,
      });
      setSearchQuery(`${newInstructeur.nom} ${newInstructeur.prenom}`);
      setShowInstructeurForm(false);
      setStaffFormData({ ...staffFormData, instructeurId: newInstructeur.id });
      setSuccess('Instructeur créé avec succès');
      setTimeout(() => setSuccess(null), 3000);
    } catch (err: any) {
      setError(err.message || 'Erreur lors de la création de l\'instructeur');
      setTimeout(() => setError(null), 5000);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (!selectedInstructeur && !editMode) {
        setError('Veuillez sélectionner ou créer un instructeur');
        return;
      }

      if (editMode && selectedStaff) {
        const updateData: UpdateStaffRequest = {};
        if (staffFormData.roleId) {
          updateData.roleId = staffFormData.roleId;
        }
        // Ajouter les informations de l'instructeur
        if (instructeurFormData.nom) updateData.nom = instructeurFormData.nom;
        if (instructeurFormData.prenom) updateData.prenom = instructeurFormData.prenom;
        if (instructeurFormData.genre) updateData.genre = instructeurFormData.genre;
        updateData.totem = instructeurFormData.totem;
        updateData.telephone = instructeurFormData.telephone;
        updateData.estChefGuide = instructeurFormData.estChefGuide;
        
        await staffService.updateStaff(selectedStaff.id, updateData);
        setSuccess('Staff modifié avec succès');
      } else {
        await staffService.createStaff(staffFormData);
        setSuccess('Staff créé avec succès');
      }

      setTimeout(() => setSuccess(null), 3000);
      setShowModal(false);
      loadData();
    } catch (err: any) {
      setError(err.message || 'Erreur lors de l\'enregistrement');
      setTimeout(() => setError(null), 5000);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer ce staff ?')) {
      return;
    }

    try {
      await staffService.deleteStaff(id);
      setSuccess('Staff supprimé avec succès');
      setTimeout(() => setSuccess(null), 3000);
      loadData();
    } catch (err: any) {
      setError(err.message || 'Erreur lors de la suppression');
      setTimeout(() => setError(null), 5000);
    }
  };

  const isDirecteur = currentUser?.role === 'Directeur';
  const isCoDirecteur = currentUser?.role === 'Co_Directeur';
  const canCreate = isDirecteur;
  const canEdit = isDirecteur || isCoDirecteur;
  const canDelete = isDirecteur;

  // Filtrage local par genre (puisque genre est dans instructeur)
  const filteredStaffs = staffs.filter((staff) => {
    if (filterGenre && staff.instructeurGenre !== filterGenre) {
      return false;
    }
    return true;
  });

  if (loading) {
    return <div className="loading">Chargement...</div>;
  }

  return (
    <div className="staffs-page">
      <div className="page-header">
        <h1>Gestion des Staffs</h1>
        {canCreate && (
          <button onClick={handleCreate} className="btn-primary">
            + Nouveau Staff
          </button>
        )}
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      {/* Filtres */}
      <div className="filters">
        <div className="filter-group">
          <label>Année d'exercice:</label>
          <select
            value={filterAnneeId || ''}
            onChange={(e) => setFilterAnneeId(e.target.value ? Number(e.target.value) : null)}
          >
            <option value="">Toutes</option>
            {anneesExercice.map((annee) => (
              <option key={annee.id} value={annee.id}>
                {new Date(annee.annee).getFullYear()}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label>Genre:</label>
          <select value={filterGenre} onChange={(e) => setFilterGenre(e.target.value)}>
            <option value="">Tous</option>
            <option value="HOMME">Homme</option>
            <option value="FEMME">Femme</option>
          </select>
        </div>

        <div className="filter-group">
          <label>Rôle:</label>
          <select
            value={filterRoleId || ''}
            onChange={(e) => setFilterRoleId(e.target.value ? Number(e.target.value) : null)}
          >
            <option value="">Tous</option>
            {roles.map((role) => (
              <option key={role.id} value={role.id}>
                {role.roleName}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label>Statut:</label>
          <select
            value={filterEstChefGuide}
            onChange={(e) => setFilterEstChefGuide(e.target.value)}
          >
            <option value="">Tous</option>
            <option value="true">Chef Guide</option>
            <option value="false">Aspirant</option>
          </select>
        </div>
      </div>

      {/* Liste des staffs */}
      <div className="staffs-table-container">
        <table className="staffs-table">
          <thead>
            <tr>
              <th>Nom</th>
              <th>Prénom</th>
              <th>Genre</th>
              <th>Totem</th>
              <th>Rôle</th>
              <th>Année</th>
              <th>Chef Guide</th>
              <th>Contact</th>
              {(canEdit || canDelete) && <th>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {filteredStaffs.length === 0 ? (
              <tr>
                <td colSpan={canEdit || canDelete ? 8 : 7} className="no-data">
                  Aucun staff trouvé
                </td>
              </tr>
            ) : (
              filteredStaffs.map((staff) => (
                <tr key={staff.id}>
                  <td>{staff.instructeurNom}</td>
                  <td>{staff.instructeurPrenom}</td>
                  <td>{staff.instructeurGenre}</td>
                  <td>{staff.instructeurTotem || '-'}</td>
                  <td>
                    <span className={`badge ${getRoleBadgeClass(staff.role)}`}>
                      {staff.role}
                    </span>
                  </td>
                  <td>{new Date(staff.anneeExercice).getFullYear()}</td>
                  <td>
                    {staff.instructeurEstChefGuide ? (
                      <span className="badge badge-success">Chef Guide</span>
                    ) : (
                      <span className="badge badge-secondary">Aspirant</span>
                    )}
                  </td>
                  <td>{staff.instructeurTelephone || '-'}</td>
                  {(canEdit || canDelete) && (
                    <td className="actions">
                      {canEdit && (
                        <button
                          onClick={() => handleEdit(staff)}
                          className="btn-edit"
                          title="Modifier"
                        >
                          ✏️
                        </button>
                      )}
                      {canDelete && (
                        <button
                          onClick={() => handleDelete(staff.id)}
                          className="btn-delete"
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

      {/* Modal de création/modification */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editMode ? 'Modifier un Staff' : 'Nouveau Staff'}</h2>
              <button className="modal-close" onClick={() => setShowModal(false)}>
                ×
              </button>
            </div>

            <form onSubmit={handleSubmit} className="modal-form">
              {/* Section Instructeur (seulement en création) */}
              {!editMode && (
                <div className="form-section">
                  <h3>Instructeur</h3>
                  
                  {/* L'année d'exercice est automatiquement définie selon l'utilisateur connecté */}
                  
                  {/* Auto-complétion */}
                  <div className="form-group autocomplete-container">
                    <label>Rechercher un instructeur:</label>
                    <input
                      type="text"
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                      placeholder="Nom ou prénom..."
                      disabled={!!selectedInstructeur}
                      className="autocomplete-input"
                    />
                    
                    {selectedInstructeur && (
                      <button
                        type="button"
                        onClick={() => {
                          setSelectedInstructeur(null);
                          setSearchQuery('');
                          setShowInstructeurForm(false);
                          setStaffFormData({ ...staffFormData, instructeurId: 0 });
                        }}
                        className="btn-clear"
                      >
                        Changer d'instructeur
                      </button>
                    )}

                    {/* Suggestions */}
                    {!selectedInstructeur && suggestions.length > 0 && (
                      <div className="autocomplete-suggestions">
                        {suggestions.map((suggestion) => (
                          <div
                            key={suggestion.id}
                            className="suggestion-item"
                            onClick={() => handleSelectInstructeur(suggestion)}
                          >
                            {suggestion.nomComplet}
                          </div>
                        ))}
                      </div>
                    )}

                    {/* Message aucun résultat */}
                    {!selectedInstructeur && searchQuery.length >= 2 && suggestions.length === 0 && !showInstructeurForm && (
                      <div className="no-results">
                        <p>Aucun instructeur trouvé</p>
                        <button
                          type="button"
                          onClick={() => setShowInstructeurForm(true)}
                          className="btn-create-new"
                        >
                          + Créer un nouvel instructeur
                        </button>
                      </div>
                    )}
                  </div>

                  {/* Formulaire de création d'instructeur */}
                  {showInstructeurForm && !selectedInstructeur && (
                    <div className="instructeur-form">
                      <h4>Nouvel instructeur</h4>
                      <div className="form-row">
                        <div className="form-group">
                          <label>Nom *:</label>
                          <input
                            type="text"
                            value={instructeurFormData.nom}
                            onChange={(e) =>
                              setInstructeurFormData({ ...instructeurFormData, nom: e.target.value })
                            }
                            required
                          />
                        </div>
                        <div className="form-group">
                          <label>Prénom *:</label>
                          <input
                            type="text"
                            value={instructeurFormData.prenom}
                            onChange={(e) =>
                              setInstructeurFormData({ ...instructeurFormData, prenom: e.target.value })
                            }
                            required
                          />
                        </div>
                      </div>

                      <div className="form-row">
                        <div className="form-group">
                          <label>Genre *:</label>
                          <select
                            value={instructeurFormData.genre}
                            onChange={(e) =>
                              setInstructeurFormData({ ...instructeurFormData, genre: e.target.value })
                            }
                            required
                          >
                            <option value="HOMME">Homme</option>
                            <option value="FEMME">Femme</option>
                          </select>
                        </div>
                        <div className="form-group">
                          <label>Totem:</label>
                          <input
                            type="text"
                            value={instructeurFormData.totem}
                            onChange={(e) =>
                              setInstructeurFormData({ ...instructeurFormData, totem: e.target.value })
                            }
                          />
                        </div>
                      </div>

                      <div className="form-row">
                        <div className="form-group">
                          <label>Téléphone:</label>
                          <input
                            type="text"
                            value={instructeurFormData.telephone}
                            onChange={(e) =>
                              setInstructeurFormData({ ...instructeurFormData, telephone: e.target.value })
                            }
                          />
                        </div>
                        <div className="form-group checkbox-group">
                          <label>
                            <input
                              type="checkbox"
                              checked={instructeurFormData.estChefGuide}
                              onChange={(e) =>
                                setInstructeurFormData({
                                  ...instructeurFormData,
                                  estChefGuide: e.target.checked,
                                })
                              }
                            />
                            Chef Guide
                          </label>
                        </div>
                      </div>

                      <div className="instructeur-form-actions">
                        <button
                          type="button"
                          onClick={() => {
                            setShowInstructeurForm(false);
                            setInstructeurFormData({
                              nom: '',
                              prenom: '',
                              genre: 'HOMME',
                              totem: '',
                              telephone: '',
                              estChefGuide: false,
                            });
                          }}
                          className="btn-cancel-instructeur"
                        >
                          Annuler
                        </button>
                        <button
                          type="button"
                          onClick={handleCreateInstructeur}
                          className="btn-create-instructeur"
                        >
                          Créer cet instructeur
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              )}

              {/* Section Staff */}
              {(selectedInstructeur || editMode) && (
                <>
                  {/* Section Instructeur (en mode édition uniquement) */}
                  {editMode && (
                    <div className="form-section">
                      <h3>Informations de l'instructeur</h3>
                      
                      <div className="form-row">
                        <div className="form-group">
                          <label>Nom *:</label>
                          <input
                            type="text"
                            value={instructeurFormData.nom}
                            onChange={(e) =>
                              setInstructeurFormData({ ...instructeurFormData, nom: e.target.value })
                            }
                            required
                          />
                        </div>
                        <div className="form-group">
                          <label>Prénom *:</label>
                          <input
                            type="text"
                            value={instructeurFormData.prenom}
                            onChange={(e) =>
                              setInstructeurFormData({ ...instructeurFormData, prenom: e.target.value })
                            }
                            required
                          />
                        </div>
                      </div>

                      <div className="form-row">
                        <div className="form-group">
                          <label>Genre *:</label>
                          <select
                            value={instructeurFormData.genre}
                            onChange={(e) =>
                              setInstructeurFormData({ ...instructeurFormData, genre: e.target.value })
                            }
                            required
                          >
                            <option value="HOMME">Homme</option>
                            <option value="FEMME">Femme</option>
                          </select>
                        </div>
                        <div className="form-group">
                          <label>Totem:</label>
                          <input
                            type="text"
                            value={instructeurFormData.totem}
                            onChange={(e) =>
                              setInstructeurFormData({ ...instructeurFormData, totem: e.target.value })
                            }
                          />
                        </div>
                      </div>

                      <div className="form-row">
                        <div className="form-group">
                          <label>Téléphone:</label>
                          <input
                            type="text"
                            value={instructeurFormData.telephone}
                            onChange={(e) =>
                              setInstructeurFormData({ ...instructeurFormData, telephone: e.target.value })
                            }
                          />
                        </div>
                        <div className="form-group checkbox-group">
                          <label>
                            <input
                              type="checkbox"
                              checked={instructeurFormData.estChefGuide}
                              onChange={(e) =>
                                setInstructeurFormData({
                                  ...instructeurFormData,
                                  estChefGuide: e.target.checked,
                                })
                              }
                            />
                            Chef Guide
                          </label>
                        </div>
                      </div>
                    </div>
                  )}

                  <div className="form-section">
                    <h3>Informations du Staff</h3>

                  <div className="form-group">
                    <label>Rôle *:</label>
                    <select
                      value={staffFormData.roleId}
                      onChange={(e) =>
                        setStaffFormData({ ...staffFormData, roleId: Number(e.target.value) })
                      }
                      required
                    >
                      <option value="">Sélectionner un rôle</option>
                      {roles.map((role) => (
                        <option key={role.id} value={role.id}>
                          {role.roleName}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
                </>
              )}

              <div className="modal-actions">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="btn-secondary"
                >
                  Annuler
                </button>
                <button
                  type="submit"
                  className="btn-primary"
                  disabled={!editMode && !selectedInstructeur}
                >
                  {editMode ? 'Modifier' : 'Créer'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
