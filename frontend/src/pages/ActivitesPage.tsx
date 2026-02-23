import React from 'react';
import './ActivitesPage.css';

export const ActivitesPage: React.FC = () => {
  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Gestion des Activités</h1>
        <button className="primary-btn">
          ➕ Créer une activité
        </button>
      </div>

      <div className="page-content">
        <div className="filter-bar">
          <select className="filter-select">
            <option>Toutes les activités</option>
            <option>En cours</option>
            <option>À venir</option>
            <option>Terminées</option>
          </select>
        </div>

        <div className="placeholder-message">
          <div className="placeholder-icon">🎯</div>
          <h3>Aucune activité planifiée</h3>
          <p>Cliquez sur "Créer une activité" pour commencer</p>
        </div>
      </div>
    </div>
  );
};
