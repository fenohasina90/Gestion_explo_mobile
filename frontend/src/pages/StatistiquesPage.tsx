import { useState, useEffect } from 'react';
import statistiqueService from '../services/statistique.service';
import anneeExerciceService from '../services/annee-exercice.service';
import classeService from '../services/classe.service';
import type { 
  StatistiqueEnfant,
  StatistiqueStaff,
  StatistiqueFilterRequest,
  AnneeExercice,
  Classe
} from '../types';
import './StatistiquesPage.css';

export function StatistiquesPage() {
  const [activeTab, setActiveTab] = useState<'enfants' | 'staffs'>('enfants');
  const [statistiquesEnfants, setStatistiquesEnfants] = useState<StatistiqueEnfant[]>([]);
  const [statistiquesStaffs, setStatistiquesStaffs] = useState<StatistiqueStaff[]>([]);
  const [annees, setAnnees] = useState<AnneeExercice[]>([]);
  const [classes, setClasses] = useState<Classe[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showFilters, setShowFilters] = useState(true);
  
  const [filters, setFilters] = useState<StatistiqueFilterRequest>({
    anneeExerciceId: undefined,
    classeId: undefined,
    genre: undefined,
  });

  useEffect(() => {
    loadAnnees();
    loadClasses();
  }, []);

  useEffect(() => {
    if (activeTab === 'enfants') {
      loadStatistiquesEnfants();
    } else {
      loadStatistiquesStaffs();
    }
  }, [activeTab, filters]);

  const loadAnnees = async () => {
    try {
      const data = await anneeExerciceService.getAllAnneesExercice();
      setAnnees(data);
    } catch (err: any) {
      console.error('Erreur lors du chargement des années', err);
    }
  };

  const loadClasses = async () => {
    try {
      const data = await classeService.getAllClasses();
      setClasses(data);
    } catch (err: any) {
      console.error('Erreur lors du chargement des classes', err);
    }
  };

  const loadStatistiquesEnfants = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await statistiqueService.getStatistiquesEnfants(filters);
      setStatistiquesEnfants(data);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement des statistiques');
    } finally {
      setLoading(false);
    }
  };

  const loadStatistiquesStaffs = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await statistiqueService.getStatistiquesStaffs(filters);
      setStatistiquesStaffs(data);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement des statistiques');
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (field: keyof StatistiqueFilterRequest, value: any) => {
    setFilters(prev => ({
      ...prev,
      [field]: value || undefined
    }));
  };

  const handleResetFilters = () => {
    setFilters({
      anneeExerciceId: undefined,
      classeId: undefined,
      genre: undefined,
    });
  };

  const formatPourcentage = (pourcentage: number) => {
    return `${pourcentage.toFixed(2)}%`;
  };

  const formatAnnee = (annee: string) => {
    // Extrait l'année de début (ex: "2023-2024" => "2023")
    return annee.split('-')[0];
  };

  const getRangBadgeColor = (rang: number) => {
    if (rang === 1) return 'badge-gold';
    if (rang === 2) return 'badge-silver';
    if (rang === 3) return 'badge-bronze';
    return 'badge-default';
  };

  return (
    <div className="statistiques-page">
      <div className="page-header">
        <h1>📊 Statistiques</h1>
        <p>Statistiques de participation et de progression des enfants et des staffs</p>
      </div>

      {error && (
        <div className="alert alert-danger">
          {error}
        </div>
      )}

      {/* Onglets */}
      <div className="tabs">
        <button 
          className={`tab ${activeTab === 'enfants' ? 'active' : ''}`}
          onClick={() => setActiveTab('enfants')}
        >
          👦 Enfants
        </button>
        <button 
          className={`tab ${activeTab === 'staffs' ? 'active' : ''}`}
          onClick={() => setActiveTab('staffs')}
        >
          👨‍🏫 Staffs
        </button>
      </div>

      {/* Filtres */}
      <div className="filters-section">
        <button 
          className="btn btn-secondary btn-sm"
          onClick={() => setShowFilters(!showFilters)}
        >
          {showFilters ? '📉 Masquer les filtres' : '📊 Afficher les filtres'}
        </button>

        {showFilters && (
          <div className="filters-card">
            <h3>🔍 Filtres</h3>
            <div className="filters-grid">
              <div className="form-group">
                <label>Année d'exercice</label>
                <select
                  value={filters.anneeExerciceId || ''}
                  onChange={(e) => handleFilterChange('anneeExerciceId', e.target.value ? Number(e.target.value) : undefined)}
                  className="form-control"
                >
                  <option value="">Toutes les années</option>
                  {annees.map(annee => (
                    <option key={annee.id} value={annee.id}>{formatAnnee(annee.annee)}</option>
                  ))}
                </select>
              </div>

              {activeTab === 'enfants' && (
                <>
                  <div className="form-group">
                    <label>Classe</label>
                    <select
                      value={filters.classeId || ''}
                      onChange={(e) => handleFilterChange('classeId', e.target.value ? Number(e.target.value) : undefined)}
                      className="form-control"
                    >
                      <option value="">Toutes les classes</option>
                      {classes.map(classe => (
                        <option key={classe.id} value={classe.id}>{classe.nom}</option>
                      ))}
                    </select>
                  </div>

                  <div className="form-group">
                    <label>Genre</label>
                    <select
                      value={filters.genre || ''}
                      onChange={(e) => handleFilterChange('genre', e.target.value || undefined)}
                      className="form-control"
                    >
                      <option value="">Tous</option>
                      <option value="M">Masculin</option>
                      <option value="F">Féminin</option>
                    </select>
                  </div>
                </>
              )}
            </div>

            <div className="filters-actions">
              <button 
                className="btn btn-secondary btn-sm"
                onClick={handleResetFilters}
              >
                🔄 Réinitialiser
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Résultats */}
      <div className="results-section">
        {loading ? (
          <div className="loading">Chargement des statistiques...</div>
        ) : activeTab === 'enfants' ? (
          <div className="statistiques-enfants">
            <h2>📊 Statistiques des Enfants ({statistiquesEnfants.length})</h2>
            
            {statistiquesEnfants.length === 0 ? (
              <div className="empty-state">
                Aucune statistique disponible pour les critères sélectionnés.
              </div>
            ) : (
              <div className="table-responsive">
                <table className="table statistiques-table">
                  <thead>
                    <tr>
                      <th>Nom & Prénom</th>
                      <th>Classe</th>
                      <th>Année</th>
                      <th>Programmes Complétés</th>
                      <th>Participation Activités</th>
                      <th>Rang</th>
                      <th>Présence CP</th>
                      <th>Rang</th>
                    </tr>
                  </thead>
                  <tbody>
                    {statistiquesEnfants.map((stat) => (
                      <tr key={stat.enfantId}>
                        <td>
                          <strong>{stat.nom} {stat.prenom}</strong>
                        </td>
                        <td>{stat.classe}</td>
                        <td>{formatAnnee(stat.anneeExercice)}</td>
                        <td>
                          <div className="stat-cell">
                            <div className="stat-value">
                              {formatPourcentage(stat.pourcentageProgrammes)}
                            </div>
                            <div className="stat-detail">
                              {stat.nombreProgrammesCompletes}/{stat.totalProgrammesClasse}
                            </div>
                          </div>
                        </td>
                        <td>
                          <div className="stat-cell">
                            <div className="stat-value">
                              {formatPourcentage(stat.pourcentageActivites)}
                            </div>
                            <div className="stat-detail">
                              {stat.nombreParticipationsActivites}/{stat.totalActivites}
                            </div>
                          </div>
                        </td>
                        <td>
                          <span className={`badge ${getRangBadgeColor(stat.rangActivites)}`}>
                            #{stat.rangActivites}
                          </span>
                        </td>
                        <td>
                          <div className="stat-cell">
                            <div className="stat-value">
                              {formatPourcentage(stat.pourcentageCP)}
                            </div>
                            <div className="stat-detail">
                              {stat.nombrePresencesCP}/{stat.totalCP}
                            </div>
                          </div>
                        </td>
                        <td>
                          <span className={`badge ${getRangBadgeColor(stat.rangCP)}`}>
                            #{stat.rangCP}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        ) : (
          <div className="statistiques-staffs">
            <h2>📊 Statistiques des Staffs ({statistiquesStaffs.length})</h2>
            
            {statistiquesStaffs.length === 0 ? (
              <div className="empty-state">
                Aucune statistique disponible pour les critères sélectionnés.
              </div>
            ) : (
              <div className="table-responsive">
                <table className="table statistiques-table">
                  <thead>
                    <tr>
                      <th>Nom & Prénom</th>
                      <th>Rôle</th>
                      <th>Année</th>
                      <th>Participation Activités</th>
                      <th>Rang</th>
                      <th>Présence CP</th>
                      <th>Rang</th>
                    </tr>
                  </thead>
                  <tbody>
                    {statistiquesStaffs.map((stat) => (
                      <tr key={stat.staffId}>
                        <td>
                          <strong>{stat.nom} {stat.prenom}</strong>
                        </td>
                        <td>{stat.role}</td>
                        <td>{formatAnnee(stat.anneeExercice)}</td>
                        <td>
                          <div className="stat-cell">
                            <div className="stat-value">
                              {formatPourcentage(stat.pourcentageActivites)}
                            </div>
                            <div className="stat-detail">
                              {stat.nombreParticipationsActivites}/{stat.totalActivites}
                            </div>
                          </div>
                        </td>
                        <td>
                          <span className={`badge ${getRangBadgeColor(stat.rangActivites)}`}>
                            #{stat.rangActivites}
                          </span>
                        </td>
                        <td>
                          <div className="stat-cell">
                            <div className="stat-value">
                              {formatPourcentage(stat.pourcentageCP)}
                            </div>
                            <div className="stat-detail">
                              {stat.nombrePresencesCP}/{stat.totalCP}
                            </div>
                          </div>
                        </td>
                        <td>
                          <span className={`badge ${getRangBadgeColor(stat.rangCP)}`}>
                            #{stat.rangCP}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
