import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import './Layout.css';

interface LayoutProps {
  children: React.ReactNode;
}

/**
 * Layout principal de l'application avec navigation
 */
export const Layout: React.FC<LayoutProps> = ({ children }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div className="layout">
      <header className="header">
        <div className="header-content">
          <h1 className="app-title">Gestion Explorateurs</h1>
          {user && (
            <div className="user-info">
              <span className="user-name">{user.username}</span>
              <span className="user-role">({user.role})</span>
              <span className="user-year">Année: {user.anneeExercice}</span>
              <button onClick={handleLogout} className="logout-btn">
                Déconnexion
              </button>
            </div>
          )}
        </div>
      </header>

      <nav className="sidebar">
        <ul className="nav-menu">
          <li>
            <Link to="/dashboard" className="nav-link">
              📊 Tableau de bord
            </Link>
          </li>
          <li>
            <Link to="/enfants" className="nav-link">
              👶 Enfants
            </Link>
          </li>
          <li>
            <Link to="/activites" className="nav-link">
              🎯 Activités
            </Link>
          </li>
          <li>
            <Link to="/inscriptions" className="nav-link">
              📝 Inscriptions
            </Link>
          </li>
          <li>
            <Link to="/staff" className="nav-link">
              👥 Staff
            </Link>
          </li>
          <li>
            <Link to="/budget" className="nav-link">
              💰 Budget
            </Link>
          </li>
          <li>
            <Link to="/mouvements-budgetaires" className="nav-link">
              💵 Mouvements Budgétaires
            </Link>
          </li>
          <li>
            <Link to="/rapports" className="nav-link">
              📈 Rapports
            </Link>
          </li>
          
          {/* Section Programmes */}
          <li className="nav-divider">
            <span>Programmes</span>
          </li>
          <li>
            <Link to="/categories-programme" className="nav-link">
              📚 Catégories
            </Link>
          </li>
          <li>
            <Link to="/programmes" className="nav-link">
              📖 Programmes
            </Link>
          </li>
          <li>
            <Link to="/cp" className="nav-link">
              🎯 Classes Progressives
            </Link>
          </li>
          <li>
            <Link to="/historique-programmes" className="nav-link">
              📈 Historique Programmes
            </Link>
          </li>
          
          {/* Section Compte */}
          <li className="nav-divider">
            <span>Mon Compte</span>
          </li>
          <li>
            <Link to="/mon-profil" className="nav-link">
              👤 Mon Profil
            </Link>
          </li>
          
          {/* Journal d'audit (visible pour tous) */}
          <li className="nav-divider">
            <span>Audit</span>
          </li>
          <li>
            <Link to="/journal" className="nav-link">
              📋 Journal d'audit
            </Link>
          </li>
          
          {/* Section Administration (visible uniquement pour le Directeur) */}
          {user && user.role === 'Directeur' && (
            <>
              <li className="nav-divider">
                <span>Administration</span>
              </li>
              <li>
                <Link to="/utilisateurs" className="nav-link">
                  👥 Utilisateurs
                </Link>
              </li>
              <li>
                <Link to="/annees-exercice" className="nav-link">
                  📅 Années d'exercice
                </Link>
              </li>
            </>
          )}
        </ul>
      </nav>

      <main className="main-content">
        {children}
      </main>
    </div>
  );
};
