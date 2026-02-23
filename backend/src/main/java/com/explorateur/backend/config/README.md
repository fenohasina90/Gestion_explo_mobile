# Configuration Backend

Ce dossier contient les classes de configuration de l'application Spring Boot.

## Fichiers de Configuration

### CorsConfig.java
**Configuration CORS (Cross-Origin Resource Sharing)**

Permet au frontend Vue.js (port 5173) de communiquer avec le backend (port 8080).

**Origines autorisées :**
- http://localhost:5173 (Vue.js dev)
- http://localhost:8080 (même origine)
- http://127.0.0.1:5173
- http://127.0.0.1:8080

**Méthodes HTTP autorisées :**
- GET
- POST
- PUT
- DELETE
- OPTIONS

**Configuration :**
- Headers : Tous autorisés (`*`)
- Credentials : Activé (`allowCredentials(true)`)
- Max Age : 3600 secondes (1 heure)
- Pattern : `/api/**`

### OpenApiConfig.java
**Configuration Swagger/OpenAPI Documentation**

Configure l'interface Swagger UI pour tester et documenter les APIs REST.

**Informations de l'API :**
- **Titre** : API Club des Explorateurs
- **Version** : 1.0.0
- **Description** : API REST pour la gestion du Club des Explorateurs
- **Contact** : contact@explorateurs.com
- **Licence** : Propriétaire

**Serveurs configurés :**
- Développement local : http://localhost:8080

**Accès Swagger UI :**
- Interface : http://localhost:8080/swagger-ui.html
- API Docs JSON : http://localhost:8080/api-docs

**Fonctionnalités :**
- Documentation automatique des endpoints
- Test interactif des APIs
- Visualisation des modèles de données
- Authentification JWT (à implémenter)

## Utilisation

### Ajouter une nouvelle configuration

1. Créer une nouvelle classe dans ce dossier
2. Annoter avec `@Configuration`
3. Définir des beans avec `@Bean`

Exemple :
```java
@Configuration
public class MaConfig {
    
    @Bean
    public MonService monService() {
        return new MonService();
    }
}
```

### Modifier CORS

Pour autoriser d'autres origines, éditez `CorsConfig.java` :

```java
.allowedOrigins(
    "http://localhost:5173",
    "http://localhost:8080",
    "http://nouvelle-origine.com"  // Ajouter ici
)
```

### Modifier Swagger

Pour personnaliser l'API documentation, éditez `OpenApiConfig.java` :

```java
Info info = new Info()
    .title("Nouveau Titre")
    .version("2.0.0")
    .description("Nouvelle description");
```

## Configurations Supplémentaires Recommandées

### SecurityConfig.java (À créer)
Configuration de Spring Security pour :
- Authentification JWT
- Autorisation par rôles
- Protection des endpoints
- Hachage des mots de passe

### DatabaseConfig.java (Optionnel)
Configuration avancée de la base de données :
- Pool de connexions
- Transactions
- Cache de second niveau

### AsyncConfig.java (Optionnel)
Configuration pour les opérations asynchrones :
- Thread pool
- Tâches planifiées
- Exécution parallèle

## Bonnes Pratiques

1. **Séparation des préoccupations** : Une classe de configuration par fonctionnalité
2. **Documentation** : Commenter les configurations complexes
3. **Externalisaton** : Utiliser `application.properties` pour les valeurs configurables
4. **Profils** : Utiliser `@Profile` pour les configurations spécifiques (dev, prod)
5. **Validation** : Valider les configurations au démarrage

## Ordre de Chargement

Spring charge les configurations dans cet ordre :
1. `@Configuration` classes
2. `@Bean` methods
3. `application.properties`
4. Command-line arguments

## Références

- [Spring Boot Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [CORS Configuration](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-cors)
- [OpenAPI Documentation](https://springdoc.org/)
