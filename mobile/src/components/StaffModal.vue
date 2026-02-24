<template>
  <ion-header>
    <ion-toolbar>
      <ion-title>{{ mode === 'create' ? 'Nouveau Staff' : 'Modifier le Staff' }}</ion-title>
      <ion-buttons slot="end">
        <ion-button @click="dismiss()">Fermer</ion-button>
      </ion-buttons>
    </ion-toolbar>
  </ion-header>

  <ion-content>
    <form @submit.prevent="handleSubmit" class="ion-padding">
      <!-- Mode création: recherche d'instructeur -->
      <div v-if="mode === 'create'">
        <ion-item>
          <ion-label position="stacked">Rechercher un instructeur *</ion-label>
          <ion-input
            v-model="searchQuery"
            type="text"
            placeholder="Nom ou prénom de l'instructeur"
            @ionInput="handleSearchInput"
          ></ion-input>
        </ion-item>

        <!-- Liste des suggestions -->
        <div v-if="showSuggestions && suggestions.length > 0" class="suggestions-list">
          <ion-list>
            <ion-item 
              v-for="suggestion in suggestions" 
              :key="suggestion.id"
              button
              @click="selectInstructeur(suggestion)"
            >
              <ion-label>
                <h2>{{ suggestion.nomComplet }}</h2>
              </ion-label>
            </ion-item>
          </ion-list>
        </div>

        <!-- Message aucun résultat -->
        <div v-if="showSuggestions && suggestions.length === 0 && searchQuery.length >= 1 && !showCreateInstructeurForm && !selectedInstructeur" class="no-results ion-margin-top">
          <ion-note color="medium" class="ion-padding-start">
            Aucun instructeur trouvé
          </ion-note>
          <ion-button 
            expand="block" 
            fill="outline" 
            size="small" 
            @click="showCreateInstructeurForm = true"
            class="ion-margin-top"
          >
            + Créer un nouvel instructeur
          </ion-button>
        </div>

        <!-- Formulaire de création d'instructeur -->
        <div v-if="showCreateInstructeurForm && !selectedInstructeur" class="ion-margin-top">
          <ion-item-divider>
            <ion-label>Nouvel instructeur</ion-label>
            <ion-button fill="clear" slot="end" @click="cancelCreateInstructeur">
              <ion-icon :icon="closeCircleOutline"></ion-icon>
            </ion-button>
          </ion-item-divider>

          <ion-item>
            <ion-label position="stacked">Nom *</ion-label>
            <ion-input
              v-model="newInstructeurData.nom"
              type="text"
              placeholder="Nom"
              required
            ></ion-input>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">Prénom *</ion-label>
            <ion-input
              v-model="newInstructeurData.prenom"
              type="text"
              placeholder="Prénom"
              required
            ></ion-input>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">Genre *</ion-label>
            <ion-select v-model="newInstructeurData.genre" interface="action-sheet" placeholder="Sélectionner">
              <ion-select-option value="HOMME">Homme</ion-select-option>
              <ion-select-option value="FEMME">Femme</ion-select-option>
            </ion-select>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">Totem</ion-label>
            <ion-input
              v-model="newInstructeurData.totem"
              type="text"
              placeholder="Totem (optionnel)"
            ></ion-input>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">Téléphone</ion-label>
            <ion-input
              v-model="newInstructeurData.telephone"
              type="tel"
              placeholder="Téléphone (optionnel)"
            ></ion-input>
          </ion-item>

          <ion-item>
            <ion-label>Chef Guide</ion-label>
            <ion-checkbox slot="end" v-model="newInstructeurData.estChefGuide"></ion-checkbox>
          </ion-item>

          <ion-button 
            expand="block" 
            @click="handleCreateInstructeur"
            :disabled="!newInstructeurData.nom || !newInstructeurData.prenom || !newInstructeurData.genre || loading"
            class="ion-margin-top"
          >
            <ion-spinner v-if="loading" name="crescent"></ion-spinner>
            <span v-else>Créer l'instructeur</span>
          </ion-button>
        </div>

        <!-- Instructeur sélectionné -->
        <div v-if="selectedInstructeur" class="selected-instructor ion-margin-top">
          <ion-item lines="none">
            <ion-label>
              <h3>Instructeur sélectionné</h3>
              <p>{{ selectedInstructeur.nomComplet }}</p>
            </ion-label>
            <ion-button fill="clear" slot="end" @click="clearInstructeur">
              <ion-icon :icon="closeCircleOutline"></ion-icon>
            </ion-button>
          </ion-item>
        </div>
      </div>

      <!-- Mode édition: affichage des champs instructeur -->
      <div v-if="mode === 'edit' && isCoDirecteur">
        <ion-item-divider>
          <ion-label>Informations de l'instructeur</ion-label>
        </ion-item-divider>

        <ion-item>
          <ion-label position="stacked">Nom *</ion-label>
          <ion-input
            v-model="instructeurData.nom"
            type="text"
            placeholder="Nom"
            required
          ></ion-input>
        </ion-item>

        <ion-item>
          <ion-label position="stacked">Prénom *</ion-label>
          <ion-input
            v-model="instructeurData.prenom"
            type="text"
            placeholder="Prénom"
            required
          ></ion-input>
        </ion-item>

        <ion-item>
          <ion-label position="stacked">Genre *</ion-label>
          <ion-select v-model="instructeurData.genre" interface="action-sheet" placeholder="Sélectionner">
            <ion-select-option value="HOMME">Homme</ion-select-option>
            <ion-select-option value="FEMME">Femme</ion-select-option>
          </ion-select>
        </ion-item>

        <ion-item>
          <ion-label position="stacked">Totem</ion-label>
          <ion-input
            v-model="instructeurData.totem"
            type="text"
            placeholder="Totem (optionnel)"
          ></ion-input>
        </ion-item>

        <ion-item>
          <ion-label position="stacked">Téléphone</ion-label>
          <ion-input
            v-model="instructeurData.telephone"
            type="tel"
            placeholder="Téléphone (optionnel)"
          ></ion-input>
        </ion-item>

        <ion-item>
          <ion-label>Chef Guide</ion-label>
          <ion-checkbox slot="end" v-model="instructeurData.estChefGuide"></ion-checkbox>
        </ion-item>
      </div>

      <!-- Rôle du staff -->
      <ion-item-divider v-if="mode === 'edit' || (mode === 'create' && selectedInstructeur)">
        <ion-label>Informations du staff</ion-label>
      </ion-item-divider>

      <ion-item v-if="mode === 'edit' || (mode === 'create' && selectedInstructeur)">
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

      <!-- Année d'exercice (création seulement) -->
      <ion-item v-if="mode === 'create' && selectedInstructeur">
        <ion-label position="stacked">Année d'exercice *</ion-label>
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

      <!-- Messages d'info -->
      <ion-note v-if="mode === 'edit' && !isCoDirecteur" class="ion-padding" color="warning">
        Seuls les Co-Directeurs peuvent modifier les informations de l'instructeur.
      </ion-note>

      <!-- Boutons -->
      <div class="button-group ion-margin-top" v-if="mode === 'edit' || (mode === 'create' && selectedInstructeur)">
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
  IonList,
  IonCheckbox,
  IonItemDivider,
  IonIcon,
  modalController,
  toastController
} from '@ionic/vue';
import { closeCircleOutline } from 'ionicons/icons';
import instructeurService from '@/services/instructeur.service';
import staffService from '@/services/staff.service';
import anneeExerciceService from '@/services/annee-exercice.service';
import { useAuthStore } from '@/stores/auth.store';
import type { 
  Staff, 
  InstructeurSuggestion, 
  RoleStaff, 
  AnneeExercice,
  CreateStaffRequest,
  UpdateStaffRequest
} from '@/types';

interface Props {
  mode: 'create' | 'edit';
  staff?: Staff;
}

const props = defineProps<Props>();

const authStore = useAuthStore();

// État pour la création
const searchQuery = ref('');
const suggestions = ref<InstructeurSuggestion[]>([]);
const showSuggestions = ref(false);
const selectedInstructeur = ref<InstructeurSuggestion | null>(null);
const showCreateInstructeurForm = ref(false);

// Données pour le nouvel instructeur
const newInstructeurData = ref({
  nom: '',
  prenom: '',
  genre: 'M',
  totem: '',
  telephone: '',
  estChefGuide: false
});

// État pour l'édition
const instructeurData = ref({
  nom: '',
  prenom: '',
  genre: 'M',
  totem: '',
  telephone: '',
  estChefGuide: false
});

// Formulaire principal
const formData = ref<CreateStaffRequest>({
  instructeurId: 0,
  roleId: 0,
  anneeExerciceId: 0
});

const roles = ref<RoleStaff[]>([]);
const anneesExercice = ref<AnneeExercice[]>([]);
const loading = ref(false);

let searchTimeout: any = null;

const isCoDirecteur = computed(() => {
  return authStore.user?.role === 'Co_Directeur' || authStore.user?.role === 'Directeur';
});

onMounted(async () => {
  await loadData();
  
  if (props.mode === 'edit' && props.staff) {
    // Charger les données du staff existant
    const role = roles.value.find(r => r.roleName === props.staff!.roleName);
    
    formData.value = {
      instructeurId: props.staff.instructeurId,
      roleId: role?.id || 0,
      anneeExerciceId: props.staff.anneeExerciceId
    };

    instructeurData.value = {
      nom: props.staff.instructeurNom,
      prenom: props.staff.instructeurPrenom,
      genre: props.staff.instructeurGenre,
      totem: props.staff.instructeurTotem || '',
      telephone: props.staff.instructeurTelephone || '',
      estChefGuide: props.staff.instructeurEstChefGuide
    };
  }
});

async function loadData() {
  try {
    const [rolesData, anneesData] = await Promise.all([
      staffService.getAllRoles(),
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

function handleSearchInput(event: any) {
  const query = event.target.value || '';
  searchQuery.value = query;

  clearTimeout(searchTimeout);
  
  if (query.length >= 1) {
    // Masquer le formulaire de création quand on recommence une recherche
    showCreateInstructeurForm.value = false;
    
    searchTimeout = setTimeout(() => {
      searchInstructeurs(query);
    }, 300);
  } else {
    suggestions.value = [];
    showSuggestions.value = false;
    showCreateInstructeurForm.value = false;
  }
}

async function searchInstructeurs(query: string) {
  try {
    const results = await instructeurService.searchInstructeurs(query);
    suggestions.value = results;
    showSuggestions.value = true;
  } catch (error: any) {
    showToast(error.message || 'Erreur lors de la recherche', 'danger');
  }
}

function selectInstructeur(instructeur: InstructeurSuggestion) {
  selectedInstructeur.value = instructeur;
  formData.value.instructeurId = instructeur.id;
  searchQuery.value = instructeur.nomComplet;
  showSuggestions.value = false;
}

function clearInstructeur() {
  selectedInstructeur.value = null;
  formData.value.instructeurId = 0;
  searchQuery.value = '';
  suggestions.value = [];
  showSuggestions.value = false;
  showCreateInstructeurForm.value = false;
  newInstructeurData.value = {
    nom: '',
    prenom: '',
    genre: 'M',
    totem: '',
    telephone: '',
    estChefGuide: false
  };
}

function cancelCreateInstructeur() {
  showCreateInstructeurForm.value = false;
  newInstructeurData.value = {
    nom: '',
    prenom: '',
    genre: 'M',
    totem: '',
    telephone: '',
    estChefGuide: false
  };
}

async function handleCreateInstructeur() {
  try {
    // Validation des champs obligatoires
    if (!newInstructeurData.value.nom || !newInstructeurData.value.prenom || !newInstructeurData.value.genre) {
      showToast('Nom, prénom et genre sont obligatoires', 'warning');
      return;
    }

    loading.value = true;
    
    // Créer l'instructeur
    const createdInstructeur = await instructeurService.createInstructeur(newInstructeurData.value);
    
    // Sélectionner automatiquement l'instructeur créé
    selectedInstructeur.value = {
      id: createdInstructeur.id,
      nom: createdInstructeur.nom,
      prenom: createdInstructeur.prenom,
      nomComplet: `${createdInstructeur.nom} ${createdInstructeur.prenom}`
    };
    
    formData.value.instructeurId = createdInstructeur.id;
    searchQuery.value = `${createdInstructeur.nom} ${createdInstructeur.prenom}`;
    showCreateInstructeurForm.value = false;
    showSuggestions.value = false;
    
    showToast('Instructeur créé avec succès', 'success');
  } catch (error: any) {
    showToast(error.response?.data?.message || 'Erreur lors de la création de l\'instructeur', 'danger');
  } finally {
    loading.value = false;
  }
}

async function handleSubmit() {
  loading.value = true;
  
  try {
    if (props.mode === 'create') {
      if (!selectedInstructeur.value) {
        showToast('Veuillez sélectionner un instructeur', 'warning');
        return;
      }
      
      await staffService.createStaff(formData.value);
      showToast('Staff créé avec succès', 'success');
    } else if (props.staff) {
      const updateData: UpdateStaffRequest = {
        roleId: formData.value.roleId
      };
      
      // Si Co_Directeur, inclure les modifications de l'instructeur
      if (isCoDirecteur.value) {
        updateData.nom = instructeurData.value.nom;
        updateData.prenom = instructeurData.value.prenom;
        updateData.genre = instructeurData.value.genre;
        updateData.totem = instructeurData.value.totem || undefined;
        updateData.telephone = instructeurData.value.telephone || undefined;
        updateData.estChefGuide = instructeurData.value.estChefGuide;
      }
      
      await staffService.updateStaff(props.staff.id, updateData);
      showToast('Staff modifié avec succès', 'success');
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

.suggestions-list {
  margin-top: 8px;
  border: 1px solid var(--ion-color-medium);
  border-radius: 8px;
  max-height: 200px;
  overflow-y: auto;
}

.selected-instructor {
  background-color: var(--ion-color-light);
  border-radius: 8px;
  padding: 8px;
}

.no-results {
  text-align: center;
  padding: 16px;
}
</style>
