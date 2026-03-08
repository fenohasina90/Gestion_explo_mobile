-- Migration: Ajout de la gestion de l'historique des programmes
-- Date: 2026-03-04
-- Description: Ajoute le champ annee_exercice_id à historique_programmes et crée la table programme_progression_annuelle
-- Mise à jour: 2026-03-04 - classe_progressive_id devient nullable pour l'initialisation automatique

-- 1. Modifier la table historique_programmes
-- Note: Avec SQLite, on doit recréer la table complètement

-- Sauvegarder les données existantes
CREATE TABLE historique_programmes_backup AS SELECT * FROM historique_programmes;

-- Supprimer l'ancienne table
DROP TABLE historique_programmes;

-- Recréer la table avec la nouvelle structure
CREATE TABLE historique_programmes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    programme_id INTEGER NOT NULL,
    ancien_statut TEXT,
    nouveau_statut TEXT NOT NULL,
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    classe_progressive_id INTEGER, -- NULL pour initialisation automatique/système
    annee_exercice_id INTEGER NOT NULL,
    FOREIGN KEY (programme_id) REFERENCES programmes(id) ON DELETE CASCADE,
    FOREIGN KEY (classe_progressive_id) REFERENCES classe_progressive(id) ON DELETE SET NULL,
    FOREIGN KEY (annee_exercice_id) REFERENCES annees_exercice(id) ON DELETE CASCADE
);

-- Restaurer les données existantes (si elles existent)
-- Cette migration suppose que vous partez d'une base vierge ou migrez depuis l'ancien modèle
-- Ajustez selon votre cas

-- Supprimer la table de backup
DROP TABLE IF EXISTS historique_programmes_backup;


-- 2. Créer la table programme_progression_annuelle
CREATE TABLE IF NOT EXISTS programme_progression_annuelle (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    programme_id INTEGER NOT NULL,
    annee_exercice_id INTEGER NOT NULL,
    statut_final TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (programme_id) REFERENCES programmes(id) ON DELETE CASCADE,
    FOREIGN KEY (annee_exercice_id) REFERENCES annees_exercice(id) ON DELETE CASCADE,
    UNIQUE(programme_id, annee_exercice_id)
);

-- Index pour les requêtes fréquentes
CREATE INDEX IF NOT EXISTS idx_historique_programme_id ON historique_programmes(programme_id);
CREATE INDEX IF NOT EXISTS idx_historique_cp_id ON historique_programmes(classe_progressive_id);
CREATE INDEX IF NOT EXISTS idx_historique_annee_id ON historique_programmes(annee_exercice_id);
CREATE INDEX IF NOT EXISTS idx_historique_date ON historique_programmes(date_changement);
CREATE INDEX IF NOT EXISTS idx_progression_programme_id ON programme_progression_annuelle(programme_id);
CREATE INDEX IF NOT EXISTS idx_progression_annee_id ON programme_progression_annuelle(annee_exercice_id);
CREATE INDEX IF NOT EXISTS idx_progression_statut ON programme_progression_annuelle(statut_final);

-- 3. Initialiser tous les programmes à "EN_ATTENTE" pour l'année active
-- (si elle existe dans annees_exercice)
INSERT OR IGNORE INTO programme_progression_annuelle (programme_id, annee_exercice_id, statut_final, created_at, updated_at)
SELECT 
    p.id as programme_id,
    ae.id as annee_exercice_id,
    'EN_ATTENTE' as statut_final,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM programmes p
CROSS JOIN annees_exercice ae
WHERE ae.est_actif = 1;

-- 4. Créer des enregistrements d'historique pour l'initialisation automatique
INSERT INTO historique_programmes (programme_id, ancien_statut, nouveau_statut, classe_progressive_id, annee_exercice_id, date_changement)
SELECT 
    p.id,
    NULL,
    'EN_ATTENTE',
    NULL, -- NULL = initialisation automatique
    ae.id,
    CURRENT_TIMESTAMP
FROM programmes p
CROSS JOIN annees_exercice ae
WHERE ae.est_actif = 1;

-- 5. Vérification des données
SELECT 'Programmes' as Info, COUNT(*) as Total FROM programmes
UNION ALL
SELECT 'Historique programmes', COUNT(*) FROM historique_programmes
UNION ALL
SELECT 'Progressions annuelles', COUNT(*) FROM programme_progression_annuelle; 
    AND ppa.annee_exercice_id = ae.id
);
