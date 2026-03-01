# Scripts SQL - Base de Données

Ce dossier contient les scripts SQL pour la base de données SQLite.

## Fichiers

### BD_sqlite.sql
Script principal de création de la base de données comprenant :

#### 📋 Structure des tables (23 tables)
- **Gestion des classes** : `classes`
- **Années d'exercice** : `annee_exercice` (année dynamique basée sur l'année courante)
- **Utilisateurs et authentification** : `utilisateur`, `roles_staff`, `roles_action`
- **Personnel** : `instructeur`, `staff`
- **Familles** : `parents`, `enfants`
- **Inscriptions** : `inscriptions`
- **Budget** : `budget_global`, `budget_status`, `mouvement_budgetaire`, `type`
- **Activités** : `activites`, `details_activites`, `activite_status`
- **Participants** : `participants_activites_explo`, `participants_activites_staff`
- **Programmes** : `programmes`, `categorie_programme`, `programme_status`, `historique_programmes`
- **Classes progressives** : `classe_progressive`, `cp_details`, `cp_presence`
- **Journal** : `journal`

#### 📊 Données de référence initiales

**Rôles Staff :**
- Directeur (ID: 1) - Permissions : CREER, MODIFIER, SUPPRIMER, CONSULTER
- Co-Directeur (ID: 2) - Permissions : MODIFIER, CONSULTER
- Secrétaire (ID: 3) - Permissions : CONSULTER
- Instructeur (ID: 4) - Permissions : CONSULTER

**Statuts :**
- Budget : Créé, Approuvé comité
- Activité : En attente, Terminé, Annulé, Rejeté
- Programme : En attente, En cours, Terminé

**Catégories de Programme :**
1. Ankapobeny
2. Fikarohana ara-panahy
3. Fanompoana ny hafa
4. Fahasalamana sy toe-batana tomady
5. Fiainana ankalamanjana
6. Lalindalina kokoa
7. Asa manavanana

**Types de Mouvement :**
- RECETTE
- DEPENSE

#### 👤 Utilisateur par défaut
- **Username** : `directeur`
- **Password** : `directeur123` (hash BCrypt)
- **Rôle** : Directeur
- **Année** : Année courante (calculée dynamiquement)

#### 📅 Année d'exercice
- Créée automatiquement avec l'année en cours de l'appareil
- Utilise la fonction SQLite : `date('now', 'start of year')`
- Format : YYYY-01-01 (1er janvier de l'année courante)

## 🚀 Utilisation

### Initialisation de la base
Depuis le dossier `backend/` :
```bash
./init-db.sh
```

Ce script :
1. Sauvegarde l'ancienne base (si existante)
2. Crée une nouvelle base `explorateurs.db`
3. Exécute `BD_sqlite.sql`
4. Affiche un rapport de création

### Accès direct avec SQLite CLI
```bash
sqlite3 ../../../explorateurs.db
```

Ou depuis le dossier `backend/` :
```bash
sqlite3 explorateurs.db
```

## 📝 Commandes SQLite utiles

```sql
-- Voir toutes les tables
.tables

-- Voir le schéma d'une table
.schema nom_table

-- Mode d'affichage plus lisible
.mode column
.headers on

-- Exemples de requêtes
SELECT * FROM roles_staff;
SELECT * FROM annee_exercice;
SELECT * FROM utilisateur;
```

## ⚙️ Configuration

Le fichier SQL utilise :
- `PRAGMA foreign_keys = ON;` - Active les contraintes de clés étrangères
- `CURRENT_TIMESTAMP` - Pour les dates de création
- `date('now', 'start of year')` - Pour l'année d'exercice dynamique

## 🔄 Mise à jour du schéma

Pour modifier la structure :
1. Éditer `BD_sqlite.sql`
2. Réinitialiser la base : `./init-db.sh`
3. Ou utiliser des migrations pour préserver les données

## 📚 Documentation

Voir aussi :
- [GUIDE_INSTALLATION.md](../../../../GUIDE_INSTALLATION.md) - Guide complet
- [CREDENTIALS.md](../../../../CREDENTIALS.md) - Informations de connexion
- [ANNEE_EXERCICE.md](../../../../ANNEE_EXERCICE.md) - Gestion des années
- [../../../README.md](../../../README.md) - Documentation backend
