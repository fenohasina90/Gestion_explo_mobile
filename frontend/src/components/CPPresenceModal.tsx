import { useState, useEffect } from 'react';
import cpPresenceService from '../services/cp-presence.service';
import classeService from '../services/classe.service';
import type { CpPersonnesDisponiblesResponse, Classe } from '../types';
import './CPPresenceModal.css';

interface CPPresenceModalProps {
  classeProgressiveId: number;
  cpDate: string;
  onClose: () => void;
  onSuccess: () => void;
}

export const CPPresenceModal: React.FC<CPPresenceModalProps> = ({ 
  classeProgressiveId, 
  cpDate, 
  onClose, 
  onSuccess 
}) => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [personnes, setPersonnes] = useState<CpPersonnesDisponiblesResponse | null>(null);
  const [classes, setClasses] = useState<Classe[]>([]);
  const [activeTab, setActiveTab] = useState<'tous' | 'enfants' | 'staff'>('tous');
  const [selectedEnfants, setSelectedEnfants] = useState<number[]>([]);
  const [selectedStaff, setSelectedStaff] = useState<number[]>([]);
  const [selectedClasseFilter, setSelectedClasseFilter] = useState<number | ''>('');

  useEffect(() => {
    loadData();
  }, [classeProgressiveId]);

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      const [personnesData, classesData, participantsData] = await Promise.all([
        cpPresenceService.getPersonnesDisponibles(classeProgressiveId),
        classeService.getAllClasses(),
        cpPresenceService.getParticipants(classeProgressiveId, true, true).catch(() => ({ enfants: [], staff: [] }))
      ]);
      
      setPersonnes(personnesData);
      setClasses(classesData);
      
      // Pré-sélectionner les participants déjà enregistrés
      if (participantsData.enfants) {
        setSelectedEnfants(participantsData.enfants.map(e => e.inscriptionId));
      }
      if (participantsData.staff) {
        setSelectedStaff(participantsData.staff.map(s => s.staffId));
      }
    } catch (err: any) {
      console.error('Erreur chargement données:', err);
      setError(err.response?.data?.message || 'Erreur lors du chargement des données');
    } finally {
      setLoading(false);
    }
  };

  const handleToggleEnfant = (inscriptionId: number) => {
    setSelectedEnfants(prev =>
      prev.includes(inscriptionId)
        ? prev.filter(id => id !== inscriptionId)
        : [...prev, inscriptionId]
    );
  };

  const handleToggleStaff = (staffId: number) => {
    setSelectedStaff(prev =>
      prev.includes(staffId)
        ? prev.filter(id => id !== staffId)
        : [...prev, staffId]
    );
  };

  const handleSelectAllEnfants = () => {
    const filteredEnfants = getFilteredEnfants();
    if (selectedEnfants.length === filteredEnfants.length && filteredEnfants.length > 0) {
      // Désélectionner tous les enfants filtrés
      const filteredIds = filteredEnfants.map(e => e.inscriptionId);
      setSelectedEnfants(prev => prev.filter(id => !filteredIds.includes(id)));
    } else {
      // Sélectionner tous les enfants filtrés
      const filteredIds = filteredEnfants.map(e => e.inscriptionId);
      setSelectedEnfants(prev => [...new Set([...prev, ...filteredIds])]);
    }
  };

  const handleSelectAllStaff = () => {
    if (personnes && selectedStaff.length === personnes.staff.length) {
      setSelectedStaff([]);
    } else {
      setSelectedStaff(personnes?.staff.map(s => s.staffId) || []);
    }
  };

  const handleSelectAll = () => {
    const filteredEnfants = getFilteredEnfants();
    const allEnfantsIds = filteredEnfants.map(e => e.inscriptionId);
    const allStaffIds = personnes?.staff.map(s => s.staffId) || [];
    
    const allSelected = 
      selectedEnfants.length === allEnfantsIds.length && 
      selectedStaff.length === allStaffIds.length &&
      allEnfantsIds.length > 0;

    if (allSelected) {
      setSelectedEnfants([]);
      setSelectedStaff([]);
    } else {
      setSelectedEnfants(allEnfantsIds);
      setSelectedStaff(allStaffIds);
    }
  };

  const getFilteredEnfants = () => {
    if (!personnes) return [];
    
    let filtered = personnes.enfants;
    
    if (selectedClasseFilter) {
      filtered = filtered.filter(e => e.classeId === selectedClasseFilter);
    }
    
    return filtered;
  };

  const handleSubmit = async () => {
    if (selectedEnfants.length === 0 && selectedStaff.length === 0) {
      alert('Veuillez sélectionner au moins un participant');
      return;
    }

    if (!window.confirm(`Confirmer l'enregistrement de la présence pour ${selectedEnfants.length} enfant(s) et ${selectedStaff.length} staff(s) ?`)) {
      return;
    }

    try {
      setLoading(true);
      await cpPresenceService.enregistrerPresence({
        classeProgressiveId,
        enfantsPresents: selectedEnfants,
        staffPresents: selectedStaff,
      });
      alert('Présence enregistrée avec succès');
      onSuccess();
      onClose();
    } catch (err: any) {
      console.error('Erreur enregistrement présence:', err);
      alert(err.response?.data?.message || 'Erreur lors de l\'enregistrement de la présence');
    } finally {
      setLoading(false);
    }
  };

  if (loading && !personnes) {
    return (
      <div className="modal-overlay">
        <div className="cp-presence-modal">
          <div className="modal-header">
            <h2>Chargement...</h2>
            <button className="close-btn" onClick={onClose}>×</button>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="modal-overlay">
        <div className="cp-presence-modal">
          <div className="modal-header">
            <h2>Erreur</h2>
            <button className="close-btn" onClick={onClose}>×</button>
          </div>
          <div className="modal-body">
            <p className="error-message">{error}</p>
            <button className="btn btn-secondary" onClick={onClose}>Fermer</button>
          </div>
        </div>
      </div>
    );
  }

  const filteredEnfants = getFilteredEnfants();

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="cp-presence-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Gérer présence - CP du {new Date(cpDate).toLocaleDateString('fr-FR')}</h2>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        <div className="modal-body">
          {/* Tabs */}
          <div className="tabs">
            <button
              className={`tab ${activeTab === 'tous' ? 'active' : ''}`}
              onClick={() => setActiveTab('tous')}
            >
              👥 Tous ({selectedEnfants.length + selectedStaff.length})
            </button>
            <button
              className={`tab ${activeTab === 'enfants' ? 'active' : ''}`}
              onClick={() => setActiveTab('enfants')}
            >
              👶 Enfants ({selectedEnfants.length}/{filteredEnfants.length})
            </button>
            <button
              className={`tab ${activeTab === 'staff' ? 'active' : ''}`}
              onClick={() => setActiveTab('staff')}
            >
              👨‍🏫 Staff ({selectedStaff.length}/{personnes?.staff.length || 0})
            </button>
          </div>

          {/* Content */}
          <div className="tab-content">
            {activeTab === 'tous' && (
              <div className="tous-section">
                <div className="section-header">
                  <button 
                    className="btn btn-sm btn-secondary" 
                    onClick={handleSelectAll}
                  >
                    {(selectedEnfants.length === filteredEnfants.length && selectedStaff.length === personnes?.staff.length && filteredEnfants.length > 0) 
                      ? 'Tout désélectionner' 
                      : 'Tout sélectionner'}
                  </button>
                </div>

                {/* Filtre par classe pour enfants */}
                <div className="filter-section">
                  <label htmlFor="classe-filter">Filtrer par classe:</label>
                  <select 
                    id="classe-filter"
                    value={selectedClasseFilter} 
                    onChange={(e) => setSelectedClasseFilter(e.target.value ? Number(e.target.value) : '')}
                    className="filter-select"
                  >
                    <option value="">Toutes les classes</option>
                    {classes.map(classe => (
                      <option key={classe.id} value={classe.id}>
                        {classe.nom}
                      </option>
                    ))}
                  </select>
                </div>
                
                {/* Enfants */}
                <h3 className="subsection-title">👶 Enfants ({selectedEnfants.length}/{filteredEnfants.length})</h3>
                <div className="checkbox-list">
                  {filteredEnfants.map((enfant) => (
                    <label key={enfant.inscriptionId} className="checkbox-item">
                      <input
                        type="checkbox"
                        checked={selectedEnfants.includes(enfant.inscriptionId)}
                        onChange={() => handleToggleEnfant(enfant.inscriptionId)}
                      />
                      <span className="checkbox-label">
                        {enfant.nom} {enfant.prenom}
                        {enfant.classeNom && (
                          <span className="badge badge-classe">{enfant.classeNom}</span>
                        )}
                        <span className="badge badge-genre">{enfant.genre}</span>
                      </span>
                    </label>
                  ))}
                  {filteredEnfants.length === 0 && (
                    <p className="no-data">Aucun enfant disponible pour ce filtre</p>
                  )}
                </div>

                {/* Staff */}
                <h3 className="subsection-title">👨‍🏫 Staff ({selectedStaff.length}/{personnes?.staff.length || 0})</h3>
                <div className="checkbox-list">
                  {personnes?.staff.map((staff) => (
                    <label key={staff.staffId} className="checkbox-item">
                      <input
                        type="checkbox"
                        checked={selectedStaff.includes(staff.staffId)}
                        onChange={() => handleToggleStaff(staff.staffId)}
                      />
                      <span className="checkbox-label">
                        {staff.nom} {staff.prenom}
                        {staff.totem && <span className="badge badge-totem">({staff.totem})</span>}
                        {staff.role && <span className="badge badge-role">{staff.role}</span>}
                      </span>
                    </label>
                  ))}
                  {personnes?.staff.length === 0 && (
                    <p className="no-data">Aucun staff disponible</p>
                  )}
                </div>
              </div>
            )}

            {activeTab === 'enfants' && (
              <div className="enfants-section">
                <div className="section-header">
                  <button 
                    className="btn btn-sm btn-secondary" 
                    onClick={handleSelectAllEnfants}
                  >
                    {selectedEnfants.length === filteredEnfants.length && filteredEnfants.length > 0 
                      ? 'Tout désélectionner' 
                      : 'Tout sélectionner'}
                  </button>
                </div>

                {/* Filtre par classe */}
                <div className="filter-section">
                  <label htmlFor="classe-filter-enfants">Filtrer par classe:</label>
                  <select 
                    id="classe-filter-enfants"
                    value={selectedClasseFilter} 
                    onChange={(e) => setSelectedClasseFilter(e.target.value ? Number(e.target.value) : '')}
                    className="filter-select"
                  >
                    <option value="">Toutes les classes</option>
                    {classes.map(classe => (
                      <option key={classe.id} value={classe.id}>
                        {classe.nom}
                      </option>
                    ))}
                  </select>
                </div>
                
                <div className="checkbox-list">
                  {filteredEnfants.map((enfant) => (
                    <label key={enfant.inscriptionId} className="checkbox-item">
                      <input
                        type="checkbox"
                        checked={selectedEnfants.includes(enfant.inscriptionId)}
                        onChange={() => handleToggleEnfant(enfant.inscriptionId)}
                      />
                      <span className="checkbox-label">
                        {enfant.nom} {enfant.prenom}
                        {enfant.classeNom && (
                          <span className="badge badge-classe">{enfant.classeNom}</span>
                        )}
                        <span className="badge badge-genre">{enfant.genre}</span>
                      </span>
                    </label>
                  ))}
                  {filteredEnfants.length === 0 && (
                    <p className="no-data">Aucun enfant disponible pour ce filtre</p>
                  )}
                </div>
              </div>
            )}

            {activeTab === 'staff' && (
              <div className="staff-section">
                <div className="section-header">
                  <button 
                    className="btn btn-sm btn-secondary" 
                    onClick={handleSelectAllStaff}
                  >
                    {selectedStaff.length === personnes?.staff.length 
                      ? 'Tout désélectionner' 
                      : 'Tout sélectionner'}
                  </button>
                </div>
                
                <div className="checkbox-list">
                  {personnes?.staff.map((staff) => (
                    <label key={staff.staffId} className="checkbox-item">
                      <input
                        type="checkbox"
                        checked={selectedStaff.includes(staff.staffId)}
                        onChange={() => handleToggleStaff(staff.staffId)}
                      />
                      <span className="checkbox-label">
                        {staff.nom} {staff.prenom}
                        {staff.totem && <span className="badge badge-totem">({staff.totem})</span>}
                        {staff.role && <span className="badge badge-role">{staff.role}</span>}
                      </span>
                    </label>
                  ))}
                  {personnes?.staff.length === 0 && (
                    <p className="no-data">Aucun staff disponible</p>
                  )}
                </div>
              </div>
            )}
          </div>
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose} disabled={loading}>
            Annuler
          </button>
          <button 
            className="btn btn-primary" 
            onClick={handleSubmit} 
            disabled={loading || (selectedEnfants.length === 0 && selectedStaff.length === 0)}
          >
            {loading ? 'Enregistrement...' : `Enregistrer (${selectedEnfants.length + selectedStaff.length})`}
          </button>
        </div>
      </div>
    </div>
  );
};
