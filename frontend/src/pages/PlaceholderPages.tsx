const placeholder = (title: string, icon: string) => () => (
  <div className="page-container">
    <div className="page-header">
      <h1>{title}</h1>
    </div>
    <div className="page-content">
      <div className="placeholder-message">
        <div className="placeholder-icon">{icon}</div>
        <h3>Section en développement</h3>
        <p>Cette fonctionnalité sera bientôt disponible</p>
      </div>
    </div>
  </div>
);

export const InscriptionsPage = placeholder('Gestion des Inscriptions', '📝');
export const StaffPage = placeholder('Gestion du Staff', '👥');
export const BudgetPage = placeholder('Gestion du Budget', '💰');
export const RapportsPage = placeholder('Rapports et Statistiques', '📈');
