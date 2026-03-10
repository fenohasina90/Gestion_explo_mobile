<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button default-href="/tabs/home"></ion-back-button>
          <!-- <img src="/assets/logo.png" alt="Logo" class="header-logo" style="margin-left: 8px;" /> -->
        </ion-buttons>
        <ion-title>Utilisateurs</ion-title>
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

      <!-- Filtre par année d'exercice -->
      <div class="ion-padding-horizontal ion-margin-top">
        <ion-item>
          <ion-label>Année</ion-label>
          <ion-select v-model="filterAnneeId" interface="action-sheet" placeholder="Toutes">
            <ion-select-option :value="null">Toutes les années</ion-select-option>
            <ion-select-option 
              v-for="annee in anneesExercice" 
              :key="annee.id" 
              :value="annee.id"
            >
              {{ new Date(annee.annee).getFullYear() }}
            </ion-select-option>
          </ion-select>
        </ion-item>
        <p v-if="filterAnneeId" class="ion-padding-start ion-text-sm">
          {{ filteredUtilisateurs.length }} utilisateur(s)
        </p>
      </div>

      <!-- Liste des utilisateurs -->
      <ion-list v-if="!loading">
        <ion-item 
          v-for="user in filteredUtilisateurs" 
          :key="user.id"
          button
          @click="openEditModal(user)"
        >
          <ion-label>
            <h2>{{ user.username }}</h2>
            <p>{{ user.role }} - {{ new Date(user.anneeExercice).getFullYear() }}</p>
          </ion-label>
          <ion-badge :color="user.active ? 'success' : 'danger'" slot="end">
            {{ user.active ? 'Actif' : 'Inactif' }}
          </ion-badge>
          <ion-buttons slot="end">
            <ion-button @click.stop="toggleActive(user)">
              <ion-icon :icon="user.active ? closeCircleOutline : checkmarkCircleOutline"></ion-icon>
            </ion-button>
            <ion-button color="danger" @click.stop="confirmDelete(user.id)">
              <ion-icon :icon="trashOutline"></ion-icon>
            </ion-button>
          </ion-buttons>
        </ion-item>
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
  IonBadge,
  IonRefresher,
  IonRefresherContent,
  IonSelect,
  IonSelectOption,
  IonSkeletonText,
  alertController,
  modalController,
  toastController
} from '@ionic/vue';
import {
  addOutline,
  trashOutline,
  checkmarkCircleOutline,
  closeCircleOutline
} from 'ionicons/icons';
import utilisateurService from '@/services/utilisateur.service';
import anneeExerciceService from '@/services/annee-exercice.service';
import { useAuthStore } from '@/stores/auth.store';
import type { Utilisateur, AnneeExercice } from '@/types';
import UtilisateurModal from '@/components/UtilisateurModal.vue';

const authStore = useAuthStore();
const utilisateurs = ref<Utilisateur[]>([]);
const anneesExercice = ref<AnneeExercice[]>([]);
const filterAnneeId = ref<number | null>(null);
const loading = ref(true);

const filteredUtilisateurs = computed(() => {
  if (!filterAnneeId.value) {
    return utilisateurs.value;
  }
  const anneeSelectionnee = anneesExercice.value.find(a => a.id === filterAnneeId.value);
  if (!anneeSelectionnee) {
    return utilisateurs.value;
  }
  return utilisateurs.value.filter(u => u.anneeExercice === anneeSelectionnee.annee);
});

onMounted(() => {
  loadData();
});

async function loadData() {
  try {
    loading.value = true;
    const [usersData, anneesData] = await Promise.all([
      utilisateurService.getAllUtilisateurs(),
      anneeExerciceService.getAllAnneesExercice()
    ]);
    utilisateurs.value = usersData;
    anneesExercice.value = anneesData;
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
    component: UtilisateurModal,
    componentProps: {
      mode: 'create'
    }
  });

  await modal.present();
  const { data } = await modal.onWillDismiss();

  if (data?.refresh) {
    loadData();
  }
}

async function openEditModal(user: Utilisateur) {
  const modal = await modalController.create({
    component: UtilisateurModal,
    componentProps: {
      mode: 'edit',
      utilisateur: user
    }
  });

  await modal.present();
  const { data } = await modal.onWillDismiss();

  if (data?.refresh) {
    loadData();
  }
}

async function toggleActive(user: Utilisateur) {
  if (authStore.user?.id === user.id) {
    showToast('Vous ne pouvez pas modifier votre propre statut', 'warning');
    return;
  }

  try {
    await utilisateurService.updateUtilisateur(user.id, {
      active: !user.active
    });
    showToast(
      `Utilisateur ${user.active ? 'désactivé' : 'activé'} avec succès`,
      'success'
    );
    loadData();
  } catch (error: any) {
    showToast(error.message || 'Erreur lors de la modification', 'danger');
  }
}

async function confirmDelete(id: number) {
  const alert = await alertController.create({
    header: 'Confirmer la suppression',
    message: 'Voulez-vous vraiment supprimer cet utilisateur ?',
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Supprimer',
        role: 'destructive',
        handler: () => deleteUser(id)
      }
    ]
  });

  await alert.present();
}

async function deleteUser(id: number) {
  try {
    await utilisateurService.deleteUtilisateur(id);
    showToast('Utilisateur supprimé avec succès', 'success');
    loadData();
  } catch (error: any) {
    showToast(error.message || 'Erreur lors de la suppression', 'danger');
  }
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
.ion-text-sm {
  font-size: 0.875rem;
  color: var(--ion-color-medium);
}
</style>
