<template>
  <ion-header>
    <ion-toolbar>
      <ion-title>Nouvelle Année d'Exercice</ion-title>
      <ion-buttons slot="end">
        <ion-button @click="dismiss()">Fermer</ion-button>
      </ion-buttons>
    </ion-toolbar>
  </ion-header>

  <ion-content>
    <form @submit.prevent="handleSubmit" class="ion-padding">
      <ion-item>
        <ion-label position="stacked">Date de début (1er janvier) *</ion-label>
        <ion-datetime-button datetime="annee-picker"></ion-datetime-button>
      </ion-item>

      <ion-modal :keep-contents-mounted="true">
        <ion-datetime
          id="annee-picker"
          v-model="formData.annee"
          presentation="date"
          :prefer-wheel="true"
          :min="minDate"
          :max="maxDate"
        ></ion-datetime>
      </ion-modal>

      <ion-note class="ion-padding">
        L'année d'exercice commence le 1er janvier et se termine le 31 décembre automatiquement.
      </ion-note>

      <div class="button-group ion-margin-top">
        <ion-button expand="block" type="submit" :disabled="loading">
          <ion-spinner v-if="loading" name="crescent"></ion-spinner>
          <span v-else>Créer</span>
        </ion-button>
        <ion-button expand="block" fill="outline" @click="dismiss()">
          Annuler
        </ion-button>
      </div>
    </form>
  </ion-content>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import {
  IonHeader,
  IonToolbar,
  IonTitle,
  IonButtons,
  IonButton,
  IonContent,
  IonItem,
  IonLabel,
  IonDatetime,
  IonDatetimeButton,
  IonModal,
  IonNote,
  IonSpinner,
  modalController,
  toastController
} from '@ionic/vue';
import anneeExerciceService from '@/services/annee-exercice.service';
import type { CreateAnneeExerciceRequest } from '@/types';

const formData = ref<CreateAnneeExerciceRequest>({
  annee: new Date().toISOString().split('T')[0]
});

const loading = ref(false);
const minDate = ref<string>('2020-01-01'); // Date minimale par défaut
const maxDate = ref<string>(''); // Sera calculé dynamiquement

onMounted(async () => {
  await loadMaxDate();
});

/**
 * Charge la date maximale basée sur l'année la plus récente dans la BD + 1
 */
async function loadMaxDate() {
  try {
    const anneesExercice = await anneeExerciceService.getAllAnneesExercice();
    
    let maxYear = new Date().getFullYear();
    
    if (anneesExercice && anneesExercice.length > 0) {
      // Trouver l'année la plus récente en utilisant dateFin
      const annees = anneesExercice.map(a => {
        // Utiliser dateFin pour déterminer l'année, car c'est la fin de l'exercice
        const dateFin = a.dateFin || a.annee;
        return new Date(dateFin + 'T00:00:00').getFullYear();
      });
      maxYear = Math.max(...annees);
    }
    
    // Définir max = année la plus récente + 1 (31 décembre pour permettre toute l'année)
    maxDate.value = `${maxYear + 1}-12-31`;
  } catch (error) {
    // En cas d'erreur, utiliser l'année actuelle + 1
    maxDate.value = `${new Date().getFullYear() + 1}-12-31`;
  }
}

async function handleSubmit() {
  if (!formData.value.annee) {
    showToast('Veuillez sélectionner une date', 'warning');
    return;
  }

  loading.value = true;

  try {
    // Extraire l'année directement depuis le format YYYY-MM-DD pour éviter les problèmes de timezone
    const anneeStr = formData.value.annee.split('T')[0]; // Nettoyer si format ISO complet
    const annee = parseInt(anneeStr.split('-')[0], 10);
    
    // Créer la date du 1er janvier en local (pas UTC)
    const anneeDebut = `${annee}-01-01`;
    
    await anneeExerciceService.createAnneeExercice({
      annee: anneeDebut
    });
    
    showToast('Année d\'exercice créée avec succès', 'success');
    dismiss(true);
  } catch (error: any) {
    showToast(error.response?.data?.message || 'Erreur lors de la création', 'danger');
  } finally {
    loading.value = false;
  }
}

function dismiss(refresh = false) {
  modalController.dismiss({ refresh });
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
.button-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
</style>
