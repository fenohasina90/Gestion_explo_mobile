# ⚡ Déploiement Render.com - Guide Express

## 🎯 En 5 minutes chrono !

### 1️⃣ Créer le service (2 min)

1. **https://render.com** → Sign up avec GitHub
2. **New +** → **Web Service** → Connectez votre repo
3. Nom : `explorateurs-backend`

### 2️⃣ Configuration (2 min)

**⚠️ IMPORTANT - Choisissez UNE option :**

#### **Option A : Avec Docker (Recommandé)** ✅

**Environment:** `Docker`

Le Dockerfile est déjà créé, pas besoin de Build/Start Command

#### **Option B : Sans Docker (Java/Maven manuel)**

**Environment:** `Java` (si disponible) ou `Node`

**Build Command:**
```bash
cd backend && mvn clean package -DskipTests
```

**Start Command:**
```bash
cd backend && java -Dspring.profiles.active=prod -jar target/*.jar
```

⚠️ **Note:** Render doit avoir Maven et Java 21 configurés

---

**Variables d'environnement (Required pour les 2 options) :**
```
PORT=8080
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=4r6x7q+eAywDb1tqm0a1tN/Do/E6dKYLvf0UvLoWONk=
```

💡 **Note JWT_SECRET** : Clé générée aléatoirement. Pour en générer une nouvelle :
```bash
openssl rand -base64 32
```
Ou utilisez le bouton **"Generate"** dans Render.

**Disk (Advanced → Disks → Add Disk):**
```
Name: data
Mount Path: /data
Size: 1 GB
```

**Plan:** Free ✅

### 3️⃣ Déployer (1 min)

Cliquez sur **"Create Web Service"**

Attendez 5-10 minutes ⏱️

### 4️⃣ Récupérer l'URL

Notez l'URL : `https://explorateurs-backend.onrender.com`

### 5️⃣ Tester

```bash
# Dans le navigateur
https://explorateurs-backend.onrender.com/swagger-ui.html
```

### 6️⃣ Configurer le mobile

```bash
# Modifier l'URL
nano mobile/.env.production
# VITE_API_URL=https://explorateurs-backend.onrender.com

# Build APK
./build-apk.sh
```

---

## ✅ Checklist rapide

- [ ] Service créé sur Render
- [ ] Build + Start commands ajoutés
- [ ] 3 variables d'environnement
- [ ] Disk `/data` créé (1GB)
- [ ] Déploiement réussi (logs verts)
- [ ] URL notée
- [ ] Swagger accessible
- [ ] Test login OK
- [ ] URL mise à jour dans mobile
- [ ] APK buildée

---

## 🆘 Erreurs courantes

**Build failed** → Vérifiez le build command
**Crash au start** → Vérifiez les variables d'env
**Base non créée** → Vérifiez le disk `/data`
**CORS errors** → Normal, CorsConfig existe déjà

---

📚 **Guide détaillé** : [RENDER_DEPLOIEMENT.md](RENDER_DEPLOIEMENT.md)
