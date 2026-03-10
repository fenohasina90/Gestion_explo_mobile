# 📅 Gestion de l'Année d'Exercice

## ✅ Solution Implémentée

L'année d'exercice est maintenant **dynamique** et utilise automatiquement **l'année en cours** de l'appareil.

### 🔧 Deux niveaux de gestion :

## 1. Niveau Base de Données (SQL)

Le fichier `backend/src/main/resources/sql/BD_sqlite.sql` utilise la fonction SQLite dynamique :

```sql
INSERT INTO annee_exercice (annee, created_at) VALUES
(date('now', 'start of year'), CURRENT_TIMESTAMP);
```

**Résultat** : Lors de l'initialisation, l'année sera automatiquement :
- 2026-01-01 si on est en 2026
- 2027-01-01 si on est en 2027
- 2028-01-01 si on est en 2028
- etc.

## 2. Niveau Application (Spring Boot) ⭐ Recommandé

Un service d'initialisation automatique crée l'année d'exercice courante au démarrage de l'application.

### Fichiers créés :

1. **Entity** : `AnneeExercice.java`
   - Représente la table `annee_exercice`
   - Utilise `LocalDate` pour la date

2. **Repository** : `AnneeExerciceRepository.java`
   - Méthodes pour trouver l'année par date
   - Méthode pour trouver l'année la plus récente

3. **Service** : `AnneeExerciceInitService.java`
   - S'exécute automatiquement au démarrage avec `@EventListener(ApplicationReadyEvent.class)`
   - Vérifie si l'année courante existe
   - Crée l'année si elle n'existe pas

4. **Controller** : `AnneeExerciceController.java`
   - Endpoint : `GET /api/annee-exercice/courante`
   - Endpoint : `GET /api/annee-exercice/recente`

### 🚀 Fonctionnement Automatique

**Au démarrage de l'application :**

```java
✅ Année d'exercice 2026 déjà existante (ID: 1)
// ou
🆕 Nouvelle année d'exercice 2027 créée (ID: 2)
```

**Avantages :**
- ✅ Création automatique chaque nouvelle année
- ✅ Pas besoin de réinitialiser la base de données
- ✅ Historique des années conservé
- ✅ Fonctionne même si la base est ancienne

## 📊 Scénarios d'utilisation

### Scénario 1 : Première installation en 2026
```
Base créée → 2026-01-01 inséré
Application démarre → 2026 détecté ✅
```

### Scénario 2 : Installation en 2026, utilisée en 2027
```
Base existe avec 2026
Application démarre en 2027 → 2027 créé automatiquement 🆕
Les deux années coexistent dans la base
```

### Scénario 3 : Changement d'année pendant l'utilisation
```
31 décembre 2026 → Année courante = 2026
1er janvier 2027 → Redémarrer l'app → 2027 créée automatiquement
```

## 🧪 Tests

### Vérifier l'année en base de données :
```bash
sqlite3 backend/explorateurs.db "SELECT * FROM annee_exercice;"
```

### Tester l'API :
```bash
# Obtenir l'année courante
curl http://localhost:8080/api/annee-exercice/courante

# Obtenir l'année la plus récente
curl http://localhost:8080/api/annee-exercice/recente
```

### Exemple de réponse :
```json
{
  "id": 1,
  "annee": "2026-01-01",
  "createdAt": "2026-02-22T18:59:37"
}
```

## 🔄 Migration d'une année à l'autre

Quand une nouvelle année commence :

1. **Automatique** : Au prochain démarrage, la nouvelle année est créée
2. Les données historiques restent liées à leur année d'exercice
3. Les nouvelles inscriptions/activités utilisent la nouvelle année

## 💡 Utilisation dans le code

### Récupérer l'année courante :
```java
@Autowired
private AnneeExerciceInitService anneeExerciceService;

// Dans une méthode
AnneeExercice anneeCourante = anneeExerciceService.getOrCreateAnneeExerciceCourante();
```

### Lors de la création d'un utilisateur :
```java
// L'année courante sera automatiquement assignée
AnneeExercice annee = anneeExerciceService.getOrCreateAnneeExerciceCourante();
utilisateur.setAnneeExercice(annee);
```

## 🎯 Prochaines étapes

1. ✅ Année d'exercice dynamique implémentée
2. ⏳ Créer l'entité `Utilisateur` avec relation vers `AnneeExercice`
3. ⏳ Mettre à jour l'utilisateur par défaut pour utiliser l'année courante
4. ⏳ Ajouter un endpoint pour changer d'année d'exercice manuellement si besoin
5. ⏳ Dashboard pour voir toutes les années d'exercice

## 📝 Notes importantes

- L'année d'exercice commence toujours le **1er janvier**
- Chaque année d'exercice a un **ID unique**
- Les relations avec d'autres tables utilisent cet ID
- L'historique est préservé pour les années précédentes
- Pas besoin de supprimer les anciennes années
