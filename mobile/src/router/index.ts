import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/tabs/',
    component: () => import('@/views/TabsPage.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/tabs/home'
      },
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/HomePage.vue')
      },
      {
        path: 'enfants',
        name: 'Enfants',
        component: () => import('@/views/EnfantsPage.vue')
      },
      {
        path: 'activites',
        name: 'Activites',
        component: () => import('@/views/ActivitesPage.vue')
      },
      {
        path: 'budget',
        name: 'Budget',
        component: () => import('@/views/BudgetPage.vue')
      },
      {
        path: 'profil',
        name: 'Profil',
        component: () => import('@/views/ProfilPage.vue')
      }
    ]
  },
  {
    path: '/utilisateurs',
    name: 'Utilisateurs',
    component: () => import('@/views/UtilisateursPage.vue'),
    meta: { requiresAuth: true, requiresDirecteur: true }
  },
  {
    path: '/staffs',
    name: 'Staffs',
    component: () => import('@/views/StaffsPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/annees-exercice',
    name: 'AnneesExercice',
    component: () => import('@/views/AnneesExercicePage.vue'),
    meta: { requiresAuth: true, requiresDirecteur: true }
  },
  {
    path: '/journal',
    name: 'Journal',
    component: () => import('@/views/JournalPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/categories',
    name: 'Categories',
    component: () => import('@/views/CategoriesPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/programmes',
    name: 'Programmes',
    component: () => import('@/views/ProgrammesPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/classes-progressives',
    name: 'ClassesProgressives',
    component: () => import('@/views/ClassesProgressivesPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/historique-programmes',
    name: 'HistoriqueProgrammes',
    component: () => import('@/views/HistoriqueProgrammesPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/mouvements-budgetaires',
    name: 'MouvementsBudgetaires',
    component: () => import('@/views/MouvementsBudgetairesPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/tabs/cp-details/:id',
    name: 'CPDetails',
    component: () => import('@/views/CPDetailsPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/tabs/cp-presence/:id',
    name: 'CPPresence',
    component: () => import('@/views/CPPresencePage.vue'),
    meta: { requiresAuth: true }
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

// Guard de navigation pour l'authentification
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  
  // Initialiser le store auth au premier chargement
  if (!authStore.isAuthenticated && to.path !== '/login') {
    await authStore.initialize();
  }
  
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false);
  const requiresDirecteur = to.matched.some(record => record.meta.requiresDirecteur === true);
  
  if (requiresAuth && !authStore.isAuthenticated) {
    // Rediriger vers login si authentification requise
    next({ name: 'Login', query: { redirect: to.fullPath } });
  } else if (requiresDirecteur && !authStore.isDirecteur) {
    // Rediriger vers home si accès Directeur requis
    next({ name: 'Home' });
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    // Rediriger vers home si déjà authentifié
    next({ name: 'Home' });
  } else {
    next();
  }
});

export default router;
