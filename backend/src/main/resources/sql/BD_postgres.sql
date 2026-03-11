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
    nom VARCHAR(100) NOT NULL,
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
-- Données initiales
-- =========================

INSERT INTO roles_staff (role_name) VALUES
('Directeur'),
('Co_Directeur'),
('Secrétaire'),
('Instructeur');

INSERT INTO classes (nom, logo, age) VALUES
('Ami', 'Ami.png', 10),
('Compagnon', 'Compagnon.png', 11),
('Eclaireur', 'Eclaireur.png', 12),
('Pionnier', 'Pionnier.png', 13),
('Voyageur', 'Voyageur.png', 14),
('Guide', 'Guide.png', 15);

INSERT INTO roles_action (role_id, action) VALUES
(1, 'CREER'),
(1, 'MODIFIER'),
(1, 'SUPPRIMER'),
(1, 'CONSULTER'),
(2, 'MODIFIER'),
(2, 'CONSULTER'),
(3, 'CONSULTER'),
(4, 'CONSULTER');

INSERT INTO budget_status (nom) VALUES
('Créé'),
('Approuvé comité');

INSERT INTO activite_status (status) VALUES
('En attente'),
('Terminé'),
('Annulé'),
('Rejeté');

INSERT INTO categorie_programme (nom) VALUES
('Lovan'' ny fiangonana'),
('Ankapobeny'),
('Fikarohana ara-panahy'),
('Fanompoana ny hafa'),
('Fahasalamana sy toe-batana tomady'),
('Fiainana ankalamanjana'),
('Lalindalina kokoa'),
('Asa manavanana');

INSERT INTO programmes (nom, description, categorie_id, classes_id) VALUES
('Boky miara-mihira', 'Mianara hira 10 vaovao ao amin''ny boky fiangonana', 1, 1), -- Ami
('Tantara ara-baiboly', 'Mitantara tantara ara-baiboly 3', 1, 1), -- Ami
('Hira fiderana', 'Mianara hira fiderana 5', 1, 2), -- Compagnon
('Lesona Alahady', 'Mandray anjara amin''ny lesona Alahady mandritra ny 3 volana', 1, 3), -- Eclaireur
('Fampianarana Baiboly', 'Manomana fampianarana Baiboly ho an''ny kilasy kely', 1, 4), -- Pionnier
('Toriteny', 'Manome toriteny fohy mandritra ny fanompoam-pivavahana', 1, 5), -- Voyageur
('Fitarika ny fiankohofana', 'Mitari-piankohofana mandritra ny 1 volana', 1, 6), -- Guide

('Fivoriana sy fandaminana', 'Mandray anjara amin''ny fivoriana fandaminana', 2, 1), -- Ami
('Fiaraha-miasa', 'Miara-miasa amin''ny namana 3', 2, 2), -- Compagnon
('Tetibola', 'Mianatra mitantana tetibola', 2, 3), -- Eclaireur
('Fanatanterahana tetikasa', 'Manatanteraka tetikasa iray', 2, 4), -- Pionnier
('Fitantanana fotoana', 'Mamorona agenda isan-kerinandro', 2, 5), -- Voyageur
('Fitarika ekipa', 'Mitondra ekipa mandritra ny 1 volana', 2, 6), -- Guide

('Vakiteny Baiboly', 'Mamaky Baiboly isanandro mandritra ny 1 volana', 3, 1), -- Ami
('Salamo', 'Mianatra Salamo 3', 3, 2), -- Compagnon
('Bokin''ny Baiboly', 'Mianatra momba ny bokin''ny Baiboly 5', 3, 2), -- Compagnon
('Toetran''Andriamanitra', 'Mianatra toetran''Andriamanitra 5', 3, 3), -- Eclaireur
('Vavaka', 'Manoratra diary vavaka mandritra ny 1 volana', 3, 4), -- Pionnier
('Famakiana andinin-teny', 'Mamakiteny andinin-teny 20', 3, 5), -- Voyageur
('Fandalinana lalina', 'Manao fandalinana lalina momba ny toko iray', 3, 6), -- Guide

('Fanampiana ray aman-dreny', 'Manampy ray aman-dreny ao an-trano', 4, 1), -- Ami
('Fitsidihana marary', 'Mitsidika olona marary', 4, 2), -- Compagnon
('Fanadiovana manodidina', 'Manadio ny manodidina ny fiangonana', 4, 2), -- Compagnon
('Fanampiana ny mpianatra kely', 'Manampy ny mpianatra kely hianatra', 4, 3), -- Eclaireur
('Fanomezana', 'Manome fanomezana ho an''ny sahirana', 4, 4), -- Pionnier
('Fikarakarana hetsika', 'Manampy amin''ny fikarakarana hetsika', 4, 5), -- Voyageur
('Tetikasa ho an''ny fokontany', 'Manatanteraka tetikasa ho an''ny fokontany', 4, 6), -- Guide

('Fanatanjahan-tena', 'Manao fanatanjahan-tena 3 isan-kerinandro', 5, 1), -- Ami
('Fisakafoana ara-pahasalamana', 'Mianatra momba ny sakafo mahasalama', 5, 2), -- Compagnon
('Fidiovana', 'Mianatra mikarakara tena', 5, 3), -- Eclaireur
('Torimaso', 'Mianatra momba ny torimaso ara-pahasalamana', 5, 4), -- Pionnier
('Fanatanjahan-tena mahery', 'Manao fanatanjahan-tena 5 isan-kerinandro', 5, 5), -- Voyageur
('Fitsaboana voalohany', 'Mianatra fitsaboana voalohany', 5, 6), -- Guide

('Fambolena', 'Mamboly voninkazo na legioma', 6, 1), -- Ami
('Fitsangatsanganana', 'Manao fitsangatsanganana 2', 6, 2), -- Compagnon
('Fanjonoana', 'Mianatra manjono', 6, 3), -- Eclaireur
('Fampiana tranolay', 'Mianatra mampianatra tranolay', 6, 4), -- Pionnier
('Fahavelomana any an''ala', 'Mianatra fomba fahavelomana any an''ala', 6, 5), -- Voyageur
('Lalan-kizorana', 'Manomana sy manao lalan-kizorana', 6, 6), -- Guide

('Zava-kanto', 'Mamorona zavakanto iray', 7, 1), -- Ami
('Mozika', 'Mianatra mozika', 7, 2), -- Compagnon
('Dihy', 'Mianatra dihy vaovao', 7, 3), -- Eclaireur
('Sary', 'Manao sary 3', 7, 4), -- Pionnier
('Tononkalo', 'Manoratra tononkalo 2', 7, 5), -- Voyageur
('Hai-tao an-tanana', 'Manao asa tanana sarotra', 7, 6), -- Guide

('Asa fanjairana', 'Mianatra manjaitra', 8, 1), -- Ami
('Fandrahoan-tsakafo', 'Mahandro sakafo tsotra', 8, 2), -- Compagnon
('Asa hazo', 'Mianatra asa hazo', 8, 3), -- Eclaireur
('Fanjairana mandroso', 'Manjaitra akanjo tsotra', 8, 4), -- Pionnier
('Fambolena mandroso', 'Mikarakara zaridaina', 8, 5), -- Voyageur
('Asa vy', 'Mianatra asa vy', 8, 6); -- Guide

INSERT INTO programme_status (status) VALUES
('En attente'),
('En cours'),
('Terminé');

INSERT INTO type (type) VALUES
('RECETTE'),
('DEPENSE');

-- Année d'exercice en cours
-- PostgreSQL: CURRENT_DATE au lieu de date('now')
INSERT INTO annee_exercice (annee, date_fin, created_at) VALUES
(DATE_TRUNC('year', CURRENT_DATE), DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year' - INTERVAL '1 day', CURRENT_TIMESTAMP);

-- Utilisateur par défaut (directeur/directeur123)
-- Hash BCrypt généré par Spring Security BCryptPasswordEncoder
INSERT INTO utilisateur (username, password_hash, role_id, active, annee_exercice_id, created_at, updated_at) VALUES
('directeur', '$2a$10$jUuOSBA7kVjDxLfvbwa2bObataLc7L3/zVz.IYQsecOE5FwaT.PZa', 1, TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
