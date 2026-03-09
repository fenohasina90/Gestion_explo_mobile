<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <img src="/assets/logo.png" alt="Logo" class="header-logo" />
        </ion-buttons>
        <ion-title>Statistiques</ion-title>
        <ion-buttons slot="end">
          <ion-button @click="loadData" :disabled="loading">
            <ion-icon :icon="syncOutline"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
      
      <!-- Segment pour switcher entre Enfants et Staffs -->
      <ion-toolbar>
        <ion-segment :value="activeTab" @ionChange="onTabChange($event)">
          <ion-segment-button value="enfants">
            <ion-label>Enfants</ion-label>
          </ion-segment-button>
          <ion-segment-button value="staffs">
            <ion-label>Staffs</ion-label>
          </ion-segment-button>
        </ion-segment>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <!-- Messages d'erreur et succès -->
      <ion-toast
        :is-open="!!error"
        :message="error || ''"
        :duration="3000"
        position="top"
        color="danger"
        @didDismiss="error = null"
      ></ion-toast>

      <!-- Loading -->
      <div v-if="loading" class="ion-text-center ion-padding">
        <ion-spinner></ion-spinner>
        <p>Chargement...</p>
      </div>

      <!-- Tab Enfants -->
      <div v-else-if="activeTab === 'enfants'">
        <!-- Filtres -->
        <ion-card>
          <ion-card-header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <ion-card-title>Filtres</ion-card-title>
              <ion-button fill="clear" @click="showFilters = !showFilters">
                <ion-icon :icon="chevronBackOutline" :class="{ 'rotate-up': showFilters, 'rotate-down': !showFilters }"></ion-icon>
              </ion-button>
            </div>
          </ion-card-header>
          <ion-card-content v-if="showFilters">
            <ion-item>
              <ion-label>Année d'exercice</ion-label>
              <ion-select v-model="filterAnneeId" placeholder="Toutes">
                <ion-select-option :value="null">Toutes</ion-select-option>
                <ion-select-option v-for="annee in anneesExercice" :key="annee.id" :value="annee.id">
                  {{ formatAnnee(annee.annee) }}
                </ion-select-option>
              </ion-select>
            </ion-item>
            
            <ion-item>
              <ion-label>Classe</ion-label>
              <ion-select v-model="filterClasseId" placeholder="Toutes">
                <ion-select-option :value="null">Toutes</ion-select-option>
                <ion-select-option v-for="classe in classes" :key="classe.id" :value="classe.id">
                  {{ classe.nom }}
                </ion-select-option>
              </ion-select>
            </ion-item>
            
            <ion-item>
              <ion-label>Genre</ion-label>
              <ion-select v-model="filterGenre" placeholder="Tous">
                <ion-select-option :value="null">Tous</ion-select-option>
                <ion-select-option value="GARCON">Garçon</ion-select-option>
                <ion-select-option value="FILLE">Fille</ion-select-option>
              </ion-select>
            </ion-item>
            
            <ion-button expand="block" fill="clear" @click="resetFilters">
              Réinitialiser les filtres
            </ion-button>
          </ion-card-content>
        </ion-card>

        <!-- Liste des statistiques enfants -->
        <ion-card v-if="statistiquesEnfants.length === 0 && !loading">
          <ion-card-content class="ion-text-center">
            Aucune statistique disponible
          </ion-card-content>
        </ion-card>

        <ion-card v-for="stat in statistiquesEnfants" :key="stat.enfantId">
          <ion-card-header>
            <ion-card-title>{{ stat.nom }} {{ stat.prenom }}</ion-card-title>
            <ion-card-subtitle>{{ stat.classe }} - {{ formatAnnee(stat.anneeExercice) }}</ion-card-subtitle>
          </ion-card-header>
          <ion-card-content>
            <!-- Programmes complétés -->
            <div class="stat-row">
              <ion-label>
                <h3>Programmes Complétés</h3>
                <p>{{ stat.nombreProgrammesCompletes }} / {{ stat.totalProgrammesClasse }}</p>
              </ion-label>
              <div class="stat-value">
                <ion-badge color="primary">{{ formatPourcentage(stat.pourcentageProgrammes) }}</ion-badge>
              </div>
            </div>
            
            <!-- Participation Activités -->
            <div class="stat-row">
              <ion-label>
                <h3>Participation Activités</h3>
                <p>{{ stat.nombreParticipationsActivites }} / {{ stat.totalActivites }}</p>
              </ion-label>
              <div class="stat-value">
                <ion-badge :color="getRangBadgeColor(stat.rangActivites)">
                  {{ formatPourcentage(stat.pourcentageActivites) }}
                </ion-badge>
                <ion-chip :color="getRangBadgeColor(stat.rangActivites)">
                  Rang #{{ stat.rangActivites }}
                </ion-chip>
              </div>
            </div>
            
            <!-- Présence CP -->
            <div class="stat-row">
              <ion-label>
                <h3>Présence CP</h3>
                <p>{{ stat.nombrePresencesCP }} / {{ stat.totalCP }}</p>
              </ion-label>
              <div class="stat-value">
                <ion-badge :color="getRangBadgeColor(stat.rangCP)">
                  {{ formatPourcentage(stat.pourcentageCP) }}
                </ion-badge>
                <ion-chip :color="getRangBadgeColor(stat.rangCP)">
                  Rang #{{ stat.rangCP }}
                </ion-chip>
              </div>
            </div>
          </ion-card-content>
        </ion-card>
      </div>

      <!-- Tab Staffs -->
      <div v-else-if="activeTab === 'staffs'">
        <!-- Filtres -->
        <ion-card>
          <ion-card-header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <ion-card-title>Filtres</ion-card-title>
              <ion-button fill="clear" @click="showFiltersStaff = !showFiltersStaff">
                <ion-icon :icon="chevronBackOutline" :class="{ 'rotate-up': showFiltersStaff, 'rotate-down': !showFiltersStaff }"></ion-icon>
              </ion-button>
            </div>
          </ion-card-header>
          <ion-card-content v-if="showFiltersStaff">
            <ion-item>
              <ion-label>Année d'exercice</ion-label>
              <ion-select v-model="filterStaffAnneeId" placeholder="Toutes">
                <ion-select-option :value="null">Toutes</ion-select-option>
                <ion-select-option v-for="annee in anneesExercice" :key="annee.id" :value="annee.id">
                  {{ formatAnnee(annee.annee) }}
                </ion-select-option>
              </ion-select>
            </ion-item>
            
            <ion-button expand="block" fill="clear" @click="resetStaffFilters">
              Réinitialiser les filtres
            </ion-button>
          </ion-card-content>
        </ion-card>

        <!-- Liste des statistiques staffs -->
        <ion-card v-if="statistiquesStaffs.length === 0 && !loading">
          <ion-card-content class="ion-text-center">
            Aucune statistique disponible
          </ion-card-content>
        </ion-card>

        <ion-card v-for="stat in statistiquesStaffs" :key="stat.staffId">
          <ion-card-header>
            <ion-card-title>{{ stat.nom }} {{ stat.prenom }}</ion-card-title>
            <ion-card-subtitle>{{ stat.role }} - {{ formatAnnee(stat.anneeExercice) }}</ion-card-subtitle>
          </ion-card-header>
          <ion-card-content>
            <!-- Participation Activités -->
            <div class="stat-row">
              <ion-label>
                <h3>Participation Activités</h3>
                <p>{{ stat.nombreParticipationsActivites }} / {{ stat.totalActivites }}</p>
              </ion-label>
              <div class="stat-value">
                <ion-badge :color="getRangBadgeColor(stat.rangActivites)">
                  {{ formatPourcentage(stat.pourcentageActivites) }}
                </ion-badge>
                <ion-chip :color="getRangBadgeColor(stat.rangActivites)">
                  Rang #{{ stat.rangActivites }}
                </ion-chip>
              </div>
            </div>
            
            <!-- Présence CP -->
            <div class="stat-row">
              <ion-label>
                <h3>Présence CP</h3>
                <p>{{ stat.nombrePresencesCP }} / {{ stat.totalCP }}</p>
              </ion-label>
              <div class="stat-value">
                <ion-badge :color="getRangBadgeColor(stat.rangCP)">
                  {{ formatPourcentage(stat.pourcentageCP) }}
                </ion-badge>
                <ion-chip :color="getRangBadgeColor(stat.rangCP)">
                  Rang #{{ stat.rangCP }}
                </ion-chip>
              </div>
            </div>
          </ion-card-content>
        </ion-card>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonButtons,
  IonButton,
  IonIcon,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardSubtitle,
  IonCardContent,
  IonSegment,
  IonSegmentButton,
  IonLabel,
  IonToast,
  IonSpinner,
  IonItem,
  IonSelect,
  IonSelectOption,
  IonBadge,
  IonChip,
} from '@ionic/vue';
import { syncOutline, chevronBackOutline } from 'ionicons/icons';
import statistiqueService from '@/services/statistique.service';
import anneeExerciceService from '@/services/annee-exercice.service';
import classeService from '@/services/classe.service';
import type {
  StatistiqueEnfant,
  StatistiqueStaff,
  StatistiqueFilterRequest,
  AnneeExercice,
  Classe,
} from '@/types';

// État
const activeTab = ref<'enfants' | 'staffs'>('enfants');
const loading = ref(false);
const error = ref<string | null>(null);

// Données
const statistiquesEnfants = ref<StatistiqueEnfant[]>([]);
const statistiquesStaffs = ref<StatistiqueStaff[]>([]);
const anneesExercice = ref<AnneeExercice[]>([]);
const classes = ref<Classe[]>([]);

// Filtres
const showFilters = ref(true);
const showFiltersStaff = ref(true);
const filterAnneeId = ref<number | null>(null);
const filterClasseId = ref<number | null>(null);
const filterGenre = ref<string | null>(null);
const filterStaffAnneeId = ref<number | null>(null);

// Watchers pour recharger les données quand les filtres changent
watch([filterAnneeId, filterClasseId, filterGenre], () => {
  if (activeTab.value === 'enfants') {
    loadStatistiquesEnfants();
  }
});

watch(filterStaffAnneeId, () => {
  if (activeTab.value === 'staffs') {
    loadStatistiquesStaffs();
  }
});

// Fonctions
const formatAnnee = (annee: string): string => {
  return annee.split('-')[0];
};

const formatPourcentage = (pourcentage: number): string => {
  return `${pourcentage.toFixed(2)}%`;
};

const getRangBadgeColor = (rang: number): string => {
  if (rang === 1) return 'warning'; // Or
  if (rang === 2) return 'medium';  // Argent
  if (rang === 3) return 'tertiary'; // Bronze
  return 'dark';
};

const onTabChange = (event: CustomEvent) => {
  activeTab.value = event.detail.value;
  if (activeTab.value === 'enfants') {
    loadStatistiquesEnfants();
  } else {
    loadStatistiquesStaffs();
  }
};

const resetFilters = () => {
  filterAnneeId.value = null;
  filterClasseId.value = null;
  filterGenre.value = null;
  loadStatistiquesEnfants();
};

const resetStaffFilters = () => {
  filterStaffAnneeId.value = null;
  loadStatistiquesStaffs();
};

const loadStatistiquesEnfants = async () => {
  try {
    loading.value = true;
    error.value = null;
    
    const filters: StatistiqueFilterRequest = {};
    if (filterAnneeId.value) filters.anneeExerciceId = filterAnneeId.value;
    if (filterClasseId.value) filters.classeId = filterClasseId.value;
    if (filterGenre.value) filters.genre = filterGenre.value;
    
    statistiquesEnfants.value = await statistiqueService.getStatistiquesEnfants(filters);
  } catch (err: any) {
    console.error('Erreur lors du chargement des statistiques enfants', err);
    error.value = err.response?.data?.message || 'Erreur lors du chargement des statistiques';
  } finally {
    loading.value = false;
  }
};

const loadStatistiquesStaffs = async () => {
  try {
    loading.value = true;
    error.value = null;
    
    const filters: StatistiqueFilterRequest = {};
    if (filterStaffAnneeId.value) filters.anneeExerciceId = filterStaffAnneeId.value;
    
    statistiquesStaffs.value = await statistiqueService.getStatistiquesStaffs(filters);
  } catch (err: any) {
    console.error('Erreur lors du chargement des statistiques staffs', err);
    error.value = err.response?.data?.message || 'Erreur lors du chargement des statistiques';
  } finally {
    loading.value = false;
  }
};

const loadAnneesExercice = async () => {
  try {
    anneesExercice.value = await anneeExerciceService.getAllAnneesExercice();
  } catch (err: any) {
    console.error('Erreur lors du chargement des années d\'exercice', err);
  }
};

const loadClasses = async () => {
  try {
    classes.value = await classeService.getAllClasses();
  } catch (err: any) {
    console.error('Erreur lors du chargement des classes', err);
  }
};

const loadData = async () => {
  await Promise.all([loadAnneesExercice(), loadClasses()]);
  if (activeTab.value === 'enfants') {
    await loadStatistiquesEnfants();
  } else {
    await loadStatistiquesStaffs();
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.header-logo {
  height: 32px;
  margin-left: 8px;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--ion-color-light);
}

.stat-row:last-child {
  border-bottom: none;
}

.stat-value {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.rotate-up {
  transform: rotate(-90deg);
  transition: transform 0.3s ease;
}

.rotate-down {
  transform: rotate(0deg);
  transition: transform 0.3s ease;
}

ion-card {
  margin: 12px;
}

ion-badge {
  font-size: 14px;
  padding: 6px 12px;
}

ion-chip {
  font-size: 12px;
  height: 24px;
}
</style>
