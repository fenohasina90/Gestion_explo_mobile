# Générer APK avec URL Render correcte

## ✅ Diagnostic effectué

### Build actuel (CORRECT):
- ✅ `dist/` buildé à 17:03 avec URL Render
- ✅ Assets Capacitor synchronisés avec URL Render  
- ✅ 0 occurrence de `localhost:8080`
- ✅ 2 occurrences de `explorateurs-backend.onrender.com`

### Problème identifié:
❌ **Aucun APK trouvé** dans le projet après le dernier build  
❌ Vous avez probablement installé une **ancienne APK** générée avant la correction `.env.production`

---

## 📱 Générer la nouvelle APK

### ⚡ Méthode recommandée: Ligne de commande (plus rapide)

```bash
cd /home/mangalahy/PERSO/AUTRE/Explo/Gestion_explo_mobile/mobile

# Clean build
rm -rf android/app/build/

# Générer APK
cd android
./gradlew assembleDebug

# APK générée dans:
# android/app/build/outputs/apk/debug/app-debug.apk
```

### OU: Via Android Studio

Android Studio est en train de s'ouvrir. Une fois ouvert:

### 1. Nettoyer le projet
```
Menu → Build → Clean Project
```
⏳ Attendez que le nettoyage se termine

### 2. Générer l'APK
```
Menu → Build → Build Bundle(s) / APK(s) → Build APK(s)
```
⏳ Attendez le message "APK(s) generated successfully" (2-3 minutes)

### 3. Localiser l'APK générée
```
android/app/build/outputs/apk/debug/app-debug.apk
```

### 4. Vérifier la date de l'APK
```bash
ls -lh android/app/build/outputs/apk/debug/app-debug.apk
```
L'APK doit être **postérieure à 17:03** (date du dernier build dist/)

---

## 📲 Installer sur téléphone

### Option 1: Via câble USB
```bash
# Activer débogage USB sur téléphone
# Connecter téléphone au PC
adb install -r android/app/build/outputs/apk/debug/app-debug.apk
```

### Option 2: Copie manuelle
1. Copier `app-debug.apk` sur téléphone (USB/Bluetooth/Email)
2. **DÉSINSTALLER l'ancienne app** sur téléphone
3. Installer la nouvelle `app-debug.apk`

---

## 🧪 Tester le login

### Credentials:
```
Username: directeur
Password: directeur123
```

### Si ça ne marche toujours pas:

#### Test 1: Backend accessible du téléphone?
```
Ouvrir browser téléphone → https://explorateurs-backend.onrender.com/swagger-ui.html
```
✅ Devrait charger la page Swagger UI

#### Test 2: Vérifier logs backend
```
# Sur Render.com dashboard
Logs → Voir si requêtes arrivent du téléphone
```

#### Test 3: Activer débogage Chrome (USB)
```
1. Téléphone connecté en USB avec débogage activé
2. Chrome desktop → chrome://inspect
3. Sélectionner l'app
4. Voir console JavaScript pour erreurs
```

---

## ⚠️ Erreurs possibles et solutions

### Erreur: "Network Error" ou "ERR_CONNECTION_REFUSED"
**Cause**: Backend Render endormi (Free tier s'arrête après 15 min)  
**Solution**: Ouvrir https://explorateurs-backend.onrender.com/swagger-ui.html pour réveiller backend, puis réessayer login

### Erreur: "Invalid credentials"
**Cause**: Credentials incorrects ou base de données non initialisée  
**Solution**: Vérifier logs Render pour voir si tables ont été créées

### Erreur: "CORS error"
**Cause**: Backend refuse requêtes de l'app mobile  
**Solution**: Vérifier configuration CORS dans backend (doit accepter toutes origines en prod)

---

## 📝 Checklist finale

Avant d'installer l'APK:

- [ ] APK générée **après 17:03** (date build dist/)
- [ ] Ancienne app **désinstallée** du téléphone
- [ ] Backend **accessible** depuis browser téléphone
- [ ] Téléphone connecté **au même réseau** (WiFi/4G actif)

Après installation:

- [ ] Login avec directeur/directeur123
- [ ] Vérifier logs Render si requêtes arrivent
- [ ] Activer Chrome inspect si erreurs persistent

---

## ✅ Succès attendu

Après installation de la nouvelle APK:

1. Ouvrir l'app
2. Écran login s'affiche
3. Entrer: `directeur` / `directeur123`
4. ✅ Login réussit et redirige vers page d'accueil
5. ✅ Données chargées depuis https://explorateurs-backend.onrender.com

---

**Note importante**: L'APK actuelle sur votre téléphone utilise encore `localhost:8080`. Il **faut absolument** désinstaller l'ancienne app et installer la nouvelle APK générée après 17:03.
