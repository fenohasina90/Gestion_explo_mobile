import { useState, useEffect } from 'react';
import participantService from '../services/participant-activite.service';
import type { PersonnesDisponiblesResponse } from '../types';
import './PresenceModal.css';

interface PresenceModalProps {
  activiteId: number;
  activiteNom: string;
  onClose: () => void;
  onSuccess: () => void;
}

export const PresenceModal: React.FC<PresenceModalProps> = ({ 
  activiteId, 
  activiteNom, 
  onClose, 
  onSuccess 
}) => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [personnes, setPersonnes] = useState<PersonnesDisponiblesResponse | null>(null);
  const [activeTab, setActiveTab] = useState<'enfants' | 'staff'>('enfants');
  const [selectedEnfants, setSelectedEnfants] = useState<number[]>([]);
  const [selectedStaff, setSelectedStaff] = useState<number[]>([]);

  useEffect(() => {
    loadPersonnesDisponibles();
  }, [activiteId]);

  const loadPersonnesDisponibles = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await participantService.getPersonnesDisponibles(activiteId);
      setPersonnes(data);
    } catch (err: any) {
      console.error('Erreur chargement personnes:', err);
      setError(err.response?.data?.message || 'Erreur lors du chargement des personnes disponibles');
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
    if (personnes && selectedEnfants.length === personnes.enfants.length) {
      setSelectedEnfants([]);
    } else {
      setSelectedEnfants(personnes?.enfants.map(e => e.inscriptionId) || []);
    }
  };

  const handleSelectAllStaff = () => {
    if (personnes && selectedStaff.length === personnes.staff.length) {
      setSelectedStaff([]);
    } else {
      setSelectedStaff(personnes?.staff.map(s => s.staffId) || []);
    }
  };

  const handleSubmit = async () => {
    if (selectedEnfants.length === 0 && selectedStaff.length === 0) {
      alert('Veuillez sélectionner au moins un participant');
      return;
    }

    try {
      setLoading(true);
      await participantService.enregistrerPresence({
        activiteId,
        enfantsPresents: selectedEnfants,
        staffPresents: selectedStaff,
      });
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
        <div className="presence-modal">
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
        <div className="presence-modal">
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

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="presence-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Faire présence - {activiteNom}</h2>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        <div className="modal-body">
          {/* Tabs */}
          <div className="tabs">
            <button
              className={`tab ${activeTab === 'enfants' ? 'active' : ''}`}
              onClick={() => setActiveTab('enfants')}
            >
              👶 Enfants ({selectedEnfants.length}/{personnes?.enfants.length || 0})
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
            {activeTab === 'enfants' && (
              <div className="enfants-section">
                <div className="section-header">
                  <button 
                    className="btn btn-sm btn-secondary" 
                    onClick={handleSelectAllEnfants}
                  >
                    {selectedEnfants.length === personnes?.enfants.length ? 'Tout désélectionner' : 'Tout sélectionner'}
                  </button>
                </div>
                
                <div className="checkbox-list">
                  {personnes?.enfants.map((enfant) => (
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
                    {selectedStaff.length === personnes?.staff.length ? 'Tout désélectionner' : 'Tout sélectionner'}
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
