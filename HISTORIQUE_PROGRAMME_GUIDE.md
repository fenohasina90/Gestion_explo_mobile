# Fonctionnalité: Historique de Programme

## Vue d'ensemble

Cette fonctionnalité implémente un système complet de suivi et de gestion de l'historique des programmes pédagogiques du Club des Explorateurs, avec des règles métier strictes pour garantir l'intégrité des données.

## Architecture

### Tables de base de données

#### 1. `classe_progressive`
Table existante avec ajout du champ `etat`:
- `etat = 0`: Classe ouverte (saisie et modifications autorisées)
- `etat = 1`: Classe clôturée (tout verrouillé)

#### 2. `historique_programmes`
Table mise à jour avec les champs:
- `id`: Identifiant unique
- `programme_id`: Programme concerné (NOT NULL)
- `classe_progressive_id`: CP où le changement a eu lieu (NULLABLE)
  - `NULL` = Initialisation automatique/système
  - Valeur non-NULL = Changement dans une CP spécifique
- `status_id`: Statut du programme (NOT NULL)
- `annee_exercice_id`: Année d'exercice (NOT NULL, dénormalisé)
- `created_at`: Date de création (immuable)

**IMPORTANT**: Cette table est **IMMUABLE** - aucune modification ou suppression n'est autorisée.

#### 3. `programme_progression_annuelle` (NOUVELLE)
Table pour suivre la progression annuelle des programmes:
- `id`: Identifiant unique
- `programme_id`: Programme concerné (NOT NULL)
- `annee_exercice_id`: Année d'exercice (NOT NULL)
- `statut_final_id`: Statut final du programme pour cette année
- `created_at`: Date de création
- `updated_at`: Date de dernière modification
- **Contrainte unique**: (programme_id, annee_exercice_id)

### Entités Java

1. **ProgrammeProgressionAnnuelle** - Nouvelle entité pour la progression annuelle
2. **HistoriqueProgramme** - Mise à jour avec le champ `anneeExercice`
3. **ClasseProgressive** - Champ `etat` déjà existant

### DTOs

1. **HistoriqueProgrammeDto** - Pour les changements de statut individuels
2. **ProgressionAnnuelleDto** - Pour la progression annuelle d'un programme
3. **StatistiquesAnnuellesDto** - Pour les statistiques agrégées
4. **ProgrammeAvancementDto** - Pour l'avancement détaillé des programmes

## Règles métier

### 1. Initialisation annuelle automatique
- **Timing**: Chaque jour à 00:01 (scheduler)
- **Action**: Tous les programmes sont initialisés à `EN ATTENTE` au début d'une nouvelle année d'exercice
- **Autonomie**: Chaque année est un cycle indépendant

### 2. Gestion de l'état des Classe Progressive
- **État ouvert (etat=0)**:
  - Saisie des présences autorisée
  - Modification des statuts de programmes autorisée
  
- **État clôturé (etat=1)**:
  - Saisie des présences **INTERDITE**
  - Modification des statuts de programmes **INTERDITE**
  - Classe définitivement fermée

### 3. Blocage définitif des programmes terminés
- Un programme avec le statut `TERMINÉ` est **définitivement verrouillé**
- **Aucune modification** ultérieure de statut n'est autorisée
- Le programme **n'apparaît plus** dans la liste des programmes sélectionnables
- Ce verrouillage est **irréversible** pour l'année en cours

### 4. Transitions de statut

Flux normal:
```
EN ATTENTE → EN COURS → TERMINÉ
```

- **EN ATTENTE → EN COURS**: Automatique lors de l'ajout du programme à une CP
- **EN COURS → TERMINÉ**: Modification manuelle quand le programme est complété
- **TERMINÉ**: État final, aucune modification possible

## Services

### ProgrammeScheduler
**Tâches planifiées**:
- `initialiserStatutsProgrammesNouvelleAnnee()`: Cron `0 1 0 * * *` (00:01 daily)
- `initialiserStatutsPourAnnee(anneeId)`: Initialisation manuelle

### HistoriqueProgrammeService
**Méthodes principales**:
- `enregistrerChangementStatut()`: Enregistre un changement de statut (IMMUABLE)
- `getHistoriqueProgramme(programmeId)`: Historique complet d'un programme
- `getHistoriqueProgrammeParAnnee(programmeId, anneeId)`: Historique pour une année
- `getStatistiquesAnnuelles(anneeId)`: Statistiques de tous les programmes
- `getAvancementProgrammes(anneeId, classeId, categorieId)`: Avancement détaillé

### ProgrammeStatusService
**Méthodes principales**:
- `changeProgrammeStatus()`: Change le statut avec validations
- Validation du verrouillage des CP clôturées
- Validation du verrouillage des programmes terminés

### ProgrammeService
**Nouvelles méthodes**:
- `getProgrammesDisponiblesParAnnee(anneeId)`: Programmes NON terminés
- `getProgrammesDisponiblesParAnneeEtClasse(anneeId, classeId)`: Avec filtre classe

## Endpoints API

### Historique & Progression
```
GET /api/historique-programmes/programme/{programmeId}
GET /api/historique-programmes/programme/{programmeId}/annee/{anneeId}
GET /api/historique-programmes/cp/{cpId}
GET /api/historique-programmes/progression/programme/{programmeId}
GET /api/historique-programmes/statistiques/annee/{anneeId}
GET /api/historique-programmes/avancement/annee/{anneeId}
POST /api/historique-programmes/initialiser-annee/{anneeId} (Directeur only)
```

### Programmes disponibles
```
GET /api/programmes/disponibles/annee/{anneeId}
GET /api/programmes/disponibles/annee/{anneeId}/classe/{classeId}
```

## Migration de base de données

Fichier: `migration_historique_programmes.sql`

**Étapes**:
1. Sauvegarde de `historique_programmes`
2. Recréation de la table avec `annee_exercice_id`
3. Restauration des données avec jointure vers `classe_progressive`
4. Création de `programme_progression_annuelle`
5. Initialisation des progressions depuis les historiques existants
6. Initialisation à "En attente" pour l'année en cours

**Exécution**:
```bash
sqlite3 explorateurs.db < migration_historique_programmes.sql
```

## Exemple de scénario

### Contexte
- Année: 2026
- Programmes: P1, P2, P3

### 01/01/2026 - Initialisation automatique
Tous les programmes → `EN ATTENTE`
- Enregistré dans `historique_programmes` avec `classe_progressive_id = NULL`
- Enregistré dans `programme_progression_annuelle` avec `statut_final = EN ATTENTE`

### CP du 15/03/2026
1. P1 est sélectionné
2. P1 passe à `EN COURS` (automatique)
3. P1 est marqué comme `TERMINÉ` (manuel)
4. CP clôturée (etat=1)

**Résultat**: P1 est **définitivement terminé**, n'apparaît plus dans les listes

### CP du 22/03/2026
1. P2 est sélectionné (P1 n'apparaît plus)
2. P2 passe à `EN COURS`
3. CP clôturée sans autre modification

**Résultat**: P2 reste `EN COURS`

### CP du 29/03/2026
1. P2 est de nouveau sélectionné (toujours `EN COURS`)
2. P2 est marqué comme `TERMINÉ`
3. CP clôturée

**Résultat**: P2 est **définitivement terminé**

### Historique final (année 2026)
```
P1:
  - 01/01/2026: EN ATTENTE (auto, CP=NULL)
  - 15/03/2026: EN COURS (CP #1)
  - 15/03/2026: TERMINÉ (CP #1)

P2:
  - 01/01/2026: EN ATTENTE (auto, CP=NULL)
  - 22/03/2026: EN COURS (CP #2)
  - 29/03/2026: TERMINÉ (CP #3)

P3:
  - 01/01/2026: EN ATTENTE (auto, CP=NULL)
  - (pas de progression en CP)
```

**Note**: Les lignes avec `CP=NULL` indiquent une initialisation automatique du système.

## Points d'attention

1. **Immuabilité de l'historique**: Les données ne sont jamais modifiées ou supprimées
2. **Verrouillage des CP**: Dès qu'une CP est clôturée, plus aucune modification
3. **Programme terminé = définitif**: Un programme terminé est verrouillé pour l'année
4. **Chaque année est indépendante**: Les statuts sont réinitialisés chaque année
5. **Initialisation automatique**: Le scheduler s'exécute quotidiennement

## Tests recommandés

1. Initialisation automatique au changement d'année
2. Ajout d'un programme à une CP (transition EN ATTENTE → EN COURS)
3. Passage d'un programme à TERMINÉ
4. Vérification du filtrage des programmes terminés
5. Tentative de modification d'un programme terminé (doit échouer)
6. Tentative de modification dans une CP clôturée (doit échouer)
7. Vérification de l'historique complet
8. Calcul des statistiques annuelles
9. Initialisation manuelle pour une année donnée

## Maintenance

### Nettoyage
L'historique est conservé indéfiniment pour l'audit. Aucun nettoyage automatique.

### Performance
- Index sur: `(programme_id, annee_exercice_id)` pour `programme_progression_annuelle`
- Index sur: `(programme_id, classe_progressive_id)` pour `historique_programmes`
- Requêtes optimisées avec jointures et agrégations

### Monitoring
- Vérifier l'exécution du scheduler via les logs
- Surveiller la taille de `historique_programmes` (croissance continue)
- Vérifier la cohérence entre `historique_programmes` et `programme_progression_annuelle`
