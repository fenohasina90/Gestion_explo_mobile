/**
 * Exemple d'intégration de la pagination dans EnfantsPage.tsx
 * 
 * Ce fichier montre comment modifier EnfantsPage.tsx pour utiliser la pagination
 * pour les listes de parents et d'enfants.
 */

import { useState, useEffect } from 'react';
import Pagination from '../components/Pagination';
import parentService from '../services/parent.service';
import enfantService from '../services/enfant.service';
import type { PageResponse, Parent, EnfantResponse } from '../types';

/**
 * ÉTAPE 1: Ajouter les états pour la pagination
 */

// Pour les parents
const [parentsPage, setParentsPage] = useState<PageResponse<Parent> | null>(null);
const [currentParentPage, setCurrentParentPage] = useState(0);
const [parentPageSize, setParentPageSize] = useState(10);

// Pour les enfants (si vous affichez une liste d'enfants)
const [enfantsPage, setEnfantsPage] = useState<PageResponse<EnfantResponse> | null>(null);
const [currentEnfantPage, setCurrentEnfantPage] = useState(0);
const [enfantPageSize, setEnfantPageSize] = useState(10);

/**
 * ÉTAPE 2: Modifier la fonction de chargement des données
 */

const loadParentsPaginated = async () => {
  try {
    setLoading(true);
    const page = await parentService.getAllParentsPaginated(
      currentParentPage,
      parentPageSize,
      'nom',
      'asc'
    );
    setParentsPage(page);
    
    // Charger les enfants de chaque parent de la page courante
    const enfantsMap: Record<number, EnfantResponse[]> = {};
    await Promise.all(
      page.content.map(async (parent) => {
        const enfants = await enfantService.getEnfantsByParentId(parent.id);
        enfantsMap[parent.id] = enfants;
      })
    );
    setParentEnfants(enfantsMap);
  } catch (err) {
    setError('Erreur lors du chargement des parents');
    console.error(err);
  } finally {
    setLoading(false);
  }
};

const loadEnfantsPaginated = async () => {
  try {
    setLoading(true);
    const page = await enfantService.getAllEnfantsPaginated(
      currentEnfantPage,
      enfantPageSize,
      'nom',
      'asc'
    );
    setEnfantsPage(page);
  } catch (err) {
    setError('Erreur lors du chargement des enfants');
    console.error(err);
  } finally {
    setLoading(false);
  }
};

/**
 * ÉTAPE 3: Ajouter les useEffect pour recharger quand la page change
 */

useEffect(() => {
  if (activeTab === 'parents') {
    loadParentsPaginated();
  }
}, [currentParentPage, parentPageSize, activeTab]);

useEffect(() => {
  if (activeTab === 'enfants') {
    loadEnfantsPaginated();
  }
}, [currentEnfantPage, enfantPageSize, activeTab]);

/**
 * ÉTAPE 4: Gérer les changements de page
 */

const handleParentPageChange = (page: number) => {
  setCurrentParentPage(page);
};

const handleParentSizeChange = (size: number) => {
  setParentPageSize(size);
  setCurrentParentPage(0); // Retour à la première page
};

const handleEnfantPageChange = (page: number) => {
  setCurrentEnfantPage(page);
};

const handleEnfantSizeChange = (size: number) => {
  setEnfantPageSize(size);
  setCurrentEnfantPage(0);
};

/**
 * ÉTAPE 5: Modifier le rendu pour utiliser parentsPage.content au lieu de parents
 */

// AVANT (sans pagination):
// {filteredParents.map(parent => (
//   <div key={parent.id}>...</div>
// ))}

// APRÈS (avec pagination):
// {parentsPage && parentsPage.content.map(parent => (
//   <div key={parent.id}>...</div>
// ))}

/**
 * ÉTAPE 6: Ajouter le composant Pagination dans le JSX
 */

/*
Dans l'onglet Parents:

<div className="parents-list">
  {parentsPage && parentsPage.content.map(parent => (
    <div key={parent.id} className="parent-card">
      // ... contenu de la carte parent
    </div>
  ))}
</div>

{parentsPage && (
  <Pagination
    currentPage={parentsPage.page}
    totalPages={parentsPage.totalPages}
    totalElements={parentsPage.totalElements}
    pageSize={parentsPage.size}
    onPageChange={handleParentPageChange}
    onSizeChange={handleParentSizeChange}
  />
)}
*/

/**
 * ÉTAPE 7: Pour combiner pagination et filtres
 * 
 * Si vous voulez garder les filtres côté client tout en ayant la pagination:
 * 1. Chargez tous les parents avec getAllParents() (sans pagination)
 * 2. Appliquez les filtres côté client
 * 3. Paginez les résultats filtrés manuellement
 * 
 * OU
 * 
 * Pour une meilleure performance avec beaucoup de données:
 * 1. Créez des endpoints backend qui acceptent les filtres + pagination
 * 2. Exemple: /api/parents?page=0&size=10&anneeExerciceId=1&classeId=2
 * 3. Le backend retourne déjà les données filtrées et paginées
 */

// Exemple de pagination manuelle côté client:
const paginateClientSide = (items: Parent[], page: number, size: number) => {
  const startIndex = page * size;
  const endIndex = startIndex + size;
  return {
    content: items.slice(startIndex, endIndex),
    page: page,
    size: size,
    totalElements: items.length,
    totalPages: Math.ceil(items.length / size),
    first: page === 0,
    last: endIndex >= items.length,
    hasNext: endIndex < items.length,
    hasPrevious: page > 0,
  };
};

// Utilisation:
useEffect(() => {
  const paginated = paginateClientSide(filteredParents, currentParentPage, parentPageSize);
  setParentsPage(paginated);
}, [filteredParents, currentParentPage, parentPageSize]);

/**
 * NOTES IMPORTANTES:
 * 
 * 1. La pagination côté serveur est recommandée pour de grandes quantités de données (>100 éléments)
 * 2. La pagination côté client est plus simple mais charge toutes les données
 * 3. N'oubliez pas de remettre currentPage à 0 quand les filtres changent
 * 4. Le composant Pagination est réutilisable pour toutes vos listes
 */

export {};
