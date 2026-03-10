# 🔧 Résolution : Problème de connexion sur l'APK

## ❌ Symptôme

L'APK installée sur le téléphone ne peut pas se connecter avec `directeur` / `directeur123`

## ✅ Backend vérifié

Le backend fonctionne parfaitement :

```bash
curl -X POST https://explorateurs-backend.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"directeur","password":"directeur123"}'
```

**Résultat :** ✅ Token JWT retourné correctement

## 🐛 Cause du problème

L'APK a probablement été buildée en **mode développement** et utilise encore l'URL locale :
```
http://localhost:8080  ❌
```

Au lieu de l'URL de production :
```
https://explorateurs-backend.onrender.com  ✅
```

---

## 🔧 Solution : Rebuilder en mode production

### Étape 1 : Vérifier la configuration

```bash
cat mobile/.env.production
```

**Doit contenir :**
```
VITE_API_URL=https://explorateurs-backend.onrender.com
```

### Étape 2 : Rebuilder l'APK correctement

```bash
cd /home/mangalahy/PERSO/AUTRE/Explo/Gestion_explo_mobile
chmod +x rebuild-apk-prod.sh
./rebuild-apk-prod.sh
```

**Ce script va :**
1. ✅ Nettoyer les anciens builds
2. ✅ Builder en mode production (avec .env.production)
3. ✅ Vérifier que l'URL Render est dans le build
4. ✅ Synchroniser avec Capacitor

### Étape 3 : Générer le nouvel APK dans Android Studio

```bash
cd mobile
npx cap open android
```

Dans Android Studio :
1. **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. Attendez 2-5 minutes
3. Cliquez sur **locate** pour trouver l'APK

### Étape 4 : Installer le nouvel APK

1. **Désinstallez** l'ancienne app du téléphone
2. Transférez le nouvel APK
3. Installez-le
4. Testez la connexion : `directeur` / `directeur123`

---

## 🔍 Comment vérifier que l'APK utilise la bonne URL

### Méthode 1 : Chrome DevTools (Téléphone)

Si vous avez activé le débogage USB :

1. Connectez le téléphone à l'ordinateur
2. Ouvrez Chrome : `chrome://inspect`
3. Sélectionnez votre app
4. Console → Tapez : `import.meta.env.VITE_API_URL`
5. Doit afficher : `https://explorateurs-backend.onrender.com`

### Méthode 2 : Logs de l'app

Dans le code, l'URL utilisée est configurée dans :
```
mobile/src/config/api.config.ts
```

Elle utilise : `import.meta.env.VITE_API_URL`

---

## 📋 Checklist de vérification

Avant de générer l'APK :

- [x] `.env.production` contient l'URL Render
- [ ] Build nettoyé (`rm -rf mobile/dist/`)
- [ ] Build fait avec `--mode production`
- [ ] URL Render trouvée dans `mobile/dist/` après build
- [ ] `npx cap sync android` exécuté
- [ ] APK générée dans Android Studio
- [ ] Ancienne app désinstallée du téléphone
- [ ] Nouvelle APK installée
- [ ] Test login OK

---

## 🆘 Si ça ne marche toujours pas

### Vérifiez les erreurs réseau

Sur le téléphone, ouvrez le navigateur et allez sur :
```
https://explorateurs-backend.onrender.com/swagger-ui.html
```

- ✅ Si ça charge → Le backend est OK
- ❌ Si erreur → Problème réseau ou backend endormi

### Vérifiez CORS

Le backend a déjà CORS configuré dans `CorsConfig.java` :
```java
.allowedOrigins("*")
.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
```

CORS devrait fonctionner.

### Testez avec l'app web

Depuis le navigateur du téléphone, allez sur :
```
http://[VOTRE_IP]:5173
```

Si vous lancez le serveur de dev :
```bash
cd mobile
npm run dev -- --host
```

---

## 📱 Commandes de build recommandées

**Développement (localhost) :**
```bash
cd mobile
npm run dev
```

**Production (Render.com) :**
```bash
cd mobile
npm run build -- --mode production
npx cap sync android
npx cap open android
```

---

## 🎯 Résumé

**Problème :** APK buildée en mode dev avec URL localhost  
**Solution :** Rebuilder avec `--mode production` pour utiliser `.env.production`  
**Script :** `./rebuild-apk-prod.sh`  
**Vérification :** URL Render doit être dans `mobile/dist/` après build

---

Date de diagnostic : 10 mars 2026
