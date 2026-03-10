<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <img src="/assets/logo.png" alt="Logo" class="header-logo" />
        </ion-buttons>
        <ion-title>Mon Profil</ion-title>
        <ion-buttons slot="end">
          <ion-button @click="openEditModal">
            <ion-icon :icon="createOutline"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-refresher slot="fixed" @ionRefresh="handleRefresh($event)">
        <ion-refresher-content></ion-refresher-content>
      </ion-refresher>

      <div class="ion-padding">
        <!-- Informations utilisateur -->
        <ion-card v-if="userData">
          <ion-card-header>
            <ion-card-title>
              <ion-icon :icon="personCircleOutline" class="profile-icon"></ion-icon>
              {{ userData.username }}
            </ion-card-title>
            <ion-card-subtitle>{{ userData.role }}</ion-card-subtitle>
          </ion-card-header>
          <ion-card-content>
            <ion-list>
              <ion-item>
                <ion-label>
                  <h3>ID Utilisateur</h3>
                  <p>{{ userData.id }}</p>
                </ion-label>
              </ion-item>
              <ion-item>
                <ion-label>
                  <h3>Année d'exercice</h3>
                  <p>{{ new Date(userData.anneeExercice).getFullYear() }}</p>
                </ion-label>
              </ion-item>
              <ion-item>
                <ion-label>
                  <h3>Statut</h3>
                  <ion-badge :color="userData.active ? 'success' : 'danger'">
                    {{ userData.active ? 'Actif' : 'Inactif' }}
                  </ion-badge>
                </ion-label>
              </ion-item>
              <ion-item>
                <ion-label>
                  <h3>Créé le</h3>
                  <p>{{ formatDate(userData.createdAt) }}</p>
                </ion-label>
              </ion-item>
            </ion-list>
          </ion-card-content>
        </ion-card>

        <!-- Bouton modifier -->
        <ion-button expand="block" @click="openEditModal" class="ion-margin-top">
          <ion-icon :icon="createOutline" slot="start"></ion-icon>
          Modifier mon profil
        </ion-button>

        <!-- Bouton À propos -->
        <ion-button expand="block" fill="outline" @click="$router.push('/a-propos')" class="ion-margin-top">
          <ion-icon :icon="informationCircleOutline" slot="start"></ion-icon>
          À propos de l'application
        </ion-button>

        <!-- Bouton déconnexion -->
        <ion-button expand="block" color="danger" @click="handleLogout" class="ion-margin-top">
          <ion-icon :icon="logOutOutline" slot="start"></ion-icon>
          Se déconnecter
        </ion-button>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardSubtitle,
  IonCardContent,
  IonList,
  IonItem,
  IonLabel,
  IonButton,
  IonButtons,
  IonIcon,
  IonBadge,
  IonRefresher,
  IonRefresherContent,
  alertController,
  modalController,
  toastController
} from '@ionic/vue';
import { personCircleOutline, logOutOutline, createOutline, informationCircleOutline } from 'ionicons/icons';
import { useAuthStore } from '@/stores/auth.store';
import utilisateurService from '@/services/utilisateur.service';
import UtilisateurModal from '@/components/UtilisateurModal.vue';
import type { Utilisateur } from '@/types';

const router = useRouter();
const authStore = useAuthStore();
const userData = ref<Utilisateur | null>(null);

onMounted(() => {
  loadUserData();
});

async function loadUserData() {
  try {
    const users = await utilisateurService.getAllUtilisateurs();
    userData.value = users.find(u => u.id === authStore.user?.id) || null;
  } catch (error: any) {
    showToast(error.message || 'Erreur lors du chargement', 'danger');
  }
}

async function handleRefresh(event: any) {
  await loadUserData();
  event.target.complete();
}

async function openEditModal() {
  if (!userData.value) return;

  const modal = await modalController.create({
    component: UtilisateurModal,
    componentProps: {
      mode: 'edit',
      utilisateur: userData.value
    }
  });

  await modal.present();
  const { data } = await modal.onWillDismiss();

  if (data?.refresh) {
    loadUserData();
  }
}

function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
}

const handleLogout = async () => {
  const alert = await alertController.create({
    header: 'Déconnexion',
    message: 'Voulez-vous vraiment vous déconnecter ?',
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Déconnexion',
        handler: async () => {
          await authStore.logout();
          router.replace('/login');
        }
      }
    ]
  });
  await alert.present();
};

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
.profile-icon {
  font-size: 24px;
  margin-right: 8px;
  vertical-align: middle;
}
</style>
