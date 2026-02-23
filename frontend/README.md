# Frontend React - Gestion Explorateurs

Application web React avec TypeScript pour la gestion du Club des Explorateurs.

## 🚀 Technologies

- **React 19** - Framework UI
- **TypeScript** - Typage statique
- **Vite** - Build tool et dev server
- **React Router** - Navigation
- **Axios** - Client HTTP
- **LocalForage** - Stockage local (IndexedDB)

## 📦 Installation

```bash
# Installer les dépendances
npm install

# Démarrer le serveur de développement
npm run dev

# Build pour la production
npm run build

# Preview du build
npm run preview
```

## 🔧 Configuration

### Variables d'environnement

Créer un fichier `.env` à la racine du projet :

```env
VITE_API_URL=http://localhost:8080
VITE_API_TIMEOUT=30000
VITE_APP_NAME=Gestion Explorateurs
VITE_APP_VERSION=1.0.0
```

## 🏗️ Architecture

```
frontend/
├── src/
│   ├── config/
│   │   └── api.config.ts          # Configuration API et constantes
│   ├── services/
│   │   ├── api.service.ts         # Client HTTP avec intercepteurs JWT
│   │   └── offline-storage.service.ts  # Stockage local IndexedDB
│   ├── contexts/
│   │   └── AuthContext.tsx        # Context React pour l'authentification
│   ├── components/
│   │   ├── Layout.tsx             # Layout principal avec navigation
│   │   └── PrivateRoute.tsx       # Route protégée
│   ├── pages/
│   │   ├── LoginPage.tsx          # Page de connexion
│   │   ├── DashboardPage.tsx      # Tableau de bord
│   │   ├── EnfantsPage.tsx        # Gestion des enfants
│   │   ├── ActivitesPage.tsx      # Gestion des activités
│   │   └── PlaceholderPages.tsx   # Pages en développement
│   ├── types/
│   │   └── index.ts               # Types TypeScript
│   ├── App.tsx                    # Composant principal avec routing
│   └── main.tsx                   # Point d'entrée
├── .env                           # Variables d'environnement
└── package.json
```

## 🔐 Authentification

L'application utilise JWT (JSON Web Tokens) pour l'authentification.

### Flow d'authentification

1. L'utilisateur se connecte via `/login` avec username/password
2. Le backend retourne un token JWT
3. Le token est sauvegardé dans IndexedDB via LocalForage
4. Toutes les requêtes API incluent le token dans l'en-tête `Authorization: Bearer {token}`
5. Si le token expire (401), l'utilisateur est redirigé vers `/login`

### Utilisation dans les composants

```tsx
import { useAuth } from './contexts/AuthContext';

function MyComponent() {
  const { user, isAuthenticated, login, logout } = useAuth();
  
  // Accès aux infos utilisateur
  console.log(user?.username, user?.role);
  
  // Connexion
  await login({ username: 'directeur', password: 'directeur123' });
  
  // Déconnexion
  await logout();
}
```

## 🛣️ Routes

| Route | Description | Protégée |
|-------|-------------|----------|
| `/login` | Page de connexion | Non |
| `/dashboard` | Tableau de bord | Oui |
| `/enfants` | Gestion des enfants | Oui |
| `/activites` | Gestion des activités | Oui |
| `/inscriptions` | Gestion des inscriptions | Oui |
| `/staff` | Gestion du staff | Oui |
| `/budget` | Gestion du budget | Oui |
| `/rapports` | Rapports et statistiques | Oui |

## 🔌 API Service

Le service API utilise Axios avec des intercepteurs pour :

- Ajouter automatiquement le token JWT à chaque requête
- Gérer l'expiration du token (redirection vers login)
- Centraliser la configuration (baseURL, timeout, headers)

```tsx
import apiService from './services/api.service';

// GET
const user = await apiService.get('/api/utilisateur/me');

// POST
const response = await apiService.post('/api/auth/login', { username, password });

// PUT
await apiService.put('/api/enfants/1', { nom: 'Dupont' });

// DELETE
await apiService.delete('/api/enfants/1');
```

## 💾 Stockage Local

Utilise LocalForage (IndexedDB en priorité, fallback sur LocalStorage) pour :

- Persistance du token JWT
- Persistance des données utilisateur
- Cache des données pour usage hors ligne

```tsx
import offlineStorage from './services/offline-storage.service';

// Sauvegarder
await offlineStorage.set('key', value);

// Récupérer
const value = await offlineStorage.get('key');

// Supprimer
await offlineStorage.remove('key');

// Tout nettoyer
await offlineStorage.clear();
```

## 🎨 Styling

- CSS modules par composant/page
- Design system cohérent avec couleurs et espacements standardisés
- Responsive design (mobile-first)
- Gradient violet pour l'identité visuelle

## 🧪 Credentials de test

```
Username: directeur
Password: directeur123
```

## 📱 Développement

```bash
# Dev server avec hot reload
npm run dev
# → http://localhost:5173

# Lint
npm run lint

# Type check
npm run type-check

# Build
npm run build
```

## 🚀 Déploiement

```bash
# Build pour la production
npm run build

# Les fichiers sont générés dans dist/
# Déployer le contenu de dist/ sur votre hébergement web
```

## 🔗 Backend

L'application se connecte au backend Spring Boot sur `http://localhost:8080`.

Documentation API : http://localhost:8080/swagger-ui/index.html

## 📝 TODO

- [ ] Implémenter les formulaires CRUD pour toutes les entités
- [ ] Ajouter la gestion des fichiers/uploads
- [ ] Implémenter la synchronisation hors ligne
- [ ] Ajouter la validation des formulaires
- [ ] Implémenter les rapports et exports
- [ ] Ajouter les tests unitaires (Jest/Vitest)
- [ ] Optimiser les performances (code splitting, lazy loading)
- [ ] Ajouter l'internationalisation (i18n)
```
