<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <img src="/assets/logo.png" alt="Logo" class="header-logo" />
        </ion-buttons>
        <ion-title>Inscriptions</ion-title>
        <ion-buttons slot="end">
          <ion-button @click="showExportOptions" v-if="inscriptions.length > 0">
            <ion-icon :icon="documentTextOutline"></ion-icon>
          </ion-button>
          <ion-button @click="loadData" :disabled="loading">
            <ion-icon :icon="syncOutline"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
      
      <!-- Segment pour switcher entre Inscriptions et Parents -->
      <ion-toolbar>
        <ion-segment :value="activeTab" @ionChange="onTabChange($event)">
          <ion-segment-button value="inscriptions">
            <ion-label>Inscriptions</ion-label>
          </ion-segment-button>
          <ion-segment-button value="parents">
            <ion-label>Parents</ion-label>
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
      
      <ion-toast
        :is-open="!!success"
        :message="success || ''"
        :duration="2000"
        position="top"
        color="success"
        @didDismiss="success = null"
      ></ion-toast>

      <!-- Loading -->
      <div v-if="loading" class="ion-text-center ion-padding">
        <ion-spinner></ion-spinner>
        <p>Chargement...</p>
      </div>

      <!-- Tab Inscriptions -->
      <div v-else-if="activeTab === 'inscriptions'">
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
              <ion-label>Année</ion-label>
              <ion-select v-model="filterAnneeId" placeholder="Toutes">
                <ion-select-option :value="null">Toutes</ion-select-option>
                <ion-select-option v-for="annee in anneesExercice" :key="annee.id" :value="annee.id">
                  {{ new Date(annee.annee).getFullYear() }}
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
                <ion-select-option value="">Tous</ion-select-option>
                <ion-select-option value="GARCON">Garçon</ion-select-option>
                <ion-select-option value="FILLE">Fille</ion-select-option>
              </ion-select>
            </ion-item>
            
            <ion-item>
              <ion-label>Assurance</ion-label>
              <ion-select v-model="filterAssurance" placeholder="Toutes">
                <ion-select-option value="">Toutes</ion-select-option>
                <ion-select-option value="OUI">Oui</ion-select-option>
                <ion-select-option value="NON">Non</ion-select-option>
              </ion-select>
            </ion-item>
            
            <ion-button expand="block" @click="resetFilters" fill="outline">
              Réinitialiser
            </ion-button>
          </ion-card-content>
        </ion-card>

        <!-- Stats -->
        <ion-card>
          <ion-card-content>
            <ion-text color="primary">
              <h3>{{ filteredInscriptions.length }} inscription(s) trouvée(s)</h3>
            </ion-text>
          </ion-card-content>
        </ion-card>

        <!-- Liste des inscriptions -->
        <ion-card v-for="inscription in filteredInscriptions" :key="inscription.id">
          <ion-card-header>
            <ion-card-title>
              {{ inscription.enfantNom }} {{ inscription.enfantPrenom }}
            </ion-card-title>
            <ion-card-subtitle>
              <ion-chip :color="getClasseColor(inscription.classeNom)">
                {{ inscription.classeNom }}
              </ion-chip>
              <ion-chip>
                {{ inscription.enfantAge }} ans
              </ion-chip>
            </ion-card-subtitle>
          </ion-card-header>
          
          <ion-card-content>
            <ion-list>
              <ion-item>
                <ion-icon :icon="peopleOutline" slot="start"></ion-icon>
                <ion-label>
                  <p>Parent</p>
                  <h3>{{ inscription.parentNom }} {{ inscription.parentPrenom }}</h3>
                  <p v-if="inscription.parentTelephone">{{ inscription.parentTelephone }}</p>
                </ion-label>
              </ion-item>
              
              <ion-item>
                <ion-icon :icon="calendarOutline" slot="start"></ion-icon>
                <ion-label>
                  <p>Année</p>
                  <h3>{{ getAnneeDisplay(inscription.anneeExercice) }}</h3>
                </ion-label>
              </ion-item>
              
              <ion-item>
                <ion-label>Assurance</ion-label>
                <ion-toggle
                  :checked="inscription.estAssurance"
                  @ionChange="toggleAssurance(inscription)"
                  :disabled="!canModify()"
                ></ion-toggle>
              </ion-item>
            </ion-list>
            
            <!-- <ion-button
              v-if="canDelete()"
              expand="block"
              color="danger"
              fill="outline"
              @click="confirmDelete(inscription.id)"
            >
              <ion-icon :icon="trashOutline" slot="start"></ion-icon>
              Supprimer
            </ion-button> -->
          </ion-card-content>
        </ion-card>

        <!-- Empty state -->
        <div v-if="filteredInscriptions.length === 0" class="ion-text-center ion-padding">
          <ion-icon :icon="documentTextOutline" size="large" color="medium"></ion-icon>
          <p>Aucune inscription trouvée</p>
        </div>
      </div>

      <!-- Tab Parents -->
      <div v-else>
        <!-- Filtres Parents -->
        <ion-card>
          <ion-card-header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <ion-card-title>Filtres</ion-card-title>
              <ion-button fill="clear" @click="showParentFilters = !showParentFilters">
                <ion-icon :icon="chevronBackOutline" :class="{ 'rotate-up': showParentFilters, 'rotate-down': !showParentFilters }"></ion-icon>
              </ion-button>
            </div>
          </ion-card-header>
          <ion-card-content v-if="showParentFilters">
            <ion-item>
              <ion-label>Année</ion-label>
              <ion-select v-model="filterParentAnneeId" placeholder="Toutes">
                <ion-select-option :value="null">Toutes</ion-select-option>
                <ion-select-option v-for="annee in anneesExercice" :key="annee.id" :value="annee.id">
                  {{ new Date(annee.annee).getFullYear() }}
                </ion-select-option>
              </ion-select>
            </ion-item>
            
            <ion-item>
              <ion-label>Classe</ion-label>
              <ion-select v-model="filterParentClasseId" placeholder="Toutes">
                <ion-select-option :value="null">Toutes</ion-select-option>
                <ion-select-option v-for="classe in classes" :key="classe.id" :value="classe.id">
                  {{ classe.nom }}
                </ion-select-option>
              </ion-select>
            </ion-item>
            
            <ion-button expand="block" @click="resetParentFilters" fill="outline">
              Réinitialiser
            </ion-button>
          </ion-card-content>
        </ion-card>

        <!-- Stats Parents -->
        <ion-card>
          <ion-card-content>
            <ion-text color="primary">
              <h3>{{ filteredParents.length }} parent(s) trouvé(s)</h3>
            </ion-text>
          </ion-card-content>
        </ion-card>

        <!-- Liste des parents -->
        <ion-card v-for="parent in filteredParents" :key="parent.id">
          <ion-card-header>
            <ion-card-title>{{ parent.nom }} {{ parent.prenom }}</ion-card-title>
            <ion-card-subtitle>
              <ion-icon :icon="callOutline"></ion-icon>
              {{ parent.telephone }}
            </ion-card-subtitle>
          </ion-card-header>
          
          <ion-card-content>
            <ion-label>
              <h3>Explorateurs inscrits :</h3>
            </ion-label>
            <ion-list v-if="parentEnfants[parent.id] && parentEnfants[parent.id].length > 0">
              <ion-item v-for="enfant in parentEnfants[parent.id]" :key="enfant.id">
                <ion-label>
                  <h3>{{ enfant.nom }} {{ enfant.prenom }}</h3>
                  <p>{{ enfant.age }} ans - Né(e) le {{ formatDate(enfant.dateNaissance) }}</p>
                </ion-label>
              </ion-item>
            </ion-list>
            <p v-else class="ion-text-center">Aucun explorateur inscrit</p>
          </ion-card-content>
        </ion-card>

        <!-- Empty state -->
        <div v-if="filteredParents.length === 0" class="ion-text-center ion-padding">
          <ion-icon :icon="peopleOutline" size="large" color="medium"></ion-icon>
          <p>Aucun parent trouvé</p>
        </div>
      </div>

      <!-- Bouton flottant pour ajouter (position fixe) -->
      <ion-fab vertical="bottom" horizontal="end" slot="fixed" v-if="canModify() && activeTab === 'inscriptions'">
        <ion-fab-button @click="openInscriptionModal">
          <ion-icon :icon="addOutline"></ion-icon>
        </ion-fab-button>
      </ion-fab>
    </ion-content>

    <!-- Modal d'inscription -->
    <InscriptionModal
      :is-open="showInscriptionModal"
      :classes="classes"
      :annees-exercice="anneesExercice"
      @close="closeInscriptionModal"
      @success="handleInscriptionSuccess"
      @error="handleInscriptionError"
    />
  </ion-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonSegment,
  IonSegmentButton,
  IonLabel,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardSubtitle,
  IonCardContent,
  IonItem,
  IonList,
  IonSelect,
  IonSelectOption,
  IonButton,
  IonButtons,
  IonIcon,
  IonChip,
  IonToggle,
  IonFab,
  IonFabButton,
  IonSpinner,
  IonToast,
  IonText,
  alertController,
  actionSheetController,
} from '@ionic/vue';
import {
  documentTextOutline,
  syncOutline,
  addOutline,
  trashOutline,
  peopleOutline,
  calendarOutline,
  callOutline,
  chevronBackOutline,
} from 'ionicons/icons';
import { useAuthStore } from '@/stores/auth.store';
import inscriptionService from '@/services/inscription.service';
import parentService from '@/services/parent.service';
import enfantService from '@/services/enfant.service';
import classeService from '@/services/classe.service';
import anneeExerciceService from '@/services/annee-exercice.service';
import InscriptionModal from '@/components/InscriptionModal.vue';
import type {
  InscriptionResponse,
  Parent,
  EnfantResponse,
  Classe,
  AnneeExercice,
} from '@/types';

// State
const authStore = useAuthStore();
const activeTab = ref<'inscriptions' | 'parents'>('inscriptions');
const loading = ref(true);
const error = ref<string | null>(null);
const success = ref<string | null>(null);
const showInscriptionModal = ref(false);
const showFilters = ref(true);
const showParentFilters = ref(true);

// Data
const inscriptions = ref<InscriptionResponse[]>([]);
const parents = ref<Parent[]>([]);
const parentEnfants = ref<Record<number, EnfantResponse[]>>({});
const classes = ref<Classe[]>([]);
const anneesExercice = ref<AnneeExercice[]>([]);

// Filtres Inscriptions
const filterAnneeId = ref<number | null>(null);
const filterClasseId = ref<number | null>(null);
const filterGenre = ref<string>('');
const filterAssurance = ref<string>('');

// Filtres Parents
const filterParentAnneeId = ref<number | null>(null);
const filterParentClasseId = ref<number | null>(null);

// Computed
const filteredInscriptions = computed(() => {
  let filtered = [...inscriptions.value];

  if (filterAnneeId.value) {
    filtered = filtered.filter(i => i.anneeExerciceId === filterAnneeId.value);
  }

  if (filterClasseId.value) {
    filtered = filtered.filter(i => i.classeId === filterClasseId.value);
  }

  if (filterGenre.value) {
    filtered = filtered.filter(i => i.enfantGenre === filterGenre.value);
  }

  if (filterAssurance.value) {
    const hasAssurance = filterAssurance.value === 'OUI';
    filtered = filtered.filter(i => i.estAssurance === hasAssurance);
  }

  return filtered;
});

const filteredParents = computed(() => {
  let filtered = [...parents.value];

  if (filterParentAnneeId.value) {
    filtered = filtered.filter(parent => {
      const enfants = parentEnfants.value[parent.id] || [];
      return enfants.some(enfant => {
        return inscriptions.value.some(i =>
          i.enfantId === enfant.id && i.anneeExerciceId === filterParentAnneeId.value
        );
      });
    });
  }

  if (filterParentClasseId.value) {
    filtered = filtered.filter(parent => {
      const enfants = parentEnfants.value[parent.id] || [];
      return enfants.some(enfant => {
        return inscriptions.value.some(i =>
          i.enfantId === enfant.id && i.classeId === filterParentClasseId.value
        );
      });
    });
  }

  return filtered;
});

// Methods
const loadData = async () => {
  try {
    loading.value = true;
    error.value = null;

    const [inscriptionsData, classesData, anneesData] = await Promise.all([
      inscriptionService.getAllInscriptions(),
      classeService.getAllClasses(),
      anneeExerciceService.getAllAnneesExercice(),
    ]);

    inscriptions.value = inscriptionsData;
    classes.value = classesData;
    anneesExercice.value = anneesData;

    // Charger les parents si on est sur l'onglet parents
    if (activeTab.value === 'parents') {
      await loadParents();
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors du chargement des données';
    console.error('Erreur:', err);
  } finally {
    loading.value = false;
  }
};

const loadParents = async () => {
  try {
    const parentsData = await parentService.getAllParents();
    parents.value = parentsData;

    // Charger les enfants de chaque parent
    const enfantsMap: Record<number, EnfantResponse[]> = {};
    await Promise.all(
      parentsData.map(async (parent) => {
        const enfants = await enfantService.getEnfantsByParentId(parent.id);
        enfantsMap[parent.id] = enfants;
      })
    );
    parentEnfants.value = enfantsMap;
  } catch (err: any) {
    error.value = 'Erreur lors du chargement des parents';
    console.error('Erreur:', err);
  }
};

const onTabChange = (event: CustomEvent) => {
  activeTab.value = event.detail.value;
  if (activeTab.value === 'parents' && parents.value.length === 0) {
    loadParents();
  }
};

const resetFilters = () => {
  filterAnneeId.value = null;
  filterClasseId.value = null;
  filterGenre.value = '';
  filterAssurance.value = '';
};

const resetParentFilters = () => {
  filterParentAnneeId.value = null;
  filterParentClasseId.value = null;
};

const toggleAssurance = async (inscription: InscriptionResponse) => {
  try {
    await inscriptionService.updateAssurance(inscription.id, !inscription.estAssurance);
    inscription.estAssurance = !inscription.estAssurance;
    success.value = 'Statut d\'assurance mis à jour';
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors de la mise à jour';
  }
};

const confirmDelete = async (id: number) => {
  const alert = await alertController.create({
    header: 'Confirmation',
    message: 'Voulez-vous vraiment supprimer cette inscription ?',
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel',
      },
      {
        text: 'Supprimer',
        role: 'destructive',
        handler: () => deleteInscription(id),
      },
    ],
  });

  await alert.present();
};

const deleteInscription = async (id: number) => {
  try {
    await inscriptionService.deleteInscription(id);
    inscriptions.value = inscriptions.value.filter(i => i.id !== id);
    success.value = 'Inscription supprimée avec succès';
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors de la suppression';
  }
};

const showExportOptions = async () => {
  const actionSheet = await actionSheetController.create({
    header: 'Exporter en PDF',
    buttons: [
      {
        text: 'Exporter avec filtres actuels',
        icon: documentTextOutline,
        handler: () => exportPdf(),
      },
      {
        text: 'Exporter tout',
        icon: documentTextOutline,
        handler: () => exportPdf(true),
      },
      {
        text: 'Annuler',
        role: 'cancel',
      },
    ],
  });

  await actionSheet.present();
};

const exportPdf = async (exportAll = false) => {
  try {
    if (exportAll) {
      await inscriptionService.exportToPdf();
    } else {
      await inscriptionService.exportToPdf(
        filterAnneeId.value || undefined,
        filterClasseId.value || undefined,
        filterGenre.value || undefined,
        filterAssurance.value ? filterAssurance.value === 'OUI' : undefined
      );
    }
    success.value = 'Export PDF en cours...';
  } catch (err: any) {
    error.value = 'Erreur lors de l\'export PDF';
  }
};

const openInscriptionModal = () => {
  showInscriptionModal.value = true;
};

const closeInscriptionModal = () => {
  showInscriptionModal.value = false;
};

const handleInscriptionSuccess = (message: string) => {
  success.value = message;
  loadData();
};

const handleInscriptionError = (message: string) => {
  error.value = message;
};

const canModify = () => {
  return authStore.user?.role === 'Directeur' || authStore.user?.role === 'Co_Directeur';
};

const canDelete = () => {
  return authStore.user?.role === 'Directeur';
};

const getClasseColor = (classeNom: string): string => {
  const nom = classeNom.toLowerCase();
  if (nom.includes('ami')) return 'primary';
  if (nom.includes('compagnon')) return 'danger';
  if (nom.includes('eclaireur') || nom.includes('éclaireur')) return 'success';
  if (nom.includes('pionnier')) return 'medium';
  if (nom.includes('voyageur')) return 'warning';
  if (nom.includes('guide')) return 'tertiary';
  return 'secondary';
};

const getAnneeDisplay = (annee: string): string => {
  return annee.substring(0, 4);
};

const formatDate = (date: string): string => {
  return new Date(date).toLocaleDateString('fr-FR');
};

// Lifecycle
onMounted(() => {
  loadData();
});

// Watch pour recharger les parents quand on filtre
watch([filterParentAnneeId, filterParentClasseId], () => {
  // Les filtres sont appliqués via computed
});
</script>

<style scoped>
ion-content {
  --background: transparent;
}

ion-card {
  margin: 0.5rem;
}

ion-card-title {
  font-size: 0.9375rem !important;
  font-weight: 600;
}

ion-card-subtitle {
  font-size: 0.8125rem !important;
}

ion-card-content h3 {
  font-size: 0.875rem !important;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

ion-chip {
  margin: 0.25rem 0.25rem 0.25rem 0;
  font-size: 0.75rem !important;
  height: 22px;
}

ion-item ion-label h3 {
  font-size: 0.875rem !important;
}

ion-item ion-label p {
  font-size: 0.75rem !important;
}

.stats-card h3 {
  font-size: 1.125rem !important;
  font-weight: 600;
  margin: 0;
}

/* Boutons compacts */
ion-button {
  --padding-start: 0.5rem;
  --padding-end: 0.5rem;
  font-size: 0.875rem !important;
}

/* Filtres plus compacts */
.filter-section ion-card-content {
  padding: 0.75rem;
}

/* Rotation des icônes pour les filtres collapsibles */
.rotate-up {
  transform: rotate(-90deg);
  transition: transform 0.3s ease;
}

.rotate-down {
  transform: rotate(90deg);
  transition: transform 0.3s ease;
}
</style>
