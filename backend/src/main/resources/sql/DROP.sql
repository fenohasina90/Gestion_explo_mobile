BEGIN;

DROP TABLE IF EXISTS
  cp_details_instructeurs,
  cp_presence_explo,
  cp_presence_staff,
  participants_activites_explo,
  participants_activites_staff,
  details_activites,
  historique_programmes,
  programme_progression_annuelle,
  cp_details,
  classe_progressive,
  mouvement_budgetaire,
  journal,
  activites,
  budget_global,
  inscriptions,
  enfants,
  parents,
  staff,
  instructeur,
  utilisateur,
  roles_action,
  roles_staff,
  programmes,
  categorie_programme,
  programme_status,
  classes,
  annee_exercice,
  budget_status,
  activite_status,
  type
CASCADE;

COMMIT;