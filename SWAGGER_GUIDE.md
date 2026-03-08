# 📚 Swagger UI - Documentation API

## ✅ Configuration Réussie !

Swagger UI a été configuré avec succès dans le backend Spring Boot.

## 🌐 Accès à Swagger UI

### Interface Swagger UI
Ouvrez votre navigateur web et accédez à :

```
http://localhost:8080/swagger-ui.html
```

Vous serez automatiquement redirigé vers :
```
http://localhost:8080/swagger-ui/index.html
```

### Documentation JSON (OpenAPI)
L'API docs au format JSON est disponible à :
```
http://localhost:8080/api-docs
```

## 📋 Fonctionnalités Swagger UI

### Interface Interactive
L'interface Swagger UI vous permet de :

✅ **Visualiser** toutes les API disponibles  
✅ **Tester** les endpoints directement depuis le navigateur  
✅ **Voir** les modèles de données (schemas)  
✅ **Explorer** les paramètres requis et optionnels  
✅ **Consulter** les codes de réponse HTTP  
✅ **Essayer** les requêtes avec "Try it out"  

### Organisation
- **Tags** : Les endpoints sont regroupés par fonctionnalité
- **Méthodes HTTP** : GET, POST, PUT, DELETE clairement identifiées
- **Codes couleur** : 
  - 🟢 GET (lecture)
  - 🟡 POST (création)
  - 🔵 PUT (modification)
  - 🔴 DELETE (suppression)

## 🧪 Test des APIs

### Exemple : Tester l'endpoint "Année d'Exercice"

#### Via Swagger UI (Interface)
1. Ouvrir http://localhost:8080/swagger-ui.html
2. Cliquer sur "Année d'Exercice" pour déplier la section
3. Cliquer sur l'endpoint `/api/annee-exercice/courante`
4. Cliquer sur "Try it out"
5. Cliquer sur "Execute"
6. Voir la réponse JSON

#### Via curl (Ligne de commande)
```bash
# Obtenir l'année d'exercice courante
curl http://localhost:8080/api/annee-exercice/courante

# Obtenir l'année d'exercice la plus récente
curl http://localhost:8080/api/annee-exercice/recente

# Avec formatage JSON (jq requis)
curl -s http://localhost:8080/api/annee-exercice/courante | jq .
```

#### Exemple de réponse
```json
{
  "id": 1,
  "annee": "2026-01-01",
  "createdAt": "2026-02-22T22:14:43.69"
}
```

## ⚙️ Configuration

### Dépendances Maven
Dans `pom.xml` :
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### Configuration application.properties
```properties
# Configuration Swagger/OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.packages-to-scan=com.explorateur.backend.controller
```

### Fichier de configuration OpenApiConfig.java
Localisation : `src/main/java/com/explorateur/backend/config/OpenApiConfig.java`

Cette classe configure :
- Titre de l'API
- Description
- Version
- Contact
- Licence
- Serveurs disponibles

## 📝 Annotations Utilisées

### Au niveau du Contrôleur
```java
@Tag(name = "Nom du Tag", description = "Description du groupe d'endpoints")
```

### Au niveau des Méthodes
```java
@Operation(
    summary = "Résumé court de l'opération",
    description = "Description détaillée"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Succès"),
    @ApiResponse(responseCode = "404", description = "Non trouvé")
})
```

### Au niveau des Entités
```java
@Schema(description = "Description de l'entité")

// Sur les champs
@Schema(
    description = "Description du champ", 
    example = "Exemple de valeur",
    accessMode = Schema.AccessMode.READ_ONLY
)
```

## 🎨 Personnalisation

### Modifier les informations de l'API
Éditez `OpenApiConfig.java` pour changer :
- Titre
- Description
- Version
- Contact
- Licence
- URL des serveurs

### Ajouter de nouveaux endpoints
1. Créer un nouveau contrôleur dans `src/main/java/.../controller/`
2. Ajouter `@RestController` et `@RequestMapping`
3. Ajouter `@Tag` pour le groupement
4. Annoter les méthodes avec `@Operation`
5. Swagger détecte et documente automatiquement

## 📊 APIs Actuellement Documentées

### Année d'Exercice
- `GET /api/annee-exercice/courante` - Obtenir l'année courante
- `GET /api/annee-exercice/recente` - Obtenir l'année la plus récente

### À venir
- Utilisateurs
- Authentification
- Classes
- Enfants
- Inscriptions
- Activités
- Budget
- Programmes
- Classes Progressives

## 🔒 Sécurité (À implémenter)

Quand Spring Security sera ajouté :
- Configuration de l'authentification JWT dans Swagger
- Bouton "Authorize" pour saisir le token
- Test des endpoints protégés

## 🚀 Production

Pour la production, modifier `OpenApiConfig.java` :
```java
Server prodServer = new Server();
prodServer.setUrl("https://api.explorateurs.com");
prodServer.setDescription("Serveur de production");
```

## 📖 Ressources

- **Documentation Springdoc** : https://springdoc.org/
- **OpenAPI Specification** : https://swagger.io/specification/
- **Swagger UI** : https://swagger.io/tools/swagger-ui/

## ⚡ Commandes Rapides

```bash
# Démarrer l'application
cd backend
./mvnw spring-boot:run

# Ouvrir Swagger UI (dans le navigateur)
http://localhost:8080/swagger-ui.html

# Tester un endpoint
curl http://localhost:8080/api/annee-exercice/courante

# Voir l'API docs JSON
curl http://localhost:8080/api-docs | jq .

# Vérifier que le serveur écoute
ss -tuln | grep 8080
```

## 🎯 Prochaines Étapes

1. ✅ Swagger UI configuré et fonctionnel
2. ⏳ Créer les entités pour toutes les tables
3. ⏳ Créer les repositories JPA
4. ⏳ Créer les services métier
5. ⏳ Créer les contrôleurs REST
6. ⏳ Documenter chaque endpoint avec Swagger
7. ⏳ Ajouter Spring Security
8. ⏳ Configurer JWT dans Swagger

## 💡 Conseils

- **Documentation au fur et à mesure** : Ajoutez les annotations Swagger pendant que vous créez les endpoints
- **Exemples concrets** : Utilisez `@Schema(example = "...")` pour aider les utilisateurs
- **Descriptions claires** : Soyez précis dans les descriptions
- **Codes de réponse** : Documentez tous les codes HTTP possibles
- **Groupement logique** : Utilisez les Tags pour organiser les endpoints

---

**🎉 Swagger UI est maintenant opérationnel !**

Vous pouvez commencer à tester vos APIs dès maintenant sur http://localhost:8080/swagger-ui.html
