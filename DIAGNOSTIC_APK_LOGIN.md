# 🔍 Diagnostic - Problème de connexion APK

## ✅ Ce qui fonctionne

1. ✅ Backend Render accessible : https://explorateurs-backend.onrender.com
2. ✅ Login API fonctionne (testé avec curl)
3. ✅ Build dist/ contient l'URL Render
4. ✅ Assets Android contiennent l'URL Render (pas de localhost)
5. ✅ APK générée après le dernier build

## ❌ Problème

L'APK ne peut pas se connecter avec `directeur` / `directeur123`

---

## 🔍 Tests de diagnostic

### Test 1 : Vérifier la connexion Internet du téléphone

Sur le téléphone, ouvrez le navigateur et allez sur :
```
https://explorateurs-backend.onrender.com/swagger-ui.html
```

**Résultat attendu :** Page Swagger s'affiche

❌ **Si ça ne charge pas** : Problème réseau ou backend endormi
✅ **Si ça charge** : Le téléphone peut accéder au backend

---

### Test 2 : Vérifier les credentials

**Credentials corrects :**
```
Username: directeur
Password: directeur123
```

⚠️ **Attention :** Pas d'espaces avant/après

---

### Test 3 : Tester l'API REST directement depuis le téléphone

Installez l'app **REST API Tester** ou **HTTP Request** depuis Google Play

Faites une requête :
```
POST https://explorateurs-backend.onrender.com/api/auth/login

Headers:
Content-Type: application/json

Body:
{
  "username": "directeur",
  "password": "directeur123"
}
```

**Résultat attendu :** Token JWT retourné

---

### Test 4 : Activer le mode développeur USB et voir les logs

1. Activez **Options développeur** sur Android
2. Activez **Débogage USB**
3. Connectez le téléphone à l'ordinateur
4. Ouvrez Chrome : `chrome://inspect`
5. Sélectionnez votre app
6. Dans la Console, vérifiez :
   - Les erreurs réseau
   - L'URL utilisée
   - Les requêtes HTTP

---

## 🔧 Solutions possibles

### Solution 1 : Re-build complet avec nettoyage

```bash
cd /home/mangalahy/PERSO/AUTRE/Explo/Gestion_explo_mobile/mobile

# Nettoyage complet
rm -rf dist/
rm -rf android/app/build/
rm -rf node_modules/.vite/

# Installation propre
npm install

# Build production
npm run build -- --mode production

# Sync Capacitor
npx cap sync android

# Ouvrir Android Studio
npx cap open android
```

**Dans Android Studio :**
1. **Build** → **Clean Project**
2. **Build** → **Rebuild Project**
3. **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**

---

### Solution 2 : Vérifier la configuration réseau Android

Ajoutez cette permission dans `android/app/src/main/AndroidManifest.xml` :

```xml
<manifest>
    ...
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    ...
</manifest>
```

---

### Solution 3 : Vérifier le code de login

Vérifiez le fichier : `mobile/src/views/LoginPage.vue`

L'URL doit être prise depuis `API_CONFIG.baseURL` qui lui-même utilise `import.meta.env.VITE_API_URL`

---

### Solution 4 : Tester en mode développement sur le réseau

Sur l'ordinateur :
```bash
cd /home/mangalahy/PERSO/AUTRE/Explo/Gestion_explo_mobile/mobile
npm run dev -- --host
```

Notez l'URL affichée (ex: `http://192.168.1.X:5173`)

Sur le téléphone (browser), allez sur cette URL

Testez le login → Si ça marche = problème dans l'APK, si ça ne marche pas = problème dans le code

---

## 🎯 Checklist de vérification

- [ ] Backend accessible depuis le navigateur du téléphone
- [ ] Credentials corrects (directeur/directeur123)
- [ ] Pas d'espaces dans username/password
- [ ] APK désinstallée et réinstallée
- [ ] Permissions Internet dans AndroidManifest.xml
- [ ] Clean + Rebuild dans Android Studio
- [ ] Test avec REST API Tester sur téléphone
- [ ] Vérification des logs via chrome://inspect

---

## 📱 Message d'erreur exact

**Quelle erreur voyez-vous exactement ?**

- [ ] "Nom d'utilisateur ou mot de passe incorrect"
- [ ] "Erreur de connexion" / "Network error"
- [ ] "Serveur non accessible"
- [ ] Rien ne se passe (bouton ne réagit pas)
- [ ] Autre : ___________________

**Cela aidera à identifier la cause exacte !**

---

## 🆘 Si rien ne fonctionne

Vérifiez les logs Render :

1. Allez sur https://dashboard.render.com
2. Cliquez sur votre service `explorateurs-backend`
3. Onglet **Logs**
4. Tentez de vous connecter depuis l'APK
5. Regardez si des requêtes arrivent dans les logs

**Si aucune requête n'arrive** = L'APK n'envoie pas vers la bonne URL  
**Si des requêtes arrivent avec 401** = Problème de credentials  
**Si des requêtes arrivent avec 500** = Problème backend

---

Date : 10 mars 2026
