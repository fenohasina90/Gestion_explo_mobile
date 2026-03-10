# 🚀 Guide Complet - Déploiement sur Render.com

Ce guide vous explique étape par étape comment déployer votre backend Spring Boot sur Render.com.

---

## 📋 Prérequis

- ✅ Un compte GitHub/GitLab avec votre code
- ✅ Un compte Render.com (gratuit)
- ✅ Le fichier `BD_sqlite.sql` dans `backend/src/main/resources/sql/`
- ✅ Le fichier `DatabaseInitializer.java` créé

---

## 🎯 Étape 1 : Créer un compte Render.com

1. Allez sur **https://render.com**

2. Cliquez sur **"Get Started"** ou **"Sign Up"**

3. Inscrivez-vous avec **GitHub** (recommandé) ou GitLab :
   ```
   ┌─────────────────────────────────┐
   │  Sign up with GitHub            │
   │  Sign up with GitLab            │
   │  Sign up with Email             │
   └─────────────────────────────────┘
   ```

4. Autorisez Render à accéder à vos repositories

---

## 📤 Étape 2 : Pousser votre code sur GitHub

Si ce n'est pas déjà fait :

```bash
cd /home/mangalahy/PERSO/AUTRE/Explo/Gestion_explo_mobile

# Initialiser git si nécessaire
git init

# Ajouter tous les fichiers
git add .

# Commit
git commit -m "Initial commit - Application Explorations"

# Créer un repository sur GitHub (via l'interface web)
# Puis ajouter le remote
git remote add origin https://github.com/VotreUsername/Gestion_explo_mobile.git

# Pousser le code
git push -u origin main
```

---

## 🌐 Étape 3 : Créer un Web Service

1. **Dans le Dashboard Render**, cliquez sur **"New +"** (en haut à droite)

2. Sélectionnez **"Web Service"**

3. **Connectez votre repository** :
   - Si c'est la première fois, cliquez sur "Connect account"
   - Autorisez Render à accéder à vos repos
   - Sélectionnez votre repository `Gestion_explo_mobile`

4. Cliquez sur **"Connect"** à côté du repository

---

## ⚙️ Étape 4 : Configuration du Service

### 4.1 Informations de base

Remplissez le formulaire avec ces valeurs :

```
┌─────────────────────────────────────────────┐
│ Name: explorateurs-backend                  │
│ (ou un nom de votre choix)                  │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ Region: Frankfurt (EU Central)              │
│ (ou Oregon/Singapore selon votre location) │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ Branch: main                                │
│ (ou votre branche principale)               │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ Root Directory: (laissez vide)              │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ Runtime: Java                               │
└─────────────────────────────────────────────┘
```

### 4.2 Build Command

```bash
cd backend && mvn clean package -DskipTests
```

**Copiez cette commande exactement !**

### 4.3 Start Command

```bash
cd backend && java -Dspring.profiles.active=prod -jar target/*.jar
```

**Copiez cette commande exactement !**

### 4.4 Plan

Sélectionnez **"Free"** (0$/mois)

```
┌─────────────────────────────────────────────┐
│ ○ Starter - $7/mo                           │
│ ● Free    - $0/mo  ← Sélectionnez celui-ci │
└─────────────────────────────────────────────┘
```

**⚠️ Important** : Le plan Free a quelques limitations :
- L'application se met en veille après 15 minutes d'inactivité
- 750 heures/mois gratuit (suffisant pour un usage normal)
- 512 MB RAM

---

## 🔧 Étape 5 : Variables d'environnement

**Descendez jusqu'à la section "Environment Variables"**

Cliquez sur **"Add Environment Variable"** et ajoutez :

### Variable 1 : PORT
```
Key:   PORT
Value: 8080
```

### Variable 2 : SPRING_PROFILES_ACTIVE
```
Key:   SPRING_PROFILES_ACTIVE
Value: prod
```

### Variable 3 : JWT_SECRET (Important !)
```
Key:   JWT_SECRET
Value: VotreCleSecreteTresLongueEtSecurisee2026ExplorateursMangalahyFenohasina
```

**⚠️ Changez cette valeur** pour quelque chose d'unique et long (minimum 32 caractères)

Vous pouvez générer une clé sécurisée avec :
```bash
openssl rand -base64 32
```

Résultat de vos variables :
```
┌─────────────────────────────────────────────────────┐
│ Environment Variables                               │
├─────────────────────────────────────────────────────┤
│ PORT                  = 8080                        │
│ SPRING_PROFILES_ACTIVE = prod                       │
│ JWT_SECRET            = VotreCle...                 │
└─────────────────────────────────────────────────────┘
```

---

## 💾 Étape 6 : Ajouter un Disque Persistant (CRUCIAL pour SQLite !)

**C'est l'étape la plus importante !** Sans disque persistant, votre base SQLite sera perdue à chaque redémarrage.

### 6.1 Cliquez sur "Advanced"

Descendez jusqu'à la section **"Disks"**

### 6.2 Cliquez sur "+ Add Disk"

Configurez le disque :

```
┌─────────────────────────────────────────────┐
│ Name:       data                            │
│ Mount Path: /data                           │
│ Size:       1 GB                            │
└─────────────────────────────────────────────┘
```

**Valeurs exactes** :
- **Name** : `data`
- **Mount Path** : `/data`
- **Size** : `1` GB (amplement suffisant pour votre base SQLite)

### 6.3 Vérification

Vous devriez voir :
```
┌─────────────────────────────────────────────┐
│ Disks                                       │
├─────────────────────────────────────────────┤
│ Name: data                                  │
│ Mount Path: /data                           │
│ Size: 1 GB                                  │
│ [Remove]                                    │
└─────────────────────────────────────────────┘
```

---

## 🚀 Étape 7 : Créer le Service

1. **Vérifiez votre configuration** :
   - ✅ Build Command : `cd backend && mvn clean package -DskipTests`
   - ✅ Start Command : `cd backend && java -Dspring.profiles.active=prod -jar target/*.jar`
   - ✅ 3 variables d'environnement (PORT, SPRING_PROFILES_ACTIVE, JWT_SECRET)
   - ✅ Disque `/data` de 1GB créé
   - ✅ Plan Free sélectionné

2. Cliquez sur **"Create Web Service"** en bas de la page

3. **Attendez le déploiement** (5-10 minutes la première fois)

---

## 📊 Étape 8 : Suivre le déploiement

### 8.1 Logs en temps réel

Render affiche automatiquement les logs. Vous devriez voir :

```
==> Building...
[INFO] Scanning for projects...
[INFO] Building backend 0.0.1-SNAPSHOT
[INFO] BUILD SUCCESS

==> Deploying...
🔧 Base de données non trouvée. Initialisation en cours...
✅ Répertoire créé : /data
✅ Base de données initialisée avec succès !
📍 Emplacement : /data/explorateurs.db
📊 Nombre de tables créées : 35

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::

Started BackendApplication in 8.123 seconds

==> Your service is live 🎉
```

### 8.2 Vérifier le succès

Dans les logs, cherchez ces lignes importantes :

✅ **Base de données initialisée** :
```
✅ Base de données initialisée avec succès !
📊 Nombre de tables créées : 35
```

✅ **Application démarrée** :
```
Started BackendApplication in X seconds
```

✅ **Service live** :
```
==> Your service is live 🎉
```

---

## 🌐 Étape 9 : Récupérer l'URL de votre API

### 9.1 Trouver l'URL

En haut de la page de votre service, vous verrez :

```
┌──────────────────────────────────────────────────────┐
│ explorateurs-backend                                 │
│ https://explorateurs-backend.onrender.com           │
│                                                      │
│ Status: ● Live                                      │
└──────────────────────────────────────────────────────┘
```

**Notez cette URL** : `https://explorateurs-backend.onrender.com`

### 9.2 Tester l'API

Ouvrez dans votre navigateur :

```
https://explorateurs-backend.onrender.com/swagger-ui.html
```

Vous devriez voir l'interface Swagger avec toutes vos API !

### 9.3 Test de connexion

Testez l'endpoint de login :

```bash
curl -X POST https://explorateurs-backend.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "directeur",
    "password": "directeur123"
  }'
```

Vous devriez recevoir un token JWT !

---

## 📱 Étape 10 : Configurer l'application mobile

### 10.1 Modifier l'URL de production

```bash
cd mobile
nano .env.production
```

Remplacez l'URL par celle de Render :

```env
# Variables d'environnement pour la production

# URL de l'API backend en production
VITE_API_URL=https://explorateurs-backend.onrender.com

# Mode production
VITE_MODE=production
```

### 10.2 Rebuild l'application mobile

```bash
# Build
./build-apk.sh

# Ouvrir Android Studio
npx cap open android
```

---

## 🔍 Étape 11 : Vérifications importantes

### ✅ Checklist de déploiement

Dans le dashboard Render, vérifiez :

- [ ] **Status** : "● Live" (point vert)
- [ ] **Logs** : Pas d'erreurs rouges
- [ ] **Disk** : `/data` monté et visible dans Settings → Disks
- [ ] **URL** : Accessible dans le navigateur
- [ ] **Swagger** : `https://votre-url.onrender.com/swagger-ui.html` fonctionne
- [ ] **Login** : Test avec directeur/directeur123 réussit

### 🔧 Vérifier la base de données

Dans les logs, vous devriez voir au démarrage :

```
✅ Base de données existante trouvée : /data/explorateurs.db
📊 Tables dans la base : 35
```

Si vous voyez "Base de données non trouvée", c'est normal **la première fois seulement**.

---

## 🎯 Fonctionnalités du plan Free

### ✅ Ce qui est inclus

- 750 heures/mois (suffisant pour 1 app)
- 512 MB RAM
- Disque persistant inclus
- SSL/HTTPS automatique
- Logs en temps réel
- Déploiement automatique depuis Git

### ⚠️ Limitations

- **Sleep après 15 min d'inactivité** : La première requête après le sleep prend ~30 secondes
- **Build time** : 10 minutes max (votre app compile en ~5 min)
- **Bande passante** : 100 GB/mois (largement suffisant)

### 💡 Astuce pour éviter le sleep

Utilisez un service de ping gratuit comme **UptimeRobot** ou **Cron-job.org** pour pinguer votre API toutes les 10 minutes :

```
https://votre-app.onrender.com/api-docs
```

---

## 📊 Monitoring et Maintenance

### Accéder aux logs

Dashboard → Votre service → **Logs** (onglet)

### Redémarrer le service

Dashboard → Votre service → **Manual Deploy** → **Deploy latest commit**

Ou utilisez le bouton **"Restart"** dans Settings

### Sauvegarder la base de données

⚠️ **Important** : Le disque Render est persistant, mais faites des sauvegardes !

Pour télécharger la base :

1. Connectez-vous via SSH (plan payant) OU
2. Créez un endpoint d'export dans votre API OU
3. Utilisez le plan payant pour accéder au shell

---

## 🆘 Dépannage

### ❌ Problème 1 : Build échoue

**Symptôme** : `Build failed` dans les logs

**Solutions** :
1. Vérifiez que `backend/pom.xml` existe
2. Vérifiez le build command : `cd backend && mvn clean package -DskipTests`
3. Testez localement : `./build-backend.sh`

### ❌ Problème 2 : Base de données non créée

**Symptôme** : Erreurs SQL dans les logs

**Solutions** :
1. Vérifiez que le disque `/data` est créé
2. Vérifiez que `BD_sqlite.sql` existe dans `backend/src/main/resources/sql/`
3. Vérifiez que `DatabaseInitializer.java` est présent

### ❌ Problème 3 : Application crash au démarrage

**Symptôme** : "Service failed" après déploiement

**Solutions** :
1. Vérifiez les logs pour l'erreur exacte
2. Vérifiez les variables d'environnement
3. Vérifiez le start command : `cd backend && java -Dspring.profiles.active=prod -jar target/*.jar`

### ❌ Problème 4 : CORS errors depuis mobile

**Symptôme** : "CORS policy" dans les logs mobile

**Solution** : Vérifiez que `CorsConfig.java` existe dans votre backend

### ❌ Problème 5 : App très lente

**Symptôme** : Première requête prend 30+ secondes

**Cause** : L'app était en sleep (normal pour le plan Free)

**Solutions** :
1. Acceptez le délai (normal)
2. Utilisez UptimeRobot pour pinguer toutes les 10 min
3. Passez au plan Starter ($7/mois) pour éviter le sleep

---

## 🎉 Félicitations !

Votre backend est maintenant déployé sur Render.com ! 🚀

**Votre API est accessible à** :
```
https://explorateurs-backend.onrender.com
```

**Prochaines étapes** :
1. ✅ Testez toutes les API via Swagger
2. ✅ Configurez l'URL dans l'application mobile
3. ✅ Créez l'APK Android
4. ✅ Testez l'application complète

---

## 📞 Support

### Documentation officielle
- Render Docs : https://render.com/docs
- Render Community : https://community.render.com

### En cas de problème
1. Consultez les logs Render
2. Vérifiez la checklist de déploiement
3. Testez localement avec `./test-prod-local.sh`

---

**Développé par** : MANGALAHY Fenohasina
**Email** : mangalahyfenohasina@gmail.com
**WhatsApp** : +261 38 43 371 19
