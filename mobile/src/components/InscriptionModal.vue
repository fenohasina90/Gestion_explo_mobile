<template>
  <ion-modal :is-open="isOpen" @didDismiss="handleClose">
    <ion-header>
      <ion-toolbar>
        <ion-title>{{ editMode ? "Modifier l'assurance" : "Nouvelle inscription" }}</ion-title>
        <ion-buttons slot="end">
          <ion-button @click="handleClose">
            <ion-icon :icon="closeOutline"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <form @submit.prevent="handleSubmit">
        <!-- Sélection année d'exercice -->
        <ion-item v-if="!editMode">
          <ion-label position="stacked">Année d'exercice *</ion-label>
          <ion-select
            v-model="formData.anneeExerciceId"
            placeholder="Sélectionner"
            interface="action-sheet"
          >
            <ion-select-option v-for="annee in anneesExercice" :key="annee.id" :value="annee.id">
              {{ getAnneeDisplay(annee.annee) }}
            </ion-select-option>
          </ion-select>
        </ion-item>

        <!-- Auto-complétion enfant -->
        <div v-if="!editMode && formData.anneeExerciceId > 0">
          <ion-item>
            <ion-label position="stacked">Rechercher un enfant (10-15 ans) *</ion-label>
            <ion-input
              v-model="enfantSearchQuery"
              placeholder="Nom ou prénom..."
              :disabled="!!selectedEnfant"
              @ionInput="onEnfantSearchChange"
            ></ion-input>
          </ion-item>

          <ion-button
            v-if="selectedEnfant"
            fill="clear"
            size="small"
            @click="clearEnfant"
          >
            Changer
          </ion-button>

          <!-- Suggestions enfants -->
          <ion-list v-if="enfantSuggestions.length > 0">
            <ion-item
              v-for="enfant in enfantSuggestions"
              :key="enfant.id"
              button
              @click="selectEnfant(enfant)"
            >
              <ion-label>
                <h3>{{ enfant.nomComplet }}</h3>
                <p>
                  <ion-chip :color="enfant.genre === 'GARCON' ? 'primary' : 'danger'" size="small">
                    {{ enfant.genre }}
                  </ion-chip>
                  <ion-chip size="small">{{ enfant.age }} ans</ion-chip>
                </p>
                <p class="ion-text-wrap">Parent: {{ enfant.parentNomComplet }}</p>
              </ion-label>
            </ion-item>
          </ion-list>

          <!-- Formulaire création enfant -->
          <ion-card v-if="showEnfantForm">
            <ion-card-header>
              <ion-card-title>Créer un nouvel enfant</ion-card-title>
            </ion-card-header>
            <ion-card-content>
              <!-- Auto-complétion parent -->
              <ion-item>
                <ion-label position="stacked">Rechercher un parent *</ion-label>
                <ion-input
                  v-model="parentSearchQuery"
                  placeholder="Nom ou prénom du parent..."
                  :disabled="!!selectedParent"
                  @ionInput="onParentSearchChange"
                ></ion-input>
              </ion-item>

              <ion-button
                v-if="selectedParent"
                fill="clear"
                size="small"
                @click="clearParent"
              >
                Changer
              </ion-button>

              <!-- Suggestions parents -->
              <ion-list v-if="parentSuggestions.length > 0">
                <ion-item
                  v-for="parent in parentSuggestions"
                  :key="parent.id"
                  button
                  @click="selectParent(parent)"
                >
                  <ion-label>
                    <h3>{{ parent.nomComplet }}</h3>
                    <p>{{ parent.telephone }} | {{ parent.adresse }}</p>
                  </ion-label>
                </ion-item>
              </ion-list>

              <!-- Formulaire création parent -->
              <ion-card v-if="showParentForm">
                <ion-card-header>
                  <ion-card-subtitle>Créer un nouveau parent</ion-card-subtitle>
                </ion-card-header>
                <ion-card-content>
                  <ion-item>
                    <ion-label position="stacked">Nom *</ion-label>
                    <ion-input v-model="parentFormData.nom" required></ion-input>
                  </ion-item>

                  <ion-item>
                    <ion-label position="stacked">Prénom *</ion-label>
                    <ion-input v-model="parentFormData.prenom" required></ion-input>
                  </ion-item>

                  <ion-item>
                    <ion-label position="stacked">Téléphone</ion-label>
                    <ion-input v-model="parentFormData.telephone" type="tel"></ion-input>
                  </ion-item>

                  <ion-item>
                    <ion-label position="stacked">Adresse</ion-label>
                    <ion-input v-model="parentFormData.adresse"></ion-input>
                  </ion-item>

                  <ion-button expand="block" @click="createParent">
                    Créer le parent
                  </ion-button>
                </ion-card-content>
              </ion-card>

              <!-- Informations enfant -->
              <div v-if="selectedParent">
                <ion-item>
                  <ion-label position="stacked">Nom *</ion-label>
                  <ion-input v-model="enfantFormData.nom" required></ion-input>
                </ion-item>

                <ion-item>
                  <ion-label position="stacked">Prénom *</ion-label>
                  <ion-input v-model="enfantFormData.prenom" required></ion-input>
                </ion-item>

                <ion-item>
                  <ion-label position="stacked">Genre *</ion-label>
                  <ion-select v-model="enfantFormData.genre" interface="action-sheet">
                    <ion-select-option value="GARCON">Garçon</ion-select-option>
                    <ion-select-option value="FILLE">Fille</ion-select-option>
                  </ion-select>
                </ion-item>

                <ion-item>
                  <ion-label position="stacked">Date de naissance *</ion-label>
                  <ion-datetime-button datetime="birthdate"></ion-datetime-button>
                  <ion-modal :keep-contents-mounted="true">
                    <ion-datetime
                      id="birthdate"
                      v-model="birthDate"
                      presentation="date"
                      :max="new Date().toISOString()"
                    ></ion-datetime>
                  </ion-modal>
                </ion-item>

                <!-- Affichage de l'âge et classe recommandée -->
                <ion-note v-if="birthDate && calculatedAge" class="ion-padding">
                  <div v-if="calculatedAge < 10 || calculatedAge > 15" class="ion-text-wrap">
                    <ion-icon :icon="warningOutline" color="warning"></ion-icon>
                    Âge en {{ selectedAnneeYear }}: {{ calculatedAge }} ans - Hors limites (10-15 ans requis)
                  </div>
                  <div v-else-if="recommendedClasse" class="ion-text-wrap">
                    <ion-icon :icon="informationCircleOutline" color="primary"></ion-icon>
                    Âge en {{ selectedAnneeYear }}: {{ calculatedAge }} ans → Classe <strong>{{ recommendedClasse.nom }}</strong>
                  </div>
                  <div v-else class="ion-text-wrap">
                    <ion-icon :icon="warningOutline" color="warning"></ion-icon>
                    Âge en {{ selectedAnneeYear }}: {{ calculatedAge }} ans - Aucune classe disponible
                  </div>
                </ion-note>

                <ion-button expand="block" @click="createEnfant" :disabled="!isEnfantValid">
                  Créer l'enfant
                </ion-button>
              </div>
            </ion-card-content>
          </ion-card>

          <!-- Sélection classe -->
          <div v-if="selectedEnfant">
            <ion-note class="ion-padding ion-text-wrap" v-if="selectedEnfantRecommendedClasse">
              <ion-icon :icon="informationCircleOutline" color="primary"></ion-icon>
              {{ selectedEnfant.prenom }} a {{ selectedEnfant.age }} ans, la classe appropriée est
              <strong>{{ selectedEnfantRecommendedClasse.nom }}</strong>
            </ion-note>
            <ion-note class="ion-padding ion-text-wrap" v-else color="warning">
              <ion-icon :icon="warningOutline" color="warning"></ion-icon>
              Aucune classe disponible pour un enfant de {{ selectedEnfant.age }} ans
            </ion-note>

            <ion-item>
              <ion-label position="stacked">Classe *</ion-label>
              <ion-select
                v-model="formData.classeId"
                placeholder="Sélectionner"
                interface="action-sheet"
                :disabled="!selectedEnfantRecommendedClasse"
              >
                <ion-select-option
                  v-for="classe in availableClasses"
                  :key="classe.id"
                  :value="classe.id"
                >
                  {{ classe.nom }} ({{ classe.age }} ans)
                </ion-select-option>
              </ion-select>
            </ion-item>
          </div>
        </div>

        <!-- Assurance -->
        <ion-item>
          <ion-label>Assurance souscrite</ion-label>
          <ion-toggle v-model="formData.estAssurance"></ion-toggle>
        </ion-item>
      </form>
    </ion-content>

    <ion-footer>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-button @click="handleClose">Annuler</ion-button>
        </ion-buttons>
        <ion-buttons slot="end">
          <ion-button
            @click="handleSubmit"
            :disabled="!isFormValid"
            color="primary"
            fill="solid"
          >
            {{ editMode ? "Mettre à jour" : "Créer l'inscription" }}
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-footer>
  </ion-modal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import {
  IonModal,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonButtons,
  IonButton,
  IonContent,
  IonFooter,
  IonItem,
  IonLabel,
  IonInput,
  IonSelect,
  IonSelectOption,
  IonList,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardSubtitle,
  IonCardContent,
  IonChip,
  IonNote,
  IonToggle,
  IonDatetime,
  IonDatetimeButton,
  IonIcon,
} from '@ionic/vue';
import {
  closeOutline,
  warningOutline,
  informationCircleOutline,
} from 'ionicons/icons';
import enfantService from '@/services/enfant.service';
import parentService from '@/services/parent.service';
import inscriptionService from '@/services/inscription.service';
import type {
  EnfantSuggestion,
  ParentSuggestion,
  CreateEnfantRequest,
  CreateParentRequest,
  CreateInscriptionRequest,
  Classe,
  AnneeExercice,
} from '@/types';

interface Props {
  isOpen: boolean;
  classes: Classe[];
  anneesExercice: AnneeExercice[];
  editMode?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  editMode: false,
});

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'success', message: string): void;
  (e: 'error', message: string): void;
}>();

// États
const formData = ref<CreateInscriptionRequest>({
  enfantId: 0,
  anneeExerciceId: 0,
  classeId: 0,
  estAssurance: false,
});

const enfantSearchQuery = ref('');
const enfantSuggestions = ref<EnfantSuggestion[]>([]);
const selectedEnfant = ref<EnfantSuggestion | null>(null);
const showEnfantForm = ref(false);

const parentSearchQuery = ref('');
const parentSuggestions = ref<ParentSuggestion[]>([]);
const selectedParent = ref<ParentSuggestion | null>(null);
const showParentForm = ref(false);

// Date de naissance séparée pour ion-datetime (accepte undefined)
const birthDate = ref<string | undefined>(undefined);

const enfantFormData = ref<CreateEnfantRequest>({
  nom: '',
  prenom: '',
  genre: 'GARCON',
  dateNaissance: '',
  adresse: '',
  parentId: 0,
  bapteme: '',
});

const parentFormData = ref<CreateParentRequest>({
  nom: '',
  prenom: '',
  adresse: '',
  telephone: '',
});

// Computed
const getAnneeDisplay = (annee: string) => {
  return annee.substring(0, 4);
};

const selectedAnneeYear = computed(() => {
  const annee = props.anneesExercice.find(a => a.id === formData.value.anneeExerciceId);
  return annee ? new Date(annee.annee).getFullYear() : null;
});

const calculatedAge = computed(() => {
  if (!birthDate.value || !selectedAnneeYear.value) return null;
  const birthYear = new Date(birthDate.value).getFullYear();
  return selectedAnneeYear.value - birthYear;
});

const recommendedClasse = computed(() => {
  if (!calculatedAge.value) return null;
  return props.classes.find(c => c.age === calculatedAge.value);
});

const selectedEnfantRecommendedClasse = computed(() => {
  if (!selectedEnfant.value) return null;
  return props.classes.find(c => c.age === selectedEnfant.value!.age);
});

const availableClasses = computed(() => {
  if (!selectedEnfant.value) return [];
  return props.classes.filter(c => c.age === selectedEnfant.value!.age);
});

const isEnfantValid = computed(() => {
  return (
    enfantFormData.value.nom &&
    enfantFormData.value.prenom &&
    birthDate.value &&
    selectedParent.value &&
    calculatedAge.value &&
    calculatedAge.value >= 10 &&
    calculatedAge.value <= 15
  );
});

const isFormValid = computed(() => {
  if (props.editMode) return true;
  return (
    selectedEnfant.value &&
    formData.value.classeId > 0 &&
    formData.value.anneeExerciceId > 0
  );
});

// Watchers
let enfantSearchTimeout: ReturnType<typeof setTimeout>;
watch(enfantSearchQuery, (newVal) => {
  clearTimeout(enfantSearchTimeout);
  if (newVal.length >= 1 && formData.value.anneeExerciceId) {
    enfantSearchTimeout = setTimeout(() => {
      searchEnfants();
    }, 300);
  } else {
    enfantSuggestions.value = [];
    showEnfantForm.value = false;
  }
});

let parentSearchTimeout: ReturnType<typeof setTimeout>;
watch(parentSearchQuery, (newVal) => {
  clearTimeout(parentSearchTimeout);
  if (newVal.length >= 1) {
    parentSearchTimeout = setTimeout(() => {
      searchParents();
    }, 300);
  } else {
    parentSuggestions.value = [];
    showParentForm.value = false;
  }
});

// Auto-sélection de la classe quand un enfant est sélectionné
watch(selectedEnfant, (newVal) => {
  if (newVal && selectedEnfantRecommendedClasse.value) {
    formData.value.classeId = selectedEnfantRecommendedClasse.value.id;
  }
});

// Méthodes
const searchEnfants = async () => {
  try {
    const results = await enfantService.searchEnfants(
      enfantSearchQuery.value,
      formData.value.anneeExerciceId
    );
    enfantSuggestions.value = results;
    showEnfantForm.value = results.length === 0;
  } catch (error) {
    console.error('Erreur lors de la recherche d\'enfants:', error);
  }
};

const searchParents = async () => {
  try {
    const results = await parentService.searchParents(parentSearchQuery.value);
    parentSuggestions.value = results;
    showParentForm.value = results.length === 0;
  } catch (error) {
    console.error('Erreur lors de la recherche de parents:', error);
  }
};

const selectEnfant = (enfant: EnfantSuggestion) => {
  selectedEnfant.value = enfant;
  enfantSearchQuery.value = enfant.nomComplet;
  formData.value.enfantId = enfant.id;
  enfantSuggestions.value = [];
};

const clearEnfant = () => {
  selectedEnfant.value = null;
  enfantSearchQuery.value = '';
  formData.value.enfantId = 0;
  formData.value.classeId = 0;
};

const selectParent = (parent: ParentSuggestion) => {
  selectedParent.value = parent;
  parentSearchQuery.value = parent.nomComplet;
  enfantFormData.value.parentId = parent.id;
  parentSuggestions.value = [];
};

const clearParent = () => {
  selectedParent.value = null;
  parentSearchQuery.value = '';
  enfantFormData.value.parentId = 0;
};

const createParent = async () => {
  try {
    const newParent = await parentService.createParent(parentFormData.value);
    selectedParent.value = {
      id: newParent.id,
      nom: newParent.nom,
      prenom: newParent.prenom,
      telephone: newParent.telephone,
      adresse: newParent.adresse,
      nomComplet: `${newParent.nom} ${newParent.prenom}`,
    };
    parentSearchQuery.value = `${newParent.nom} ${newParent.prenom}`;
    enfantFormData.value.parentId = newParent.id;
    showParentForm.value = false;
    emit('success', 'Parent créé avec succès');
  } catch (error: any) {
    emit('error', error.response?.data?.message || 'Erreur lors de la création du parent');
  }
};

const createEnfant = async () => {
  if (!selectedParent.value || enfantFormData.value.parentId === 0) {
    emit('error', 'Veuillez sélectionner ou créer un parent');
    return;
  }

  if (!birthDate.value) {
    emit('error', 'Veuillez sélectionner une date de naissance');
    return;
  }

  try {
    // Convertir la date ISO en format date simple (YYYY-MM-DD)
    const dateOnly = birthDate.value.split('T')[0];
    const requestData = {
      ...enfantFormData.value,
      dateNaissance: dateOnly,
    };
    
    const newEnfant = await enfantService.createEnfant(
      requestData,
      formData.value.anneeExerciceId
    );
    
    // Trouver la classe recommandée pour cet enfant
    const recommendedClass = props.classes.find(c => c.age === newEnfant.age);
    
    selectedEnfant.value = {
      id: newEnfant.id,
      nom: newEnfant.nom,
      prenom: newEnfant.prenom,
      genre: newEnfant.genre,
      dateNaissance: newEnfant.dateNaissance,
      age: newEnfant.age,
      parentNom: newEnfant.parentNom,
      parentPrenom: newEnfant.parentPrenom,
      nomComplet: `${newEnfant.nom} ${newEnfant.prenom}`,
      parentNomComplet: `${newEnfant.parentNom} ${newEnfant.parentPrenom}`,
      classeId: recommendedClass?.id || 0,
      classeNom: recommendedClass?.nom || '',
    };
    enfantSearchQuery.value = `${newEnfant.nom} ${newEnfant.prenom}`;
    formData.value.enfantId = newEnfant.id;
    showEnfantForm.value = false;
    emit('success', 'Enfant créé avec succès');
  } catch (error: any) {
    emit('error', error.response?.data?.message || 'Erreur lors de la création de l\'enfant');
  }
};

const handleSubmit = async () => {
  try {
    await inscriptionService.createInscription(formData.value);
    emit('success', 'Inscription créée avec succès');
    handleClose();
  } catch (error: any) {
    emit('error', error.response?.data?.message || 'Erreur lors de l\'enregistrement');
  }
};

const handleClose = () => {
  resetForm();
  emit('close');
};

const resetForm = () => {
  formData.value = {
    enfantId: 0,
    anneeExerciceId: 0,
    classeId: 0,
    estAssurance: false,
  };
  enfantSearchQuery.value = '';
  selectedEnfant.value = null;
  enfantSuggestions.value = [];
  showEnfantForm.value = false;
  parentSearchQuery.value = '';
  selectedParent.value = null;
  parentSuggestions.value = [];
  showParentForm.value = false;
  birthDate.value = undefined;
  enfantFormData.value = {
    nom: '',
    prenom: '',
    genre: 'GARCON',
    dateNaissance: '',
    adresse: '',
    parentId: 0,
    bapteme: '',
  };
  parentFormData.value = {
    nom: '',
    prenom: '',
    adresse: '',
    telephone: '',
  };
};

const onEnfantSearchChange = () => {
  // Le debounce est géré par le watcher
};

const onParentSearchChange = () => {
  // Le debounce est géré par le watcher
};
</script>

<style scoped>
ion-note {
  display: block;
  margin: 8px 0;
}

ion-card {
  margin: 16px 0;
}

ion-chip {
  margin-right: 4px;
}
</style>
