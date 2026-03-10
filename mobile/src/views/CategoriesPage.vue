<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button default-href="/tabs/home"></ion-back-button>
          <!-- <img src="/assets/logo.png" alt="Logo" class="header-logo" style="margin-left: 8px;" /> -->
        </ion-buttons>
        <ion-title>Catégories de Programme</ion-title>
        <ion-buttons slot="end">
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

      <!-- Liste des catégories -->
      <ion-list v-if="!loading">
        <ion-item 
          v-for="categorie in categories" 
          :key="categorie.id"
        >
          <ion-label>
            <h2>{{ categorie.nom }}</h2>
            <p>{{ categorie.nombreProgrammes }} programme(s)</p>
          </ion-label>
          <ion-buttons slot="end">
            <ion-button 
              v-if="canModify"
              @click.stop="openEditModal(categorie)"
            >
              <ion-icon :icon="createOutline"></ion-icon>
            </ion-button>
            <ion-button 
              v-if="canModify"
              color="danger" 
              @click.stop="confirmDelete(categorie.id)"
            >
              <ion-icon :icon="trashOutline"></ion-icon>
            </ion-button>
          </ion-buttons>
        </ion-item>

        <!-- Message si aucune catégorie -->
        <div v-if="categories.length === 0" class="ion-padding ion-text-center">
          <ion-note>Aucune catégorie trouvée</ion-note>
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
  IonRefresher,
  IonRefresherContent,
  IonSkeletonText,
  IonNote,
  alertController,
  modalController,
  toastController
} from '@ionic/vue';
import { addOutline, createOutline, trashOutline } from 'ionicons/icons';
import { useAuthStore } from '@/stores/auth.store';
import categorieProgrammeService from '@/services/categorie-programme.service';
import type { CategorieProgramme } from '@/types';

const authStore = useAuthStore();
const loading = ref(false);
const categories = ref<CategorieProgramme[]>([]);

const canModify = computed(() => {
  const role = authStore.user?.role;
  return role === 'Directeur' || role === 'Co_Directeur';
});

onMounted(() => {
  loadCategories();
});

async function loadCategories() {
  try {
    loading.value = true;
    categories.value = await categorieProgrammeService.getAllCategories();
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors du chargement des catégories',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  } finally {
    loading.value = false;
  }
}

async function handleRefresh(event: any) {
  await loadCategories();
  event.target.complete();
}

async function openCreateModal() {
  const alert = await alertController.create({
    header: 'Nouvelle Catégorie',
    inputs: [
      {
        name: 'nom',
        type: 'text',
        placeholder: 'Nom de la catégorie'
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
          if (!data.nom?.trim()) {
            const toast = await toastController.create({
              message: 'Le nom est requis',
              duration: 2000,
              color: 'warning'
            });
            await toast.present();
            return false;
          }
          await createCategorie(data.nom);
          return true;
        }
      }
    ]
  });
  await alert.present();
}

async function openEditModal(categorie: CategorieProgramme) {
  const alert = await alertController.create({
    header: 'Modifier Catégorie',
    inputs: [
      {
        name: 'nom',
        type: 'text',
        value: categorie.nom,
        placeholder: 'Nom de la catégorie'
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
          if (!data.nom?.trim()) {
            const toast = await toastController.create({
              message: 'Le nom est requis',
              duration: 2000,
              color: 'warning'
            });
            await toast.present();
            return false;
          }
          await updateCategorie(categorie.id, data.nom);
          return true;
        }
      }
    ]
  });
  await alert.present();
}

async function createCategorie(nom: string) {
  try {
    await categorieProgrammeService.createCategorie({ nom });
    const toast = await toastController.create({
      message: 'Catégorie créée avec succès',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadCategories();
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors de la création',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  }
}

async function updateCategorie(id: number, nom: string) {
  try {
    await categorieProgrammeService.updateCategorie(id, { nom });
    const toast = await toastController.create({
      message: 'Catégorie modifiée avec succès',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadCategories();
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
    message: 'Voulez-vous vraiment supprimer cette catégorie ?',
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Supprimer',
        role: 'destructive',
        handler: () => deleteCategorie(id)
      }
    ]
  });
  await alert.present();
}

async function deleteCategorie(id: number) {
  try {
    await categorieProgrammeService.deleteCategorie(id);
    const toast = await toastController.create({
      message: 'Catégorie supprimée avec succès',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadCategories();
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors de la suppression',
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
