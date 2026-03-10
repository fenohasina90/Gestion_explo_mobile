# 📱 Guide de Déploiement - Application Explorations

Ce guide vous explique comment déployer votre application mobile en production.

---

## 📋 Table des matières

1. [Déploiement du Backend](#-1-déploiement-du-backend)
2. [Création de l'APK Android](#-2-création-de-lapk-android)
3. [Configuration en production](#-3-configuration-en-production)

---

## 🚀 1. Déploiement du Backend

### Option A : Render.com (Recommandé - Gratuit)

#### Étape 1 : Créer un compte Render.com

1. Allez sur [render.com](https://render.com)
2. Créez un compte gratuit avec GitHub
3. Connectez votre dépôt GitHub

#### Étape 2 : Créer un nouveau Web Service

1. Dans le dashboard Render, cliquez sur **"New +"** → **"Web Service"**
2. Connectez votre repository GitHub
3. Configurez le service :
   - **Name** : `explorateurs-backend`
   - **Region** : Choisissez la plus proche (Frankfurt pour Europe)
   - **Branch** : `main` (ou votre branche principale)
   - **Root Directory** : laissez vide
   - **Runtime** : `Java`
   - **Build Command** : `cd backend && mvn clean package -DskipTests`
   - **Start Command** : `cd backend && java -Dspring.profiles.active=prod -jar target/*.jar`
   - **Plan** : `Free`

4. **Variables d'environnement** (dans Advanced) :
   ```
   PORT=8080
   JWT_SECRET=votre_secret_jwt_tres_long_et_securise
   SPRING_PROFILES_ACTIVE=prod
   ```

5. **Disque persistant** (dans Advanced → Disks) :
   - Cliquez sur "Add Disk"
   - **Name** : `data`
   - **Mount Path** : `/data`
   - **Size** : 1 GB (suffisant pour SQLite)

6. Cliquez sur **"Create Web Service"**

#### Étape 3 : Récupérer l'URL de votre API

- Une fois déployé, Render vous donne une URL comme : `https://explorateurs-backend.onrender.com`
- Notez cette URL, vous en aurez besoin pour l'application mobile

#### Configuration automatique de la base de données

✅ La base de données SQLite sera créée automatiquement au premier démarrage grâce au fichier `DatabaseInitializer.java`

---

### Option B : Railway.app (Alternative gratuite)

1. Allez sur [railway.app](https://railway.app)
2. Créez un compte et un nouveau projet
3. Cliquez sur **"Deploy from GitHub repo"**
4. Configurez :
   - **Build Command** : `cd backend && mvn package -DskipTests`
   - **Start Command** : `cd backend && java -Dspring.profiles.active=prod -jar target/*.jar`
5. Ajoutez les variables d'environnement
6. Créez un volume pour `/data`

---

### Option C : Fly.io (Alternative)

```bash
# Installer flyctl
curl -L https://fly.io/install.sh | sh

# Se connecter
flyctl auth login

# Lancer le déploiement
cd backend
flyctl launch
```

---

## 📦 2. Création de l'APK Android

### Prérequis

- **Node.js** 18+ installé
- **Android Studio** installé
- **JDK** 17+ installé

### Étape 1 : Configurer l'URL de production

1. Ouvrez `mobile/.env.production`
2. Remplacez l'URL par celle de Render :
   ```env
   VITE_API_URL=https://explorateurs-backend.onrender.com
   ```

### Étape 2 : Installer les dépendances Capacitor

```bash
cd mobile

# Installer les dépendances
npm install

# Ajouter la plateforme Android si pas déjà fait
npx cap add android
```

### Étape 3 : Build l'application web

```bash
# Build de production
npm run build
```

### Étape 4 : Synchroniser avec Capacitor

```bash
# Copier les fichiers web vers Android
npx cap sync android

# Copier les ressources (icônes, splash screen)
npx cap copy android
```

### Étape 5 : Ouvrir dans Android Studio

```bash
npx cap open android
```

### Étape 6 : Générer l'APK

Dans Android Studio :

1. **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**

2. Attendez la fin de la compilation

3. Cliquez sur **"locate"** pour trouver l'APK généré :
   ```
   mobile/android/app/build/outputs/apk/debug/app-debug.apk
   ```

### Étape 7 : Générer une APK signée (pour publication)

1. **Build** → **Generate Signed Bundle / APK**
2. Sélectionnez **APK**
3. Créez un nouveau keystore ou utilisez un existant :
   ```
   Key store path: /chemin/vers/explorateurs-keystore.jks
   Key store password: VotreMotDePasse
   Key alias: explorateurs-key
   Key password: VotreMotDePasse
   ```
4. Sélectionnez **release** comme build variant
5. Cliquez sur **Finish**

L'APK sera dans :
```
mobile/android/app/build/outputs/apk/release/app-release.apk
```

---

## ⚙️ 3. Configuration en Production

### Configuration de l'icône et du nom de l'application

1. **Nom de l'application** - Modifiez `mobile/android/app/src/main/res/values/strings.xml` :
   ```xml
   <resources>
       <string name="app_name">Explorations</string>
   </resources>
   ```

2. **Icône** - Placez vos icônes dans :
   ```
   mobile/android/app/src/main/res/mipmap-*/ic_launcher.png
   ```

### Vérification avant déploiement

✅ Backend déployé et accessible
✅ URL de production dans `.env.production`
✅ Build réussi : `npm run build`
✅ Sync Capacitor : `npx cap sync`
✅ APK générée et testée

---

## 📊 Monitoring et Logs

### Sur Render.com

- Dashboard → Votre service → **Logs** (temps réel)
- Vérifiez les logs au démarrage :
  ```
  ✅ Base de données initialisée avec succès !
  📍 Emplacement : /data/explorateurs.db
  📊 Nombre de tables créées : 35
  ```

### Base de données SQLite

La base sera créée automatiquement avec :
- ✅ Toutes les tables
- ✅ Données initiales (classes, rôles, etc.)
- ✅ Utilisateur par défaut : `directeur` / `directeur123`

---

## 🔧 Dépannage

### Le backend ne démarre pas

1. Vérifiez les logs sur Render
2. Vérifiez que le disque `/data` est bien monté
3. Vérifiez les variables d'environnement

### L'application mobile ne se connecte pas

1. Vérifiez l'URL dans `.env.production`
2. Testez l'URL backend dans le navigateur : `https://votre-app.onrender.com/api-docs`
3. Vérifiez que le backend accepte les requêtes CORS

### Build APK échoue

```bash
# Nettoyer le cache
cd mobile/android
./gradlew clean

# Rebuild
cd ..
npx cap sync android
npx cap open android
```

---

## 📱 Distribution de l'APK

### Option 1 : Installation directe

1. Transférez l'APK sur le téléphone
2. Activez "Sources inconnues" dans les paramètres
3. Installez l'APK

### Option 2 : Google Play Store (pour publication)

1. Créez un compte Google Play Developer (25$ unique)
2. Préparez les ressources (icônes, captures d'écran, description)
3. Uploadez l'APK signée
4. Suivez le processus de révision

---

## 🎉 Félicitations !

Votre application est maintenant déployée en production ! 🚀

**Prochaines étapes :**
- Testez l'application sur plusieurs appareils
- Surveillez les logs pour détecter les erreurs
- Configurez les sauvegardes de la base SQLite
- Ajoutez Google Analytics (optionnel)

---

## 📞 Support

En cas de problème :
- Vérifiez les logs Render
- Consultez la documentation Capacitor : https://capacitorjs.com
- Consultez la documentation Render : https://render.com/docs
