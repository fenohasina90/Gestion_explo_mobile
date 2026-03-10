# Backend - Club des Explorateurs

API REST Spring Boot pour la gestion du Club des Explorateurs.

## 🗄️ Base de Données SQLite

### Initialisation

Pour initialiser la base de données avec toutes les tables et données par défaut :

```bash
./init-db.sh
```

Ce script va :
1. Sauvegarder l'ancienne base (si elle existe)
2. Créer une nouvelle base `explorateurs.db`
3. Créer toutes les tables (23 tables)
4. Insérer les données de référence
5. Créer l'utilisateur par défaut et l'année d'exercice

### Emplacement

- **Fichier**: `explorateurs.db`
- **Chemin**: `/backend/explorateurs.db`

### Accès Direct

Pour accéder à la base de données avec SQLite CLI :

```bash
sqlite3 explorateurs.db
```

Commandes utiles :
```sql
.tables                          -- Liste des tables
.schema nom_table                -- Schéma d'une table
SELECT * FROM utilisateur;       -- Voir les utilisateurs
SELECT * FROM roles_staff;       -- Voir les rôles
```

## 🔐 Credentials par Défaut

- **Username**: `directeur`
- **Password**: `directeur123`
- **Rôle**: Directeur (ID: 1)

> ⚠️ **Sécurité**: Le mot de passe est haché avec BCrypt. Changez-le après la première connexion !

## 🚀 Démarrage

### Avec Maven Wrapper

```bash
./mvnw spring-boot:run
```

### Avec Maven installé

```bash
mvn spring-boot:run
```

Le serveur démarre sur **http://localhost:8080**

## 🎯 Tester les APIs

### Swagger UI (Interface Interactive)

L'application dispose d'une interface Swagger UI pour tester les APIs :

```
http://localhost:8080/swagger-ui.html
```

**Fonctionnalités :**
- 📖 Documentation interactive de toutes les APIs
- 🧪 Test des endpoints directement depuis le navigateur
- 📊 Visualisation des modèles de données
- ✅ Validation des requêtes et réponses

Pour plus de détails, consultez [SWAGGER_GUIDE.md](../SWAGGER_GUIDE.md)

### Via curl (Ligne de commande)

```bash
# Tester un endpoint
curl http://localhost:8080/api/annee-exercice/courante
```

## 📦 Build

### Package JAR

```bash
./mvnw clean package
```

Le fichier JAR sera généré dans `target/backend-0.0.1-SNAPSHOT.jar`

### Exécuter le JAR

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## 🔧 Configuration

La configuration se trouve dans `src/main/resources/application.properties` :

- **Port**: 8080
- **Base de données**: SQLite (explorateurs.db)
- **JPA**: DDL auto = validate (ne modifie pas la structure)
- **SQL Logging**: Activé (show-sql=true)

## 📁 Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/explorateur/backend/
│   │   │   ├── BackendApplication.java
│   │   │   └── config/
│   │   │       └── CorsConfig.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── explorateurs.db              # Base de données SQLite
├── init-db.sh                   # Script d'initialisation
├── pom.xml                      # Dépendances Maven
└── README.md                    # Ce fichier
```

## 🛠️ Dépendances Principales

- Spring Boot 4.0.3
- Spring Data JPA
- SQLite JDBC Driver 3.45.1.0
- Hibernate Community Dialects
- Lombok
- Spring Boot Validation

## 🧪 Tests

```bash
./mvnw test
```

## 📝 Commandes Utiles

### Nettoyer et compiler
```bash
./mvnw clean compile
```

### Vérifier les dépendances
```bash
./mvnw dependency:tree
```

### Formater le code
```bash
./mvnw spring-javaformat:apply
```

## 🔄 Réinitialisation

Pour réinitialiser complètement la base de données :

```bash
./init-db.sh
```

Une sauvegarde automatique sera créée avant la réinitialisation.

## 📖 Documentation Complète

Consultez le fichier [GUIDE_INSTALLATION.md](../GUIDE_INSTALLATION.md) à la racine du projet.
