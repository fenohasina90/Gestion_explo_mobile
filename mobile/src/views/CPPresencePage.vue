<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button :default-href="`/classes-progressives`"></ion-back-button>
          <img src="/assets/logo.png" alt="Logo" class="header-logo" style="margin-left: 8px;" />
        </ion-buttons>
        <ion-title>{{ cp?.etat === 1 ? 'Participants' : 'Présence CP' }}</ion-title>
        <ion-buttons slot="end">
          <ion-button 
            v-if="cp?.etat !== 1"
            @click="handleSave" 
            :disabled="saving"
            color="primary"
          >
            <ion-icon :icon="checkmarkOutline" slot="start"></ion-icon>
            Enregistrer
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <!-- Info CP -->
      <div v-if="cp" class="ion-padding-horizontal ion-padding-top">
        <ion-card>
          <ion-card-header>
            <ion-card-title>{{ formatDate(cp.dateCp) }}</ion-card-title>
            <ion-card-subtitle>
              {{ cp.heureDebut }} - {{ cp.heureFin }}
            </ion-card-subtitle>
          </ion-card-header>
        </ion-card>
        
        <!-- Message d'information si clôturée -->
        <ion-card v-if="cp.etat === 1" color="light">
          <ion-card-content>
            ℹ️ Mode consultation - Cette CP est clôturée.
          </ion-card-content>
        </ion-card>
      </div>

      <!-- Filtres -->
      <div class="ion-padding-horizontal">
        <ion-segment v-model="filtreType" @ionChange="onFiltreChange">
          <ion-segment-button value="tous">
            <ion-label>Tous</ion-label>
          </ion-segment-button>
          <ion-segment-button value="enfants">
            <ion-label>Enfants ({{ selectedEnfants.length }})</ion-label>
          </ion-segment-button>
          <ion-segment-button value="staff">
            <ion-label>Staff ({{ selectedStaff.length }})</ion-label>
          </ion-segment-button>
        </ion-segment>

        <ion-item v-if="filtreType !== 'staff'">
          <ion-label>Filtrer par classe</ion-label>
          <ion-select 
            v-model="selectedClasseId" 
            placeholder="Toutes les classes"
            @ionChange="onClasseFilterChange"
          >
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
      </div>

      <!-- Liste des enfants -->
      <div v-if="filtreType === 'tous' || filtreType === 'enfants'">
        <ion-list-header>
          <ion-label>
            Enfants
            <ion-note style="margin-left: 8px;">
              ({{ selectedEnfants.length }}/{{ filteredEnfants.length }})
            </ion-note>
          </ion-label>
          <ion-button 
            v-if="cp?.etat !== 1"
            size="small" 
            fill="outline"
            @click="toggleSelectAllEnfants"
          >
            {{ selectedEnfants.length === filteredEnfants.length ? 'Désélectionner tout' : 'Sélectionner tout' }}
          </ion-button>
        </ion-list-header>

        <ion-list>
          <ion-item v-for="enfant in filteredEnfants" :key="enfant.inscriptionId">
            <ion-checkbox 
              slot="start" 
              :checked="isEnfantSelected(enfant.inscriptionId)"
              @ionChange="toggleEnfant(enfant.inscriptionId)"
              :disabled="cp?.etat === 1"
            ></ion-checkbox>
            <ion-label>
              <h3>{{ enfant.prenom }} {{ enfant.nom }}</h3>
              <p>{{ enfant.classeNom }} • {{ enfant.genre }}</p>
            </ion-label>
          </ion-item>

          <div v-if="filteredEnfants.length === 0" class="ion-padding ion-text-center">
            <ion-note>Aucun enfant disponible</ion-note>
          </div>
        </ion-list>
      </div>

      <!-- Liste du staff -->
      <div v-if="filtreType === 'tous' || filtreType === 'staff'">
        <ion-list-header>
          <ion-label>
            Staff
            <ion-note style="margin-left: 8px;">
              ({{ selectedStaff.length }}/{{ personnesDisponibles.staff.length }})
            </ion-note>
          </ion-label>
          <ion-button 
            v-if="cp?.etat !== 1"
            size="small" 
            fill="outline"
            @click="toggleSelectAllStaff"
          >
            {{ selectedStaff.length === personnesDisponibles.staff.length ? 'Désélectionner tout' : 'Sélectionner tout' }}
          </ion-button>
        </ion-list-header>

        <ion-list>
          <ion-item v-for="staff in personnesDisponibles.staff" :key="staff.staffId">
            <ion-checkbox 
              slot="start" 
              :checked="isStaffSelected(staff.staffId)"
              @ionChange="toggleStaff(staff.staffId)"
              :disabled="cp?.etat === 1"
            ></ion-checkbox>
            <ion-label>
              <h3>{{ staff.prenom }} {{ staff.nom }}</h3>
              <p>{{ staff.role }}<span v-if="staff.totem"> • {{ staff.totem }}</span></p>
            </ion-label>
          </ion-item>

          <div v-if="personnesDisponibles.staff.length === 0" class="ion-padding ion-text-center">
            <ion-note>Aucun staff disponible</ion-note>
          </div>
        </ion-list>
      </div>

      <!-- Message de chargement -->
      <div v-if="loading" class="ion-padding ion-text-center">
        <ion-spinner></ion-spinner>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
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
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardSubtitle,
  IonCardContent,
  IonList,
  IonListHeader,
  IonItem,
  IonLabel,
  IonCheckbox,
  IonSegment,
  IonSegmentButton,
  IonSelect,
  IonSelectOption,
  IonNote,
  IonSpinner,
  alertController,
  toastController,
} from '@ionic/vue';
import { checkmarkOutline } from 'ionicons/icons';
import cpPresenceService from '@/services/cp-presence.service';
import classeProgressiveService from '@/services/classe-progressive.service';
import type { 
  PersonnesDisponiblesResponse, 
  ClasseProgressive,
  ParticipantEnfantDto,
  ParticipantStaffDto
} from '@/types';

// Route et router
const route = useRoute();
const router = useRouter();
const classeProgressiveId = parseInt(route.params.id as string);

// États
const loading = ref(true);
const saving = ref(false);
const cp = ref<ClasseProgressive | null>(null);
const personnesDisponibles = ref<PersonnesDisponiblesResponse>({
  enfants: [],
  staff: []
});
const selectedEnfants = ref<number[]>([]);
const selectedStaff = ref<number[]>([]);
const filtreType = ref<'tous' | 'enfants' | 'staff'>('tous');
const selectedClasseId = ref<number | null>(null);

// Classes pour le filtre
const classes = ref([
  { id: 1, nom: 'Ami' },
  { id: 2, nom: 'Compagnon' },
  { id: 3, nom: 'Eclaireur' },
  { id: 4, nom: 'Pionnier' },
  { id: 5, nom: 'Voyageur' },
  { id: 6, nom: 'Guide' }
]);

// Computed - Enfants filtrés par classe
const filteredEnfants = computed(() => {
  if (!selectedClasseId.value) {
    return personnesDisponibles.value.enfants;
  }
  return personnesDisponibles.value.enfants.filter(
    e => e.classeId === selectedClasseId.value
  );
});

// Fonctions utilitaires
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString('fr-FR', { 
    weekday: 'long', 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  });
};

// Fonctions de sélection enfants
const isEnfantSelected = (inscriptionId: number) => {
  return selectedEnfants.value.includes(inscriptionId);
};

const toggleEnfant = (inscriptionId: number) => {
  const index = selectedEnfants.value.indexOf(inscriptionId);
  if (index > -1) {
    selectedEnfants.value.splice(index, 1);
  } else {
    selectedEnfants.value.push(inscriptionId);
  }
};

const toggleSelectAllEnfants = () => {
  if (selectedEnfants.value.length === filteredEnfants.value.length) {
    // Désélectionner tous les enfants filtrés
    const filteredIds = filteredEnfants.value.map(e => e.inscriptionId);
    selectedEnfants.value = selectedEnfants.value.filter(
      id => !filteredIds.includes(id)
    );
  } else {
    // Sélectionner tous les enfants filtrés
    const filteredIds = filteredEnfants.value.map(e => e.inscriptionId);
    const uniqueIds = new Set([...selectedEnfants.value, ...filteredIds]);
    selectedEnfants.value = Array.from(uniqueIds);
  }
};

// Fonctions de sélection staff
const isStaffSelected = (staffId: number) => {
  return selectedStaff.value.includes(staffId);
};

const toggleStaff = (staffId: number) => {
  const index = selectedStaff.value.indexOf(staffId);
  if (index > -1) {
    selectedStaff.value.splice(index, 1);
  } else {
    selectedStaff.value.push(staffId);
  }
};

const toggleSelectAllStaff = () => {
  if (selectedStaff.value.length === personnesDisponibles.value.staff.length) {
    selectedStaff.value = [];
  } else {
    selectedStaff.value = personnesDisponibles.value.staff.map(s => s.staffId);
  }
};

// Gestionnaires de filtres
const onFiltreChange = () => {
  // Pas besoin de logique supplémentaire, le v-if gère l'affichage
};

const onClasseFilterChange = () => {
  // Pas besoin de logique supplémentaire, computed gère le filtrage
};

// Charger les données
const loadData = async () => {
  try {
    loading.value = true;

    // Charger les infos de la CP
    cp.value = await classeProgressiveService.getCPById(classeProgressiveId);

    // Charger les personnes disponibles
    personnesDisponibles.value = await cpPresenceService.getPersonnesDisponibles(classeProgressiveId);

    // Charger les participants actuels (pré-sélection)
    const participants = await cpPresenceService.getParticipants(classeProgressiveId);
    
    if (participants.enfants) {
      selectedEnfants.value = participants.enfants.map(e => e.inscriptionId);
    }
    
    if (participants.staff) {
      selectedStaff.value = participants.staff.map(s => s.staffId);
    }

  } catch (error: any) {
    console.error('Erreur lors du chargement:', error);
    const toast = await toastController.create({
      message: error.message || 'Erreur lors du chargement',
      duration: 3000,
      color: 'danger',
      position: 'top'
    });
    await toast.present();
  } finally {
    loading.value = false;
  }
};

// Enregistrer la présence
const handleSave = async () => {
  // Vérifier si la CP est clôturée
  if (cp.value?.etat === 1) {
    const toast = await toastController.create({
      message: 'Impossible d\'enregistrer la présence : la CP est clôturée',
      duration: 2000,
      color: 'warning',
      position: 'top'
    });
    await toast.present();
    return;
  }
  
  // Confirmation
  const alert = await alertController.create({
    header: 'Confirmer',
    message: `Enregistrer la présence de ${selectedEnfants.value.length} enfant(s) et ${selectedStaff.value.length} staff(s) ?`,
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Confirmer',
        handler: async () => {
          try {
            saving.value = true;

            await cpPresenceService.enregistrerPresence({
              classeProgressiveId: classeProgressiveId,
              enfantsPresents: selectedEnfants.value,
              staffPresents: selectedStaff.value
            });

            const toast = await toastController.create({
              message: 'Présence enregistrée avec succès',
              duration: 2000,
              color: 'success',
              position: 'top'
            });
            await toast.present();

            // Retour à la liste des classes progressives
            router.push('/classes-progressives');

          } catch (error: any) {
            console.error('Erreur lors de l\'enregistrement:', error);
            const toast = await toastController.create({
              message: error.message || 'Erreur lors de l\'enregistrement',
              duration: 3000,
              color: 'danger',
              position: 'top'
            });
            await toast.present();
          } finally {
            saving.value = false;
          }
        }
      }
    ]
  });

  await alert.present();
};

// Lifecycle
onMounted(() => {
  loadData();
});
</script>

<style scoped>
.header-logo {
  height: 32px;
  width: auto;
}

ion-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-right: 16px;
}

ion-note {
  font-size: 0.9rem;
}
</style>
