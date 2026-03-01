import { useState, useEffect } from 'react';
import participantService from '../services/participant-activite.service';
import type { ParticipantsResponse } from '../types';
import './ParticipantsModal.css';

interface ParticipantsModalProps {
  activiteId: number;
  activiteNom: string;
  onClose: () => void;
}

export const ParticipantsModal: React.FC<ParticipantsModalProps> = ({
  activiteId,
  activiteNom,
  onClose,
}) => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [participants, setParticipants] = useState<ParticipantsResponse | null>(null);
  const [filtreEnfant, setFiltreEnfant] = useState(true);
  const [filtreStaff, setFiltreStaff] = useState(true);
  const [classeId, setClasseId] = useState<number | undefined>(undefined);
  const [classes, setClasses] = useState<{ id: number; nom: string }[]>([]);

  // Fonction pour obtenir la couleur du badge selon la classe
  const getClasseBadgeColor = (classeNom: string): string => {
    const nom = classeNom.toLowerCase();
    if (nom.includes('ami')) return 'badge-ami';
    if (nom.includes('compagnon')) return 'badge-compagnon';
    if (nom.includes('eclaireur') || nom.includes('éclaireur')) return 'badge-eclaireur';
    if (nom.includes('pionnier')) return 'badge-pionnier';
    if (nom.includes('voyageur')) return 'badge-voyageur';
    if (nom.includes('guide')) return 'badge-guide';
    return 'badge-success';
  };

  // Charger les classes une seule fois au début (sans filtre)
  useEffect(() => {
    const loadClasses = async () => {
      try {
        const data = await participantService.getParticipants(
          activiteId,
          true,
          false,
          undefined
        );
        if (data.enfants) {
          const uniqueClasses = Array.from(
            new Map(
              data.enfants
                .filter(e => e.classeId && e.classeNom)
                .map(e => [e.classeId, { id: e.classeId!, nom: e.classeNom! }])
            ).values()
          ).sort((a, b) => a.nom.localeCompare(b.nom));
          setClasses(uniqueClasses);
        }
      } catch (err: any) {
        console.error('Erreur chargement classes:', err);
      }
    };
    loadClasses();
  }, [activiteId]);

  useEffect(() => {
    loadParticipants();
  }, [activiteId, filtreEnfant, filtreStaff, classeId]);

  const loadParticipants = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await participantService.getParticipants(
        activiteId,
        filtreEnfant,
        filtreStaff,
        classeId
      );
      setParticipants(data);
    } catch (err: any) {
      console.error('Erreur chargement participants:', err);
      setError(err.response?.data?.message || 'Erreur lors du chargement des participants');
    } finally {
      setLoading(false);
    }
  };

  const totalParticipants = 
    (participants?.enfants?.length || 0) + 
    (participants?.staff?.length || 0);

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="participants-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Participants - {activiteNom}</h2>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        <div className="modal-body">
          {/* Filters */}
          <div className="filters-section">
            <h3>Filtres</h3>
            <div className="filter-options">
              <label className="filter-checkbox">
                <input
                  type="checkbox"
                  checked={filtreEnfant}
                  onChange={(e) => {
                    setFiltreEnfant(e.target.checked);
                    if (!e.target.checked) {
                      setClasseId(undefined);
                    }
                  }}
                />
                <span>👶 Enfants</span>
              </label>

              <label className="filter-checkbox">
                <input
                  type="checkbox"
                  checked={filtreStaff}
                  onChange={(e) => setFiltreStaff(e.target.checked)}
                />
                <span>👨‍🏫 Staff</span>
              </label>

              {filtreEnfant && classes.length > 0 && (
                <div className="classe-filter">
                  <label htmlFor="classe-select">Classe:</label>
                  <select
                    id="classe-select"
                    value={classeId || ''}
                    onChange={(e) => setClasseId(e.target.value ? Number(e.target.value) : undefined)}
                  >
                    <option value="">Toutes les classes</option>
                    {classes.map(classe => (
                      <option key={classe.id} value={classe.id}>
                        {classe.nom}
                      </option>
                    ))}
                  </select>
                </div>
              )}
            </div>

            <div className="total-count">
              Total: <strong>{totalParticipants}</strong> participant(s)
            </div>
          </div>

          {/* Participants List */}
          {loading ? (
            <div className="loading-state">Chargement...</div>
          ) : error ? (
            <div className="error-message">{error}</div>
          ) : totalParticipants === 0 ? (
            <div className="empty-state">Aucun participant trouvé</div>
          ) : (
            <div className="participants-grid">
              {/* Enfants */}
              {filtreEnfant && participants?.enfants && participants.enfants.length > 0 && (
                <>
                  <h4 className="section-title">Enfants ({participants.enfants.length})</h4>
                  {participants.enfants.map((enfant) => (
                    <div key={enfant.inscriptionId} className="participant-card enfant-card">
                      <div className="participant-name">
                        {enfant.nom} {enfant.prenom}
                      </div>
                      <div className="participant-details">
                        {enfant.classeNom && (
                          <span className={`badge ${getClasseBadgeColor(enfant.classeNom)}`}>
                            {enfant.classeNom}
                          </span>
                        )}
                        <span className="badge badge-genre">{enfant.genre}</span>
                      </div>
                    </div>
                  ))}
                </>
              )}

              {/* Staff */}
              {filtreStaff && participants?.staff && participants.staff.length > 0 && (
                <>
                  <h4 className="section-title">Staff ({participants.staff.length})</h4>
                  {participants.staff.map((staff) => (
                    <div key={staff.staffId} className="participant-card staff-card">
                      <div className="participant-name">
                        {staff.nom} {staff.prenom}
                      </div>
                      <div className="participant-details">
                        {staff.totem && (
                          <span className="badge badge-totem">({staff.totem})</span>
                        )}
                        {staff.role && (
                          <span className="badge badge-role">{staff.role}</span>
                        )}
                      </div>
                    </div>
                  ))}
                </>
              )}
            </div>
          )}
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>
            Fermer
          </button>
        </div>
      </div>
    </div>
  );
};
