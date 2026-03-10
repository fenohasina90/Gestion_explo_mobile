-- Migration: Ajout du champ description et programme_id nullable dans cp_details
-- Date: 3 mars 2026

PRAGMA foreign_keys = OFF;

BEGIN TRANSACTION;

-- 1. Créer la nouvelle table avec programme_id nullable et description
CREATE TABLE cp_details_new (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    classe_progressive_id INTEGER NOT NULL,
    programme_id INTEGER,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (classe_progressive_id) REFERENCES classe_progressive(id),
    FOREIGN KEY (programme_id) REFERENCES programmes(id),
    UNIQUE(classe_progressive_id, programme_id)
);

-- 2. Copier les données existantes (description sera NULL pour les anciennes entrées)
INSERT INTO cp_details_new (id, classe_progressive_id, programme_id, description, created_at)
SELECT id, classe_progressive_id, programme_id, NULL, created_at
FROM cp_details;

-- 3. Supprimer l'ancienne table
DROP TABLE cp_details;

-- 4. Renommer la nouvelle table
ALTER TABLE cp_details_new RENAME TO cp_details;

COMMIT;

PRAGMA foreign_keys = ON;

-- 5. Vérifier la migration
SELECT 'Migration terminée avec succès!' as message;
SELECT COUNT(*) as nb_cp_details FROM cp_details;
