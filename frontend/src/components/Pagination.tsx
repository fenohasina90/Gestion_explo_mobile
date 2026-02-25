import React from 'react';
import './Pagination.css';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  totalElements: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  onSizeChange: (size: number) => void;
}

const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  totalElements,
  pageSize,
  onPageChange,
  onSizeChange,
}) => {
  const startItem = currentPage * pageSize + 1;
  const endItem = Math.min((currentPage + 1) * pageSize, totalElements);

  const handlePrevious = () => {
    if (currentPage > 0) {
      onPageChange(currentPage - 1);
    }
  };

  const handleNext = () => {
    if (currentPage < totalPages - 1) {
      onPageChange(currentPage + 1);
    }
  };

  const handleFirst = () => {
    onPageChange(0);
  };

  const handleLast = () => {
    onPageChange(totalPages - 1);
  };

  const getPageNumbers = () => {
    const pages: (number | string)[] = [];
    const maxVisiblePages = 5;

    if (totalPages <= maxVisiblePages) {
      // Afficher toutes les pages
      for (let i = 0; i < totalPages; i++) {
        pages.push(i);
      }
    } else {
      // Afficher avec ellipses
      if (currentPage < 3) {
        // Début
        for (let i = 0; i < 4; i++) {
          pages.push(i);
        }
        pages.push('...');
        pages.push(totalPages - 1);
      } else if (currentPage > totalPages - 4) {
        // Fin
        pages.push(0);
        pages.push('...');
        for (let i = totalPages - 4; i < totalPages; i++) {
          pages.push(i);
        }
      } else {
        // Milieu
        pages.push(0);
        pages.push('...');
        for (let i = currentPage - 1; i <= currentPage + 1; i++) {
          pages.push(i);
        }
        pages.push('...');
        pages.push(totalPages - 1);
      }
    }

    return pages;
  };

  return (
    <div className="pagination-container">
      <div className="pagination-info">
        Affichage de {startItem} à {endItem} sur {totalElements} éléments
      </div>

      <div className="pagination-controls">
        <button
          className="pagination-btn"
          onClick={handleFirst}
          disabled={currentPage === 0}
          title="Première page"
        >
          «
        </button>

        <button
          className="pagination-btn"
          onClick={handlePrevious}
          disabled={currentPage === 0}
          title="Page précédente"
        >
          ‹
        </button>

        {getPageNumbers().map((page, index) =>
          typeof page === 'number' ? (
            <button
              key={index}
              className={`pagination-btn ${page === currentPage ? 'active' : ''}`}
              onClick={() => onPageChange(page)}
            >
              {page + 1}
            </button>
          ) : (
            <span key={index} className="pagination-ellipsis">
              {page}
            </span>
          )
        )}

        <button
          className="pagination-btn"
          onClick={handleNext}
          disabled={currentPage >= totalPages - 1}
          title="Page suivante"
        >
          ›
        </button>

        <button
          className="pagination-btn"
          onClick={handleLast}
          disabled={currentPage >= totalPages - 1}
          title="Dernière page"
        >
          »
        </button>
      </div>

      <div className="pagination-size">
        <label htmlFor="pageSize">Éléments par page :</label>
        <select
          id="pageSize"
          value={pageSize}
          onChange={(e) => onSizeChange(Number(e.target.value))}
          className="pagination-select"
        >
          <option value="5">5</option>
          <option value="10">10</option>
          <option value="20">20</option>
          <option value="50">50</option>
          <option value="100">100</option>
        </select>
      </div>
    </div>
  );
};

export default Pagination;
