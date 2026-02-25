<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button default-href="/tabs/home"></ion-back-button>
          <img src="/assets/logo.png" alt="Logo" class="header-logo" style="margin-left: 8px;" />
        </ion-buttons>
        <ion-title>Staffs</ion-title>
        <ion-buttons slot="end">
          <ion-button @click="showFilters = !showFilters">
            <ion-icon :icon="filterOutline"></ion-icon>
          </ion-button>
          <ion-button v-if="isDirecteur" @click="openCreateModal">
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
          <ion-label>Année d'exercice</ion-label>
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

        <ion-item>
          <ion-label>Rôle</ion-label>
          <ion-select v-model="filterRoleId" interface="action-sheet" placeholder="Tous">
            <ion-select-option :value="null">Tous les rôles</ion-select-option>
            <ion-select-option 
              v-for="role in roles" 
              :key="role.id" 
              :value="role.id"
            >
              {{ role.roleName }}
            </ion-select-option>
          </ion-select>
        </ion-item>

        <ion-item>
          <ion-label>Statut</ion-label>
          <ion-select v-model="filterEstChefGuide" interface="action-sheet" placeholder="Tous">
            <ion-select-option :value="null">Tous</ion-select-option>
            <ion-select-option :value="true">Chef Guide</ion-select-option>
            <ion-select-option :value="false">Aspirant</ion-select-option>
          </ion-select>
        </ion-item>

        <p v-if="filterAnneeId || filterRoleId || filterEstChefGuide !== null" class="ion-padding-start ion-text-sm">
          {{ filteredStaffs.length }} staff(s)
        </p>
      </div>

      <!-- Liste des staffs -->
      <ion-list v-if="!loading">
        <ion-item 
          v-for="staff in filteredStaffs" 
          :key="staff.id"
        >
          <ion-label>
            <h2>{{ staff.instructeurPrenom }} {{ staff.instructeurNom }}</h2>
            <!-- <p></p> -->
            <p class="ion-text-wrap" style="font-size: 0.85rem; color: var(--ion-color-medium);">
              <span v-if="staff.instructeurTotem">{{ staff.instructeurTotem }} • </span>
              <span v-if="staff.instructeurTelephone">{{ staff.instructeurTelephone }} • </span>
              <span>{{ new Date(staff.anneeExercice).getFullYear() }}</span>
            </p>
          </ion-label>
          <ion-badge :color="getRoleColor(staff.role)">
            {{ staff.role }}
          </ion-badge>
          <ion-buttons slot="end">
            <ion-button 
              v-if="canModify"
              @click.stop="openEditModal(staff)"
            >
              <ion-icon :icon="createOutline"></ion-icon>
            </ion-button>
            <ion-button 
              v-if="isDirecteur"
              color="danger" 
              @click.stop="confirmDelete(staff.id)"
            >
              <ion-icon :icon="trashOutline"></ion-icon>
            </ion-button>
          </ion-buttons>
        </ion-item>

        <!-- Message si aucun staff -->
        <div v-if="filteredStaffs.length === 0" class="ion-padding ion-text-center">
          <ion-note>Aucun staff trouvé</ion-note>
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
  IonNote,
  alertController,
  modalController,
  toastController
} from '@ionic/vue';
import {
  addOutline,
  trashOutline,
  createOutline,
  filterOutline
} from 'ionicons/icons';
import staffService from '@/services/staff.service';
import anneeExerciceService from '@/services/annee-exercice.service';
import { useAuthStore } from '@/stores/auth.store';
import type { Staff, RoleStaff, AnneeExercice } from '@/types';
import StaffModal from '@/components/StaffModal.vue';

const authStore = useAuthStore();
const staffs = ref<Staff[]>([]);
const roles = ref<RoleStaff[]>([]);
const anneesExercice = ref<AnneeExercice[]>([]);
const filterAnneeId = ref<number | null>(null);
const filterRoleId = ref<number | null>(null);
const filterEstChefGuide = ref<boolean | null>(null);
const loading = ref(true);
const showFilters = ref(true);

const isDirecteur = computed(() => {
  return authStore.user?.role === 'Directeur';
});

const canModify = computed(() => {
  return authStore.user?.role === 'Directeur' || authStore.user?.role === 'Co_Directeur';
});

const filteredStaffs = computed(() => {
  let result = staffs.value;

  if (filterAnneeId.value) {
    const anneeSelectionnee = anneesExercice.value.find(a => a.id === filterAnneeId.value);
    if (anneeSelectionnee) {
      result = result.filter(s => s.anneeExercice === anneeSelectionnee.annee);
    }
  }

  if (filterRoleId.value) {
    result = result.filter(s => s.roleId === filterRoleId.value);
  }

  if (filterEstChefGuide.value !== null) {
    result = result.filter(s => s.instructeurEstChefGuide === filterEstChefGuide.value);
  }

  return result;
});

function getRoleColor(roleName: string): string {
  const roleColors: Record<string, string> = {
    'Directeur': 'success',
    'Co_Directeur': 'warning',
    'Secrétaire': 'primary',
    'Instructeur': 'medium'
  };
  return roleColors[roleName] || 'medium';
}

onMounted(() => {
  loadData();
});

async function loadData() {
  try {
    loading.value = true;
    const [staffsData, rolesData, anneesData] = await Promise.all([
      staffService.getAllStaffs(),
      staffService.getAllRoles(),
      anneeExerciceService.getAllAnneesExercice()
    ]);
    staffs.value = staffsData;
    roles.value = rolesData;
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
    component: StaffModal,
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

async function openEditModal(staff: Staff) {
  const modal = await modalController.create({
    component: StaffModal,
    componentProps: {
      mode: 'edit',
      staff: staff
    }
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
    message: 'Voulez-vous vraiment supprimer ce staff ?',
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Supprimer',
        role: 'destructive',
        handler: () => deleteStaff(id)
      }
    ]
  });

  await alert.present();
}

async function deleteStaff(id: number) {
  try {
    await staffService.deleteStaff(id);
    showToast('Staff supprimé avec succès', 'success');
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
