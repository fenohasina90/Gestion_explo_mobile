-- PostgreSQL Schema
-- Converti depuis BD_sqlite.sql

-- =========================
-- Taom-piasana
-- =========================

CREATE TABLE classes (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    logo VARCHAR(255),
    age INTEGER
);

CREATE TABLE annee_exercice (
    id SERIAL PRIMARY KEY,
    annee DATE NOT NULL,
    date_fin DATE NOT NULL,
    statuts_initialises INTEGER DEFAULT 0 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- Utilisateur & Staff
-- =========================

CREATE TABLE roles_staff (
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL
);

CREATE TABLE roles_action (
    id SERIAL PRIMARY KEY,
    role_id INTEGER,
    action VARCHAR(100) NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles_staff(id)
);

CREATE TABLE utilisateur (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role_id INTEGER,
    active BOOLEAN DEFAULT TRUE,
    annee_exercice_id INTEGER,
    etat INTEGER DEFAULT 1 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles_staff(id),
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id)
);

CREATE TABLE instructeur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    genre VARCHAR(10) NOT NULL,
    totem VARCHAR(100),
    telephone VARCHAR(15),
    est_chef_guide BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE staff (
    id SERIAL PRIMARY KEY,
    id_instructeur INTEGER,
    annee_exercice_id INTEGER,
    role_id INTEGER,
    etat INTEGER DEFAULT 1 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_instructeur) REFERENCES instructeur(id),
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id),
    FOREIGN KEY (role_id) REFERENCES roles_staff(id)
);

-- =========================
-- Parents & Enfants
-- =========================

CREATE TABLE parents (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    adresse VARCHAR(255),
    telephone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



CREATE TABLE enfants (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    genre VARCHAR(10) NOT NULL,
    date_naissance DATE,
    adresse VARCHAR(255),
    parent_id INTEGER,
    bapteme DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES parents(id)
);
INSERT INTO parents (nom, prenom, adresse, telephone) VALUES
('Mme', 'Fara 2', null, null); --21
INSERT INTO enfants (nom, prenom, genre, date_naissance, adresse, parent_id, bapteme) VALUES
('MANOROMANDA', 'Landa Heira Tielz', 'GARCON', '2016-11-21', null, 2, null),
('RASANJY', 'Tsiaroniaina Manohisoa Ariel', 'GARCON', '2016-09-29', null, 3, null),
('ANDRIANANTENAINA', 'Fanomezantsoa Filamatra Diary', 'GARCON', '2015-02-20', null, 4, null),
('ANDRIANARISON', 'Tanjona Ny Aina Itokiana', 'GARCON', '2015-12-13', null, 5, null),
('FENOINTSOA', 'Henika Fitahiana Lucah', 'GARCON', '2015-06-20', null, 6, null),
('RAKOTOZAFY', 'Tanjona', 'GARCON', '2015-03-16', null, 7, null),
('RANDRIAMIANDRISOA', 'Hajaniaina Harena', 'GARCON', '2015-12-19', null, 8, null),
('RANDRIANARIJAONA', 'Solofofandresena Princia', 'FILLE', '2015-09-09', null, 9, null),
('RAKOTONDRAJAO', 'Mathieu Fidinirina', 'GARCON', '2014-04-29', null, 1, null),
('RAMIANDRISOA', 'Tsaralova Fiàna', 'FILLE', '2014-01-31', null, 11, null),
('RANARISOLO', 'Nomenjanahary Honoré', 'GARCON', '2014-01-13', null, 12, null),
('MIARINTSOA', 'Sahaza Fitia Andriah', 'FILLE', '2013-02-28', null, 13, null),
('RAKOTONDRAJAO', 'Tiffah Yonnah', 'FILLE', '2013-06-23', null, 14, null),
('RAKOTONDRATSIMBA', 'Ny Tendry Milanto', 'FILLE', '2013-03-29', null, 15, null),
('RASOARINIRINA', 'Hanitriniaina Valimbavaka', 'FILLE', '2013-01-31', null, 16, null),
('TOMBOLAZA', 'Andriamisaina Manda', 'GARCON', '2013-09-06', null, 17, null),
('TOMBOLAZA', 'Andriamisaina Ny Aro', 'GARCON', '2013-09-06', null, 17, null),
('FANOMEZANTSOA', 'Hariseheno Nilaina', 'FILLE', '2012-03-25', null, 18, null),
('FENOINTSOA', 'Andrianina Mathieu', 'GARCON', '2012-07-31', null, 6, null),
('RAMIANDRISOA', 'Fifaliana', 'GARCON', '2012-06-07', null, 8, null),
('RANDRIAMAMPIANINA', 'Nirina Joannah', 'FILLE', '2012-09-14', null, 19, null),
('RATSARAEFADAHY', 'Tefinjanahary André', 'GARCON', '2012-01-01', null, 20, null),
('ANDRIANARISON', 'Fitahiantsoa Nandrianina', 'GARCON', '2011-06-05', null, 5, null),
('MANOROMANDA', 'Landa Ahiëlle', 'FILLE', '2011-06-05', null, 2, null),
('RAKOTONIAINA', 'Fanomezanjanahary Jonathan Jeremia', 'GARCON', '2011-06-20', null, 21, null),
('RAKOTONIAINA', 'Fanomezanjanahary Jonathan Jeremie', 'GARCON', '2011-06-20', null, 21, null);





CREATE TABLE inscriptions (
    id SERIAL PRIMARY KEY,
    enfant_id INTEGER,
    annee_exercice_id INTEGER,
    est_assurance BOOLEAN DEFAULT FALSE,
    classe_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (enfant_id) REFERENCES enfants(id),
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id),
    FOREIGN KEY (classe_id) REFERENCES classes(id)
);

-- =========================
-- Budget & Activités
-- =========================

CREATE TABLE budget_status (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE budget_global (
    id SERIAL PRIMARY KEY,
    annee_exercice_id INTEGER,
    montant DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status_id INTEGER,
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id),
    FOREIGN KEY (status_id) REFERENCES budget_status(id)
);

CREATE TABLE activite_status (
    id SERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE activites (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    date_debut DATE,
    date_fin DATE,
    montant DECIMAL(10,2),
    id_budget INTEGER,
    status_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_budget) REFERENCES budget_global(id),
    FOREIGN KEY (status_id) REFERENCES activite_status(id)
);

CREATE TABLE details_activites (
    id SERIAL PRIMARY KEY,
    activite_id INTEGER,
    details TEXT,
    montant DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (activite_id) REFERENCES activites(id)
);

-- =========================
-- Participants
-- =========================

CREATE TABLE participants_activites_explo (
    id SERIAL PRIMARY KEY,
    activite_id INTEGER,
    enfant_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (activite_id) REFERENCES activites(id),
    FOREIGN KEY (enfant_id) REFERENCES inscriptions(id)
);

CREATE TABLE participants_activites_staff (
    id SERIAL PRIMARY KEY,
    activite_id INTEGER,
    staff_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (activite_id) REFERENCES activites(id),
    FOREIGN KEY (staff_id) REFERENCES staff(id)
);

-- =========================
-- Programme
-- =========================

CREATE TABLE categorie_programme (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);

CREATE TABLE programmes (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    description TEXT,
    categorie_id INTEGER,
    classes_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (categorie_id) REFERENCES categorie_programme(id),
    FOREIGN KEY (classes_id) REFERENCES classes(id)
);

CREATE TABLE programme_status (
    id SERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL
);

-- =========================
-- Classe Progressive (CP)
-- =========================

CREATE TABLE classe_progressive (
    id SERIAL PRIMARY KEY,
    date_cp DATE NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    niveau INTEGER,
    etat INTEGER DEFAULT 0 NOT NULL,
    annee_exercice_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id)
);

CREATE TABLE cp_details (
    id SERIAL PRIMARY KEY,
    classe_progressive_id INTEGER NOT NULL,
    programme_id INTEGER,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (classe_progressive_id) REFERENCES classe_progressive(id),
    FOREIGN KEY (programme_id) REFERENCES programmes(id),
    UNIQUE(classe_progressive_id, programme_id)
);

CREATE TABLE cp_details_instructeurs (
    id SERIAL PRIMARY KEY,
    cp_details_id INTEGER NOT NULL,
    instructeur_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cp_details_id) REFERENCES cp_details(id) ON DELETE CASCADE,
    FOREIGN KEY (instructeur_id) REFERENCES instructeur(id),
    UNIQUE(cp_details_id, instructeur_id)
);

CREATE TABLE cp_presence_explo (
    id SERIAL PRIMARY KEY,
    classe_progressive_id INTEGER,
    enfant_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (classe_progressive_id) REFERENCES classe_progressive(id),
    FOREIGN KEY (enfant_id) REFERENCES inscriptions(id)
);

CREATE TABLE cp_presence_staff (
    id SERIAL PRIMARY KEY,
    classe_progressive_id INTEGER,
    staff_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (classe_progressive_id) REFERENCES classe_progressive(id),
    FOREIGN KEY (staff_id) REFERENCES staff(id)
);

CREATE TABLE historique_programmes (
    id SERIAL PRIMARY KEY,
    programme_id INTEGER NOT NULL,
    classe_progressive_id INTEGER,  -- NULL pour initialisation automatique/système
    status_id INTEGER NOT NULL,
    annee_exercice_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (programme_id) REFERENCES programmes(id),
    FOREIGN KEY (classe_progressive_id) REFERENCES classe_progressive(id),
    FOREIGN KEY (status_id) REFERENCES programme_status(id),
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id)
);

CREATE TABLE programme_progression_annuelle (
    id SERIAL PRIMARY KEY,
    programme_id INTEGER NOT NULL,
    annee_exercice_id INTEGER NOT NULL,
    statut_final_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (programme_id) REFERENCES programmes(id),
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id),
    FOREIGN KEY (statut_final_id) REFERENCES programme_status(id),
    UNIQUE(programme_id, annee_exercice_id)
);

-- =========================
-- Mouvement budgétaire
-- =========================

CREATE TABLE type (
    id SERIAL PRIMARY KEY,
    type VARCHAR(20)
);

CREATE TABLE mouvement_budgetaire (
    id SERIAL PRIMARY KEY,
    annee_exercice_id INTEGER,
    type_id INTEGER,
    montant DECIMAL(10,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id),
    FOREIGN KEY (type_id) REFERENCES type(id)
);

-- =========================
-- Journal
-- =========================

CREATE TABLE journal (
    id SERIAL PRIMARY KEY,
    action VARCHAR(255) NOT NULL,
    utilisateur_id INTEGER,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id)
);

-- =========================
-- Optimisation Performance
-- =========================

-- Recherche textuelle rapide (ILIKE/LIKE sur texte libre)
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Index FK principaux (accelere JOIN + filtres frequents)
CREATE INDEX IF NOT EXISTS idx_roles_action_role_id ON roles_action(role_id);
CREATE INDEX IF NOT EXISTS idx_utilisateur_role_id ON utilisateur(role_id);
CREATE INDEX IF NOT EXISTS idx_utilisateur_annee_exercice_id ON utilisateur(annee_exercice_id);
CREATE INDEX IF NOT EXISTS idx_staff_id_instructeur ON staff(id_instructeur);
CREATE INDEX IF NOT EXISTS idx_staff_annee_exercice_id ON staff(annee_exercice_id);
CREATE INDEX IF NOT EXISTS idx_staff_role_id ON staff(role_id);
CREATE INDEX IF NOT EXISTS idx_enfants_parent_id ON enfants(parent_id);
CREATE INDEX IF NOT EXISTS idx_inscriptions_enfant_id ON inscriptions(enfant_id);
CREATE INDEX IF NOT EXISTS idx_inscriptions_annee_exercice_id ON inscriptions(annee_exercice_id);
CREATE INDEX IF NOT EXISTS idx_inscriptions_classe_id ON inscriptions(classe_id);
CREATE INDEX IF NOT EXISTS idx_budget_global_annee_exercice_id ON budget_global(annee_exercice_id);
CREATE INDEX IF NOT EXISTS idx_budget_global_status_id ON budget_global(status_id);
CREATE INDEX IF NOT EXISTS idx_activites_id_budget ON activites(id_budget);
CREATE INDEX IF NOT EXISTS idx_activites_status_id ON activites(status_id);
CREATE INDEX IF NOT EXISTS idx_details_activites_activite_id ON details_activites(activite_id);
CREATE INDEX IF NOT EXISTS idx_participants_explo_activite_id ON participants_activites_explo(activite_id);
CREATE INDEX IF NOT EXISTS idx_participants_explo_enfant_id ON participants_activites_explo(enfant_id);
CREATE INDEX IF NOT EXISTS idx_participants_staff_activite_id ON participants_activites_staff(activite_id);
CREATE INDEX IF NOT EXISTS idx_participants_staff_staff_id ON participants_activites_staff(staff_id);
CREATE INDEX IF NOT EXISTS idx_programmes_categorie_id ON programmes(categorie_id);
CREATE INDEX IF NOT EXISTS idx_programmes_classes_id ON programmes(classes_id);
CREATE INDEX IF NOT EXISTS idx_classe_progressive_annee_exercice_id ON classe_progressive(annee_exercice_id);
CREATE INDEX IF NOT EXISTS idx_cp_details_programme_id ON cp_details(programme_id);
CREATE INDEX IF NOT EXISTS idx_cp_details_instructeurs_cp_details_id ON cp_details_instructeurs(cp_details_id);
CREATE INDEX IF NOT EXISTS idx_cp_details_instructeurs_instructeur_id ON cp_details_instructeurs(instructeur_id);
CREATE INDEX IF NOT EXISTS idx_cp_presence_explo_cp_id ON cp_presence_explo(classe_progressive_id);
CREATE INDEX IF NOT EXISTS idx_cp_presence_explo_enfant_id ON cp_presence_explo(enfant_id);
CREATE INDEX IF NOT EXISTS idx_cp_presence_staff_cp_id ON cp_presence_staff(classe_progressive_id);
CREATE INDEX IF NOT EXISTS idx_cp_presence_staff_staff_id ON cp_presence_staff(staff_id);
CREATE INDEX IF NOT EXISTS idx_historique_programmes_programme_id ON historique_programmes(programme_id);
CREATE INDEX IF NOT EXISTS idx_historique_programmes_cp_id ON historique_programmes(classe_progressive_id);
CREATE INDEX IF NOT EXISTS idx_historique_programmes_status_id ON historique_programmes(status_id);
CREATE INDEX IF NOT EXISTS idx_historique_programmes_annee_id ON historique_programmes(annee_exercice_id);
CREATE INDEX IF NOT EXISTS idx_programme_progression_annee_id ON programme_progression_annuelle(annee_exercice_id);
CREATE INDEX IF NOT EXISTS idx_mouvement_budgetaire_annee_id ON mouvement_budgetaire(annee_exercice_id);
CREATE INDEX IF NOT EXISTS idx_mouvement_budgetaire_type_id ON mouvement_budgetaire(type_id);
CREATE INDEX IF NOT EXISTS idx_journal_utilisateur_id ON journal(utilisateur_id);

-- Index composes pour tri/filtrage les plus frequents
CREATE INDEX IF NOT EXISTS idx_mouvement_budgetaire_annee_type_created_at
    ON mouvement_budgetaire(annee_exercice_id, type_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_mouvement_budgetaire_created_at
    ON mouvement_budgetaire(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_journal_timestamp
    ON journal(timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_journal_utilisateur_timestamp
    ON journal(utilisateur_id, timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_classe_progressive_annee_date
    ON classe_progressive(annee_exercice_id, date_cp DESC);
CREATE INDEX IF NOT EXISTS idx_historique_programmes_programme_created_at
    ON historique_programmes(programme_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_historique_programmes_cp_created_at
    ON historique_programmes(classe_progressive_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_inscriptions_annee_classe
    ON inscriptions(annee_exercice_id, classe_id);
CREATE INDEX IF NOT EXISTS idx_inscriptions_enfant_annee
    ON inscriptions(enfant_id, annee_exercice_id);

-- Index partiels pour tables avec suppression logique (etat <> 11)
CREATE INDEX IF NOT EXISTS idx_utilisateur_active_username
    ON utilisateur(username)
    WHERE etat <> 11;
CREATE INDEX IF NOT EXISTS idx_utilisateur_active_annee
    ON utilisateur(annee_exercice_id)
    WHERE etat <> 11;
CREATE INDEX IF NOT EXISTS idx_staff_active_annee
    ON staff(annee_exercice_id)
    WHERE etat <> 11;
CREATE INDEX IF NOT EXISTS idx_staff_active_instructeur_annee
    ON staff(id_instructeur, annee_exercice_id)
    WHERE etat <> 11;

-- Index trigram pour recherche "contains" sur texte
CREATE INDEX IF NOT EXISTS idx_mouvement_budgetaire_description_trgm
    ON mouvement_budgetaire USING gin (LOWER(COALESCE(description, '')) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_journal_action_trgm
    ON journal USING gin (LOWER(COALESCE(action, '')) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_parents_nom_prenom_trgm
    ON parents USING gin (LOWER(nom || ' ' || prenom) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_enfants_nom_prenom_trgm
    ON enfants USING gin (LOWER(nom || ' ' || prenom) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_instructeur_nom_prenom_trgm
    ON instructeur USING gin (LOWER(nom || ' ' || prenom) gin_trgm_ops);