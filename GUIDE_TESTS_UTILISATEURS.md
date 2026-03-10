# Guide de Test - Gestion des Utilisateurs dans Swagger UI

Ce guide vous permettra de tester toutes les fonctionnalités de gestion des utilisateurs implémentées.

## 📋 Prérequis

- Backend Spring Boot démarré sur http://localhost:8080
- Accès à Swagger UI : http://localhost:8080/swagger-ui/index.html
- Compte Directeur actif : `directeur` / `directeur123`

## 🔐 Étape 1: Authentification

### 1.1 Se connecter en tant que Directeur

1. Dans Swagger UI, trouvez `Auth Controller`
2. Cliquez sur `POST /api/auth/login`
3. Cliquez sur "Try it out"
4. Entrez les credentials :
```json
{
  "username": "directeur",
  "password": "directeur123"
}
```
5. Cliquez sur "Execute"
6. **Copiez le token JWT** retourné dans la réponse

### 1.2 Autoriser les requêtes avec le token

1. En haut de la page Swagger, cliquez sur le bouton **"Authorize"** (cadenas)
2. Entrez : `Bearer <votre_token_copié>`
3. Cliquez sur "Authorize" puis "Close"

## 👥 Étape 2: Récupérer les Rôles Disponibles

### 2.1 Liste des rôles

1. Trouvez `Roles Controller`
2. Cliquez sur `GET /api/roles`
3. Cliquez sur "Try it out"  
4. Cliquez sur "Execute"

**Résultat attendu** : Liste des 4 rôles avec leurs IDs
```json
[
  {"id": 1, "roleName": "Directeur"},
  {"id": 2, "roleName": "Co-Directeur"},
  {"id": 3, "roleName": "Secrétaire"},
  {"id": 4, "roleName": "Instructeur"}
]
```

**Note les IDs** : Vous en aurez besoin pour créer des utilisateurs.

## 📅 Étape 3: Récupérer les Années d'Exercice

### 3.1 Liste des années d'exercice

1. Trouvez `Annee Exercice Controller`
2. Cliquez sur `GET /api/annee-exercice/all`
3. Cliquez sur "Try it out"
4. Cliquez sur "Execute"

**Résultat attendu** : Liste des années avec leurs IDs
```json
[
  {"id": 1, "annee": "2026-01-01", "createdAt": "..."}
]
```

**Note l'ID** : Vous en aurez besoin pour créer des utilisateurs.

## ✅ Étape 4: Tests de Création d'Utilisateurs

### 4.1 Créer un Co-Directeur (Succès attendu)

1. Trouvez `Utilisateur Controller`
2. Cliquez sur `POST /api/utilisateur`
3. Cliquez sur "Try it out"
4. Entrez :
```json
{
  "username": "codirecteur2026",
  "password": "password123",
  "roleId": 2,
  "anneeExerciceId": 1
}
```
5. Cliquez sur "Execute"

**Résultat attendu** : HTTP 201, utilisateur créé avec :
- `active`: true
- `role`: "Co-Directeur"
- `anneeExercice`: "2026-01-01"

### 4.2 Créer un Secrétaire (Succès attendu)

```json
{
  "username": "secretaire2026",
  "password": "password123",
  "roleId": 3,
  "anneeExerciceId": 1
}
```

**Résultat attendu** : HTTP 201, utilisateur créé avec role "Secrétaire"

### 4.3 Créer un Instructeur (Succès attendu)

```json
{
  "username": "instructeur2026",
  "password": "password123",
  "roleId": 4,
  "anneeExerciceId": 1
}
```

**Résultat attendu** : HTTP 201, utilisateur créé avec role "Instructeur"

### 4.4 Tenter de créer un doublon (Erreur attendue)

Réessayez de créer le même username :
```json
{
  "username": "secretaire2026",
  "password": "autrepassword",
  "roleId": 3,
  "anneeExerciceId": 1
}
```

**Résultat attendu** : HTTP 500, message "Ce nom d'utilisateur existe déjà"

### 4.5 Créer un nouveau Directeur (Désactivation automatique)

```json
{
  "username": "directeur2027",
  "password": "password123",
  "roleId": 1,
  "anneeExerciceId": 1
}
```

**Résultat attendu** : 
- HTTP 201, nouveau directeur créé avec `active`: true
- L'ancien directeur (`directeur`) devrait maintenant être `active`: false

**Vérifier** : 
1. Allez sur `GET /api/utilisateur`
2. Cherchez l'utilisateur "directeur" → `active` devrait être `false`
3. Cherchez l'utilisateur "directeur2027" → `active` devrait être `true`

## 🔄 Étape 5: Tests de Modification d'Utilisateurs

### 5.1 Se reconnecter avec le nouveau Directeur

Puisque l'ancien directeur est maintenant inactif, vous devez vous reconnecter :

1. Allez sur `POST /api/auth/login`
2. Connectez-vous avec :
```json
{
  "username": "directeur2027",
  "password": "password123"
}
```
3. **Copiez le nouveau token**
4. Cliquez sur "Authorize" et entrez le nouveau token

### 5.2 Modifier un utilisateur (ID à adapter)

1. Cliquez sur `PUT /api/utilisateur/{id}`
2. Entrez l'ID du secrétaire (généralement 2 ou 3)
3. Cliquez sur "Try it out"
4. Entrez :
```json
{
  "password": "nouveauPassword123"
}
```

**Résultat attendu** : HTTP 200, mot de passe modifié

### 5.3 Désactiver un utilisateur

```json
{
  "active": false
}
```

**Résultat attendu** : HTTP 200, utilisateur désactivé

## 🗑️ Étape 6: Tests de Suppression

### 6.1 Supprimer un utilisateur

1. Cliquez sur `DELETE /api/utilisateur/{id}`
2. Entrez l'ID d'un utilisateur (par exemple l'instructeur)
3. Cliquez sur "Execute"

**Résultat attendu** : HTTP 204 No Content, utilisateur supprimé

### 6.2 Tenter de se supprimer soi-même (Erreur attendue)

1. Trouvez votre propre ID en allant sur `GET /api/utilisateur/me`
2. Essayez de vous supprimer avec `DELETE /api/utilisateur/{votre_id}`

**Résultat attendu** : HTTP 500, message "Vous ne pouvez pas supprimer votre propre compte"

## 📊 Étape 7: Tests de Consultation

### 7.1 Liste de tous les utilisateurs

`GET /api/utilisateur` → Retourne tous les utilisateurs créés

### 7.2 Utilisateur par ID

`GET /api/utilisateur/{id}` → Retourne un utilisateur spécifique

### 7.3 Utilisateurs actifs uniquement

`GET /api/utilisateur/actifs` → Retourne uniquement les utilisateurs avec `active = true`

### 7.4 Utilisateurs par année d'exercice

`GET /api/utilisateur/annee/{anneeExerciceId}` → Retourne les utilisateurs d'une année spécifique

## 🔒 Étape 8: Tests des Restrictions de Sécurité

### 8.1 Se connecter en tant que Secrétaire

1. Réactivez le secrétaire si nécessaire (avec le compte Directeur)
2. Connectez-vous avec :
```json
{
  "username": "secretaire2026",
  "password": "password123"
}
```
3. Autorisez avec le nouveau token

### 8.2 Tenter de créer un utilisateur (Erreur attendue)

Essayez `POST /api/utilisateur`

**Résultat attendu** : HTTP 403 Forbidden, car seul le Directeur peut créer des utilisateurs

### 8.3 Tenter de modifier un utilisateur (Erreur attendue)

Essayez `PUT /api/utilisateur/{id}`

**Résultat attendu** : HTTP 403 Forbidden, car seul le Directeur et Co-Directeur peuvent modifier

### 8.4 Tenter de supprimer un utilisateur (Erreur attendue)

Essayez `DELETE /api/utilisateur/{id}`

**Résultat attendu** : HTTP 403 Forbidden, car seul le Directeur peut supprimer

## ✅ Validation Finale

### Règles métier implémentées et testées :

- ✅ Seul un Directeur peut créer un utilisateur
- ✅ Seuls les utilisateurs actifs peuvent se connecter
- ✅ Un nouveau Directeur désactive automatiquement l'ancien
- ✅ Les actions de création/modification/suppression sont restreintes par rôle
- ✅ Un utilisateur ne peut pas se supprimer lui-même
- ✅ Les doublons de username sont interdits
- ✅ Tous les utilisateurs sont liés à une année d'exercice

## 🔍 Cas de Tests Supplémentaires

### Test 1: Vérifier que l'ancien directeur ne peut plus se connecter

Essayez de vous connecter avec `directeur` / `directeur123`

**Résultat attendu** : HTTP 500, message "Utilisateur non trouvé ou inactif"

### Test 2: Se connecter en tant que Co-Directeur et modifier un utilisateur

1. Connectez-vous avec le Co-Directeur
2. Modifiez un utilisateur avec `PUT /api/utilisateur/{id}`

**Résultat attendu** : HTTP 200, succès (le Co-Directeur a le droit de modifier)

### Test 3: Co-Directeur tente de supprimer (Erreur attendue)

**Résultat attendu** : HTTP 403 Forbidden (seul le Directeur peut supprimer)

## 📝 Notes Importantes

1. **Tokens JWT** : Expirent après 24 heures. Reconnectez-vous si nécessaire.
2. **IDs** : Les IDs des utilisateurs, rôles et années d'exercice peuvent varier selon votre base de données.
3. **Active** : Seuls les utilisateurs avec `active=true` peuvent se connecter.
4. **Rôles** : 
   - Directeur (ID: 1) → CRUD complet
   - Co-Directeur (ID: 2) → Consultation et modification
   - Secrétaire (ID: 3) → Consultation uniquement
   - Instructeur (ID: 4) → Consultation uniquement

## 🐛 Dépannage

### Erreur 401 Unauthorized
→ Token JWT expiré ou invalide, reconnectez-vous

### Erreur 403 Forbidden
→ Votre rôle n'a pas les droits pour cette action

### Erreur 500 avec message métier
→ Règle métier non respectée (doublon, auto-suppression, etc.)

### Erreur "Utilisateur non trouvé ou inactif"
→ L'utilisateur a été désactivé (par exemple l'ancien directeur)
