<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>Mon Profil</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <div class="ion-padding">
        <ion-card v-if="authStore.user">
          <ion-card-header>
            <ion-card-title>
              <ion-icon :icon="personCircleOutline" class="profile-icon"></ion-icon>
              {{ authStore.user.username }}
            </ion-card-title>
            <ion-card-subtitle>{{ authStore.user.role }}</ion-card-subtitle>
          </ion-card-header>
          <ion-card-content>
            <ion-list>
              <ion-item>
                <ion-label>
                  <h3>ID Utilisateur</h3>
                  <p>{{ authStore.user.id }}</p>
                </ion-label>
              </ion-item>
              <ion-item>
                <ion-label>
                  <h3>Année d'exercice</h3>
                  <p>{{ authStore.user.anneeExercice }}</p>
                </ion-label>
              </ion-item>
              <ion-item>
                <ion-label>
                  <h3>Statut</h3>
                  <p>{{ authStore.user.active ? 'Actif' : 'Inactif' }}</p>
                </ion-label>
              </ion-item>
            </ion-list>
          </ion-card-content>
        </ion-card>

        <ion-button expand="block" color="danger" @click="handleLogout" class="ion-margin-top">
          <ion-icon :icon="logOutOutline" slot="start"></ion-icon>
          Se déconnecter
        </ion-button>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
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
  IonIcon,
  alertController
} from '@ionic/vue';
import { personCircleOutline, logOutOutline } from 'ionicons/icons';
import { useAuthStore } from '@/stores/auth.store';

const router = useRouter();
const authStore = useAuthStore();

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
</script>

<style scoped>
.profile-icon {
  font-size: 24px;
  margin-right: 8px;
  vertical-align: middle;
}
</style>
