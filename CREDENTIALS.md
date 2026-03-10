# 🔐 Informations de Connexion

## Utilisateur par Défaut

Lors de l'initialisation de la base de données, un utilisateur administrateur est automatiquement créé :

### Credentials
- **Username**: `directeur`
- **Password**: `directeur123`
- **Rôle**: Directeur (accès complet)
- **Permissions**: CREER, MODIFIER, SUPPRIMER, CONSULTER

### Année d'exercice par défaut
- **Année**: 2026-01-01
- **ID**: 1

## 🔒 Sécurité du Mot de Passe

Le mot de passe est stocké de manière sécurisée avec le hash BCrypt :
```
$2a$10$N9qo8uLOickgx2ZMRZoMye7I9E7eOLp85GxPj5Ik3rkHcSZWOPeGu
```

**⚠️ IMPORTANT**: Changez ce mot de passe immédiatement après la première connexion en production !

## 📊 Structure des Rôles

| ID | Rôle | Permissions |
|----|------|-------------|
| 1 | Directeur | CREER, MODIFIER, SUPPRIMER, CONSULTER |
| 2 | Co-Directeur | MODIFIER, CONSULTER |
| 3 | Secrétaire | CONSULTER |
| 4 | Instructeur | CONSULTER |

## 🚀 Test de Connexion

### Avec SQLite CLI
```bash
cd backend
sqlite3 explorateurs.db "SELECT id, username, role_id, active FROM utilisateur WHERE username='directeur';"
```

### Résultat attendu
```
1|directeur|1|1
```

## 📝 Création d'Utilisateurs Supplémentaires

Pour créer de nouveaux utilisateurs, vous devrez :

1. Générer un hash BCrypt du mot de passe (via Spring Security)
2. Insérer l'utilisateur dans la table `utilisateur`
3. Assigner un rôle approprié (role_id)
4. Lier à l'année d'exercice en cours (annee_exercice_id)

### Exemple SQL (avec hash déjà généré)
```sql
INSERT INTO utilisateur (username, password_hash, role_id, active, annee_exercice_id, created_at, updated_at) 
VALUES ('nouveau_user', '$2a$10$...hash_bcrypt...', 2, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

## 🔄 Réinitialisation de la Base de Données

Pour réinitialiser la base de données avec les données par défaut :

```bash
cd backend
./init-db.sh
```

**Note**: Cela créera une sauvegarde de l'ancienne base avant de la réinitialiser.

## 🔐 Prochaines Étapes de Sécurité

1. ✅ Hash BCrypt pour les mots de passe
2. ⏳ Ajouter Spring Security pour l'authentification
3. ⏳ Implémenter JWT pour les sessions
4. ⏳ Ajouter la fonctionnalité de changement de mot de passe
5. ⏳ Implémenter la récupération de mot de passe
6. ⏳ Ajouter l'audit trail dans la table `journal`
