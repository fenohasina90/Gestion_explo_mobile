# ⚡ Guide Rapide : Migration PostgreSQL

## 🎯 Ce qui a été fait

✅ **pom.xml** modifié : SQLite → PostgreSQL  
✅ **application.properties** modifié : Configuration PostgreSQL  
✅ **BD.sql** corrigé : Compatible PostgreSQL  
✅ **.gitignore** mis à jour : .env protégé  
✅ **Scripts** créés : test-postgres-migration.sh  

---

## 📝 Étapes à suivre (dans l'ordre)

### 1️⃣ Créer la base de données Supabase (15 min)

Allez sur : **https://supabase.com/**

1. Créez un compte (gratuit)
2. Créez un nouveau projet :
   - Nom : `explorateurs-db`
   - Mot de passe : **Sauvegardez-le !**
   - Région : Choisissez la plus proche
3. Attendez 2-3 minutes (création automatique)

### 2️⃣ Récupérer l'URL de connexion

Dans Supabase :
1. **Settings** → **Database**
2. Descendez à **"Connection string"**
3. Sélectionnez **"URI"**
4. Copiez l'URL complète
5. Remplacez `[YOUR-PASSWORD]` par votre mot de passe

**Exemple :**
```
postgresql://postgres.abcdefghijk:MonMotDePasse123@aws-0-eu-central-1.pooler.supabase.com:6543/postgres
```

### 3️⃣ Exécuter le schéma SQL

Dans Supabase :
1. **SQL Editor** → **New query**
2. Copiez tout le contenu de `backend/src/main/resources/sql/BD_postgres.sql`
3. Collez et cliquez **"Run"**
4. Vérifiez qu'il n'y a pas d'erreurs

### 4️⃣ Vérifier les données initiales

### 4️⃣ Vérifier les données initiales

Le script `BD_postgres.sql` a déjà créé :
- ✅ L'année d'exercice 2026
- ✅ L'utilisateur `directeur` / `directeur123`
- ✅ Les 6 classes (Ami, Compagnon, Eclaireur, Pionnier, Voyageur, Guide)
- ✅ Les 48 programmes
- ✅ Les rôles et statuts

Vérifiez dans Supabase **Table Editor** :
```sql
SELECT * FROM utilisateur;
SELECT * FROM annee_exercice;
SELECT COUNT(*) FROM programmes;  -- Devrait afficher 48
```

### 5️⃣ Configurer le backend local

```bash
cd backend
cp .env.example .env
nano .env  # Ou utilisez votre éditeur préféré
```

**Remplissez avec vos valeurs :**
```bash
DATABASE_URL=postgresql://postgres.xxx:VotreMotDePasse@xxx.supabase.com:6543/postgres
JWT_SECRET=VotreCleSecrete123456789
```

**Sauvegardez et fermez**

### 6️⃣ Tester localement

```bash
cd /home/mangalahy/PERSO/AUTRE/Explo/Gestion_explo_mobile
./test-postgres-migration.sh
```

Le backend va démarrer sur `http://localhost:8080`

**Testez avec :**
```bash
# Dans un autre terminal
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"directeur","password":"directeur123"}'
```

✅ Si vous recevez un token JWT → **Migration réussie !**

### 7️⃣ Déployer sur Render

1. Allez sur : **https://dashboard.render.com/**
2. Sélectionnez **explorateurs-backend**
3. **Environment** → **Add Environment Variable**
4. Ajoutez :
   - **Key** : `DATABASE_URL`
   - **Value** : Votre URL Supabase complète
5. **Save Changes**
6. Render va redéployer automatiquement (3-5 min)

### 8️⃣ Vérifier le déploiement

```bash
curl -X POST https://explorateurs-backend.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"directeur","password":"directeur123"}'
```

✅ Si login OK → **Déploiement réussi !**

---

## 🎉 L'app mobile n'a RIEN à changer !

L'URL du backend reste la même :
```
https://explorateurs-backend.onrender.com
```

Testez simplement la connexion dans l'app.

---

## 📚 Documentation complète

Consultez **SUPABASE_MIGRATION.md** pour :
- Guide détaillé pas à pas
- Résolution de problèmes
- Configuration avancée
- Optimisations

---

## 🆘 Aide rapide

**Erreur "password authentication failed"**  
→ Vérifiez le mot de passe dans DATABASE_URL

**Erreur "relation does not exist"**  
→ Réexécutez BD.sql dans Supabase

**Backend ne démarre pas**  
→ Vérifiez les logs : `mvn spring-boot:run`

**Timeout de connexion**  
→ Vérifiez que l'URL contient `.pooler.supabase.com:6543`

---

Date : 11 mars 2026
