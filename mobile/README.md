# Application Mobile - Club des Explorateurs

Application mobile Ionic Vue pour la gestion du Club des Explorateurs.

## 🚀 Démarrage rapide

### Installation des dépendances

```bash
npm install
```

### Lancement en mode développement

```bash
npm run dev
```

L'application sera accessible sur http://localhost:5173

### Build pour la production

```bash
npm run build
```

### Ajout des plateformes natives

#### Android
```bash
npx cap add android
npx cap sync android
npx cap open android
```

#### iOS
```bash
npx cap add ios
npx cap sync ios
npx cap open ios
```

## 📱 Fonctionnalités

### ✅ Actuellement implémentées

- **Authentification JWT** : Connexion sécurisée avec token
- **Stockage offline** : Données sauvegardées localement avec IndexedDB
- **Synchronisation** : Sync automatique et manuelle des données
- **Détection réseau** : Sync automatique lors de la reconnexion
- **Interface utilisateur** : 
  - Page de connexion
  - Tableau de bord avec menu
  - Onglets de navigation
  - Status de synchronisation

### 🔄 Mode Offline

L'application fonctionne entièrement hors ligne :
- Les données sont stockées localement avec LocalForage
- Les modifications sont mises en attente de synchronisation
- Synchronisation automatique dès la reconnexion réseau

### 🔐 Authentification

- Connexion avec username/password
- Token JWT stocké localement
- Déconnexion automatique si token invalide
- Identifiants par défaut : `directeur` / `directeur123`

## 🛠️ Architecture technique

### Stack technologique

- **Ionic 8** : Framework UI mobile
- **Vue 3** : Framework JavaScript
- **TypeScript** : Typage statique
- **Vite** : Build tool
- **Pinia** : State management
- **Axios** : Client HTTP
- **LocalForage** : Stockage offline (IndexedDB)
- **Capacitor** : Accès aux APIs natives

### Structure des dossiers

```
src/
├── config/           # Configuration (API, storage, sync)
├── services/         # Services (API, offline, sync)
├── stores/           # Stores Pinia (auth, sync)
├── views/            # Pages de l'application
├── router/           # Configuration des routes
├── theme/            # Thème et styles
├── App.vue           # Composant racine
└── main.ts           # Point d'entrée
```

### Services principaux

- **api.service.ts** : Gestion des requêtes HTTP avec intercepteurs
- **offline-storage.service.ts** : Stockage local avec LocalForage
- **sync.service.ts** : Synchronisation avec le backend

### Stores Pinia

- **auth.store.ts** : Gestion de l'authentification
- **sync.store.ts** : Gestion de la synchronisation

## 📡 Synchronisation en réseau local

La synchronisation fonctionne en réseau local via HTTP :

1. **Détection de connexion** : Utilise Capacitor Network API
2. **Sync automatique** : Toutes les 5 minutes si connecté
3. **Sync manuelle** : Bouton dans l'interface
4. **Données pendantes** : Stockées localement et envoyées lors de la reconnexion

### Configuration réseau local

Pour connecter à un serveur sur le même réseau :

1. Trouvez l'IP du serveur : 
   ```bash
   ip addr show
   ```

2. Modifiez `.env` :
   ```
   VITE_API_URL=http://192.168.1.X:8080
   ```

3. Sur le serveur backend, configurez CORS pour accepter l'IP mobile

## 🔧 Configuration

### Variables d'environnement

Fichier `.env` pour le développement :
```env
VITE_API_URL=http://localhost:8080
VITE_MODE=development
```

Fichier `.env.production` pour la production :
```env
VITE_API_URL=http://192.168.1.100:8080
VITE_MODE=production
```

### Capacitor

Le fichier `capacitor.config.json` configure l'application native :
- App ID : `com.explorateurs.mobile`
- App Name : `Club Explorateurs`
- Splash screen configuré

## 📝 Routes disponibles

- `/login` : Page de connexion
- `/tabs/home` : Page d'accueil (protégée)
- `/tabs/enfants` : Gestion des enfants (à implémenter)
- `/tabs/activites` : Gestion des activités (à implémenter)
- `/tabs/profil` : Profil utilisateur (à implémenter)

## 🧪 Tests

```bash
# Tests unitaires
npm run test:unit

# Tests E2E
npm run test:e2e
```

## 📦 Build et déploiement

### Build web
```bash
npm run build
npm run preview
```

### Build Android
```bash
npm run build
npx cap sync android
npx cap open android
# Puis build depuis Android Studio
```

### Build iOS
```bash
npm run build
npx cap sync ios
npx cap open ios
# Puis build depuis Xcode
```

## 🐛 Débogage

### Inspection de l'app mobile

#### Android (Chrome DevTools)
1. Connectez votre appareil en USB
2. Ouvrez Chrome : `chrome://inspect`
3. Sélectionnez votre appareil

#### iOS (Safari)
1. Connectez votre iPhone
2. Safari > Développement > [Nom appareil]
3. Sélectionnez l'application

### Logs Capacitor
```bash
# Android
npx cap run android --livereload

# iOS
npx cap run ios --livereload
```

## 📚 Documentation

- [Ionic Vue Documentation](https://ionicframework.com/docs/vue/overview)
- [Capacitor Documentation](https://capacitorjs.com/docs)
- [Pinia Documentation](https://pinia.vuejs.org/)
- [LocalForage Documentation](https://localforage.github.io/localForage/)

## 👥 Équipe

Développé pour le Club des Explorateurs - Église Adventiste

## 📄 Licence

Propriétaire - Tous droits réservés



salama daholo,
soson-kevitra ity:
nijery an'ilay slide zah teo dia ao anatiny ao misy an'io cotisation io.
maninona raha esorina tao io satria isika hanentana olona hiditra ato amin'ilay club nefa efa misy resaka vola hivoaka amzareo sahady dia mety hanakana anazy tsy hiditra indray ilay izy fa rehefa tafiditra ao anatiny ilay olona dia izay vao resahana io sy izay mety ho fitsipika hafa rehetra.
Rehefa nijery ny an'olona nanao presentation teny tsinona otran tsy nisy niresaka anzan.
Soson-kevitra ftsn ny ah iny fa tsy haiko izay efa tapakareo tao amin'ny fivoriana, ialana tsiny moa fa zah tsy nivory.
Mankasitraka