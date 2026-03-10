-- Migration pour ajouter le champ statuts_initialises à la table annee_exercice
-- Date: 2026-03-04
-- Description: Permet de tracker si les statuts des programmes ont été initialisés pour une année

-- Ajouter le champ statuts_initialises (par défaut FALSE)
ALTER TABLE annee_exercice ADD COLUMN statuts_initialises INTEGER DEFAULT 0 NOT NULL;

-- Commentaire pour SQLite (les années déjà existantes ne sont pas initialisées)
-- Si vous souhaitez marquer certaines années comme déjà initialisées, utilisez:
-- UPDATE annee_exercice SET statuts_initialises = 1 WHERE id = <id_annee>;
