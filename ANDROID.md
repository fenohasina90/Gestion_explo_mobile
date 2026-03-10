# Guide rapide – Tester une application mobile Ionic + Vue + Capacitor (Android)

Ce guide résume les commandes nécessaires pour **builder, synchroniser et tester** une application mobile Ionic/Vue avec **Capacitor** sur Android.

---

# 1. Installer les dépendances Capacitor

Installe les outils nécessaires pour transformer l'application web en application mobile.

```bash
npm install @capacitor/core@6 @capacitor/cli@6 @capacitor/android@6
```

**Utilité** :
- `@capacitor/core` : cœur de Capacitor
- `@capacitor/cli` : commandes pour gérer le projet mobile
- `@capacitor/android` : plateforme Android


# 2. Initialiser Capacitor
Créer la configuration Capacitor dans le projet.
```bash
npx cap init
```
Exemple :
```Plain text
App name: Gestion Explorateur
App id: com.explorateur.app
```
**Utilité** :
- Crée le fichier `capacitor.config.ts`
- Configure le nom et l'identifiant de l'application


# 3. Construire l'application web
Compiler l'application Vue/Ionic pour produire les fichiers statiques.
```bash
npm run build
```
**Utilité** :
- Génère le dossier `dist/`
- Contient les fichiers web utilisés dans l'application mobile

Structure générée :
```Plain text
dist/
 ├── index.html
 ├── assets/
```

# 4. Ajouter la plateforme Android
Créer le projet Android natif.
```bash
npx cap add android
```
**Utilité**:
- Crée le dossier `android/`
- Initialise le projet Android compatible avec Android Studio


# 5. Synchroniser le projet
Copier les fichiers web et les plugins dans le projet Android.
```bash
npx cap sync
```
**Utilité** :
- Copie le contenu du dossier `dist` vers Android
- Installe les plugins Capacitor
- Met à jour la configuration Android


# 6. Configurer le chemin d'Android Studio (Linux / Snap)
Si Capacitor ne trouve pas Android Studio, définir la variable d’environnement :
```bash
export CAPACITOR_ANDROID_STUDIO_PATH=/snap/android-studio/209/bin/studio.sh
```
Pour rendre permanent, ajouter à la fin de `~/.bashrc` :
```bash
echo 'export CAPACITOR_ANDROID_STUDIO_PATH=/snap/android-studio/209/bin/studio.sh' >> ~/.bashrc
source ~/.bashrc
```
**Utilité** :
- Capacitor peut ouvrir Android Studio automatiquement

# 7. Ouvrir le projet dans Android Studio
```bash
npx cap open android
```
**Utilité** :
- Ouvre le dossier android/ dans Android Studio
- Permet de lancer l'application sur un téléphone ou un émulateur

# 8. Lancer l'application dans Android Studio
Dans Android Studio :
```Plain text
Run ▶
```
**Utilité** :
- Compile l'application
- Installe l'application sur un téléphone ou un émulateur

# 9. Mettre à jour l'application après modification
Après modification du code Vue/Ionic :
```bash
npm run build
npx cap sync
```
Puis relancer dans Android Studio.

**Utilité** :
- Reconstruire l'application
- Mettre à jour les fichiers dans Android
  
# 10. Générer un APK (optionnel)
Dans Android Studio :
```Plain text
Build → Build APK
```
APK généré dans :
```Plain text
android/app/build/outputs/apk/debug/app-debug.apk
```
**Utilité** :
- Installer l'application sur d'autres téléphones Android

# 11. Structure finale du projet
```Plain text
mobile/
│
├── src/                # Code source Vue/Ionic
├── dist/               # Build web
├── android/            # Projet Android natif
│
├── capacitor.config.ts
├── package.json
└── node_modules/
```

# 12. Résumé rapide des commandes

| Commande                                                              | Utilité                       |
| --------------------------------------------------------------------- | ----------------------------- |
| `npm install @capacitor/core@6 @capacitor/cli@6 @capacitor/android@6` | Installer Capacitor           |
| `npx cap init`                                                        | Initialiser Capacitor         |
| `npm run build`                                                       | Construire l'application web  |
| `npx cap add android`                                                 | Ajouter la plateforme Android |
| `npx cap sync`                                                        | Synchroniser le projet        |
| `npx cap open android`                                                | Ouvrir Android Studio         |
| `Run ▶`                                                               | Lancer l'application          |
