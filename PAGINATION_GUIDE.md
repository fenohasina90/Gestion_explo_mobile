# Guide d'implémentation de la Pagination

## Vue d'ensemble

La pagination a été ajoutée pour les listes de parents et d'enfants afin d'améliorer les performances et l'expérience utilisateur lors de l'affichage de grandes quantités de données.

## Composants créés

### 1. Backend

#### DTO PageResponse
- **Fichier**: `backend/src/main/java/com/explorateur/backend/dto/PageResponse.java`
- **Description**: DTO générique pour encapsuler les réponses paginées
- **Propriétés**:
  - `content`: Liste des éléments de la page courante
  - `page`: Numéro de la page (commence à 0)
  - `size`: Nombre d'éléments par page
  - `totalElements`: Nombre total d'éléments
  - `totalPages`: Nombre total de pages
  - `first`: Booléen indiquant si c'est la première page
  - `last`: Booléen indiquant si c'est la dernière page
  - `hasNext`: Booléen indiquant s'il y a une page suivante
  - `hasPrevious`: Booléen indiquant s'il y a une page précédente

#### Services modifiés
- **ParentService.getAllParents(Pageable)**
- **EnfantService.getAllEnfants(Pageable)**

#### Contrôleurs modifiés
- **ParentController.getAllParents()**: Accepte maintenant `page`, `size`, `sort`, `direction`
- **EnfantController.getAllEnfants()**: Accepte maintenant `page`, `size`, `sort`, `direction`

### 2. Frontend

#### Type TypeScript
- **Fichier**: `frontend/src/types/index.ts`
- **Interface**: `PageResponse<T>`

#### Services modifiés
- **parentService.getAllParentsPaginated()**: Nouvelle méthode avec pagination
- **enfantService.getAllEnfantsPaginated()**: Nouvelle méthode avec pagination
- Les méthodes `getAllParents()` et `getAllEnfants()` sans pagination sont conservées pour la compatibilité

#### Composant Pagination
- **Fichier**: `frontend/src/components/Pagination.tsx`
- **Props**:
  - `currentPage`: Page courante (0-based)
  - `totalPages`: Nombre total de pages
  - `totalElements`: Nombre total d'éléments
  - `pageSize`: Taille de la page
  - `onPageChange`: Callback pour changer de page
  - `onSizeChange`: Callback pour changer la taille de page

## Utilisation

### API Backend

#### Récupérer les parents avec pagination
```http
GET /api/parents?page=0&size=10&sort=nom&direction=asc
```

**Paramètres**:
- `page`: Numéro de la page (défaut: 0)
- `size`: Nombre d'éléments par page (défaut: 10)
- `sort`: Champ de tri (défaut: "nom")
- `direction`: Direction du tri, "asc" ou "desc" (défaut: "asc")

**Réponse**:
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 156,
  "totalPages": 16,
  "first": true,
  "last": false,
  "hasNext": true,
  "hasPrevious": false
}
```

### Frontend React

#### Import du composant
```typescript
import Pagination from '../components/Pagination';
import parentService from '../services/parent.service';
import type { PageResponse, Parent } from '../types';
```

#### État de pagination
```typescript
const [parentsPage, setParentsPage] = useState<PageResponse<Parent> | null>(null);
const [currentPage, setCurrentPage] = useState(0);
const [pageSize, setPageSize] = useState(10);
```

#### Chargement des données
```typescript
const loadParents = async () => {
  try {
    const page = await parentService.getAllParentsPaginated(
      currentPage,
      pageSize,
      'nom',
      'asc'
    );
    setParentsPage(page);
  } catch (err) {
    console.error('Erreur:', err);
  }
};

useEffect(() => {
  loadParents();
}, [currentPage, pageSize]);
```

#### Rendu
```tsx
<div>
  {parentsPage?.content.map(parent => (
    <div key={parent.id}>
      {parent.nom} {parent.prenom}
    </div>
  ))}

  {parentsPage && (
    <Pagination
      currentPage={parentsPage.page}
      totalPages={parentsPage.totalPages}
      totalElements={parentsPage.totalElements}
      pageSize={parentsPage.size}
      onPageChange={setCurrentPage}
      onSizeChange={(size) => {
        setPageSize(size);
        setCurrentPage(0); // Retour à la première page
      }}
    />
  )}
</div>
```

## Stratégies de pagination

### 1. Pagination côté serveur (Recommandée)
✅ **Avantages**:
- Performance optimale avec de grandes quantités de données
- Charge réduite sur le client et le réseau
- Évolutivité

❌ **Inconvénients**:
- Plus complexe à implémenter
- Requiert des appels API pour chaque changement de page

**Quand l'utiliser**: Datasets > 100 éléments

### 2. Pagination côté client
✅ **Avantages**:
- Simple à implémenter
- Navigation instantanée entre les pages
- Pas de requêtes réseau supplémentaires

❌ **Inconvénients**:
- Charge toutes les données en mémoire
- Temps de chargement initial plus long
- Moins évolutif

**Quand l'utiliser**: Datasets < 100 éléments

### 3. Pagination hybride (avec filtres)
Pour combiner filtres côté client et pagination:

```typescript
// Option A: Filtres côté client, pagination côté serveur
// 1. Charger toutes les données
const allParents = await parentService.getAllParents();

// 2. Appliquer les filtres
const filtered = allParents.filter(p => /* conditions */);

// 3. Paginer manuellement
const paginated = paginateClientSide(filtered, currentPage, pageSize);

// Option B: Filtres et pagination côté serveur (optimal)
// Créer des endpoints backend avec filtres + pagination
const page = await parentService.getFilteredParentsPaginated(
  currentPage,
  pageSize,
  { anneeExerciceId, classeId }
);
```

## Intégration dans EnfantsPage

Voir le fichier `EnfantsPage.pagination-example.tsx` pour un exemple complet d'intégration.

### Étapes d'intégration:

1. **Ajouter les états de pagination**: `parentsPage`, `currentPage`, `pageSize`
2. **Modifier loadData()**: Utiliser `getAllParentsPaginated()`
3. **Ajouter useEffect**: Recharger quand page/size change
4. **Modifier le rendu**: Utiliser `parentsPage.content` au lieu de `parents`
5. **Ajouter le composant Pagination**: Sous la liste

## Personnalisation

### Tailles de page disponibles
Modifier dans `Pagination.tsx`:
```tsx
<option value="5">5</option>
<option value="10">10</option>
<option value="20">20</option>
<option value="50">50</option>
<option value="100">100</option>
```

### Style
Modifier `Pagination.css`:
- `.pagination-btn.active`: Style du bouton de page active
- Couleur principale: `#a4c639` (vert Scout)

### Tris multiples
Pour trier par plusieurs champs:
```typescript
// Backend: Modifier le contrôleur pour accepter plusieurs champs
Sort sort = Sort.by(Sort.Direction.ASC, "nom").and(Sort.by(Sort.Direction.ASC, "prenom"));
```

## Tests

### Test manuel
1. Démarrer le backend: `cd backend && mvn spring-boot:run`
2. Démarrer le frontend: `cd frontend && npm run dev`
3. Naviguer vers la page Enfants
4. Vérifier:
   - Changement de page fonctionne
   - Changement de taille de page fonctionne
   - Information "Affichage de X à Y sur Z" correcte
   - Boutons désactivés correctement (première/dernière page)

### Test API avec curl
```bash
# Page 1 avec 10 éléments
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/parents?page=0&size=10"

# Page 2 avec 20 éléments, tri par prénom descendant
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/parents?page=1&size=20&sort=prenom&direction=desc"
```

## Troubleshooting

### Problème: "page" est toujours 0
- Vérifier que `onPageChange` est bien appelé
- Vérifier que l'état `currentPage` est mis à jour

### Problème: Données ne se rechargent pas
- Vérifier les dépendances de `useEffect`
- S'assurer que `loadData()` est appelé dans `useEffect`

### Problème: Pagination ne s'affiche pas
- Vérifier que `parentsPage` n'est pas null
- Vérifier l'import du CSS: `import './Pagination.css'`

### Problème: Performance lente
- Utiliser la pagination côté serveur
- Réduire la taille de page par défaut
- Optimiser les requêtes backend (index sur les champs de tri)

## Prochaines étapes

1. ✅ Pagination backend implémentée
2. ✅ Pagination frontend implémentée
3. ✅ Composant réutilisable créé
4. ⏳ Intégrer dans EnfantsPage.tsx
5. ⏳ Ajouter pagination pour InscriptionsPage (si nécessaire)
6. ⏳ Tests unitaires et d'intégration
7. ⏳ Documentation API (Swagger)

## Support

Pour toute question ou problème, consulter:
- Code source: `frontend/src/components/Pagination.tsx`
- Exemple: `frontend/src/pages/EnfantsPage.pagination-example.tsx`
- Documentation Spring Data: https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Page.html
