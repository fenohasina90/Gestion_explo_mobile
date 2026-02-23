import React from 'react';
import './EnfantsPage.css';

export const EnfantsPage: React.FC = () => {
  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Gestion des Enfants</h1>
        <button className="primary-btn">
          ➕ Ajouter un enfant
        </button>
      </div>

      <div className="page-content">
        <div className="search-bar">
          <input 
            type="text" 
            placeholder="Rechercher un enfant..." 
            className="search-input"
          />
          <button className="search-btn">🔍 Rechercher</button>
        </div>

        <div className="placeholder-message">
          <div className="placeholder-icon">👶</div>
          <h3>Aucun enfant pour le moment</h3>
          <p>Cliquez sur "Ajouter un enfant" pour commencer</p>
        </div>
      </div>
    </div>
  );
};
