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
('Lovan'' ny fiangonana'), --1
('Ankapobeny'), --2
('Fikarohana ara-panahy/ara-baiboly'), --3
('Fanompoana ny hafa'), --4
('Fampivelarana ny Fisakaizana'), -- 5
('Fahasalamana sy toe-batana tomady'),--6
('Fiainana ankalamanjana'), --7
('Lalindalina kokoa'), --8
('Asa manavanana'), --9
('Fampivelarana ny fandaminana sy fahaiza-mitarika'), --10
('Fandinihana zavaboary'); --11
-- ('Fanatsarana ny fomba fiaina'); --12



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
