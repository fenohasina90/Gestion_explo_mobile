# backend_beta

Service backend de consultation uniquement pour les mouvements budgetaires.

## Objectif

- Consultation sans verification de role ou token.
- Les operations d'ecriture (insertion, modification, suppression) restent dans `backend`.

## Endpoints exposes

- `GET /api/budget-mouvements`
- `GET /api/budget-mouvements/all`
- `GET /api/budget-mouvements/{id}`
- `POST /api/budget-mouvements/search`
- `GET /api/budget-mouvements/etat-caisse`

## Configuration BDD

Variables d'environnement supportees:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `PORT` (optionnel, default: 8081)

## Lancement local

```bash
cd backend_beta
mvn spring-boot:run
```

## Build

```bash
cd backend_beta
mvn clean package -DskipTests
```
