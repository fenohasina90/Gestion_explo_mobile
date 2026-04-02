<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <!-- <ion-back-button default-href="/tabs/home"></ion-back-button> -->
          <img src="/assets/logo.png" alt="Logo" class="header-logo" style="margin-left: 8px;" />
        </ion-buttons>
        <ion-title>Classes Progressives</ion-title>
        <ion-buttons slot="end">
          <ion-button @click="showFilters = !showFilters">
            <ion-icon :icon="filterOutline"></ion-icon>
          </ion-button>
          <ion-button v-if="canModify" @click="openCreateModal">
            <ion-icon :icon="addOutline"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-refresher slot="fixed" @ionRefresh="handleRefresh($event)">
        <ion-refresher-content></ion-refresher-content>
      </ion-refresher>

      <!-- Filtres -->
      <div v-if="showFilters" class="ion-padding-horizontal ion-margin-top">
        <ion-item>
          <ion-label>Date début</ion-label>
          <ion-datetime-button datetime="dateDebut"></ion-datetime-button>
          <ion-modal :keep-contents-mounted="true">
            <ion-datetime 
              id="dateDebut" 
              v-model="filterDateDebut" 
              presentation="date"
            ></ion-datetime>
          </ion-modal>
        </ion-item>

        <ion-item>
          <ion-label>Date fin</ion-label>
          <ion-datetime-button datetime="dateFin"></ion-datetime-button>
          <ion-modal :keep-contents-mounted="true">
            <ion-datetime 
              id="dateFin" 
              v-model="filterDateFin" 
              presentation="date"
            ></ion-datetime>
          </ion-modal>
        </ion-item>

        <ion-button expand="block" @click="applyFilters" class="ion-margin">
          Filtrer
        </ion-button>

        <p v-if="filteredCPs.length > 0" class="ion-padding-start ion-text-sm">
          {{ filteredCPs.length }} CP(s)
        </p>
      </div>

      <!-- Liste des CPs -->
      <ion-list v-if="!loading">
        <ion-item 
          v-for="cp in filteredCPs" 
          :key="cp.id"
          @click="goToCPDetails(cp.id)"
          button
        >
          <ion-label>
            <h2>{{ formatDate(cp.dateCp) }}</h2>
            <p class="ion-text-wrap" style="font-size: 0.85rem; color: var(--ion-color-medium);">
              {{ cp.heureDebut }} - {{ cp.heureFin }}
              <span v-if="cp.niveau"> • {{ cp.niveau }}</span>
            </p>
            <p style="font-size: 0.8rem; color: var(--ion-color-medium-shade);">
              {{ cp.nombreProgrammes }} programme(s) • {{ getYear(cp.anneeExercice) }}
              <ion-badge 
                :color="cp.etat === 1 ? 'danger' : 'success'" 
                style="margin-left: 8px; font-size: 0.7rem;"
              >
                {{ cp.etat === 1 ? '🔒 Clôturée' : '🔓 Ouverte' }}
              </ion-badge>
            </p>
          </ion-label>
          <ion-buttons slot="end">
            <ion-button 
              v-if="canModify"
              @click.stop="openEditModal(cp)"
            >
              <ion-icon :icon="createOutline"></ion-icon>
            </ion-button>
            <ion-button 
              v-if="canModify && cp.etat !== 1"
              color="warning" 
              @click.stop="confirmCloturer(cp)"
            >
              <ion-icon :icon="closeCircleOutline"></ion-icon>
            </ion-button>
            <ion-button 
              v-if="canDelete"
              color="danger" 
              @click.stop="confirmDelete(cp.id)"
            >
              <ion-icon :icon="trashOutline"></ion-icon>
            </ion-button>
            <ion-button>
              <ion-icon :icon="chevronForwardOutline"></ion-icon>
            </ion-button>
          </ion-buttons>
        </ion-item>

        <!-- Message si aucune CP -->
        <div v-if="filteredCPs.length === 0" class="ion-padding ion-text-center">
          <ion-note>Aucune classe progressive trouvée</ion-note>
        </div>
      </ion-list>

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
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
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
  IonBadge,
  IonRefresher,
  IonRefresherContent,
  IonDatetime,
  IonDatetimeButton,
  IonModal,
  IonSkeletonText,
  IonNote,
  alertController,
  toastController
} from '@ionic/vue';
import { addOutline, createOutline, trashOutline, filterOutline, chevronForwardOutline, closeCircleOutline } from 'ionicons/icons';
import { useAuthStore } from '@/stores/auth.store';
import classeProgressiveService from '@/services/classe-progressive.service';
import type { ClasseProgressive } from '@/types';

const router = useRouter();
const authStore = useAuthStore();
const loading = ref(false);
const cps = ref<ClasseProgressive[]>([]);
const filteredCPs = ref<ClasseProgressive[]>([]);
const showFilters = ref(false);
const filterDateDebut = ref<string | null>(null);
const filterDateFin = ref<string | null>(null);

const canModify = computed(() => {
  const role = authStore.user?.role;
  return role === 'Directeur' || role === 'Co_Directeur';
});

const canDelete = computed(() => authStore.user?.role === 'Directeur');

onMounted(() => {
  loadCPs();
});

async function loadCPs() {
  try {
    loading.value = true;
    cps.value = await classeProgressiveService.getAllCP();
    filteredCPs.value = cps.value;
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors du chargement des CPs',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  } finally {
    loading.value = false;
  }
}

async function applyFilters() {
  try {
    loading.value = true;
    const filters: any = {};
    if (filterDateDebut.value) filters.dateDebut = filterDateDebut.value.split('T')[0];
    if (filterDateFin.value) filters.dateFin = filterDateFin.value.split('T')[0];
    if (authStore.user?.anneeExerciceId) filters.anneeExerciceId = authStore.user.anneeExerciceId;
    
    filteredCPs.value = await classeProgressiveService.filterCP(filters);
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
}

async function handleRefresh(event: any) {
  await loadCPs();
  event.target.complete();
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return date.toLocaleDateString('fr-FR', { 
    weekday: 'long', 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  });
}

function getYear(anneeStr: string): string {
  return new Date(anneeStr).getFullYear().toString();
}

function goToCPDetails(cpId: number) {
  router.push(`/tabs/cp-details/${cpId}`);
}

async function openCreateModal() {
  const alert = await alertController.create({
    header: 'Nouvelle CP',
    inputs: [
      {
        name: 'dateCp',
        type: 'date',
        placeholder: 'Date'
      },
      {
        name: 'heureDebut',
        type: 'time',
        placeholder: 'Heure début'
      },
      {
        name: 'heureFin',
        type: 'time',
        placeholder: 'Heure fin'
      },
      {
        name: 'niveau',
        type: 'text',
        placeholder: 'Niveau (optionnel)'
      }
    ],
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Créer',
        handler: async (data) => {
          if (!data.dateCp || !data.heureDebut || !data.heureFin) {
            const toast = await toastController.create({
              message: 'Tous les champs obligatoires doivent être remplis',
              duration: 2000,
              color: 'warning'
            });
            await toast.present();
            return false;
          }
          await createCP(data);
          return true;
        }
      }
    ]
  });
  await alert.present();
}

async function createCP(data: any) {
  try {
    const request = {
      dateCp: data.dateCp,
      heureDebut: data.heureDebut,
      heureFin: data.heureFin,
      niveau: data.niveau || undefined,
      anneeExerciceId: authStore.user!.anneeExerciceId
    };
    
    await classeProgressiveService.createCP(request);
    const toast = await toastController.create({
      message: 'CP créée avec succès',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadCPs();
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors de la création',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  }
}

async function openEditModal(cp: ClasseProgressive) {
  const alert = await alertController.create({
    header: 'Modifier CP',
    inputs: [
      {
        name: 'dateCp',
        type: 'date',
        value: cp.dateCp
      },
      {
        name: 'heureDebut',
        type: 'time',
        value: cp.heureDebut
      },
      {
        name: 'heureFin',
        type: 'time',
        value: cp.heureFin
      },
      {
        name: 'niveau',
        type: 'text',
        value: cp.niveau,
        placeholder: 'Niveau (optionnel)'
      }
    ],
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Modifier',
        handler: async (data) => {
          if (!data.dateCp || !data.heureDebut || !data.heureFin) {
            const toast = await toastController.create({
              message: 'Tous les champs obligatoires doivent être remplis',
              duration: 2000,
              color: 'warning'
            });
            await toast.present();
            return false;
          }
          await updateCP(cp.id, data);
          return true;
        }
      }
    ]
  });
  await alert.present();
}

async function updateCP(id: number, data: any) {
  try {
    const request = {
      dateCp: data.dateCp,
      heureDebut: data.heureDebut,
      heureFin: data.heureFin,
      niveau: data.niveau || undefined,
      anneeExerciceId: authStore.user!.anneeExerciceId
    };
    
    await classeProgressiveService.updateCP(id, request);
    const toast = await toastController.create({
      message: 'CP modifiée avec succès',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadCPs();
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
    message: 'Voulez-vous vraiment supprimer cette CP ?',
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Supprimer',
        role: 'destructive',
        handler: () => deleteCP(id)
      }
    ]
  });
  await alert.present();
}

async function deleteCP(id: number) {
  try {
    await classeProgressiveService.deleteCP(id);
    const toast = await toastController.create({
      message: 'CP supprimée avec succès',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadCPs();
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors de la suppression',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  }
}

async function confirmCloturer(cp: ClasseProgressive) {
  if (cp.etat === 1) {
    const toast = await toastController.create({
      message: 'Cette CP est déjà clôturée',
      duration: 2000,
      color: 'warning'
    });
    await toast.present();
    return;
  }

  const alert = await alertController.create({
    header: 'Clôturer la CP',
    message: 'Voulez-vous clôturer cette CP ? Une fois clôturée, vous ne pourrez plus modifier les présences ni les statuts des programmes.',
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Clôturer',
        role: 'destructive',
        handler: () => cloturerCP(cp.id)
      }
    ]
  });
  await alert.present();
}

async function cloturerCP(id: number) {
  try {
    await classeProgressiveService.cloturerCP(id);
    const toast = await toastController.create({
      message: 'CP clôturée avec succès',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadCPs();
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors de la clôture',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  }
}
</script>

<style scoped>
.header-logo {
  height: 32px;
  width: auto;
}
</style>
