PRAGMA foreign_keys = ON;

-- =========================
-- Taom-piasana
-- =========================

CREATE TABLE classes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    logo TEXT,
    age INTEGER
);

INSERT INTO classes (nom, logo, age) VALUES
('Ami', 'Ami.png', 10),
('Compagnon', 'Compagnon.png', 11),
('Eclaireur', 'Eclaireur.png', 12),
('Pionnier', 'Pionnier.png', 13),
('Voyageur', 'Voyageur.png', 14),
('Guide', 'Guide.png', 15);

CREATE TABLE annee_exercice (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    annee DATE NOT NULL,
    date_fin DATE NOT NULL,
    statuts_initialises INTEGER DEFAULT 0 NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- Utilisateur & Staff
-- =========================

CREATE TABLE roles_staff (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_name TEXT NOT NULL
);

INSERT INTO roles_staff (role_name) VALUES
('Directeur'),
('Co_Directeur'),
('Secrétaire'),
('Instructeur');

CREATE TABLE roles_action (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_id INTEGER,
    action TEXT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles_staff(id)
);

INSERT INTO roles_action (role_id, action) VALUES
(1, 'CREER'),
(1, 'MODIFIER'),
(1, 'SUPPRIMER'),
(1, 'CONSULTER'),
(2, 'MODIFIER'),
(2, 'CONSULTER'),
(3, 'CONSULTER'),
(4, 'CONSULTER');

CREATE TABLE utilisateur (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role_id INTEGER,
    active BOOLEAN DEFAULT 1,
    annee_exercice_id INTEGER,
    etat INTEGER DEFAULT 1 NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles_staff(id),
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id)
);

CREATE TABLE instructeur (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    prenom TEXT NOT NULL,
    genre TEXT NOT NULL,
    totem TEXT,
    telephone TEXT,
    est_chef_guide BOOLEAN DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE staff (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_instructeur INTEGER,
    annee_exercice_id INTEGER,
    role_id INTEGER,
    etat INTEGER DEFAULT 1 NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_instructeur) REFERENCES instructeur(id),
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id),
    FOREIGN KEY (role_id) REFERENCES roles_staff(id)
);

-- =========================
-- Parents & Enfants
-- =========================

CREATE TABLE parents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    prenom TEXT NOT NULL,
    adresse TEXT,
    telephone TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enfants (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    prenom TEXT NOT NULL,
    genre TEXT NOT NULL,
    date_naissance DATE,
    adresse TEXT,
    parent_id INTEGER,
    bapteme DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES parents(id)
);

CREATE TABLE inscriptions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    enfant_id INTEGER,
    annee_exercice_id INTEGER,
    est_assurance BOOLEAN DEFAULT 0,
    classe_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (enfant_id) REFERENCES enfants(id),
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id),
    FOREIGN KEY (classe_id) REFERENCES classes(id)
);

-- =========================
-- Budget & Activités
-- =========================

CREATE TABLE budget_status (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO budget_status (nom) VALUES
('Créé'),
('Approuvé comité');

CREATE TABLE budget_global (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    annee_exercice_id INTEGER,
    montant REAL NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    status_id INTEGER,
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id),
    FOREIGN KEY (status_id) REFERENCES budget_status(id)
);

CREATE TABLE activite_status (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    status TEXT NOT NULL
);

INSERT INTO activite_status (status) VALUES
('En attente'),
('Terminé'),
('Annulé'),
('Rejeté');

CREATE TABLE activites (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    description TEXT,
    date_debut DATE,
    date_fin DATE,
    montant REAL,
    id_budget INTEGER,
    status_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_budget) REFERENCES budget_global(id),
    FOREIGN KEY (status_id) REFERENCES activite_status(id)
);

CREATE TABLE details_activites (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    activite_id INTEGER,
    details TEXT,
    montant REAL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (activite_id) REFERENCES activites(id)
);

-- =========================
-- Participants
-- =========================

CREATE TABLE participants_activites_explo (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    activite_id INTEGER,
    enfant_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (activite_id) REFERENCES activites(id),
    FOREIGN KEY (enfant_id) REFERENCES inscriptions(id)
);

CREATE TABLE participants_activites_staff (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    activite_id INTEGER,
    staff_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (activite_id) REFERENCES activites(id),
    FOREIGN KEY (staff_id) REFERENCES staff(id)
);

-- =========================
-- Programme
-- =========================

CREATE TABLE categorie_programme (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL
);

INSERT INTO categorie_programme (nom) VALUES
('Lovan'' ny fiangonana'),
('Ankapobeny'),
('Fikarohana ara-panahy'),
('Fanompoana ny hafa'),
('Fahasalamana sy toe-batana tomady'),
('Fiainana ankalamanjana'),
('Lalindalina kokoa'),
('Asa manavanana');

CREATE TABLE programmes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    description TEXT,
    categorie_id INTEGER,
    classes_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (categorie_id) REFERENCES categorie_programme(id),
    FOREIGN KEY (classes_id) REFERENCES classes(id)
);

-- Données de test pour la table programmes
INSERT INTO programmes (nom, description, categorie_id, classes_id) VALUES
-- Catégorie 1: Lovan' ny fiangonana (id=1)
('Boky miara-mihira', 'Mianara hira 10 vaovao ao amin''ny boky fiangonana', 1, 1), -- Ami
('Tantara ara-baiboly', 'Mitantara tantara ara-baiboly 3', 1, 1), -- Ami
('Hira fiderana', 'Mianara hira fiderana 5', 1, 2), -- Compagnon
('Lesona Alahady', 'Mandray anjara amin''ny lesona Alahady mandritra ny 3 volana', 1, 3), -- Eclaireur
('Fampianarana Baiboly', 'Manomana fampianarana Baiboly ho an''ny kilasy kely', 1, 4), -- Pionnier
('Toriteny', 'Manome toriteny fohy mandritra ny fanompoam-pivavahana', 1, 5), -- Voyageur
('Fitarika ny fiankohofana', 'Mitari-piankohofana mandritra ny 1 volana', 1, 6), -- Guide

-- Catégorie 2: Ankapobeny (id=2)
('Fivoriana sy fandaminana', 'Mandray anjara amin''ny fivoriana fandaminana', 2, 1), -- Ami
('Fiaraha-miasa', 'Miara-miasa amin''ny namana 3', 2, 2), -- Compagnon
('Tetibola', 'Mianatra mitantana tetibola', 2, 3), -- Eclaireur
('Fitantanana fotoana', 'Mamorona agenda isan-kerinandro', 2, 4), -- Pionnier
('Fanatanterahana tetikasa', 'Manatanteraka tetikasa iray', 2, 5), -- Voyageur
('Fitarika ekipa', 'Mitondra ekipa mandritra ny 1 volana', 2, 6), -- Guide

-- Catégorie 3: Fikarohana ara-panahy (id=3)
('Vakiteny Baiboly', 'Mamaky Baiboly isanandro mandritra ny 1 volana', 3, 1), -- Ami
('Salamo', 'Mianatra Salamo 3', 3, 2), -- Compagnon
('Bokin''ny Baiboly', 'Mianatra momba ny bokin''ny Baiboly 5', 3, 2), -- Compagnon
('Toetran''Andriamanitra', 'Mianatra toetran''Andriamanitra 5', 3, 3), -- Eclaireur
('Vavaka', 'Manoratra diary vavaka mandritra ny 1 volana', 3, 4), -- Pionnier
('Famakiana andinin-teny', 'Mamakiteny andinin-teny 20', 3, 5), -- Voyageur
('Fandalinana lalina', 'Manao fandalinana lalina momba ny toko iray', 3, 6), -- Guide

-- Catégorie 4: Fanompoana ny hafa (id=4)
('Fanampiana ray aman-dreny', 'Manampy ray aman-dreny ao an-trano', 4, 1), -- Ami
('Fitsidihana marary', 'Mitsidika olona marary', 4, 2), -- Compagnon
('Fanadiovana manodidina', 'Manadio ny manodidina ny fiangonana', 4, 2), -- Compagnon
('Fanampiana ny mpianatra kely', 'Manampy ny mpianatra kely hianatra', 4, 3), -- Eclaireur
('Fanomezana', 'Manome fanomezana ho an''ny sahirana', 4, 4), -- Pionnier
('Fikarakarana hetsika', 'Manampy amin''ny fikarakarana hetsika', 4, 5), -- Voyageur
('Tetikasa ho an''ny fokontany', 'Manatanteraka tetikasa ho an''ny fokontany', 4, 6), -- Guide

-- Catégorie 5: Fahasalamana sy toe-batana tomady (id=5)
('Fanatanjahan-tena', 'Manao fanatanjahan-tena 3 isan-kerinandro', 5, 1), -- Ami
('Fisakafoana ara-pahasalamana', 'Mianatra momba ny sakafo mahasalama', 5, 2), -- Compagnon
('Fidiovana', 'Mianatra mikarakara tena', 5, 3), -- Eclaireur
('Torimaso', 'Mianatra momba ny torimaso ara-pahasalamana', 5, 4), -- Pionnier
('Fanatanjahan-tena mahery', 'Manao fanatanjahan-tena 5 isan-kerinandro', 5, 5), -- Voyageur
('Fitsaboana voalohany', 'Mianatra fitsaboana voalohany', 5, 6), -- Guide

-- Catégorie 6: Fiainana ankalamanjana (id=6)
('Fambolena', 'Mamboly voninkazo na legioma', 6, 1), -- Ami
('Fitsangatsanganana', 'Manao fitsangatsanganana 2', 6, 2), -- Compagnon
('Fanjonoana', 'Mianatra manjono', 6, 3), -- Eclaireur
('Fampiana tranolay', 'Mianatra mampianatra tranolay', 6, 4), -- Pionnier
('Fahavelomana any an''ala', 'Mianatra fomba fahavelomana any an''ala', 6, 5), -- Voyageur
('Lalan-kizorana', 'Manomana sy manao lalan-kizorana', 6, 6), -- Guide

-- Catégorie 7: Lalindalina kokoa (id=7)
('Zava-kanto', 'Mamorona zavakanto iray', 7, 1), -- Ami
('Mozika', 'Mianatra mozika', 7, 2), -- Compagnon
('Dihy', 'Mianatra dihy vaovao', 7, 3), -- Eclaireur
('Sary', 'Manao sary 3', 7, 4), -- Pionnier
('Tononkalo', 'Manoratra tononkalo 2', 7, 5), -- Voyageur
('Hai-tao an-tanana', 'Manao asa tanana sarotra', 7, 6), -- Guide

-- Catégorie 8: Asa manavanana (id=8)
('Asa fanjairana', 'Mianatra manjaitra', 8, 1), -- Ami
('Fandrahoan-tsakafo', 'Mahandro sakafo tsotra', 8, 2), -- Compagnon
('Asa hazo', 'Mianatra asa hazo', 8, 3), -- Eclaireur
('Fanjairana mandroso', 'Manjaitra akanjo tsotra', 8, 4), -- Pionnier
('Fambolena mandroso', 'Mikarakara zaridaina', 8, 5), -- Voyageur
('Asa vy', 'Mianatra asa vy', 8, 6); -- Guide

CREATE TABLE programme_status (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    status TEXT NOT NULL
);

INSERT INTO programme_status (status) VALUES
('En attente'),
('En cours'),
('Terminé');

-- =========================
-- Classe Progressive (CP)
-- =========================

CREATE TABLE classe_progressive (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    date_cp DATE NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    niveau INTEGER,
    etat INTEGER DEFAULT 0 NOT NULL,
    annee_exercice_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id)
);

CREATE TABLE cp_details (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    classe_progressive_id INTEGER NOT NULL,
    programme_id INTEGER,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (classe_progressive_id) REFERENCES classe_progressive(id),
    FOREIGN KEY (programme_id) REFERENCES programmes(id),
    UNIQUE(classe_progressive_id, programme_id)
);

CREATE TABLE cp_details_instructeurs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cp_details_id INTEGER NOT NULL,
    instructeur_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cp_details_id) REFERENCES cp_details(id) ON DELETE CASCADE,
    FOREIGN KEY (instructeur_id) REFERENCES instructeur(id),
    UNIQUE(cp_details_id, instructeur_id)
);

CREATE TABLE cp_presence_explo (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    classe_progressive_id INTEGER,
    enfant_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (classe_progressive_id) REFERENCES classe_progressive(id),
    FOREIGN KEY (enfant_id) REFERENCES inscriptions(id)
);

CREATE TABLE cp_presence_staff (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    classe_progressive_id INTEGER,
    staff_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (classe_progressive_id) REFERENCES classe_progressive(id),
    FOREIGN KEY (staff_id) REFERENCES staff(id)
);

CREATE TABLE historique_programmes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    programme_id INTEGER NOT NULL,
    classe_progressive_id INTEGER,  -- NULL pour initialisation automatique/système
    status_id INTEGER NOT NULL,
    annee_exercice_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (programme_id) REFERENCES programmes(id),
    FOREIGN KEY (classe_progressive_id) REFERENCES classe_progressive(id),
    FOREIGN KEY (status_id) REFERENCES programme_status(id),
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id)
);

CREATE TABLE programme_progression_annuelle (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    programme_id INTEGER NOT NULL,
    annee_exercice_id INTEGER NOT NULL,
    statut_final_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (programme_id) REFERENCES programmes(id),
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id),
    FOREIGN KEY (statut_final_id) REFERENCES programme_status(id),
    UNIQUE(programme_id, annee_exercice_id)
);

-- =========================
-- Mouvement budgétaire
-- =========================

CREATE TABLE type (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    type TEXT
);

INSERT INTO type (type) VALUES
('RECETTE'),
('DEPENSE');

CREATE TABLE mouvement_budgetaire (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    annee_exercice_id INTEGER,
    type_id INTEGER,
    montant REAL NOT NULL,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (annee_exercice_id) REFERENCES annee_exercice(id),
    FOREIGN KEY (type_id) REFERENCES type(id)
);

-- =========================
-- Journal
-- =========================

CREATE TABLE journal (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    action TEXT NOT NULL,
    utilisateur_id INTEGER,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id)
);
-- =========================
-- Données initiales
-- =========================

-- Année d'exercice en cours (année actuelle de l'appareil)
INSERT INTO annee_exercice (annee, date_fin, created_at) VALUES
(date('now', 'start of year'), date('now', 'start of year', '+1 year', '-1 day'), CURRENT_TIMESTAMP);

-- Utilisateur par défaut (directeur/directeur123)
-- Hash BCrypt généré par Spring Security BCryptPasswordEncoder
INSERT INTO utilisateur (username, password_hash, role_id, active, annee_exercice_id, created_at, updated_at) VALUES
('directeur', '$2a$10$jUuOSBA7kVjDxLfvbwa2bObataLc7L3/zVz.IYQsecOE5FwaT.PZa', 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
