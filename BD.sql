-- Taom-piasana
create table classes (
    id serial primary key,
    nom varchar(100) not null, -- Sakaiza, Namana, Mpanazava, ...
    logo varchar(255),
    age int
);

insert into classes (nom, logo, age) values 
('Sakaiza', 'sakaiza.jpg', 6),
('Namana', 'namana.jpg', 8),
('Mpanazava', 'mpanazava.jpg', 10),
('Mpamaky lay', 'mpamaky_lay.jpg', 11),
('Mpandeha lavitra', 'mpandeha_lavitra.jpg', 12),
('Mpitarika', 'mpitarika.jpg', 12);

create table annee_exercice (
    id serial primary key,
    annee date not null,
    created_at timestamp default current_timestamp
);



-- Utilisateur ary mpitarika

create table roles_staff (
    id serial primary key,
    role_name varchar(100) not null -- directeur, secretariat, ...
);

insert into roles_staff (role_name) values 
('Directeur'),
('Co-Directeur'),
('Secrétaire'),
('Instructeur');

create table roles_action (
    id serial primary key,
    role_id integer references roles_staff(id),
    action varchar(100) not null -- CREER, MODIFIER, SUPPRIMER, CONSULTER
);

insert into roles_action (role_id, action) values 
(1, 'CREER'),
(1, 'MODIFIER'),
(1, 'SUPPRIMER'),
(1, 'CONSULTER'),
(2, 'MODIFIER'),
(2, 'CONSULTER'),
(3, 'CONSULTER'),
(4, 'CONSULTER'),
(5, 'CONSULTER');

create table utilisateur (
    id serial primary key,
    username varchar(50) unique not null,
    password_hash varchar(255) not null,
    role_id integer references roles_staff(id),
    active boolean default true,
    annee_exercice_id integer references annee_exercice(id),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

create table instructeur (
    id serial primary key,
    nom varchar(100) not null,  
    prenom varchar(100) not null,
    genre varchar(10) not null,
    totem varchar(100),
    telephone varchar(15),
    est_chef_guide boolean default false,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

create table staff (
    id serial primary key,
    id_instructeur integer references instructeur(id),
    annee_exercice_id integer references annee_exercice(id),
    role_id integer references roles_staff(id),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp  
);



-- Ankizy sy Ray aman-dreny

create table parents (
    id serial primary key,
    nom varchar(100) not null,  
    prenom varchar(100) not null,
    adresse varchar(255),
    telephone varchar(15),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);


create table enfants (
    id serial primary key,
    nom varchar(100) not null,  
    prenom varchar(100) not null,
    genre varchar(10) not null,
    date_naissance date,
    adresse varchar(255),
    parent_id integer references parents(id),
    bapteme date,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

create table inscriptions (
    id serial primary key,
    enfant_id integer references enfants(id),
    annee_exercice_id integer references annee_exercice(id),
    est_assurance boolean default false,
    classe_id integer references classes(id),
    created_at timestamp default current_timestamp
);

-- rafitra sy tetikasa
create table budget_status (
    id serial primary key,
    nom varchar(100) not null,
    created_at timestamp default current_timestamp
);

insert into budget_status (nom) values 
('Créé');,
('Approuvé comite');

create table budget_global(
    id serial primary key,
    annee_exercice_id integer references annee_exercice(id),
    montant decimal(10,2) not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp,
    status_id integer references budget_status(id)
);

create table activite_status (
    id serial primary key,
    status varchar(50) not null -- en attente, terminee, annulee
);

insert into activite_status (status) values 
('En attente'),
('Terminé'),
('Annulé'),
('Rejeté');

create table activites (
    id serial primary key,
    nom varchar(100) not null,
    description text,
    date_debut date,
    date_fin date,
    montant decimal(10,2),
    id_budget int references budget_global(id),
    status_id int references activite_status(id),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp  
);

create table details_activites (
    id serial primary key,
    activite_id integer references activites(id),
    details text,
    montant decimal(10,2),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);


-- Presence amin'ny activite (ankizy sy staff)

create table participants_activites_explo (
    id serial primary key,
    activite_id integer references activites(id),
    enfant_id integer references inscriptions(id),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

create table participants_activites_staff (
    id serial primary key,
    activite_id integer references activites(id),
    staff_id integer references staff(id),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

-- Kilasim-pandrosoana : programa
create table categorie_programme (
    id serial primary key,
    nom varchar(100) not null
);

insert into categorie_programme (nom) values 
('Ankapobeny'),
('Fikarohana ara-panahy'),
('Fanompoana ny hafa'),
('Fahasalamana sy toe-batana tomady'),
('Fiainana ankalamanjana'),
('Lalindalina kokoa'),
('Asa manavanana');

create table programmes (
    id serial primary key,
    nom varchar(100) not null,
    description text,
    categorie_id integer references categorie_programme(id),
    classes_id integer references classes(id),  
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

create table programme_status (
    id serial primary key,
    status varchar(50) not null
);

insert into programme_status (status) values 
('En attente'),
('En cours'),
('Terminé');

-- Kilasim-pandrosoana : CP

create table classe_progressive(
    id serial primary key,
    date_cp date not null,  
    heure_debut time not null,
    heure_fin time not null,
    niveau int,
    created_at timestamp default current_timestamp
);

create table cp_details (
    id serial primary key,
    classe_progressive_id integer references classe_progressive(id),
    programme_id integer references programmes(id),
    instructeur_id int references instructeur(id),
    created_at timestamp default current_timestamp
);

create table cp_presence (
    id serial primary key,
    classe_progressive_id integer references classe_progressive(id),
    enfant_id integer references inscriptions(id),
    staff_id integer references staff(id),
    created_at timestamp default current_timestamp
);

create table historique_programmes (
    id serial primary key,
    programme_id integer references programmes(id),
    classe_progressive_id integer references classe_progressive(id),
    status_id integer references programme_status(id),
    created_at timestamp default current_timestamp
);

-- gestion budgetaire
create table type (
    id serial primary key,
    type VARCHAR(20) 
);

insert into type (type) values 
('RECETTE'),
('DEPENSE');

create table mouvement_budgetaire (
    id serial primary key,
    annee_exercice_id integer references annee_exercice(id),
    type_id integer references type(id),
    montant decimal(10,2) not null,
    description text,
    created_at timestamp default current_timestamp
);

-- historique
create table journal (
    id serial primary key,
    action varchar(255) not null,
    utilisateur_id integer references utilisateur(id),
    timestamp timestamp default current_timestamp
);



