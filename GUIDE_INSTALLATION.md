# Application de Gestion du Club des Explorateurs

Application web et mobile offline pour la gestion du Club des Explorateurs (type scout de l'Église Adventiste).

## 🛠️ Stack Technique

### Backend
- **Framework**: Spring Boot 4.0.3
- **Langage**: Java 21
- **Base de données**: SQLite
- **ORM**: Spring Data JPA avec Hibernate

### Frontend Web
- **Framework**: Vue.js 3 (Composition API)
- **Langage**: TypeScript
- **Router**: Vue Router
- **State Management**: Pinia
- **UI Framework**: PrimeVue
- **Build Tool**: Vite
- **Stockage Offline**: LocalForage
- **HTTP Client**: Axios

## 📁 Structure du Projet

```
Gestion_explo_mobile/
├── backend/                    # Application Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/explorateur/backend/
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
├── frontend/                   # Application Vue.js
│   ├── src/
│   │   ├── assets/
│   │   ├── components/
│   │   ├── router/
│   │   ├── services/          # Services API et Offline
│   │   │   ├── api.ts
│   │   │   ├── offlineStorage.ts
│   │   │   └── syncService.ts
│   │   ├── stores/            # Stores Pinia
│   │   ├── views/
│   │   ├── App.vue
│   │   └── main.ts
│   ├── .env                   # Variables d'environnement
│   └── package.json
└── backend/src/main/resources/sql/
    └── BD_sqlite.sql          # Structure de la base de données
```

## 🚀 Installation et Démarrage

### Prérequis
- Java 21+
- Node.js 18+ et npm
- Maven

### Backend (Spring Boot)

1. Naviguer vers le dossier backend:
```bash
cd backend
```

2. Installer les dépendances Maven:
```bash
./mvnw clean install
```

3. Lancer l'application:
```bash
./mvnw spring-boot:run
```

Le backend sera accessible sur `http://localhost:8080`

### Frontend (Vue.js)

1. Naviguer vers le dossier frontend:
```bash
cd frontend
```

2. Les dépendances sont déjà installées. Pour lancer le serveur de développement:
```bash
npm run dev
```

Le frontend sera accessible sur `http://localhost:5173`

## 📦 Dépendances Backend

### Principales dépendances Maven:
- `spring-boot-starter-web` - API REST
- `spring-boot-starter-data-jpa` - Accès aux données
- `sqlite-jdbc` - Driver SQLite
- `hibernate-community-dialects` - Dialecte Hibernate pour SQLite
- `spring-boot-starter-validation` - Validation des données
- `lombok` - Réduction du code boilerplate
- `spring-boot-devtools` - Outils de développement

## 📦 Dépendances Frontend

### Principales dépendances npm:
- `vue` - Framework Vue.js 3
- `vue-router` - Routage
- `pinia` - Gestion d'état
- `primevue` - Composants UI
- `axios` - Client HTTP
- `localforage` - Stockage local indexedDB
- `typescript` - Support TypeScript

## ⚙️ Configuration

### Base de donnée
- Réinitialiser la base de données :
```bash 
    cd backend
    ./init-db.sh
```
- Consulter les tables :
```bash
    sqlite3 backend/explorateurs.db ".tables"
```
- Accéder à la console SQLite :
```bash
    sqlite3 backend/explorateurs.db
```
- Vérifier les données :
```bash
    sqlite3 backend/explorateurs.db "SELECT * FROM roles_staff;"
```
### Backend - application.properties

```properties
# SQLite Database
spring.datasource.url=jdbc:sqlite:explorateurs.db
spring.datasource.driver-class-name=org.sqlite.JDBC

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# CORS Configuration
spring.web.cors.allowed-origins=http://localhost:5173,http://localhost:8080

# Server
server.port=8080
```

### Frontend - .env

```env
VITE_API_URL=http://localhost:8080/api
VITE_APP_MODE=development
```

## 🔌 Fonctionnalité Offline

L'application supporte le mode offline grâce à:

1. **LocalForage**: Stockage des données localement dans IndexedDB
2. **Service de Synchronisation**: Synchronisation automatique lors de la reconnexion
3. **Gestion des états**: Identification des données non synchronisées

### Services Offline

- `offlineStorage.ts` - Gestion du stockage local
- `syncService.ts` - Synchronisation avec le serveur
- `api.ts` - Configuration de l'API avec intercepteurs

## 📊 Base de Données

La structure de la base de données SQLite est définie dans `backend/src/main/resources/sql/BD_sqlite.sql` et comprend:

- **Gestion des classes** (scouts)
- **Utilisateurs et staff**
- **Parents et enfants**
- **Inscriptions**
- **Budget et activités**
- **Programmes et classes progressives**
- **Mouvements budgétaires**
- **Journal d'activités**

### 🗄️ Initialisation de la Base

La base de données est créée automatiquement dans le dossier `backend/` lors de l'exécution du script d'initialisation :

```bash
cd backend
./init-db.sh
```

**Emplacement**: `/backend/explorateurs.db`

### 📝 Données Initiales

Lors de l'initialisation, les données suivantes sont automatiquement créées :

**Rôles du Staff:**
- Directeur (ID: 1) - Permissions: CREER, MODIFIER, SUPPRIMER, CONSULTER
- Co-Directeur (ID: 2) - Permissions: MODIFIER, CONSULTER
- Secrétaire (ID: 3) - Permissions: CONSULTER
- Instructeur (ID: 4) - Permissions: CONSULTER

**Utilisateur par défaut:**
- Username: `directeur`
- Password: `directeur123`
- Rôle: Directeur

**Année d'exercice:**
- Année: 2026-01-01 (ID: 1)

**Autres données de référence:**
- 2 statuts de budget (Créé, Approuvé comité)
- 4 statuts d'activité (En attente, Terminé, Annulé, Rejeté)
- 7 catégories de programme
- 3 statuts de programme (En attente, En cours, Terminé)
- 2 types de mouvement budgétaire (RECETTE, DEPENSE)

## 🔐 Sécurité

### Utilisateur par Défaut
Lors de l'initialisation de la base de données, un compte administrateur est créé :
- **Username**: `directeur`
- **Password**: `directeur123`
- **Rôle**: Directeur (accès complet : CREER, MODIFIER, SUPPRIMER, CONSULTER)

**⚠️ IMPORTANT**: Changez ce mot de passe immédiatement après la première connexion en production !

Pour plus de détails, consultez [CREDENTIALS.md](CREDENTIALS.md)

### Sécurité Implémentée
- Hachage des mots de passe avec BCrypt
- Gestion des rôles et permissions (4 niveaux)
- CORS configuré pour la sécurité
- Foreign keys activées dans SQLite

### À Implémenter
- Authentification par token JWT
- Changement de mot de passe
- Session management
- Audit trail complet

## 📱 Développement Mobile

La version mobile sera développée ultérieurement avec synchronisation en réseau local.

## 🛠️ Commandes Utiles

### Backend
```bash
# Compiler
./mvnw clean compile

# Tests
./mvnw test

# Package
./mvnw package

# Lancer l'application
./mvnw spring-boot:run
```

### Frontend
```bash
# Développement
npm run dev

# Build production
npm run build

# Preview production
npm run preview

# Linter
npm run lint

# Tests
npm run test
```

## 📝 Prochaines Étapes

### ✅ Déjà Implémenté
1. ✅ Structure de base Spring Boot + Vue.js
2. ✅ Base de données SQLite avec schéma complet
3. ✅ Année d'exercice dynamique (auto-création)
4. ✅ Utilisateur par défaut (directeur/directeur123)
5. ✅ Configuration CORS pour communication Frontend-Backend
6. ✅ Swagger UI pour documentation et test des APIs
7. ✅ Première entité JPA (AnneeExercice)
8. ✅ Service d'initialisation automatique
9. ✅ Mode offline avec LocalForage

### ⏳ À Développer
1. Créer les entités JPA pour toutes les tables restantes
2. Implémenter les repositories et services
3. Créer les contrôleurs REST et documenter avec Swagger
4. Implémenter l'authentification JWT avec Spring Security
5. Développer les composants Vue.js
6. Créer les vues pour chaque module de gestion
7. Tester la fonctionnalité offline complète
8. Développer la version mobile

## 🧪 Tester les APIs

### Swagger UI (Recommandé)
Interface interactive pour tester toutes les APIs :

```
http://localhost:8080/swagger-ui.html
```

Consultez le guide complet : [SWAGGER_GUIDE.md](SWAGGER_GUIDE.md)

### Endpoints Disponibles

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/annee-exercice/courante` | Année d'exercice en cours |
| GET | `/api/annee-exercice/recente` | Année la plus récente |

### Exemple de test
```bash
curl http://localhost:8080/api/annee-exercice/courante
```

## 👥 Contribution

Ce projet est destiné à la gestion du Club des Explorateurs de l'Église Adventiste.

## 📄 Licence

Propriétaire
