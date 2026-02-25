<template>
  <ion-page>
    <ion-content :fullscreen="true" class="ion-padding">
      <div class="login-container">
        <div class="login-header">
          <img src="/assets/logo.png" alt="Logo Explorateur" class="login-logo" />
          <h1>EXPLORATEUR MAHAZO</h1>
          <p>Connexion</p>
        </div>

        <ion-card>
          <ion-card-content>
            <form @submit.prevent="handleLogin">
              <ion-item>
                <!-- <ion-label position="floating">Nom d'utilisateur</ion-label> -->
                <ion-input
                  v-model="username"
                  type="text"
                  required
                  autocomplete="username"
                  placeholder="Nom d'utilisateur"
                ></ion-input>
              </ion-item>

              <ion-item>
                <!-- <ion-label position="floating">Mot de passe</ion-label> -->
                <ion-input
                  v-model="password"
                  type="password"
                  required
                  autocomplete="current-password"
                  placeholder="Mot de passe"
                ></ion-input>
              </ion-item>

              <ion-button
                expand="block"
                type="submit"
                class="ion-margin-top"
                :disabled="isLoading"
              >
                <ion-spinner v-if="isLoading" name="crescent"></ion-spinner>
                <span v-else>Se connecter</span>
              </ion-button>

              <ion-text color="danger" v-if="error" class="ion-margin-top">
                <p class="error-text">{{ error }}</p>
              </ion-text>
            </form>
          </ion-card-content>
        </ion-card>

        <ion-text color="medium" class="ion-text-center">
          <p class="version-text">Version 1.0.0</p>
        </ion-text>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import {
  IonPage,
  IonContent,
  IonCard,
  IonCardContent,
  IonItem,
  IonLabel,
  IonInput,
  IonButton,
  IonText,
  IonSpinner,
  alertController
} from '@ionic/vue';
import { useAuthStore } from '@/stores/auth.store';

const router = useRouter();
const authStore = useAuthStore();

const username = ref('');
const password = ref('');
const isLoading = ref(false);
const error = ref('');

const handleLogin = async () => {
  if (!username.value || !password.value) {
    error.value = 'Veuillez remplir tous les champs';
    return;
  }

  isLoading.value = true;
  error.value = '';

  const success = await authStore.login({
    username: username.value,
    password: password.value
  });

  isLoading.value = false;

  if (success) {
    router.replace('/tabs/home');
  } else {
    error.value = authStore.error || 'Erreur de connexion';
    
    const alert = await alertController.create({
      header: 'Erreur de connexion',
      message: error.value,
      buttons: ['OK']
    });
    await alert.present();
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

.login-logo {
  width: 100px;
  height: auto;
  margin-bottom: 1rem;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
}

.login-header h1 {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0;
  color: var(--ion-color-primary);
  line-height: 1.3;
}

.login-header p {
  color: var(--ion-color-medium);
  margin-top: 0.5rem;
  font-size: 0.875rem;
}

ion-item {
  margin-bottom: 1rem;
}

.error-text {
  text-align: center;
  font-size: 0.875rem;
}

.version-text {
  font-size: 0.75rem;
  margin-top: 2rem;
}
</style>
