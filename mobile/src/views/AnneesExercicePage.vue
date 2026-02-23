<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button default-href="/tabs/home"></ion-back-button>
        </ion-buttons>
        <ion-title>Années d'Exercice</ion-title>
        <ion-buttons slot="end">
          <ion-button @click="openCreateModal">
            <ion-icon :icon="addOutline"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-refresher slot="fixed" @ionRefresh="handleRefresh($event)">
        <ion-refresher-content></ion-refresher-content>
      </ion-refresher>

      <!-- Liste des années d'exercice -->
      <ion-list v-if="!loading">
        <ion-card v-for="annee in anneesExercice" :key="annee.id" class="annee-card">
          <ion-card-header>
            <div class="card-header-content">
              <ion-card-title>
                {{ new Date(annee.annee).getFullYear() }}
              </ion-card-title>
              <ion-button 
                fill="clear" 
                color="danger" 
                @click="confirmDelete(annee.id)"
                size="small"
              >
                <ion-icon :icon="trashOutline"></ion-icon>
              </ion-button>
            </div>
            <ion-badge v-if="isExpired(annee)" color="warning">
              Expirée
            </ion-badge>
          </ion-card-header>

          <ion-card-content>
            <ion-list lines="none">
              <ion-item>
                <ion-label>
                  <p class="label-text">Période</p>
                  <h3>
                    {{ formatDate(annee.annee) }} - {{ formatDate(annee.dateFin) }}
                  </h3>
                </ion-label>
              </ion-item>
              <ion-item>
                <ion-label>
                  <p class="label-text">Créée le</p>
                  <h3>{{ formatDate(annee.createdAt) }}</h3>
                </ion-label>
              </ion-item>
            </ion-list>
          </ion-card-content>
        </ion-card>

        <div v-if="anneesExercice.length === 0" class="empty-state ion-padding">
          <ion-icon :icon="calendarOutline" class="empty-icon"></ion-icon>
          <p>Aucune année d'exercice</p>
        </div>
      </ion-list>

      <!-- Skeleton lors du chargement -->
      <div v-else class="ion-padding">
        <ion-card v-for="i in 3" :key="i">
          <ion-card-header>
            <ion-skeleton-text animated style="width: 40%"></ion-skeleton-text>
          </ion-card-header>
          <ion-card-content>
            <ion-skeleton-text animated style="width: 80%"></ion-skeleton-text>
            <ion-skeleton-text animated style="width: 60%"></ion-skeleton-text>
          </ion-card-content>
        </ion-card>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
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
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardContent,
  IonBadge,
  IonRefresher,
  IonRefresherContent,
  IonSkeletonText,
  alertController,
  modalController,
  toastController
} from '@ionic/vue';
import {
  addOutline,
  trashOutline,
  calendarOutline
} from 'ionicons/icons';
import anneeExerciceService from '@/services/annee-exercice.service';
import type { AnneeExercice } from '@/types';
import AnneeExerciceModal from '@/components/AnneeExerciceModal.vue';

const anneesExercice = ref<AnneeExercice[]>([]);
const loading = ref(true);

onMounted(() => {
  loadData();
});

async function loadData() {
  try {
    loading.value = true;
    const data = await anneeExerciceService.getAllAnneesExercice();
    anneesExercice.value = data;
  } catch (error: any) {
    showToast(error.message || 'Erreur lors du chargement', 'danger');
  } finally {
    loading.value = false;
  }
}

async function handleRefresh(event: any) {
  await loadData();
  event.target.complete();
}

async function openCreateModal() {
  const modal = await modalController.create({
    component: AnneeExerciceModal
  });

  await modal.present();
  const { data } = await modal.onWillDismiss();

  if (data?.refresh) {
    loadData();
  }
}

async function confirmDelete(id: number) {
  const alert = await alertController.create({
    header: 'Confirmer la suppression',
    message: 'Voulez-vous vraiment supprimer cette année d\'exercice ?',
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Supprimer',
        role: 'destructive',
        handler: () => deleteAnnee(id)
      }
    ]
  });

  await alert.present();
}

async function deleteAnnee(id: number) {
  try {
    await anneeExerciceService.deleteAnneeExercice(id);
    showToast('Année d\'exercice supprimée avec succès', 'success');
    loadData();
  } catch (error: any) {
    showToast(error.message || 'Erreur lors de la suppression', 'danger');
  }
}

function isExpired(annee: AnneeExercice): boolean {
  return new Date(annee.dateFin) < new Date();
}

function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
}

async function showToast(message: string, color: string = 'primary') {
  const toast = await toastController.create({
    message,
    duration: 2000,
    color,
    position: 'bottom'
  });
  await toast.present();
}
</script>

<style scoped>
.annee-card {
  margin: 16px;
}

.card-header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.label-text {
  font-size: 0.75rem;
  color: var(--ion-color-medium);
  margin-bottom: 4px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  color: var(--ion-color-medium);
  margin-bottom: 16px;
}

.empty-state p {
  color: var(--ion-color-medium);
  font-size: 1rem;
}
</style>
