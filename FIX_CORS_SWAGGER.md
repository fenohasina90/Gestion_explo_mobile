# 🔧 Fix CORS pour Swagger UI et Mobile

## ❌ Problème identifié

Swagger UI sur https://explorateurs-backend.onrender.com retourne erreur CORS:
```
Failed to fetch.
Possible Reasons: CORS
```

**Cause**: La configuration CORS du backend n'autorisait que `localhost`, pas l'URL Render.com

## ✅ Solution appliquée

### 1. Modification de SecurityConfig.java

**Avant** (rejetait les requêtes):
```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:5173",
    "http://localhost:5174",
    // ... seulement localhost
));
configuration.setAllowCredentials(true);
```

**Après** (accepte toutes les origines):
```java
configuration.addAllowedOriginPattern("*");  // ✅ Toutes origines
configuration.setAllowCredentials(false);    // ✅ Obligatoire avec "*"
```

### 2. Suppression de CorsConfig.java

Fichier redondant supprimé (SecurityConfig gère déjà CORS).

---

## 🚀 Redéployer sur Render

### Option 1: Auto-déploiement (si connecté à Git)

Si votre projet est connecté à GitHub/GitLab:

```bash
cd /home/mangalahy/PERSO/AUTRE/Explo/Gestion_explo_mobile

# Commit les changements
git add backend/src/main/java/com/explorateur/backend/security/SecurityConfig.java
git add backend/src/main/java/com/explorateur/backend/config/CorsConfig.java
git commit -m "Fix CORS: autoriser toutes origines pour API mobile"
git push
```

Render détectera le push et redéploiera automatiquement.

### Option 2: Déploiement manuel

Si pas de Git auto-deploy, reconstruire et pusher le Docker:

```bash
# Rebuild local pour tester
cd backend
./mvnw clean package -DskipTests

# Si OK, Render.com:
# → Dashboard → explorateurs-backend → Manual Deploy → Deploy latest commit
```

### Option 3: Forcer un redéploiement

Sur Render.com Dashboard:
1. Sélectionner **explorateurs-backend**
2. Cliquer **Manual Deploy**
3. Sélectionner **Clear build cache & deploy**

⏳ Attendez 5-10 minutes pour le build Docker

---

## 🧪 Tester après redéploiement

### Test 1: Swagger UI

```
https://explorateurs-backend.onrender.com/swagger-ui.html
```

1. Ouvrir la page
2. Section **auth-controller**
3. Cliquer **POST /api/auth/login**
4. **Try it out**
5. Body:
   ```json
   {
     "username": "directeur",
     "password": "directeur123"
   }
   ```
6. **Execute**

✅ **Résultat attendu**: HTTP 200 avec token JWT

### Test 2: Curl depuis terminal

```bash
curl -X POST https://explorateurs-backend.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"directeur","password":"directeur123"}'
```

✅ **Résultat attendu**: 
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "directeur",
  "role": "DIRECTEUR"
}
```

### Test 3: APK Mobile

Sur téléphone:
1. **Désinstaller** l'ancienne app
2. **Installer** la nouvelle APK (générée après fix CORS)
3. Login: `directeur` / `directeur123`

✅ **Résultat attendu**: Login réussit

---

## 📋 Changements techniques

### Fichiers modifiés:
- ✅ `backend/src/main/java/com/explorateur/backend/security/SecurityConfig.java`

### Fichiers supprimés:
- ✅ `backend/src/main/java/com/explorateur/backend/config/CorsConfig.java`

### Configuration CORS finale:
```java
// Autorise TOUTES les origines
configuration.addAllowedOriginPattern("*");

// Méthodes HTTP autorisées
configuration.setAllowedMethods(Arrays.asList(
    "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
));

// Headers autorisés
configuration.setAllowedHeaders(Arrays.asList("*"));

// Credentials désactivés (obligatoire avec "*")
configuration.setAllowCredentials(false);
```

---

## ℹ️ Pourquoi cette configuration?

### allowedOriginPattern("*") au lieu de allowedOrigins()
- `setAllowedOrigins("*")` est **déprécié** depuis Spring Security 5.7+
- `addAllowedOriginPattern("*")` est la **nouvelle méthode recommandée**
- Permet patterns plus flexibles

### allowCredentials(false)
- Requis avec `allowedOriginPattern("*")`
- Sécurité: ne peut pas avoir credentials + wildcard origin
- OK pour API mobile (pas de cookies/credentials nécessaires)

### Pourquoi supprimer CorsConfig.java?
- Redondance: SecurityConfig gère déjà CORS
- Confusion: deux configurations CORS peuvent conflictuer
- Simplicité: une seule configuration centralisée

---

## 🔒 Sécurité

Cette configuration autorise **toutes les origines** à appeler l'API.

**C'est OK** car:
- ✅ API mobile **publique** (pas de données sensibles dans les endpoints publics)
- ✅ Authentification **JWT** sécurise les endpoints protégés
- ✅ Pas de credentials/cookies (pas de risque CSRF)

**Endpoints publics** (pas besoin token):
- `/api/auth/login` - Login
- `/api/auth/register` - Inscription
- `/swagger-ui/**` - Documentation

**Endpoints protégés** (token JWT requis):
- `/api/explorateurs/**`
- `/api/parcelles/**`
- `/api/visites/**`
- Etc.

---

## ✅ Checklist de déploiement

- [ ] Changements committés (si Git)
- [ ] Redéploiement Render déclenché
- [ ] Build réussi (5-10 min)
- [ ] Service redémarré
- [ ] Test Swagger UI → Login OK
- [ ] Test curl → Token retourné
- [ ] APK mobile → Login fonctionne

---

Date du fix: 10 mars 2026  
Status: ✅ Prêt à redéployer
