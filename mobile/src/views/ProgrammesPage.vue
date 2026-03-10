<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button default-href="/tabs/home"></ion-back-button>
          <!-- <img src="/assets/logo.png" alt="Logo" class="header-logo" style="margin-left: 8px;" /> -->
        </ion-buttons>
        <ion-title>Programmes</ion-title>
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
          <ion-label>Catégorie</ion-label>
          <ion-select v-model="filterCategorieId" interface="action-sheet" placeholder="Toutes">
            <ion-select-option :value="null">Toutes les catégories</ion-select-option>
            <ion-select-option 
              v-for="categorie in categories" 
              :key="categorie.id" 
              :value="categorie.id"
            >
              {{ categorie.nom }}
            </ion-select-option>
          </ion-select>
        </ion-item>

        <ion-item>
          <ion-label>Classe</ion-label>
          <ion-select v-model="filterClasseId" interface="action-sheet" placeholder="Toutes">
            <ion-select-option :value="null">Toutes les classes</ion-select-option>
            <ion-select-option 
              v-for="classe in classes" 
              :key="classe.id" 
              :value="classe.id"
            >
              {{ classe.nom }}
            </ion-select-option>
          </ion-select>
        </ion-item>

        <p v-if="filterCategorieId || filterClasseId" class="ion-padding-start ion-text-sm">
          {{ filteredProgrammes.length }} programme(s)
        </p>
      </div>

      <!-- Liste des programmes -->
      <ion-list v-if="!loading">
        <ion-item 
          v-for="programme in filteredProgrammes" 
          :key="programme.id"
        >
          <ion-label>
            <h2>{{ programme.nom }}</h2>
            <p class="ion-text-wrap" style="font-size: 0.85rem; color: var(--ion-color-medium);">
              {{ programme.categorieNom }} • {{ programme.classeNom }}
            </p>
            <p v-if="programme.description" class="ion-text-wrap" style="font-size: 0.8rem; color: var(--ion-color-medium-shade);">
              {{ programme.description }}
            </p>
          </ion-label>
          <ion-buttons slot="end">
            <ion-button 
              v-if="canModify"
              @click.stop="openEditModal(programme)"
            >
              <ion-icon :icon="createOutline"></ion-icon>
            </ion-button>
            <ion-button 
              v-if="canModify"
              color="danger" 
              @click.stop="confirmDelete(programme.id)"
            >
              <ion-icon :icon="trashOutline"></ion-icon>
            </ion-button>
          </ion-buttons>
        </ion-item>

        <!-- Message si aucun programme -->
        <div v-if="filteredProgrammes.length === 0" class="ion-padding ion-text-center">
          <ion-note>Aucun programme trouvé</ion-note>
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
import { ref, computed, onMounted, watch } from 'vue';
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
  IonSelect,
  IonSelectOption,
  IonSkeletonText,
  IonNote,
  alertController,
  modalController,
  toastController
} from '@ionic/vue';
import { addOutline, createOutline, trashOutline, filterOutline } from 'ionicons/icons';
import { useAuthStore } from '@/stores/auth.store';
import programmeService from '@/services/programme.service';
import categorieProgrammeService from '@/services/categorie-programme.service';
import classeService from '@/services/classe.service';
import type { Programme, CategorieProgramme, Classe } from '@/types';

const authStore = useAuthStore();
const loading = ref(false);
const programmes = ref<Programme[]>([]);
const categories = ref<CategorieProgramme[]>([]);
const classes = ref<Classe[]>([]);
const showFilters = ref(false);
const filterCategorieId = ref<number | null>(null);
const filterClasseId = ref<number | null>(null);

const canModify = computed(() => {
  const role = authStore.user?.role;
  return role === 'Directeur' || role === 'Co_Directeur';
});

const filteredProgrammes = computed(() => {
  let result = programmes.value;
  
  if (filterCategorieId.value) {
    result = result.filter(p => p.categorieId === filterCategorieId.value);
  }
  
  if (filterClasseId.value) {
    result = result.filter(p => p.classeId === filterClasseId.value);
  }
  
  return result;
});

onMounted(() => {
  loadData();
});

async function loadData() {
  try {
    loading.value = true;
    await Promise.all([
      loadProgrammes(),
      loadCategories(),
      loadClasses()
    ]);
  } finally {
    loading.value = false;
  }
}

async function loadProgrammes() {
  try {
    programmes.value = await programmeService.getAllProgrammes();
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors du chargement des programmes',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  }
}

async function loadCategories() {
  try {
    categories.value = await categorieProgrammeService.getAllCategories();
  } catch (error: any) {
    console.error('Erreur chargement catégories:', error);
  }
}

async function loadClasses() {
  try {
    classes.value = await classeService.getAllClasses();
  } catch (error: any) {
    console.error('Erreur chargement classes:', error);
  }
}

async function handleRefresh(event: any) {
  await loadData();
  event.target.complete();
}

async function openCreateModal() {
  const alert = await alertController.create({
    header: 'Nouveau Programme',
    inputs: [
      {
        name: 'nom',
        type: 'text',
        placeholder: 'Nom du programme'
      },
      {
        name: 'description',
        type: 'textarea',
        placeholder: 'Description (optionnel)'
      }
    ],
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Suivant',
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
          await showCategoryAndClassSelection(data.nom, data.description);
          return true;
        }
      }
    ]
  });
  await alert.present();
}

async function showCategoryAndClassSelection(nom: string, description: string) {
  const alert = await alertController.create({
    header: 'Sélectionner Catégorie et Classe',
    inputs: [
      {
        label: 'Catégorie',
        type: 'radio',
        value: categories.value[0]?.id,
        checked: true
      },
      ...categories.value.map(cat => ({
        label: cat.nom,
        type: 'radio' as const,
        value: cat.id
      }))
    ],
    buttons: [
      {
        text: 'Retour',
        role: 'cancel'
      },
      {
        text: 'Suivant',
        handler: async (categorieId) => {
          await showClassSelection(nom, description, categorieId);
        }
      }
    ]
  });
  await alert.present();
}

async function showClassSelection(nom: string, description: string, categorieId: number) {
  const alert = await alertController.create({
    header: 'Sélectionner une Classe',
    inputs: classes.value.map((classe, index) => ({
      label: classe.nom,
      type: 'radio' as const,
      value: classe.id,
      checked: index === 0
    })),
    buttons: [
      {
        text: 'Retour',
        role: 'cancel'
      },
      {
        text: 'Créer',
        handler: async (classeId) => {
          await createProgramme(nom, description, categorieId, classeId);
        }
      }
    ]
  });
  await alert.present();
}

async function createProgramme(nom: string, description: string, categorieId: number, classeId: number) {
  try {
    await programmeService.createProgramme({ nom, description, categorieId, classeId });
    const toast = await toastController.create({
      message: 'Programme créé avec succès',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadProgrammes();
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors de la création',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  }
}

async function openEditModal(programme: Programme) {
  const alert = await alertController.create({
    header: 'Modifier Programme',
    inputs: [
      {
        name: 'nom',
        type: 'text',
        value: programme.nom,
        placeholder: 'Nom du programme'
      },
      {
        name: 'description',
        type: 'textarea',
        value: programme.description,
        placeholder: 'Description (optionnel)'
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
          await updateProgramme(programme.id, data.nom, data.description, programme.categorieId, programme.classeId);
          return true;
        }
      }
    ]
  });
  await alert.present();
}

async function updateProgramme(id: number, nom: string, description: string, categorieId: number, classeId: number) {
  try {
    await programmeService.updateProgramme(id, { nom, description, categorieId, classeId });
    const toast = await toastController.create({
      message: 'Programme modifié avec succès',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadProgrammes();
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
    message: 'Voulez-vous vraiment supprimer ce programme ?',
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Supprimer',
        role: 'destructive',
        handler: () => deleteProgramme(id)
      }
    ]
  });
  await alert.present();
}

async function deleteProgramme(id: number) {
  try {
    await programmeService.deleteProgramme(id);
    const toast = await toastController.create({
      message: 'Programme supprimé avec succès',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadProgrammes();
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
