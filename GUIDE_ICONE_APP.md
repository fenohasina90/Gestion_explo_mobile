# 📱 Guide: Personnalisation de l'icône de l'application

## 🎯 Objectif
Remplacer l'icône par défaut par le logo Pathfinder Club dans la liste des applications Android.

---

## 📋 Prérequis

Votre logo doit avoir ces caractéristiques :
- **Format** : PNG (avec transparence de préférence)
- **Taille** : 1024x1024 pixels minimum
- **Qualité** : Haute résolution, centré, marges égales

---

## 🚀 Méthode rapide (automatique)

### Étape 1 : Préparer le logo

1. **Ouvrez votre logo** (`pathfinder-logo.png`) avec GIMP ou un éditeur d'images
2. **Redimensionnez** à 1024x1024 pixels :
   - Gardez les proportions
   - Ajoutez un fond blanc si nécessaire
   - Centrez le logo
3. **Exportez** en PNG de haute qualité
4. **Sauvegardez** dans :
   ```
   mobile/resources/icon.png
   ```

### Étape 2 : Générer toutes les tailles d'icônes

```bash
cd mobile
./generate-icons.sh
```

Le script va automatiquement :
- ✅ Installer cordova-res si nécessaire
- ✅ Générer 5 tailles d'icônes (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi)
- ✅ Copier dans les bons dossiers Android

### Étape 3 : Rebuild l'APK

```bash
# Rebuild le projet
npm run build

# Synchroniser avec Android
npx cap sync

# Générer l'APK
cd android
./gradlew assembleRelease
```

L'APK sera dans : `android/app/build/outputs/apk/release/app-release-unsigned.apk`

---

## 🔧 Méthode manuelle (si besoin)

Si vous voulez contrôler chaque taille d'icône :

### 1. Créer les tailles requises

Créez ces 5 versions de votre logo :

| Densité | Taille | Dossier de destination |
|---------|--------|------------------------|
| mdpi    | 48x48  | `android/app/src/main/res/mipmap-mdpi/` |
| hdpi    | 72x72  | `android/app/src/main/res/mipmap-hdpi/` |
| xhdpi   | 96x96  | `android/app/src/main/res/mipmap-xhdpi/` |
| xxhdpi  | 144x144| `android/app/src/main/res/mipmap-xxhdpi/` |
| xxxhdpi | 192x192| `android/app/src/main/res/mipmap-xxxhdpi/` |

### 2. Remplacer les fichiers

Dans chaque dossier, remplacez :
- `ic_launcher.png` (icône carrée)
- `ic_launcher_round.png` (icône ronde)
- `ic_launcher_foreground.png` (premier plan pour icône adaptative)

### 3. Rebuild

Même étape que la méthode automatique.

---

## 🎨 Conseils de design

**Pour un meilleur rendu :**

1. **Icône carrée** (`ic_launcher.png`) :
   - Ajoutez un fond de couleur (rouge/jaune Pathfinder)
   - Ou gardez le transparent avec un contour

2. **Icône ronde** (`ic_launcher_round.png`) :
   - Assurez-vous que le logo reste visible quand rogné en cercle
   - Testez avec un masque circulaire

3. **Icône adaptative** (`ic_launcher_foreground.png`) :
   - Utilisez uniquement le logo sans fond
   - Le système Android ajoutera le fond automatiquement

---

## ✅ Vérification

Après génération, vérifiez que ces fichiers existent :

```bash
ls -la android/app/src/main/res/mipmap-*/ic_launcher.png
```

Vous devriez voir 5 fichiers (un par densité).

---

## 🐛 Dépannage

**Problème : L'icône ne change pas après rebuild**

Solution :
```bash
# Nettoyer le cache
cd android
./gradlew clean

# Désinstaller l'ancienne version du téléphone
adb uninstall com.explorateurs.mobile

# Rebuild et réinstaller
cd ..
npm run build
npx cap sync
cd android
./gradlew assembleRelease
```

**Problème : cordova-res ne fonctionne pas**

Solution alternative :
```bash
# Utiliser @capacitor/assets (plus récent)
npm install -g @capacitor/assets
npx capacitor-assets generate --android
```

---

## 📦 Fichiers générés

Après exécution, ces fichiers seront créés/remplacés :

```
android/app/src/main/res/
├── mipmap-mdpi/
│   ├── ic_launcher.png           (48x48)
│   ├── ic_launcher_foreground.png
│   └── ic_launcher_round.png
├── mipmap-hdpi/
│   ├── ic_launcher.png           (72x72)
│   ├── ic_launcher_foreground.png
│   └── ic_launcher_round.png
├── mipmap-xhdpi/
│   ├── ic_launcher.png           (96x96)
│   ├── ic_launcher_foreground.png
│   └── ic_launcher_round.png
├── mipmap-xxhdpi/
│   ├── ic_launcher.png           (144x144)
│   ├── ic_launcher_foreground.png
│   └── ic_launcher_round.png
└── mipmap-xxxhdpi/
    ├── ic_launcher.png           (192x192)
    ├── ic_launcher_foreground.png
    └── ic_launcher_round.png
```

---

## 🎯 Résultat attendu

Après installation de l'APK, vous verrez :
- ✅ Logo Pathfinder dans la liste des applications
- ✅ Logo Pathfinder dans les paramètres Android
- ✅ Logo Pathfinder sur l'écran d'accueil

---

## 📝 Checklist complète

- [ ] Logo préparé (1024x1024, PNG)
- [ ] Logo sauvegardé dans `mobile/resources/icon.png`
- [ ] Script `generate-icons.sh` exécuté
- [ ] 5 tailles d'icônes générées
- [ ] `npx cap sync` exécuté
- [ ] APK rebuilded
- [ ] Ancienne version désinstallée du téléphone
- [ ] Nouvelle APK installée
- [ ] Icône visible dans liste des applications ✅
