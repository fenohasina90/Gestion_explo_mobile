import { useState, useEffect } from 'react';
import journalService from '../services/journal.service';
import type { JournalEntry, JournalFilterRequest } from '../types';
import './JournalPage.css';

export function JournalPage() {
  const [journalEntries, setJournalEntries] = useState<JournalEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showFilters, setShowFilters] = useState(false);
  const [filters, setFilters] = useState<JournalFilterRequest>({
    dateDebut: '',
    dateFin: '',
    searchText: '',
  });
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const journalData = await journalService.getAllJournal();
      setJournalEntries(journalData);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement du journal');
    } finally {
      setLoading(false);
    }
  };

  const handleFilter = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      
      // Préparer les filtres en format ISO pour le backend
      const filterRequest: JournalFilterRequest = {
        searchText: filters.searchText || undefined,
      };

      // Convertir les dates au format attendu par le backend (yyyy-MM-ddTHH:mm:ss)
      if (filters.dateDebut) {
        filterRequest.dateDebut = `${filters.dateDebut}T00:00:00`;
      }
      if (filters.dateFin) {
        filterRequest.dateFin = `${filters.dateFin}T23:59:59`;
      }

      // Si aucun filtre n'est défini, récupérer tout
      if (!filterRequest.dateDebut && !filterRequest.dateFin && !filterRequest.searchText) {
        const data = await journalService.getAllJournal();
        setJournalEntries(data);
      } else {
        const data = await journalService.filterJournal(filterRequest);
        setJournalEntries(data);
      }
    } catch (err: any) {
      setError(err.message || 'Erreur lors du filtrage');
    } finally {
      setLoading(false);
    }
  };

  const handleResetFilters = async () => {
    setFilters({
      dateDebut: '',
      dateFin: '',
      searchText: '',
    });
    setCurrentPage(1);
    await loadData();
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    }).format(date);
  };

  // Calculs de pagination
  const totalPages = Math.ceil(journalEntries.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentEntries = journalEntries.slice(startIndex, endIndex);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleItemsPerPageChange = (value: number) => {
    setItemsPerPage(value);
    setCurrentPage(1);
  };

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div className="journal-page">
      <div className="page-header">
        <h1>Journal d'Audit</h1>
        <button
          className="btn-filters"
          onClick={() => setShowFilters(!showFilters)}
        >
          {showFilters ? '🔽 Masquer les filtres' : '🔼 Afficher les filtres'}
        </button>
      </div>

      {error && (
        <div className="error-message">
          {error}
          <button onClick={() => setError(null)}>×</button>
        </div>
      )}

      {showFilters && (
        <div className="filters-section">
          <form onSubmit={handleFilter} className="filters-form">
            <div className="filters-grid">
              <div className="filter-group">
                <label htmlFor="dateDebut">Date de début</label>
                <input
                  type="date"
                  id="dateDebut"
                  value={filters.dateDebut}
                  onChange={(e) => setFilters({ ...filters, dateDebut: e.target.value })}
                />
              </div>

              <div className="filter-group">
                <label htmlFor="dateFin">Date de fin</label>
                <input
                  type="date"
                  id="dateFin"
                  value={filters.dateFin}
                  onChange={(e) => setFilters({ ...filters, dateFin: e.target.value })}
                />
              </div>

              <div className="filter-group">
                <label htmlFor="searchText">Recherche</label>
                <input
                  type="text"
                  id="searchText"
                  placeholder="Rechercher dans les actions..."
                  value={filters.searchText}
                  onChange={(e) => setFilters({ ...filters, searchText: e.target.value })}
                />
              </div>
            </div>

            <div className="filters-actions">
              <button type="submit" className="btn-primary">
                Appliquer les filtres
              </button>
              <button type="button" className="btn-secondary" onClick={handleResetFilters}>
                Réinitialiser
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="journal-stats">
        <div className="stats-left">
          <p>
            <strong>{journalEntries.length}</strong> entrée{journalEntries.length !== 1 ? 's' : ''} trouvée{journalEntries.length !== 1 ? 's' : ''}
          </p>
        </div>
        <div className="stats-right">
          <label htmlFor="itemsPerPage">Afficher :</label>
          <select
            id="itemsPerPage"
            value={itemsPerPage}
            onChange={(e) => handleItemsPerPageChange(Number(e.target.value))}
          >
            <option value={10}>10</option>
            <option value={25}>25</option>
            <option value={50}>50</option>
            <option value={100}>100</option>
          </select>
          <span>par page</span>
        </div>
      </div>

      <div className="journal-list">
        {journalEntries.length === 0 ? (
          <div className="empty-state">
            <p>Aucune entrée trouvée dans le journal</p>
          </div>
        ) : (
          <table className="journal-table">
            <thead>
              <tr>
                <th>Date et heure</th>
                <th>Utilisateur</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {currentEntries.map((entry) => (
                <tr key={entry.id}>
                  <td className="date-cell">{formatDate(entry.timestamp)}</td>
                  <td className="user-cell">
                    <span className="username">{entry.username}</span>
                  </td>
                  <td className="action-cell">{entry.action}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {journalEntries.length > 0 && totalPages > 1 && (
        <div className="pagination">
          <button
            className="pagination-btn"
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 1}
          >
            ‹ Précédent
          </button>
          
          <div className="pagination-numbers">
            {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => {
              // Afficher les premières pages, les dernières pages, et les pages autour de la page actuelle
              if (
                page === 1 ||
                page === totalPages ||
                (page >= currentPage - 1 && page <= currentPage + 1)
              ) {
                return (
                  <button
                    key={page}
                    className={`pagination-number ${page === currentPage ? 'active' : ''}`}
                    onClick={() => handlePageChange(page)}
                  >
                    {page}
                  </button>
                );
              } else if (page === currentPage - 2 || page === currentPage + 2) {
                return <span key={page} className="pagination-ellipsis">...</span>;
              }
              return null;
            })}
          </div>

          <button
            className="pagination-btn"
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
          >
            Suivant ›
          </button>
        </div>
      )}
    </div>
  );
}
