<template>
  <ion-header>
    <ion-toolbar>
      <ion-title>{{ mode === 'create' ? 'Nouvel Utilisateur' : 'Modifier l\'utilisateur' }}</ion-title>
      <ion-buttons slot="end">
        <ion-button @click="dismiss()">Fermer</ion-button>
      </ion-buttons>
    </ion-toolbar>
  </ion-header>

  <ion-content>
    <form @submit.prevent="handleSubmit" class="ion-padding">
      <!-- Nom d'utilisateur -->
      <ion-item v-if="!isAdminEditingOther">
        <ion-label position="stacked">Nom d'utilisateur *</ion-label>
        <ion-input
          v-model="formData.username"
          type="text"
          placeholder="Entrez le nom d'utilisateur"
          required
        ></ion-input>
      </ion-item>

      <!-- Mot de passe -->
      <ion-item v-if="!isAdminEditingOther">
        <ion-label position="stacked">
          Mot de passe {{ mode === 'create' ? '*' : '(laisser vide pour ne pas changer)' }}
        </ion-label>
        <ion-input
          v-model="formData.password"
          type="password"
          placeholder="Entrez le mot de passe"
          :required="mode === 'create'"
        ></ion-input>
      </ion-item>

      <!-- Rôle -->
      <ion-item v-if="mode === 'create' || (mode === 'edit' && isAdminEditingOther)">
        <ion-label position="stacked">Rôle *</ion-label>
        <ion-select v-model="formData.roleId" interface="action-sheet" placeholder="Sélectionner">
          <ion-select-option 
            v-for="role in roles" 
            :key="role.id" 
            :value="role.id"
          >
            {{ role.roleName }}
          </ion-select-option>
        </ion-select>
      </ion-item>

      <!-- Année d'exercice -->
      <ion-item v-if="mode === 'create' || (mode === 'edit' && isAdminEditingOther)">
        <ion-label position="stacked">Année *</ion-label>
        <ion-select v-model="formData.anneeExerciceId" interface="action-sheet" placeholder="Sélectionner">
          <ion-select-option 
            v-for="annee in anneesExercice" 
            :key="annee.id" 
            :value="annee.id"
          >
            {{ new Date(annee.annee).getFullYear() }}
          </ion-select-option>
        </ion-select>
      </ion-item>

      <!-- Info messages -->
      <ion-note v-if="mode === 'edit' && !isAdminEditingOther" class="ion-padding">
        Vous ne pouvez modifier que votre nom d'utilisateur et votre mot de passe.
      </ion-note>

      <ion-note v-if="mode === 'edit' && isAdminEditingOther" class="ion-padding" color="warning">
        En tant que Directeur, vous ne pouvez modifier que le rôle, le statut et l'année d'exercice.
      </ion-note>

      <!-- Boutons -->
      <div class="button-group ion-margin-top">
        <ion-button expand="block" type="submit" :disabled="loading">
          <ion-spinner v-if="loading" name="crescent"></ion-spinner>
          <span v-else>{{ mode === 'create' ? 'Créer' : 'Modifier' }}</span>
        </ion-button>
        <ion-button expand="block" fill="outline" @click="dismiss()">
          Annuler
        </ion-button>
      </div>
    </form>
  </ion-content>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import {
  IonHeader,
  IonToolbar,
  IonTitle,
  IonButtons,
  IonButton,
  IonContent,
  IonItem,
  IonLabel,
  IonInput,
  IonSelect,
  IonSelectOption,
  IonNote,
  IonSpinner,
  modalController,
  toastController
} from '@ionic/vue';
import utilisateurService from '@/services/utilisateur.service';
import anneeExerciceService from '@/services/annee-exercice.service';
import { useAuthStore } from '@/stores/auth.store';
import type { Utilisateur, Role, AnneeExercice, CreateUtilisateurRequest, UpdateUtilisateurRequest } from '@/types';

interface Props {
  mode: 'create' | 'edit';
  utilisateur?: Utilisateur;
}

const props = defineProps<Props>();

const authStore = useAuthStore();
const formData = ref<CreateUtilisateurRequest>({
  username: '',
  password: '',
  roleId: 0,
  anneeExerciceId: 0
});

const roles = ref<Role[]>([]);
const anneesExercice = ref<AnneeExercice[]>([]);
const loading = ref(false);

const isAdminEditingOther = computed(() => {
  return props.mode === 'edit' && 
         authStore.isDirecteur && 
         authStore.user?.id !== props.utilisateur?.id;
});

onMounted(async () => {
  await loadData();
  
  if (props.mode === 'edit' && props.utilisateur) {
    const role = roles.value.find(r => r.roleName === props.utilisateur!.role);
    const annee = anneesExercice.value.find(a => a.annee === props.utilisateur!.anneeExercice);
    
    formData.value = {
      username: props.utilisateur.username,
      password: '',
      roleId: role?.id || 0,
      anneeExerciceId: annee?.id || 0
    };
  }
});

async function loadData() {
  try {
    const [rolesData, anneesData] = await Promise.all([
      utilisateurService.getAllRoles(),
      anneeExerciceService.getAllAnneesExercice()
    ]);
    roles.value = rolesData;
    anneesExercice.value = anneesData;
    
    if (props.mode === 'create' && roles.value.length > 0 && anneesExercice.value.length > 0) {
      formData.value.roleId = roles.value[0].id;
      formData.value.anneeExerciceId = anneesExercice.value[0].id;
    }
  } catch (error: any) {
    showToast(error.message || 'Erreur lors du chargement', 'danger');
  }
}

async function handleSubmit() {
  loading.value = true;
  
  try {
    if (props.mode === 'create') {
      await utilisateurService.createUtilisateur(formData.value);
      showToast('Utilisateur créé avec succès', 'success');
    } else if (props.utilisateur) {
      const updateData: UpdateUtilisateurRequest = {};
      
      if (!isAdminEditingOther.value) {
        // Self-edit: username et password uniquement
        if (formData.value.username !== props.utilisateur.username) {
          updateData.username = formData.value.username;
        }
        if (formData.value.password) {
          updateData.password = formData.value.password;
        }
      } else {
        // Admin edit: role et anneeExerciceId uniquement
        updateData.roleId = formData.value.roleId;
        updateData.anneeExerciceId = formData.value.anneeExerciceId;
      }
      
      if (Object.keys(updateData).length === 0) {
        showToast('Aucune modification détectée', 'warning');
        return;
      }
      
      await utilisateurService.updateUtilisateur(props.utilisateur.id, updateData);
      
      // Si username changé, rediriger vers login
      if (updateData.username) {
        showToast('Nom d\'utilisateur modifié. Reconnexion nécessaire.', 'warning');
        setTimeout(async () => {
          await authStore.logout();
        }, 2000);
      } else {
        showToast('Utilisateur modifié avec succès', 'success');
      }
    }
    
    dismiss(true);
  } catch (error: any) {
    showToast(error.response?.data?.message || 'Erreur lors de l\'opération', 'danger');
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
