import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import './DashboardPage.css';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Tableau de bord</h1>
        <p>Bienvenue, {user?.username}</p>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">👶</div>
          <div className="stat-content">
            <h3>Enfants inscrits</h3>
            <p className="stat-value">--</p>
            <p className="stat-label">Total actifs</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">🎯</div>
          <div className="stat-content">
            <h3>Activités en cours</h3>
            <p className="stat-value">--</p>
            <p className="stat-label">Ce mois</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">📝</div>
          <div className="stat-content">
            <h3>Inscriptions</h3>
            <p className="stat-value">--</p>
            <p className="stat-label">Cette semaine</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">💰</div>
          <div className="stat-content">
            <h3>Budget</h3>
            <p className="stat-value">-- Ar</p>
            <p className="stat-label">Disponible</p>
          </div>
        </div>
      </div>

      <div className="dashboard-content">
        <div className="content-section">
          <h2>Informations utilisateur</h2>
          <div className="info-card">
            <div className="info-item">
              <span className="info-label">Nom d'utilisateur:</span>
              <span className="info-value">{user?.username}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Rôle:</span>
              <span className="info-value">{user?.role}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Année d'exercice:</span>
              <span className="info-value">{user?.anneeExercice}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Statut:</span>
              <span className="info-value status-active">
                {user?.active ? 'Actif' : 'Inactif'}
              </span>
            </div>
          </div>
        </div>

        <div className="content-section">
          <h2>Actions rapides</h2>
          <div className="quick-actions">
            <button className="action-btn">
              <span className="action-icon">➕</span>
              Nouvel enfant
            </button>
            <button className="action-btn">
              <span className="action-icon">📅</span>
              Nouvelle activité
            </button>
            <button className="action-btn">
              <span className="action-icon">✏️</span>
              Nouvelle inscription
            </button>
            <button className="action-btn">
              <span className="action-icon">📊</span>
              Générer rapport
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
