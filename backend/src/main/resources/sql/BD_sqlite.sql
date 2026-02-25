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
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cp_details (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    classe_progressive_id INTEGER,
    programme_id INTEGER,
    instructeur_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (classe_progressive_id) REFERENCES classe_progressive(id),
    FOREIGN KEY (programme_id) REFERENCES programmes(id),
    FOREIGN KEY (instructeur_id) REFERENCES instructeur(id)
);

CREATE TABLE cp_presence (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    classe_progressive_id INTEGER,
    enfant_id INTEGER,
    staff_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (classe_progressive_id) REFERENCES classe_progressive(id),
    FOREIGN KEY (enfant_id) REFERENCES inscriptions(id),
    FOREIGN KEY (staff_id) REFERENCES staff(id)
);

CREATE TABLE historique_programmes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    programme_id INTEGER,
    classe_progressive_id INTEGER,
    status_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (programme_id) REFERENCES programmes(id),
    FOREIGN KEY (classe_progressive_id) REFERENCES classe_progressive(id),
    FOREIGN KEY (status_id) REFERENCES programme_status(id)
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
