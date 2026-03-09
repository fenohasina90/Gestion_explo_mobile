<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button default-href="/tabs/home"></ion-back-button>
          <img src="/assets/logo.png" alt="Logo" class="header-logo" style="margin-left: 8px;" />
        </ion-buttons>
        <ion-title>Mouvements Budgétaires</ion-title>
        <ion-buttons slot="end">
          <ion-button v-if="isDirecteur" @click="openCreateModal">
            <ion-icon :icon="addOutline"></ion-icon>
          </ion-button>
          <ion-button @click="toggleFilters">
            <ion-icon :icon="filterOutline"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-refresher slot="fixed" @ionRefresh="handleRefresh($event)">
        <ion-refresher-content></ion-refresher-content>
      </ion-refresher>

      <!-- État de caisse -->
      <div v-if="etatCaisse" class="etat-caisse-container">
        <ion-card class="etat-card recettes">
          <ion-card-content>
            <div class="etat-icon">💵</div>
            <div class="etat-label">Total Recettes</div>
            <div class="etat-value">{{ formatMontant(etatCaisse.totalRecettes) }}</div>
          </ion-card-content>
        </ion-card>

        <ion-card class="etat-card depenses">
          <ion-card-content>
            <div class="etat-icon">💸</div>
            <div class="etat-label">Total Dépenses</div>
            <div class="etat-value">{{ formatMontant(etatCaisse.totalDepenses) }}</div>
          </ion-card-content>
        </ion-card>

        <ion-card :class="['etat-card', 'solde', etatCaisse.solde >= 0 ? 'positif' : 'negatif']">
          <ion-card-content>
            <div class="etat-icon">{{ etatCaisse.solde >= 0 ? '✅' : '⚠️' }}</div>
            <div class="etat-label">Solde</div>
            <div class="etat-value">{{ formatMontant(etatCaisse.solde) }}</div>
          </ion-card-content>
        </ion-card>
      </div>

      <!-- Filtres -->
      <ion-card v-if="showFilters" class="filters-card">
        <ion-card-header>
          <ion-card-title>Filtres</ion-card-title>
        </ion-card-header>
        <ion-card-content>
          <ion-item>
            <ion-label position="stacked">Recherche</ion-label>
            <ion-input 
              v-model="filters.recherche" 
              placeholder="Description..."
              @ionInput="applyFilters"
            ></ion-input>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">Date de début</ion-label>
            <ion-input 
              v-model="filters.dateDebut" 
              type="date"
              @ionChange="applyFilters"
            ></ion-input>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">Date de fin</ion-label>
            <ion-input 
              v-model="filters.dateFin" 
              type="date"
              @ionChange="applyFilters"
            ></ion-input>
          </ion-item>

          <ion-item>
            <ion-label>Type de mouvement</ion-label>
            <ion-select 
              v-model="filters.typeId" 
              @ionChange="applyFilters"
            >
              <ion-select-option :value="undefined">Tous les types</ion-select-option>
              <ion-select-option v-for="type in types" :key="type.id" :value="type.id">
                {{ type.type }}
              </ion-select-option>
            </ion-select>
          </ion-item>

          <ion-item>
            <ion-label>Année d'exercice</ion-label>
            <ion-select 
              v-model="filters.anneeExerciceId" 
              @ionChange="applyFilters"
            >
              <ion-select-option :value="undefined">Toutes les années</ion-select-option>
              <ion-select-option v-for="annee in annees" :key="annee.id" :value="annee.id">
                {{ new Date(annee.annee).getFullYear() }}
              </ion-select-option>
            </ion-select>
          </ion-item>

          <ion-button expand="block" @click="resetFilters" fill="outline" class="ion-margin-top">
            Réinitialiser
          </ion-button>
        </ion-card-content>
      </ion-card>

      <!-- Résumé pagination -->
      <div v-if="mouvementsPage" class="ion-padding-horizontal">
        <ion-note>
          {{ mouvementsPage.totalElements }} mouvement(s) trouvé(s)
        </ion-note>
      </div>

      <!-- Liste des mouvements -->
      <ion-list v-if="!loading">
        <ion-item 
          v-for="mouvement in mouvements" 
          :key="mouvement.id"
        >
          <ion-label>
            <h2>
              <ion-badge :color="mouvement.type.type === 'RECETTE' ? 'success' : 'danger'">
                {{ mouvement.type.type }}
              </ion-badge>
              <span style="margin-left: 8px;">{{ formatMontant(mouvement.montant) }}</span>
            </h2>
            <p>{{ mouvement.description || 'Sans description' }}</p>
            <p>
              <ion-note>{{ formatDate(mouvement.createdAt) }}</ion-note>
            </p>
          </ion-label>
          <ion-buttons slot="end">
            <ion-button 
              v-if="isDirecteurOrCo"
              @click.stop="openEditModal(mouvement)"
            >
              <ion-icon :icon="createOutline"></ion-icon>
            </ion-button>
            <ion-button 
              v-if="isDirecteur"
              color="danger" 
              @click.stop="confirmDelete(mouvement.id)"
            >
              <ion-icon :icon="trashOutline"></ion-icon>
            </ion-button>
          </ion-buttons>
        </ion-item>

        <!-- Message si aucun mouvement -->
        <div v-if="mouvements.length === 0" class="ion-padding ion-text-center">
          <ion-note>Aucun mouvement budgétaire trouvé</ion-note>
        </div>
      </ion-list>

      <!-- Skeleton lors du chargement -->
      <ion-list v-else>
        <ion-item v-for="i in 5" :key="i">
          <ion-label>
            <ion-skeleton-text animated style="width: 80%"></ion-skeleton-text>
            <ion-skeleton-text animated style="width: 60%"></ion-skeleton-text>
          </ion-label>
        </ion-item>
      </ion-list>

      <!-- Pagination -->
      <div v-if="mouvementsPage && mouvementsPage.totalPages > 1" class="pagination-container">
        <ion-button 
          :disabled="mouvementsPage.first" 
          @click="goToPage(0)"
          fill="outline"
          size="small"
        >
          <ion-icon :icon="arrowBackOutline"></ion-icon>
        </ion-button>
        
        <ion-button 
          :disabled="!mouvementsPage.hasPrevious" 
          @click="goToPage(currentPage - 1)"
          fill="outline"
          size="small"
        >
          <ion-icon :icon="chevronBackOutline"></ion-icon>
        </ion-button>
        
        <ion-chip>
          Page {{ mouvementsPage.page + 1 }} / {{ mouvementsPage.totalPages }}
        </ion-chip>
        
        <ion-button 
          :disabled="!mouvementsPage.hasNext" 
          @click="goToPage(currentPage + 1)"
          fill="outline"
          size="small"
        >
          <ion-icon :icon="chevronForwardOutline"></ion-icon>
        </ion-button>
        
        <ion-button 
          :disabled="mouvementsPage.last" 
          @click="goToPage(mouvementsPage.totalPages - 1)"
          fill="outline"
          size="small"
        >
          <ion-icon :icon="arrowForwardOutline"></ion-icon>
        </ion-button>
      </div>

      <!-- Sélection taille de page -->
      <div v-if="mouvementsPage" class="ion-padding">
        <ion-item>
          <ion-label>Éléments par page</ion-label>
          <ion-select v-model="pageSize" @ionChange="changePageSize">
            <ion-select-option :value="5">5</ion-select-option>
            <ion-select-option :value="10">10</ion-select-option>
            <ion-select-option :value="20">20</ion-select-option>
            <ion-select-option :value="50">50</ion-select-option>
          </ion-select>
        </ion-item>
      </div>
    </ion-content>

    <!-- Modal de création/modification -->
    <ion-modal :is-open="showModal" @didDismiss="closeModal">
      <ion-page>
        <ion-header>
          <ion-toolbar>
            <ion-title>{{ isEditMode ? 'Modifier Mouvement' : 'Nouveau Mouvement' }}</ion-title>
            <ion-buttons slot="end">
              <ion-button @click="closeModal">Fermer</ion-button>
            </ion-buttons>
          </ion-toolbar>
        </ion-header>
        <ion-content class="ion-padding">
          <ion-list>
            <ion-item>
              <ion-label position="stacked">Type de mouvement *</ion-label>
              <ion-select v-model="formData.typeId" placeholder="Sélectionner le type">
                <ion-select-option :value="1">Recette</ion-select-option>
                <ion-select-option :value="2">Dépense</ion-select-option>
              </ion-select>
            </ion-item>

            <ion-item>
              <ion-label position="stacked">Montant (Ar) *</ion-label>
              <ion-input 
                v-model="formData.montant" 
                type="number" 
                placeholder="Entrer le montant"
                min="0"
              ></ion-input>
            </ion-item>

            <ion-item>
              <ion-label position="stacked">Description</ion-label>
              <ion-textarea 
                v-model="formData.description" 
                placeholder="Ajouter une description (optionnel)"
                :rows="4"
              ></ion-textarea>
            </ion-item>
          </ion-list>

          <div class="ion-padding">
            <ion-button expand="block" @click="submitForm" :disabled="!isFormValid">
              {{ isEditMode ? 'Modifier' : 'Créer' }}
            </ion-button>
          </div>
        </ion-content>
      </ion-page>
    </ion-modal>
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
  IonInput,
  IonSelect,
  IonSelectOption,
  IonRefresher,
  IonRefresherContent,
  IonSkeletonText,
  IonNote,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardContent,
  IonBadge,
  IonChip,
  IonModal,
  IonTextarea,
  alertController,
  toastController
} from '@ionic/vue';
import { 
  addOutline, 
  createOutline, 
  trashOutline, 
  filterOutline,
  chevronBackOutline,
  chevronForwardOutline,
  arrowBackOutline,
  arrowForwardOutline
} from 'ionicons/icons';
import { useAuthStore } from '@/stores/auth.store';
import mouvementBudgetaireService from '@/services/mouvement-budgetaire.service';
import anneeExerciceService from '@/services/annee-exercice.service';
import type { 
  MouvementBudgetaire, 
  MouvementBudgetaireFilterRequest,
  EtatCaisse,
  TypeMouvement,
  AnneeExercice,
  PageResponse
} from '@/types';

const authStore = useAuthStore();
const loading = ref(false);
const mouvements = ref<MouvementBudgetaire[]>([]);
const mouvementsPage = ref<PageResponse<MouvementBudgetaire> | null>(null);
const etatCaisse = ref<EtatCaisse | null>(null);
const types = ref<TypeMouvement[]>([]);
const annees = ref<AnneeExercice[]>([]);
const showFilters = ref(false);

// Modal et formulaire
const showModal = ref(false);
const isEditMode = ref(false);
const editingId = ref<number | null>(null);
const formData = ref({
  typeId: 1,
  montant: 0,
  description: ''
});

// Pagination
const currentPage = ref(0);
const pageSize = ref(10);

// Filtres
const filters = ref<MouvementBudgetaireFilterRequest>({
  recherche: '',
  dateDebut: '',
  dateFin: '',
  typeId: undefined,
  anneeExerciceId: undefined,
});

const isDirecteur = computed(() => authStore.user?.role === 'Directeur');
const isDirecteurOrCo = computed(() => {
  const role = authStore.user?.role;
  return role === 'Directeur' || role === 'Co_Directeur';
});

const isFormValid = computed(() => {
  return formData.value.typeId > 0 && formData.value.montant > 0;
});

onMounted(() => {
  loadData();
  loadTypes();
  loadAnnees();
});

async function loadData() {
  try {
    loading.value = true;
    const [mouvementsData, etatData] = await Promise.all([
      mouvementBudgetaireService.getMouvementsWithFiltersPaginated(
        filters.value, 
        currentPage.value, 
        pageSize.value, 
        'createdAt', 
        'desc'
      ),
      mouvementBudgetaireService.getEtatCaisse(filters.value.anneeExerciceId),
    ]);
    mouvementsPage.value = mouvementsData;
    mouvements.value = mouvementsData.content;
    etatCaisse.value = etatData;
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors du chargement des données',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  } finally {
    loading.value = false;
  }
}

async function loadTypes() {
  try {
    types.value = await mouvementBudgetaireService.getAllTypes();
  } catch (error: any) {
    console.error('Erreur lors du chargement des types', error);
  }
}

async function loadAnnees() {
  try {
    annees.value = await anneeExerciceService.getAllAnneesExercice();
  } catch (error: any) {
    console.error('Erreur lors du chargement des années', error);
  }
}

async function handleRefresh(event: any) {
  await loadData();
  event.target.complete();
}

function toggleFilters() {
  showFilters.value = !showFilters.value;
}

async function applyFilters() {
  currentPage.value = 0;
  await loadData();
}

async function resetFilters() {
  filters.value = {
    recherche: '',
    dateDebut: '',
    dateFin: '',
    typeId: undefined,
    anneeExerciceId: undefined,
  };
  currentPage.value = 0;
  await loadData();
}

async function goToPage(page: number) {
  currentPage.value = page;
  await loadData();
}

async function changePageSize() {
  currentPage.value = 0;
  await loadData();
}

async function openCreateModal() {
  isEditMode.value = false;
  editingId.value = null;
  formData.value = {
    typeId: 1,
    montant: 0,
    description: ''
  };
  showModal.value = true;
}

async function openEditModal(mouvement: MouvementBudgetaire) {
  isEditMode.value = true;
  editingId.value = mouvement.id;
  formData.value = {
    typeId: mouvement.type.id,
    montant: mouvement.montant,
    description: mouvement.description || ''
  };
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
  formData.value = {
    typeId: 1,
    montant: 0,
    description: ''
  };
}

async function submitForm() {
  if (!isFormValid.value) {
    const toast = await toastController.create({
      message: 'Type et montant sont requis',
      duration: 2000,
      color: 'warning'
    });
    await toast.present();
    return;
  }

  if (isEditMode.value && editingId.value) {
    await updateMouvement(editingId.value, formData.value);
  } else {
    await createMouvement(formData.value);
  }
  
  closeModal();
}

async function createMouvement(data: any) {
  try {
    await mouvementBudgetaireService.createMouvement({
      typeId: parseInt(data.typeId),
      montant: parseFloat(data.montant),
      description: data.description
    });
    const toast = await toastController.create({
      message: 'Mouvement créé avec succès',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadData();
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors de la création',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  }
}

async function updateMouvement(id: number, data: any) {
  try {
    await mouvementBudgetaireService.updateMouvement(id, {
      typeId: parseInt(data.typeId),
      montant: parseFloat(data.montant),
      description: data.description
    });
    const toast = await toastController.create({
      message: 'Mouvement modifié avec succès',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadData();
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors de la modification',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  }
}

async function confirmDelete(id: number) {
  const alert = await alertController.create({
    header: 'Confirmer la suppression',
    message: 'Voulez-vous vraiment supprimer ce mouvement budgétaire ?',
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Supprimer',
        role: 'destructive',
        handler: () => deleteMouvement(id)
      }
    ]
  });
  await alert.present();
}

async function deleteMouvement(id: number) {
  try {
    await mouvementBudgetaireService.deleteMouvement(id);
    const toast = await toastController.create({
      message: 'Mouvement supprimé avec succès',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadData();
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors de la suppression',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  }
}

function formatMontant(montant: number): string {
  return new Intl.NumberFormat('fr-MG', {
    style: 'currency',
    currency: 'MGA',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(montant);
}

function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}
</script>

<style scoped>
.header-logo {
  height: 32px;
  width: auto;
}

.etat-caisse-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 8px;
  padding: 16px;
}

.etat-card {
  margin: 0;
}

.etat-card.recettes {
  border-left: 4px solid var(--ion-color-success);
}

.etat-card.depenses {
  border-left: 4px solid var(--ion-color-danger);
}

.etat-card.solde.positif {
  border-left: 4px solid var(--ion-color-success);
}

.etat-card.solde.negatif {
  border-left: 4px solid var(--ion-color-danger);
}

.etat-card ion-card-content {
  padding: 12px;
  text-align: center;
}

.etat-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.etat-label {
  font-size: 11px;
  color: var(--ion-color-medium);
  margin-bottom: 4px;
}

.etat-value {
  font-size: 16px;
  font-weight: bold;
  color: var(--ion-color-primary);
}

.filters-card {
  margin: 8px 16px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  padding: 16px;
}
</style>
