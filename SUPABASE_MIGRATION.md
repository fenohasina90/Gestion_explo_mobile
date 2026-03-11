# 🚀 Migration SQLite → PostgreSQL (Supabase)

## 📋 Vue d'ensemble

**Problème actuel :** 
- Render.com Free tier a un système de fichiers **éphémère**
- Données SQLite perdues à chaque redémarrage (après 15 min inactivité)

**Solution :**
- Migrer vers **PostgreSQL** hébergé sur **Supabase**
- Base de données persistante, gratuite (500 MB)

---

## Étape 1 : Créer la base de données sur Supabase

### 1.1 Créer un compte Supabase

1. Allez sur : **https://supabase.com/**
2. Cliquez sur **"Start your project"**
3. Connectez-vous avec GitHub (recommandé) ou email
4. Vérifiez votre email si nécessaire

### 1.2 Créer un nouveau projet

1. Cliquez sur **"New Project"**
2. Remplissez les informations :
   - **Name** : `explorateurs-db` (ou votre choix)
   - **Database Password** : Générez un mot de passe fort (SAUVEGARDEZ-LE !)
   - **Region** : Choisissez le plus proche (ex: `Frankfurt (eu-central-1)`)
   - **Pricing Plan** : **Free** (500 MB, 2 CPU, 1 GB RAM)
3. Cliquez sur **"Create new project"**
4. ⏳ Attendez 2-3 minutes (création de la base)

### 1.3 Récupérer les informations de connexion

Une fois le projet créé :

1. Allez dans **Settings** (⚙️ dans la sidebar)
2. Cliquez sur **Database**
3. Descendez jusqu'à **"Connection string"**
4. Sélectionnez **"URI"** (pas "Session mode" ni "Transaction")
5. Copiez l'URL qui ressemble à :
```
postgresql://postgres.xxxxxxxxxxxx:[YOUR-PASSWORD]@aws-0-eu-central-1.pooler.supabase.com:6543/postgres
```

### 1.4 Remplacer le mot de passe

Dans l'URL copiée, remplacez `[YOUR-PASSWORD]` par le mot de passe que vous avez créé à l'étape 1.2.

**Exemple :**
```
postgresql://postgres.abcdefghijklmno:MonMotDePasse123!@aws-0-eu-central-1.pooler.supabase.com:6543/postgres
```

**⚠️ IMPORTANT : Sauvegardez cette URL complète dans un fichier sécurisé !**

---

## Étape 2 : Exécuter le schéma SQL sur Supabase

### 2.1 Ouvrir l'éditeur SQL

1. Dans Supabase, allez dans **SQL Editor** (📝 dans la sidebar)
2. Cliquez sur **"New query"**

### 2.2 Copier le schéma PostgreSQL

1. Ouvrez le fichier : `backend/src/main/resources/sql/BD_postgres.sql` dans votre projet
2. Copiez **tout le contenu**
3. Collez-le dans l'éditeur SQL de Supabase

### 2.3 Exécuter le script

1. Cliquez sur **"Run"** (ou `Ctrl+Enter`)
2. ✅ Vérifiez qu'il n'y a pas d'erreurs
3. Si erreurs → Corrigez et réexécutez

### 2.4 Vérifier les tables créées

1. Allez dans **Table Editor** (📊 dans la sidebar)
2. Vous devriez voir toutes les tables :
   - `classes`
   - `annee_exercice`
   - `roles_staff`
   - `utilisateur`
   - `parents`
   - `enfants`
   - `inscriptions`
   - `budget_global`
   - `activites`
   - etc.

---

## Étape 3 : Vérifier les données initiales

### 3.1 Données déjà créées

Le script `BD_postgres.sql` contient déjà toutes les données initiales :
- ✅ **6 classes** : Ami, Compagnon, Eclaireur, Pionnier, Voyageur, Guide
- ✅ **48 programmes** répartis sur 8 catégories
- ✅ **Utilisateur directeur** (`directeur` / `directeur123`)
- ✅ **Année d'exercice 2026** avec date de fin
- ✅ **Rôles et statuts**

### 3.2 Vérifier dans Table Editor

1. Allez dans **Table Editor** (📊 dans la sidebar)
2. Vérifiez les tables :

**Vérifier l'utilisateur :**
```sql
SELECT id, username, role_id, active FROM utilisateur;
```

Devrait retourner :
```
id | username   | role_id | active
1  | directeur  | 1       | true
```

**Vérifier les programmes :**
```sql
SELECT COUNT(*) FROM programmes;
```

Devrait retourner : **48**

**Vérifier l'année :**
```sql
SELECT id, annee, date_fin FROM annee_exercice;
```

Devrait retourner l'année 2026.

---

## Étape 4 : Modifier le backend Spring Boot

### 4.1 Modifier `pom.xml`

Remplacer la dépendance SQLite par PostgreSQL :

```xml
<!-- SUPPRIMER ces lignes SQLite -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.1.0</version>
</dependency>
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-community-dialects</artifactId>
</dependency>

<!-- AJOUTER cette dépendance PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 4.2 Modifier `application.properties`

Remplacer la configuration SQLite :

```properties
# AVANT (SQLite) - SUPPRIMER
spring.datasource.url=jdbc:sqlite:explorateurs.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect

# APRÈS (PostgreSQL) - AJOUTER
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://localhost:5432/explorateurs}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Pool de connexions (optimisation)
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=20000
```

**Note :** `${DATABASE_URL:...}` signifie :
- Utiliser la variable d'environnement `DATABASE_URL` si elle existe
- Sinon, utiliser `jdbc:postgresql://localhost:5432/explorateurs` par défaut

### 4.3 Créer un fichier `.env` pour le développement local

Créez `backend/.env` :

```bash
DATABASE_URL=postgresql://postgres.xxxxxxxxxxxx:VotreMotDePasse@aws-0-eu-central-1.pooler.supabase.com:6543/postgres
JWT_SECRET=maCleSecreteTresLonguePourLeJWTDeLApplicationExplorateurClubScoutAventiste2026
```

**⚠️ IMPORTANT : Ajoutez `.env` au `.gitignore` pour ne pas commit les secrets !**

---

## Étape 5 : Tester localement

### 5.1 Clean et rebuild

```bash
cd backend
mvn clean install
```

### 5.2 Lancer le backend

```bash
mvn spring-boot:run
```

### 5.3 Tester la connexion

```bash
# Healthcheck
curl http://localhost:8080/actuator/health

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"directeur","password":"directeur123"}'
```

**✅ Si vous recevez un token JWT → Migration réussie !**

---

## Étape 6 : Déployer sur Render avec Supabase

### 6.1 Ajouter la variable d'environnement sur Render

1. Allez sur **https://dashboard.render.com/**
2. Sélectionnez votre service **explorateurs-backend**
3. Allez dans **Environment**
4. Cliquez sur **"Add Environment Variable"**
5. Ajoutez :
   - **Key** : `DATABASE_URL`
   - **Value** : `postgresql://postgres.xxxx:VotreMotDePasse@aws-0-eu-central-1.pooler.supabase.com:6543/postgres`
6. Cliquez sur **"Save Changes"**

### 6.2 Redéployer

Render va automatiquement redéployer avec la nouvelle variable.

Ou forcez le redéploiement :
1. **Manual Deploy** → **"Deploy latest commit"**
2. ⏳ Attendez 3-5 minutes

### 6.3 Vérifier le déploiement

```bash
# Healthcheck
curl https://explorateurs-backend.onrender.com/actuator/health

# Login
curl -X POST https://explorateurs-backend.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"directeur","password":"directeur123"}'
```

---

## Étape 7 : Tester l'application mobile

### 7.1 L'app mobile n'a **AUCUNE modification** à faire

L'URL du backend reste la même :
```
https://explorateurs-backend.onrender.com
```

### 7.2 Tester la connexion

1. Ouvrez l'app mobile
2. Connectez-vous : `directeur` / `directeur123`
3. ✅ Devrait fonctionner normalement

---

## ✅ Checklist finale

- [ ] Compte Supabase créé
- [ ] Projet PostgreSQL créé sur Supabase
- [ ] URL de connexion récupérée et sauvegardée
- [ ] Schéma SQL (`BD_postgres.sql`) exécuté sur Supabase
- [ ] Données initiales vérifiées (48 programmes, utilisateur directeur)
- [ ] `pom.xml` modifié (PostgreSQL au lieu de SQLite)
- [ ] `application.properties` modifié
- [ ] Backend testé localement avec Supabase
- [ ] Variable `DATABASE_URL` ajoutée sur Render
- [ ] Backend redéployé sur Render
- [ ] App mobile testée avec le nouveau backend

---

## 🔧 Résolution de problèmes

### Erreur : "password authentication failed"

Vérifiez que :
- Le mot de passe dans `DATABASE_URL` est correct
- Il n'y a pas de caractères spéciaux non encodés (utilisez URL encoding)

### Erreur : "relation does not exist"

Le schéma SQL n'a pas été exécuté correctement :
- Retournez dans Supabase SQL Editor
- Réexécutez `BD.sql`

### Backend démarre mais ne peut pas se connecter

Vérifiez dans les logs Render :
```bash
# Sur Render → Logs
# Cherchez : "org.postgresql.util.PSQLException"
```

### Timeout de connexion

Le firewall Render peut bloquer Supabase :
- Vérifiez que vous utilisez le **port 6543** (port pooler, pas 5432)
- URL doit contenir `.pooler.supabase.com`

---

## 📊 Avantages de PostgreSQL + Supabase

✅ **Données persistantes** (pas de perte à chaque redémarrage)  
✅ **Gratuit** jusqu'à 500 MB  
✅ **Backups automatiques** (Supabase garde 7 jours)  
✅ **Interface web** pour gérer les données  
✅ **Supporte des milliers de connexions** (pooling)  
✅ **Meilleure performance** que SQLite pour plusieurs utilisateurs  

---

Date de migration : 11 mars 2026
