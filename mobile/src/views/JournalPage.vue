<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button default-href="/tabs/home"></ion-back-button>
        </ion-buttons>
        <ion-title>Journal d'Audit</ion-title>
        <ion-buttons slot="end">
          <ion-button @click="showFilters = !showFilters">
            <ion-icon :icon="filterOutline"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-refresher slot="fixed" @ionRefresh="handleRefresh($event)">
        <ion-refresher-content></ion-refresher-content>
      </ion-refresher>

      <!-- Filtres -->
      <div v-if="showFilters" class="filters-section ion-padding">
        <ion-item>
          <ion-label position="stacked">Date de début</ion-label>
          <ion-datetime-button datetime="dateDebut"></ion-datetime-button>
        </ion-item>

        <ion-modal :keep-contents-mounted="true">
          <ion-datetime 
            id="dateDebut" 
            v-model="filters.dateDebut" 
            presentation="date"
            :max="new Date().toISOString()"
          ></ion-datetime>
        </ion-modal>

        <ion-item>
          <ion-label position="stacked">Date de fin</ion-label>
          <ion-datetime-button datetime="dateFin"></ion-datetime-button>
        </ion-item>

        <ion-modal :keep-contents-mounted="true">
          <ion-datetime 
            id="dateFin" 
            v-model="filters.dateFin" 
            presentation="date"
            :max="new Date().toISOString()"
          ></ion-datetime>
        </ion-modal>

        <ion-item>
          <ion-label position="stacked">Recherche</ion-label>
          <ion-input 
            v-model="filters.searchText" 
            placeholder="Rechercher dans les actions..."
            clear-input
          ></ion-input>
        </ion-item>

        <div class="ion-padding-top">
          <ion-button expand="block" @click="applyFilters">
            Appliquer les filtres
          </ion-button>
          <ion-button expand="block" fill="outline" @click="resetFilters">
            Réinitialiser
          </ion-button>
        </div>
      </div>

      <!-- Statistiques -->
      <div class="stats-section ion-padding">
        <ion-chip color="primary">
          <ion-label>
            <strong>{{ paginatedEntries.totalCount }}</strong> entrée{{ paginatedEntries.totalCount !== 1 ? 's' : '' }}
          </ion-label>
        </ion-chip>
        <ion-chip>
          <ion-label>Page {{ currentPage }} / {{ totalPages }}</ion-label>
        </ion-chip>
        
        <!-- Sélecteur items par page -->
        <ion-item lines="none">
          <ion-label>Afficher:</ion-label>
          <ion-select v-model="itemsPerPage" interface="popover">
            <ion-select-option :value="10">10</ion-select-option>
            <ion-select-option :value="25">25</ion-select-option>
            <ion-select-option :value="50">50</ion-select-option>
          </ion-select>
        </ion-item>
      </div>

      <!-- Liste des entrées -->
      <ion-list v-if="!loading && paginatedEntries.items.length > 0">
        <ion-item v-for="entry in paginatedEntries.items" :key="entry.id">
          <ion-label class="ion-text-wrap">
            <h2>{{ entry.action }}</h2>
            <p>
              <ion-icon :icon="personOutline" size="small"></ion-icon>
              {{ entry.username }}
            </p>
            <p>
              <ion-icon :icon="calendarOutline" size="small"></ion-icon>
              {{ formatDate(entry.timestamp) }}
            </p>
          </ion-label>
        </ion-item>
      </ion-list>

      <!-- Message si aucune entrée -->
      <div v-else-if="!loading && paginatedEntries.items.length === 0" class="empty-state ion-padding ion-text-center">
        <ion-icon :icon="documentTextOutline" size="large" color="medium"></ion-icon>
        <p>Aucune entrée trouvée dans le journal</p>
      </div>

      <!-- Skeleton lors du chargement -->
      <ion-list v-else>
        <ion-item v-for="i in 5" :key="i">
          <ion-label>
            <ion-skeleton-text animated style="width: 80%"></ion-skeleton-text>
            <ion-skeleton-text animated style="width: 60%"></ion-skeleton-text>
            <ion-skeleton-text animated style="width: 40%"></ion-skeleton-text>
          </ion-label>
        </ion-item>
      </ion-list>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination-controls ion-padding">
        <ion-button 
          :disabled="currentPage === 1" 
          @click="goToPage(currentPage - 1)"
          size="small"
        >
          <ion-icon :icon="chevronBackOutline"></ion-icon>
          Précédent
        </ion-button>
        
        <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
        
        <ion-button 
          :disabled="currentPage === totalPages" 
          @click="goToPage(currentPage + 1)"
          size="small"
        >
          Suivant
          <ion-icon :icon="chevronForwardOutline"></ion-icon>
        </ion-button>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonButtons,
  IonBackButton,
  IonButton,
  IonIcon,
  IonList,
  IonItem,
  IonLabel,
  IonRefresher,
  IonRefresherContent,
  IonInput,
  IonDatetime,
  IonDatetimeButton,
  IonModal,
  IonSkeletonText,
  IonChip,
  IonSelect,
  IonSelectOption,
  toastController
} from '@ionic/vue';
import {
  filterOutline,
  personOutline,
  calendarOutline,
  documentTextOutline,
  chevronBackOutline,
  chevronForwardOutline
} from 'ionicons/icons';
import journalService from '@/services/journal.service';
import type { JournalEntry, JournalFilterRequest } from '@/types';

// État
const loading = ref(true);
const showFilters = ref(false);
const journalEntries = ref<JournalEntry[]>([]);
const filters = ref<JournalFilterRequest>({
  dateDebut: '',
  dateFin: '',
  searchText: ''
});
const currentPage = ref(1);
const itemsPerPage = ref(10);

// Computed
const totalPages = computed(() => {
  return Math.ceil(journalEntries.value.length / itemsPerPage.value);
});

const paginatedEntries = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return {
    items: journalEntries.value.slice(start, end),
    totalCount: journalEntries.value.length
  };
});

// Méthodes
const loadData = async () => {
  try {
    loading.value = true;
    journalEntries.value = await journalService.getAllJournal();
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors du chargement du journal',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  } finally {
    loading.value = false;
  }
};

const applyFilters = async () => {
  try {
    loading.value = true;
    
    // Préparer les filtres
    const filterRequest: JournalFilterRequest = {
      searchText: filters.value.searchText || undefined
    };

    // Convertir les dates au format ISO avec heure
    if (filters.value.dateDebut) {
      filterRequest.dateDebut = `${filters.value.dateDebut.split('T')[0]}T00:00:00`;
    }
    if (filters.value.dateFin) {
      filterRequest.dateFin = `${filters.value.dateFin.split('T')[0]}T23:59:59`;
    }

    // Si aucun filtre, charger tout
    if (!filterRequest.dateDebut && !filterRequest.dateFin && !filterRequest.searchText) {
      await loadData();
    } else {
      journalEntries.value = await journalService.filterJournal(filterRequest);
    }
    
    currentPage.value = 1;
    showFilters.value = false;
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors du filtrage',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  } finally {
    loading.value = false;
  }
};

const resetFilters = async () => {
  filters.value = {
    dateDebut: '',
    dateFin: '',
    searchText: ''
  };
  currentPage.value = 1;
  await loadData();
};

const handleRefresh = async (event: any) => {
  await loadData();
  event.target.complete();
};

const goToPage = (page: number) => {
  currentPage.value = page;
  // Scroll vers le haut
  const content = document.querySelector('ion-content');
  content?.scrollToTop(500);
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
};

// Lifecycle
onMounted(() => {
  loadData();
});
</script>

<style scoped>
.filters-section {
  background: var(--ion-color-light);
  border-bottom: 1px solid var(--ion-color-light-shade);
}

.stats-section {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  background: var(--ion-color-light);
  padding: 12px 16px;
}

.stats-section ion-item {
  --padding-start: 0;
  --inner-padding-end: 0;
}

.empty-state {
  margin-top: 60px;
}

.empty-state ion-icon {
  font-size: 80px;
  margin-bottom: 16px;
}

.empty-state p {
  color: var(--ion-color-medium);
  font-size: 16px;
}

.pagination-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: var(--ion-color-light);
  border-top: 1px solid var(--ion-color-light-shade);
}

.page-info {
  font-weight: 500;
  color: var(--ion-color-medium);
}

ion-label h2 {
  font-weight: 600;
  color: var(--ion-color-dark);
  margin-bottom: 4px;
}

ion-label p {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--ion-color-medium);
  margin: 2px 0;
}

ion-label p ion-icon {
  margin-right: 4px;
}
</style>
