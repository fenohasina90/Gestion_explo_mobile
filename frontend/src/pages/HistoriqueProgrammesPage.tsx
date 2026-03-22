import { useState, useEffect } from 'react';
import historiqueProgrammeService from '../services/historique-programme.service';
import anneeExerciceService from '../services/annee-exercice.service';
import type { 
  StatistiquesAnnuelles,
  ProgressionAnnuelle,
  ProgrammeAvancement,
  AnneeExercice
} from '../types';
import './HistoriqueProgrammesPage.css';

type ViewMode = 'statistiques' | 'progression' | 'avancement';

export function HistoriqueProgrammesPage() {
  const [annees, setAnnees] = useState<AnneeExercice[]>([]);
  const [selectedAnneeId, setSelectedAnneeId] = useState<number | null>(null);
  const [viewMode, setViewMode] = useState<ViewMode>('statistiques');
  
  const [progression, setProgression] = useState<ProgressionAnnuelle[]>([]);
  const [avancement, setAvancement] = useState<ProgrammeAvancement[]>([]);
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // Filtres pour la vue Statistiques
  const [filterStatCategorie, setFilterStatCategorie] = useState<string>('');
  const [filterStatClasse, setFilterStatClasse] = useState<string>('');
  const [filterStatStatut, setFilterStatStatut] = useState<string>('');
  
  // Filtres pour la vue Progression
  const [filterStatut, setFilterStatut] = useState<string>('');
  const [filterSearch, setFilterSearch] = useState<string>('');
  const [filterCategorie, setFilterCategorie] = useState<string>('');
  const [filterClasse, setFilterClasse] = useState<string>('');
  
  // Filtres pour la vue Avancement
  const [filterAvancementCategorie, setFilterAvancementCategorie] = useState<string>('');
  const [filterAvancementClasse, setFilterAvancementClasse] = useState<string>('');
  const [filterAvancementStatut, setFilterAvancementStatut] = useState<string>('');
  
  // État d'expansion pour la vue Avancement
  const [expandedProgrammes, setExpandedProgrammes] = useState<Set<number>>(new Set());

  useEffect(() => {
    loadAnnees();
  }, []);

  useEffect(() => {
    if (viewMode === 'statistiques') {
      loadStatistiques();
    } else if (viewMode === 'progression') {
      loadProgression();
    } else if (viewMode === 'avancement') {
      loadAvancement();
    }
  }, [viewMode, selectedAnneeId]);

  const loadAnnees = async () => {
    try {
      const data = await anneeExerciceService.getAllAnneesExercice();
      setAnnees(data);
      
      // Sélectionner l'année active par défaut
      const activeAnnee = data.find((a: AnneeExercice) => a.estActif);
      if (activeAnnee) {
        setSelectedAnneeId(activeAnnee.id);
      }
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement des années');
    }
  };

  const loadStatistiques = async () => {
    try {
      setLoading(true);
      // La vue statistiques est calculée depuis la progression
      const progData = await historiqueProgrammeService.getProgressionAnnuelle(
        selectedAnneeId || undefined
      );
      setProgression(progData);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement des statistiques');
    } finally {
      setLoading(false);
    }
  };

  const loadProgression = async () => {
    try {
      setLoading(true);
      const data = await historiqueProgrammeService.getProgressionAnnuelle(
        selectedAnneeId || undefined
      );
      setProgression(data);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement de la progression');
    } finally {
      setLoading(false);
    }
  };

  const loadAvancement = async () => {
    try {
      setLoading(true);
      const data = await historiqueProgrammeService.getAvancementProgrammes(
        selectedAnneeId || undefined
      );
      setAvancement(data);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement de l\'avancement');
    } finally {
      setLoading(false);
    }
  };

  const toggleProgrammeExpansion = (programmeId: number) => {
    const newExpanded = new Set(expandedProgrammes);
    if (newExpanded.has(programmeId)) {
      newExpanded.delete(programmeId);
    } else {
      newExpanded.add(programmeId);
    }
    setExpandedProgrammes(newExpanded);
  };

  const getStatutBadgeClass = (statut: string): string => {
    if (!statut) return '';
    const statutNormalized = statut.trim().toLowerCase();
    
    if (statutNormalized.includes('attente')) {
      return 'badge-attente';
    } else if (statutNormalized.includes('cours')) {
      return 'badge-cours';
    } else if (statutNormalized.includes('termin')) {
      return 'badge-termine';
    }
    return '';
  };

  const getStatutLabel = (statut: string): string => {
    if (!statut) return '';
    const statutNormalized = statut.trim().toLowerCase();
    
    if (statutNormalized.includes('attente')) {
      return 'EN ATTENTE';
    } else if (statutNormalized.includes('cours')) {
      return 'EN COURS';
    } else if (statutNormalized.includes('termin')) {
      return 'TERMINÉ';
    }
    return statut;
  };

  const getStatutBadgeStyle = (statut: string): React.CSSProperties => {
    if (!statut) return {};
    const statutNormalized = statut.trim().toLowerCase();
    
    if (statutNormalized.includes('attente')) {
      return {
        backgroundColor: '#ffc107',
        color: '#000000',
        border: '2px solid #f59e0b',
        fontWeight: 700
      };
    }
    return {};
  };

  const getClasseBadgeClass = (classe: string): string => {
    if (!classe) return '';
    const classeNormalized = classe.trim().toLowerCase();
    
    if (classeNormalized.includes('ami')) {
      return 'badge-ami';
    } else if (classeNormalized.includes('compagnon')) {
      return 'badge-compagnon';
    } else if (classeNormalized.includes('eclaireur') || classeNormalized.includes('éclaireur')) {
      return 'badge-eclaireur';
    } else if (classeNormalized.includes('pionnier')) {
      return 'badge-pionnier';
    } else if (classeNormalized.includes('voyageur')) {
      return 'badge-voyageur';
    } else if (classeNormalized.includes('guide')) {
      return 'badge-guide';
    }
    return '';
  };

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatAnneeExercice = (anneeExercice: string): string => {
    // Extraire seulement l'année (format YYYY)
    if (!anneeExercice) return '';
    const date = new Date(anneeExercice);
    return date.getFullYear().toString();
  };

  // Listes uniques pour les filtres
  const categories = Array.from(new Set(progression.map(p => p.categorieNom))).sort();
  const classes = Array.from(new Set(progression.map(p => p.classeNom))).sort();

  const filteredProgression = progression.filter((prog) => {
    const matchStatut = !filterStatut || prog.statutFinalNom === filterStatut;
    const matchCategorie = !filterCategorie || prog.categorieNom === filterCategorie;
    const matchClasse = !filterClasse || prog.classeNom === filterClasse;
    const matchSearch = !filterSearch || 
      prog.programmeNom.toLowerCase().includes(filterSearch.toLowerCase()) ||
      prog.categorieNom.toLowerCase().includes(filterSearch.toLowerCase()) ||
      prog.classeNom.toLowerCase().includes(filterSearch.toLowerCase());
    return matchStatut && matchCategorie && matchClasse && matchSearch;
  });

  // Listes uniques pour les filtres d'avancement
  const categoriesAvancement = Array.from(new Set(avancement.map(p => p.categorieNom))).sort();
  const classesAvancement = Array.from(new Set(avancement.map(p => p.classeNom))).sort();

  const filteredAvancement = avancement.filter((prog) => {
    const matchCategorie = !filterAvancementCategorie || prog.categorieNom === filterAvancementCategorie;
    const matchClasse = !filterAvancementClasse || prog.classeNom === filterAvancementClasse;
    const matchStatut = !filterAvancementStatut || prog.statutActuelNom === filterAvancementStatut;
    return matchCategorie && matchClasse && matchStatut;
  });

  // Statistiques filtrées calculées à partir de la progression
  const filteredStatistiques = (() => {
    // Filtrer la progression selon les critères
    const filtered = progression.filter((prog) => {
      const matchCategorie = !filterStatCategorie || prog.categorieNom === filterStatCategorie;
      const matchClasse = !filterStatClasse || prog.classeNom === filterStatClasse;
      const matchStatut = !filterStatStatut || prog.statutFinalNom === filterStatStatut;
      return matchCategorie && matchClasse && matchStatut;
    });

    // Grouper par année et calculer les statistiques
    const statsMap = new Map<string, StatistiquesAnnuelles>();
    
    filtered.forEach(prog => {
      const annee = prog.anneeExercice;
      if (!statsMap.has(annee)) {
        statsMap.set(annee, {
          anneeExercice: annee,
          totalProgrammesTravailles: 0,
          programmesTermines: 0,
          programmesEnCours: 0,
          programmesEnAttente: 0,
          tauxCompletion: 0,
          totalChangements: 0,
          nombreCPs: 0
        });
      }
      
      const stat = statsMap.get(annee)!;
      stat.totalProgrammesTravailles++;
      
      const statutNorm = prog.statutFinalNom.toLowerCase();
      if (statutNorm.includes('termin')) {
        stat.programmesTermines++;
      } else if (statutNorm.includes('cours')) {
        stat.programmesEnCours++;
      } else if (statutNorm.includes('attente')) {
        stat.programmesEnAttente++;
      }
    });

    // Calculer le taux de complétion
    statsMap.forEach(stat => {
      if (stat.totalProgrammesTravailles > 0) {
        stat.tauxCompletion = (stat.programmesTermines / stat.totalProgrammesTravailles) * 100;
      }
    });

    return Array.from(statsMap.values());
  })();

  const renderStatistiques = () => (
    <div className="statistiques-container">
      {/* Filtres */}
      <div className="filters">
        <select
          value={filterStatCategorie}
          onChange={(e) => setFilterStatCategorie(e.target.value)}
          className="filter-select"
        >
          <option value="">Toutes les catégories</option>
          {categories.map((cat, idx) => (
            <option key={idx} value={cat}>{cat}</option>
          ))}
        </select>
        <select
          value={filterStatClasse}
          onChange={(e) => setFilterStatClasse(e.target.value)}
          className="filter-select"
        >
          <option value="">Toutes les classes</option>
          {classes.map((cls, idx) => (
            <option key={idx} value={cls}>{cls}</option>
          ))}
        </select>
        <select
          value={filterStatStatut}
          onChange={(e) => setFilterStatStatut(e.target.value)}
          className="filter-select"
        >
          <option value="">Tous les statuts</option>
          <option value="En attente">En attente</option>
          <option value="En cours">En cours</option>
          <option value="Terminé">Terminé</option>
        </select>
      </div>

      {filteredStatistiques.length === 0 ? (
        <p className="no-data">Aucune statistique disponible</p>
      ) : (
        filteredStatistiques.map((stat, index) => (
          <div key={index} className="stats-year-section">
            <div className="stats-year-header">
              <h2>📅 Année d'exercice {formatAnneeExercice(stat.anneeExercice)}</h2>
            </div>

            {/* Cartes métriques principales */}
            <div className="metrics-grid">
              <div className="metric-card metric-total">
                <div className="metric-icon">📚</div>
                <div className="metric-content">
                  <div className="metric-value">{stat.totalProgrammesTravailles}</div>
                  <div className="metric-label">Total programmes</div>
                </div>
              </div>

              <div className="metric-card metric-attente">
                <div className="metric-icon">⏳</div>
                <div className="metric-content">
                  <div className="metric-value">{stat.programmesEnAttente}</div>
                  <div className="metric-label">En attente</div>
                  <div className="metric-percentage">
                    {stat.totalProgrammesTravailles > 0 ? ((stat.programmesEnAttente / stat.totalProgrammesTravailles) * 100).toFixed(1) : 0}%
                  </div>
                </div>
              </div>

              <div className="metric-card metric-cours">
                <div className="metric-icon">▶️</div>
                <div className="metric-content">
                  <div className="metric-value">{stat.programmesEnCours}</div>
                  <div className="metric-label">En cours</div>
                  <div className="metric-percentage">
                    {stat.totalProgrammesTravailles > 0 ? ((stat.programmesEnCours / stat.totalProgrammesTravailles) * 100).toFixed(1) : 0}%
                  </div>
                </div>
              </div>

              <div className="metric-card metric-termine">
                <div className="metric-icon">✅</div>
                <div className="metric-content">
                  <div className="metric-value">{stat.programmesTermines}</div>
                  <div className="metric-label">Terminés</div>
                  <div className="metric-percentage">
                    {stat.tauxCompletion.toFixed(1)}%
                  </div>
                </div>
              </div>
            </div>

            {/* Section progression visuelle */}
            <div className="progression-section">
              <h3>📊 Progression globale</h3>
              
              <div className="progress-visual">
                <div className="progress-bar-container">
                  <div 
                    className="progress-segment progress-termine" 
                    style={{ width: `${stat.tauxCompletion}%` }}
                  >
                    {stat.tauxCompletion > 10 && (
                      <span className="progress-label">{stat.programmesTermines} terminés</span>
                    )}
                  </div>
                  <div 
                    className="progress-segment progress-cours" 
                    style={{ width: `${stat.totalProgrammesTravailles > 0 ? (stat.programmesEnCours / stat.totalProgrammesTravailles * 100) : 0}%` }}
                  >
                    {stat.programmesEnCours > 0 && stat.totalProgrammesTravailles > 0 && ((stat.programmesEnCours / stat.totalProgrammesTravailles * 100) > 10) && (
                      <span className="progress-label">{stat.programmesEnCours} en cours</span>
                    )}
                  </div>
                  <div 
                    className="progress-segment progress-attente" 
                    style={{ width: `${stat.totalProgrammesTravailles > 0 ? (stat.programmesEnAttente / stat.totalProgrammesTravailles * 100) : 0}%` }}
                  >
                    {stat.programmesEnAttente > 0 && stat.totalProgrammesTravailles > 0 && ((stat.programmesEnAttente / stat.totalProgrammesTravailles * 100) > 10) && (
                      <span className="progress-label">{stat.programmesEnAttente} en attente</span>
                    )}
                  </div>
                </div>
              </div>

              {/* Légende */}
              <div className="progress-legend">
                <div className="legend-item">
                  <div className="legend-color legend-termine"></div>
                  <span>Terminés ({stat.programmesTermines})</span>
                </div>
                <div className="legend-item">
                  <div className="legend-color legend-cours"></div>
                  <span>En cours ({stat.programmesEnCours})</span>
                </div>
                <div className="legend-item">
                  <div className="legend-color legend-attente"></div>
                  <span>En attente ({stat.programmesEnAttente})</span>
                </div>
              </div>

              {/* Taux de complétion grand format */}
              <div className="completion-highlight">
                <div className="completion-circle">
                  <svg viewBox="0 0 100 100" className="circular-progress">
                    <circle cx="50" cy="50" r="45" className="circle-bg" />
                    <circle 
                      cx="50" 
                      cy="50" 
                      r="45" 
                      className="circle-progress"
                      style={{
                        strokeDasharray: `${stat.tauxCompletion * 2.827} 282.7`,
                        transform: 'rotate(-90deg)',
                        transformOrigin: '50% 50%'
                      }}
                    />
                  </svg>
                  <div className="completion-text">
                    <div className="completion-value">{stat.tauxCompletion.toFixed(1)}%</div>
                    <div className="completion-label">Complétés</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        ))
      )}
    </div>
  );

  const renderProgression = () => (
    <div className="progression-container">
      <div className="filters">
        <input
          type="text"
          placeholder="🔍 Rechercher un programme..."
          value={filterSearch}
          onChange={(e) => setFilterSearch(e.target.value)}
          className="search-input"
        />
        <select
          value={filterCategorie}
          onChange={(e) => setFilterCategorie(e.target.value)}
          className="filter-select"
        >
          <option value="">Toutes les catégories</option>
          {categories.map((cat, idx) => (
            <option key={idx} value={cat}>{cat}</option>
          ))}
        </select>
        <select
          value={filterClasse}
          onChange={(e) => setFilterClasse(e.target.value)}
          className="filter-select"
        >
          <option value="">Toutes les classes</option>
          {classes.map((cls, idx) => (
            <option key={idx} value={cls}>{cls}</option>
          ))}
        </select>
        <select
          value={filterStatut}
          onChange={(e) => setFilterStatut(e.target.value)}
          className="filter-select"
        >
          <option value="">Tous les statuts</option>
          <option value="En attente">En attente</option>
          <option value="En cours">En cours</option>
          <option value="Terminé">Terminé</option>
        </select>
      </div>

      {filteredProgression.length === 0 ? (
        <p className="no-data">Aucun programme trouvé</p>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Programme</th>
                <th>Catégorie</th>
                <th>Classe</th>
                <th>Année</th>
                <th>Statut Actuel</th>
              </tr>
            </thead>
            <tbody>
              {filteredProgression.map((prog, index) => (
                <tr key={index}>
                  <td>{prog.programmeNom}</td>
                  <td>{prog.categorieNom}</td>
                  <td>
                    <span className={`badge ${getClasseBadgeClass(prog.classeNom)}`}>
                      {prog.classeNom}
                    </span>
                  </td>
                  <td>{formatAnneeExercice(prog.anneeExercice)}</td>
                  <td>
                    <span 
                      className={`badge ${getStatutBadgeClass(prog.statutFinalNom)}`}
                      style={getStatutBadgeStyle(prog.statutFinalNom)}
                    >
                      {getStatutLabel(prog.statutFinalNom)}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );

  const renderAvancement = () => (
    <div className="avancement-container">
      <div className="filters">
        <select
          value={filterAvancementCategorie}
          onChange={(e) => setFilterAvancementCategorie(e.target.value)}
          className="filter-select"
        >
          <option value="">Toutes les catégories</option>
          {categoriesAvancement.map((cat, idx) => (
            <option key={idx} value={cat}>{cat}</option>
          ))}
        </select>
        <select
          value={filterAvancementClasse}
          onChange={(e) => setFilterAvancementClasse(e.target.value)}
          className="filter-select"
        >
          <option value="">Toutes les classes</option>
          {classesAvancement.map((cls, idx) => (
            <option key={idx} value={cls}>{cls}</option>
          ))}
        </select>
        <select
          value={filterAvancementStatut}
          onChange={(e) => setFilterAvancementStatut(e.target.value)}
          className="filter-select"
        >
          <option value="">Tous les statuts</option>
          <option value="En attente">En attente</option>
          <option value="En cours">En cours</option>
          <option value="Terminé">Terminé</option>
        </select>
      </div>

      {filteredAvancement.length === 0 ? (
        <p className="no-data">Aucun programme trouvé</p>
      ) : (
        <div className="avancement-list">
          {filteredAvancement.map((prog) => (
            <div key={prog.programmeId} className="avancement-card">
              <div 
                className="avancement-header"
                onClick={() => toggleProgrammeExpansion(prog.programmeId)}
              >
                <div className="header-left">
                  <h4>
                    <span className="expand-icon">
                      {expandedProgrammes.has(prog.programmeId) ? '▼' : '▶'}
                    </span>
                    {' '}{prog.programmeNom}
                  </h4>
                  <div className="program-meta">
                    <span className="badge badge-category">📚 {prog.categorieNom}</span>
                    <span className={`badge ${getClasseBadgeClass(prog.classeNom)}`}>🎯 {prog.classeNom}</span>
                    <span className="badge badge-year">📅 {formatAnneeExercice(prog.anneeExercice)}</span>
                  </div>
                </div>
                <div className="header-right">
                  <span 
                    className={`badge badge-statut ${getStatutBadgeClass(prog.statutActuelNom)}`}
                    style={getStatutBadgeStyle(prog.statutActuelNom)}
                  >
                    {getStatutLabel(prog.statutActuelNom)}
                    
                  </span>
                  <span className="badge badge-changes">📊 {prog.nombreChangements} changement(s)</span>
                </div>
              </div>

              {expandedProgrammes.has(prog.programmeId) && (
                <div className="avancement-details">
                  {prog.datePremiereCP && (
                    <div className="detail-row">
                      <span className="detail-label">Première CP:</span>
                      <span className="detail-value">{formatDate(prog.datePremiereCP)}</span>
                    </div>
                  )}
                  {prog.dateDerniereCP && (
                    <div className="detail-row">
                      <span className="detail-label">Dernière CP:</span>
                      <span className="detail-value">{formatDate(prog.dateDerniereCP)}</span>
                    </div>
                  )}

                  <h5>📜 Historique des changements</h5>
                  <div className="historique-timeline">
                    {/* Afficher l'état initial "En attente" si le premier changement n'est pas "En attente" */}
                    {prog.historique.length > 0 && prog.historique[0].statusNom !== 'En attente' && (
                      <div className="timeline-item">
                        <div className="timeline-dot"></div>
                        <div className="timeline-content">
                          <div className="timeline-date">Période initiale</div>
                          <div className="timeline-change">
                            <span 
                              className={`badge ${getStatutBadgeClass('En attente')}`}
                              style={getStatutBadgeStyle('En attente')}
                            >
                              {getStatutLabel('En attente')}
                            </span>
                          </div>
                          <div className="timeline-auto">
                            <em>État initial du programme</em>
                          </div>
                        </div>
                      </div>
                    )}
                    {prog.historique.map((hist, index) => {
                      const ancienStatut = index > 0 ? prog.historique[index - 1].statusNom : 'En attente';
                      const isFirstChange = index === 0;
                      return (
                        <div key={hist.id} className="timeline-item">
                          <div className="timeline-dot"></div>
                          <div className="timeline-content">
                            <div className="timeline-date">{formatDate(hist.dateChangement)}</div>
                            <div className="timeline-change">
                              {(ancienStatut || isFirstChange) && (
                                <>
                                  <span 
                                    className={`badge ${getStatutBadgeClass(ancienStatut)}`}
                                    style={getStatutBadgeStyle(ancienStatut)}
                                  >
                                    {getStatutLabel(ancienStatut)}
                                  </span>
                                  <span className="arrow">→</span>
                                </>
                              )}
                              <span 
                                className={`badge ${getStatutBadgeClass(hist.statusNom)}`}
                                style={getStatutBadgeStyle(hist.statusNom)}
                              >
                                {getStatutLabel(hist.statusNom)}
                              </span>
                            </div>
                            {hist.classeProgressiveDate && (
                              <div className="timeline-cp">
                                CP du {new Date(hist.classeProgressiveDate).toLocaleDateString('fr-FR')}
                              </div>
                            )}
                            {!hist.classeProgressiveId && (
                              <div className="timeline-auto">
                                <em>Initialisation automatique</em>
                              </div>
                            )}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );

  return (
    <div className="historique-page">
      <div className="page-header">
        <h1>📈 Historique des Programmes</h1>
        <div className="header-actions">
          <select
            value={selectedAnneeId || ''}
            onChange={(e) => setSelectedAnneeId(e.target.value ? Number(e.target.value) : null)}
            className="annee-select"
          >
            <option value="">Toutes les années</option>
            {annees.map((annee) => (
              <option key={annee.id} value={annee.id}>
                {formatAnneeExercice(annee.annee)} {annee.estActif && '(Actif)'}
              </option>
            ))}
          </select>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="view-tabs">
        <button 
          className={`tab-btn ${viewMode === 'statistiques' ? 'active' : ''}`}
          onClick={() => setViewMode('statistiques')}
        >
          📊 Statistiques
        </button>
        <button 
          className={`tab-btn ${viewMode === 'progression' ? 'active' : ''}`}
          onClick={() => setViewMode('progression')}
        >
          📋 Progression Annuelle
        </button>
        <button 
          className={`tab-btn ${viewMode === 'avancement' ? 'active' : ''}`}
          onClick={() => setViewMode('avancement')}
        >
          🔍 Avancement Détaillé
        </button>
      </div>

      {loading ? (
        <div className="loading">Chargement...</div>
      ) : (
        <div className="view-content">
          {viewMode === 'statistiques' && renderStatistiques()}
          {viewMode === 'progression' && renderProgression()}
          {viewMode === 'avancement' && renderAvancement()}
        </div>
      )}
    </div>
  );
}
