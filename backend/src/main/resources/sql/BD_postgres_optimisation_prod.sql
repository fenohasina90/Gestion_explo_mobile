-- Optimisation PostgreSQL pour base existante (production)
-- Important: executer ce script hors transaction (psql par defaut)
-- Ce script utilise CREATE INDEX CONCURRENTLY: ne pas executer dans Supabase SQL Editor
-- (le SQL Editor encapsule souvent les requetes dans une transaction).
-- Pour Supabase SQL Editor, utiliser le fichier:
-- backend/src/main/resources/sql/BD_postgres_optimisation_supabase_editor.sql

CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Mouvements budgetaires
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_mouvement_budgetaire_annee_type_created_at
    ON mouvement_budgetaire(annee_exercice_id, type_id, created_at DESC);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_mouvement_budgetaire_created_at
    ON mouvement_budgetaire(created_at DESC);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_mouvement_budgetaire_description_trgm
    ON mouvement_budgetaire USING gin (LOWER(COALESCE(description, '')) gin_trgm_ops);

-- Journal
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_journal_timestamp
    ON journal(timestamp DESC);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_journal_utilisateur_timestamp
    ON journal(utilisateur_id, timestamp DESC);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_journal_action_trgm
    ON journal USING gin (LOWER(COALESCE(action, '')) gin_trgm_ops);

-- Inscriptions/annee-classe
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_inscriptions_annee_classe
    ON inscriptions(annee_exercice_id, classe_id);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_inscriptions_enfant_annee
    ON inscriptions(enfant_id, annee_exercice_id);

-- Classe progressive / historique programmes
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_classe_progressive_annee_date
    ON classe_progressive(annee_exercice_id, date_cp DESC);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_historique_programmes_programme_created_at
    ON historique_programmes(programme_id, created_at DESC);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_historique_programmes_cp_created_at
    ON historique_programmes(classe_progressive_id, created_at DESC);

-- Suppression logique
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_utilisateur_active_annee
    ON utilisateur(annee_exercice_id)
    WHERE etat <> 11;
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_staff_active_annee
    ON staff(annee_exercice_id)
    WHERE etat <> 11;

-- Recherche texte personnes
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_parents_nom_prenom_trgm
    ON parents USING gin (LOWER(nom || ' ' || prenom) gin_trgm_ops);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_enfants_nom_prenom_trgm
    ON enfants USING gin (LOWER(nom || ' ' || prenom) gin_trgm_ops);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_instructeur_nom_prenom_trgm
    ON instructeur USING gin (LOWER(nom || ' ' || prenom) gin_trgm_ops);

-- Mettre a jour les stats planner apres indexation
ANALYZE;
