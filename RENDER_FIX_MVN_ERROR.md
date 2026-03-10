# 🔧 Résolution : Build Failed - mvn: command not found

## ❌ Erreur reçue

```
bash: line 1: mvn: command not found
==> Build failed 😞
```

## 🎯 Cause

Render n'a pas Maven installé car l'environnement n'est pas configuré pour Java.

---

## ✅ Solution 1 : Utiliser Docker (RECOMMANDÉ)

### Étape 1 : Pusher le Dockerfile

```bash
cd /home/mangalahy/PERSO/AUTRE/Explo/Gestion_explo_mobile
git add Dockerfile
git commit -m "Add Dockerfile for Render deployment"
git push
```

### Étape 2 : Reconfigurer Render

1. Allez dans votre service `explorateurs-backend` sur Render.com
2. Cliquez sur **Settings** (à gauche)
3. Trouvez **Environment**
4. Changez de `Node` ou autre vers **`Docker`**
5. Laissez **Dockerfile Path** vide (il détectera automatiquement `/Dockerfile`)
6. Supprimez ou laissez vides les champs **Build Command** et **Start Command**
7. Cliquez sur **Save Changes** en bas
8. Retournez à l'onglet **Events** et cliquez **Manual Deploy → Deploy latest commit**

---

## ✅ Solution 2 : Corriger les Build Commands (Alternative)

Si vous ne voulez pas utiliser Docker :

### 1. Dans Render Settings → Build & Deploy

**Environment :** Choisissez `Java` si disponible, sinon laissez `Node`

**Build Command :**
```bash
cd backend && mvn clean package -DskipTests
```

**Start Command :**
```bash
cd backend && java -Dspring.profiles.active=prod -jar target/*.jar
```

### 2. Ajoutez un script de pré-installation

Si Maven n'est toujours pas reconnu, créez un fichier `render-build.sh` :

```bash
#!/bin/bash
# Installation de Java et Maven pour Render
echo "Installation de Java et Maven..."
apt-get update
apt-get install -y openjdk-21-jdk maven
cd backend && mvn clean package -DskipTests
```

Puis dans Render :
- **Build Command :** `bash render-build.sh`

---

## 📝 Vérification avant le prochain déploiement

Checklist :

- [ ] **Dockerfile** créé et pushé sur GitHub
- [ ] Environment = `Docker` dans Render
- [ ] Variables d'environnement configurées :
  - `PORT=8080`
  - `SPRING_PROFILES_ACTIVE=prod`
  - `JWT_SECRET=4r6x7q+eAywDb1tqm0a1tN/Do/E6dKYLvf0UvLoWONk=`
- [ ] Disk `/data` configuré (1GB)
- [ ] Build/Start commands supprimés (Docker gère tout)

---

## 🚀 Redéployer

Après avoir fait les changements :

1. **Manual Deploy** → **Deploy latest commit**
2. Attendez 5-10 minutes
3. Vérifiez les logs : vous devriez voir Maven compiler et Spring Boot démarrer

---

## 📊 Logs attendus (succès)

```
==> Building with Dockerfile
Step 1/11 : FROM maven:3.9-eclipse-temurin-21 AS build
Step 2/11 : WORKDIR /app
...
[INFO] BUILD SUCCESS
...
Step 8/11 : EXPOSE 8080
Step 9/11 : ENV SPRING_PROFILES_ACTIVE=prod
==> Build succeeded 🎉
==> Starting service...
Started BackendApplication in X seconds
```

---

🆘 **Besoin d'aide ?** Vérifiez [RENDER_DEPLOIEMENT.md](RENDER_DEPLOIEMENT.md) pour le guide complet
