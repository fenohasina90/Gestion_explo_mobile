import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import utilisateurService from '../services/utilisateur.service';
import anneeExerciceService from '../services/annee-exercice.service';
import type { Utilisateur, Role, AnneeExercice, CreateUtilisateurRequest, UpdateUtilisateurRequest } from '../types';
import './UtilisateursPage.css';

export function UtilisateursPage() {
  const { user: currentUser } = useAuth();
  const [utilisateurs, setUtilisateurs] = useState<Utilisateur[]>([]);
  const [roles, setRoles] = useState<Role[]>([]);
  const [anneesExercice, setAnneesExercice] = useState<AnneeExercice[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [selectedUser, setSelectedUser] = useState<Utilisateur | null>(null);
  const [isSelfEdit, setIsSelfEdit] = useState(false);
  const [filterAnneeId, setFilterAnneeId] = useState<number | null>(null);
  const [formData, setFormData] = useState<CreateUtilisateurRequest>({
    username: '',
    password: '',
    roleId: 0,
    anneeExerciceId: 0,
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [usersData, rolesData, anneesData] = await Promise.all([
        utilisateurService.getAllUtilisateurs(),
        utilisateurService.getAllRoles(),
        anneeExerciceService.getAllAnneesExercice(),
      ]);
      setUtilisateurs(usersData);
      setRoles(rolesData);
      setAnneesExercice(anneesData);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement des données');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditMode(false);
    setSelectedUser(null);
    setIsSelfEdit(false);
    setFormData({
      username: '',
      password: '',
      roleId: roles[0]?.id || 0,
      anneeExerciceId: anneesExercice[0]?.id || 0,
    });
    setShowModal(true);
  };

  const handleEdit = (user: Utilisateur) => {
    setEditMode(true);
    setSelectedUser(user);
    const isSelf = currentUser?.id === user.id;
    setIsSelfEdit(isSelf);
    
    const role = roles.find(r => r.roleName === user.role);
    const annee = anneesExercice.find(a => a.annee === user.anneeExercice);
    setFormData({
      username: user.username,
      password: '',
      roleId: role?.id || 0,
      anneeExerciceId: annee?.id || 0,
    });
    setShowModal(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editMode && selectedUser) {
        const updateData: UpdateUtilisateurRequest = {};
        
        // Si l'utilisateur modifie son propre profil : seulement username et password
        if (isSelfEdit) {
          if (formData.username) {
            updateData.username = formData.username;
          }
          if (formData.password) {
            updateData.password = formData.password;
          }
        } 
        // Si le Directeur modifie un autre utilisateur : seulement role, active, anneeExerciceId
        else {
          if (formData.roleId) {
            updateData.roleId = formData.roleId;
          }
          if (formData.anneeExerciceId) {
            updateData.anneeExerciceId = formData.anneeExerciceId;
          }
          // Note: active est géré par le bouton toggle, pas par le formulaire
        }
        
        await utilisateurService.updateUtilisateur(selectedUser.id, updateData);
        setSuccess('Utilisateur modifié avec succès !');
      } else {
        await utilisateurService.createUtilisateur(formData);
        setSuccess('Utilisateur créé avec succès !');
      }
      setShowModal(false);
      setError(null);
      loadData();
      setTimeout(() => setSuccess(null), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de l\'enregistrement');
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?')) {
      try {
        await utilisateurService.deleteUtilisateur(id);
        setSuccess('Utilisateur supprimé avec succès !');
        setError(null);
        loadData();
        setTimeout(() => setSuccess(null), 3000);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Erreur lors de la suppression');
      }
    }
  };

  const toggleActive = async (user: Utilisateur) => {
    try {
      await utilisateurService.updateUtilisateur(user.id, { active: !user.active });
      setSuccess(`Utilisateur ${!user.active ? 'activé' : 'désactivé'} avec succès !`);
      setError(null);
      loadData();
      setTimeout(() => setSuccess(null), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erreur lors de la modification');
    }
  };

  // Filtrer les utilisateurs par année d'exercice
  const getFilteredUtilisateurs = () => {
    if (!filterAnneeId) {
      return utilisateurs;
    }
    const anneeSelectionnee = anneesExercice.find(a => a.id === filterAnneeId);
    if (!anneeSelectionnee) {
      return utilisateurs;
    }
    return utilisateurs.filter(u => u.anneeExercice === anneeSelectionnee.annee);
  };

  const filteredUtilisateurs = getFilteredUtilisateurs();

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

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div className="utilisateurs-page">
      <div className="page-header">
        <h1>Gestion des Utilisateurs</h1>
        {currentUser?.role === 'Directeur' && (
          <button className="btn btn-primary" onClick={handleCreate}>
            + Nouvel Utilisateur
          </button>
        )}
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

      {/* Filtre par année d'exercice */}
      <div className="filter-section">
        <label htmlFor="filter-annee">Filtrer par année d'exercice :</label>
        <select
          id="filter-annee"
          className="form-control filter-select"
          value={filterAnneeId || ''}
          onChange={(e) => setFilterAnneeId(e.target.value ? Number(e.target.value) : null)}
        >
          <option value="">Toutes les années</option>
          {anneesExercice.map(annee => (
            <option key={annee.id} value={annee.id}>
              {new Date(annee.annee).getFullYear()}
            </option>
          ))}
        </select>
        {filterAnneeId && (
          <span className="filter-count">
            {filteredUtilisateurs.length} utilisateur(s) trouvé(s)
          </span>
        )}
      </div>

      <div className="table-container">
        <table className="table">
          <thead>
            <tr>
              {/* <th>ID</th> */}
              <th>Nom d'utilisateur</th>
              <th>Rôle</th>
              <th>Année d'exercice</th>
              <th>Statut</th>
              <th>Créé le</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredUtilisateurs.map(user => (
              <tr key={user.id}>
                {/* <td>{user.id}</td> */}
                <td>{user.username}</td>
                <td>
                  <span className={`badge ${getRoleBadgeClass(user.role)}`}>
                    {user.role}
                  </span>
                </td>
                <td>{new Date(user.anneeExercice).getFullYear()}</td>
                <td>
                  <span className={`status-badge ${user.active ? 'active' : 'inactive'}`}>
                    {user.active ? 'Actif' : 'Inactif'}
                  </span>
                </td>
                <td>{new Date(user.createdAt).toLocaleDateString()}</td>
                <td>
                  <div className="action-buttons">
                    <button
                      className="btn btn-sm btn-info"
                      onClick={() => handleEdit(user)}
                      title="Modifier"
                    >
                      ✏️
                    </button>
                    
                    {/* Bouton Activer/Désactiver : uniquement pour le Directeur sur les autres utilisateurs */}
                    {currentUser?.role === 'Directeur' && currentUser?.id !== user.id && (
                      <button
                        className="btn btn-sm btn-warning"
                        onClick={() => toggleActive(user)}
                        title={user.active ? 'Désactiver' : 'Activer'}
                      >
                        {user.active ? '🔒' : '🔓'}
                      </button>
                    )}
                    
                    {/* Bouton Supprimer : uniquement pour le Directeur */}
                    {currentUser?.role === 'Directeur' && (
                      <button
                        className="btn btn-sm btn-danger"
                        onClick={() => handleDelete(user.id)}
                        title="Supprimer"
                      >
                        🗑️
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>
                {editMode 
                  ? (isSelfEdit ? 'Modifier mon profil' : 'Modifier l\'utilisateur') 
                  : 'Nouvel utilisateur'}
              </h2>
              <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            </div>
            <form onSubmit={handleSubmit}>
              {/* Champs affichés pour : création OU auto-modification */}
              {(!editMode || isSelfEdit) && (
                <>
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
                    <label>Mot de passe {editMode && '(laisser vide pour ne pas changer)'}</label>
                    <input
                      type="password"
                      className="form-control"
                      value={formData.password}
                      onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                      required={!editMode}
                    />
                  </div>
                </>
              )}

              {/* Champs affichés pour : création OU Directeur modifie un autre */}
              {(!editMode || !isSelfEdit) && (
                <>
                  <div className="form-group">
                    <label>Rôle</label>
                    <select
                      className="form-control"
                      value={formData.roleId}
                      onChange={(e) => setFormData({ ...formData, roleId: Number(e.target.value) })}
                      required
                      disabled={editMode && isSelfEdit}
                    >
                      <option value="">-- Sélectionner un rôle --</option>
                      {roles.map(role => (
                        <option key={role.id} value={role.id}>{role.roleName}</option>
                      ))}
                    </select>
                  </div>
                  <div className="form-group">
                    <label>Année d'exercice</label>
                    <select
                      className="form-control"
                      value={formData.anneeExerciceId}
                      onChange={(e) => setFormData({ ...formData, anneeExerciceId: Number(e.target.value) })}
                      required
                      disabled={editMode && isSelfEdit}
                    >
                      <option value="">-- Sélectionner une année --</option>
                      {anneesExercice.map(annee => (
                        <option key={annee.id} value={annee.id}>
                          {new Date(annee.annee).getFullYear()}
                        </option>
                      ))}
                    </select>
                  </div>
                </>
              )}

              {editMode && isSelfEdit && (
                <div className="alert alert-info">
                  <small>Vous ne pouvez modifier que votre nom d'utilisateur et votre mot de passe.</small>
                </div>
              )}

              {editMode && !isSelfEdit && (
                <div className="alert alert-info">
                  <small>Vous ne pouvez modifier que le rôle et l'année d'exercice de cet utilisateur.</small>
                </div>
              )}

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Annuler
                </button>
                <button type="submit" className="btn btn-primary">
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
