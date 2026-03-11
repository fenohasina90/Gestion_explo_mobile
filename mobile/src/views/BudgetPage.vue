<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <img src="/assets/logo.png" alt="Logo" class="header-logo" />
        </ion-buttons>
        <ion-title>Budget & Activités</ion-title>
        <ion-buttons slot="end">
          <ion-button @click="exportPdf" :disabled="loading || !budget" title="Exporter en PDF">
            <ion-icon :icon="documentTextOutline"></ion-icon>
          </ion-button>
          <ion-button @click="loadData" :disabled="loading">
            <ion-icon :icon="syncOutline"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <!-- Messages -->
      <ion-toast
        :is-open="!!error"
        :message="error || ''"
        :duration="3000"
        position="top"
        color="danger"
        @didDismiss="error = null"
      ></ion-toast>
      
      <ion-toast
        :is-open="!!success"
        :message="success || ''"
        :duration="2000"
        position="top"
        color="success"
        @didDismiss="success = null"
      ></ion-toast>

      <!-- Loading -->
      <div v-if="loading" class="ion-text-center ion-padding">
        <ion-spinner></ion-spinner>
        <p>Chargement...</p>
      </div>

      <div v-else class="ion-padding">
        <!-- Sélection année d'exercice -->
        <ion-card>
          <ion-card-header>
            <ion-card-title>Année</ion-card-title>
          </ion-card-header>
          <ion-card-content>
            <ion-item>
              <ion-label>Sélectionner l'année</ion-label>
              <ion-select v-model="selectedAnneeId" @ionChange="loadBudgetAndActivites">
                <ion-select-option v-for="annee in anneesExercice" :key="annee.id" :value="annee.id">
                  {{ new Date(annee.annee).getFullYear() }}
                </ion-select-option>
              </ion-select>
            </ion-item>
          </ion-card-content>
        </ion-card>

        <!-- Budget info -->
        <ion-card v-if="budget">
          <ion-card-header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <ion-card-title>Budget Global</ion-card-title>
              <ion-chip :color="getBudgetStatusColor(budget.status)">
                {{ budget.status }}
              </ion-chip>
            </div>
          </ion-card-header>
          
          <ion-card-content>
            <ion-grid>
              <ion-row>
                <ion-col size="6">
                  <ion-text color="medium">
                    <p>Montant total</p>
                  </ion-text>
                  <ion-text color="primary">
                    <h2>{{ formatMontant(budget.montant) }}</h2>
                  </ion-text>
                </ion-col>
                <ion-col size="6">
                  <ion-text color="medium">
                    <p>Activités</p>
                  </ion-text>
                  <ion-text color="primary">
                    <h2>{{ budget.nombreActivites }}</h2>
                  </ion-text>
                </ion-col>
              </ion-row>
            </ion-grid>

            <!-- Modifier statut budget (Directeur/Co_Directeur) -->
            <ion-item v-if="canEditBudget()">
              <ion-label>Statut</ion-label>
              <ion-select v-model="budget.statusId" @ionChange="updateBudgetStatus" :disabled="!isBudgetEditable()">
                <ion-select-option :value="1">Créé</ion-select-option>
                <ion-select-option :value="2">Approuvé comité</ion-select-option>
              </ion-select>
            </ion-item>

            <ion-note v-if="!isBudgetEditable()" color="warning" class="ion-padding-top">
              ⚠️ Ce budget a été approuvé et ne peut plus être modifié
            </ion-note>
          </ion-card-content>
        </ion-card>

        <!-- Messages d'information -->
        <ion-card v-if="!canManageBudget()" color="light" class="ion-margin">
          <ion-card-content>
            <ion-text color="medium">
              ℹ️ Seuls les Directeurs et Co-Directeurs peuvent gérer les activités.
            </ion-text>
          </ion-card-content>
        </ion-card>

        <ion-card v-else-if="!canEditBudget() && budget" color="warning" class="ion-margin">
          <ion-card-content>
            <ion-text color="dark">
              ⚠️ Cette année ({{ budget.anneeExercice }}) n'est pas votre année d'exercice ({{ getUserYear() }}).
              Vous pouvez consulter mais pas créer/modifier.
            </ion-text>
          </ion-card-content>
        </ion-card>

        <ion-card v-else-if="budget && !isBudgetEditable()" color="warning" class="ion-margin">
          <ion-card-content>
            <ion-text color="dark">
              ⚠️ Ce budget est "{{ budget.status }}". Vous ne pouvez créer d'activités que si le statut est "Créé".
            </ion-text>
          </ion-card-content>
        </ion-card>

        <!-- Bouton nouvelle activité -->
        <ion-button
          v-if="canEditBudget() && budget && isBudgetEditable()"
          expand="block"
          @click="openActiviteModal()"
          class="ion-margin"
        >
          <ion-icon :icon="addOutline" slot="start"></ion-icon>
          Nouvelle activité
        </ion-button>

        <!-- Liste des activités -->
        <ion-text color="medium" class="ion-padding">
          <h3>Activités ({{ activites.length }})</h3>
        </ion-text>

        <div v-if="activites.length === 0" class="ion-text-center ion-padding">
          <ion-text color="medium">
            <p>Aucune activité pour cette année</p>
          </ion-text>
        </div>

        <ion-card v-for="activite in activites" :key="activite.id">
          <ion-card-header>
            <div style="display: flex; justify-content: space-between; align-items: start;">
              <div>
                <ion-card-title>{{ activite.nom }}</ion-card-title>
                <ion-card-subtitle>
                  {{ formatDate(activite.dateDebut) }} - {{ formatDate(activite.dateFin) }}
                </ion-card-subtitle>
              </div>
              <ion-chip :color="getActiviteStatusColor(activite.status)">
                {{ activite.status }}
              </ion-chip>
            </div>
          </ion-card-header>
          
          <ion-card-content>
            <p v-if="activite.description">{{ activite.description }}</p>
            
            <!-- Détails -->
            <ion-list>
              <ion-list-header>
                <ion-label>Détails du budget</ion-label>
              </ion-list-header>
              <ion-item v-for="(detail, index) in activite.details" :key="index">
                <ion-label>
                  <p>{{ detail.details }}</p>
                </ion-label>
                <ion-note slot="end">{{ formatMontant(detail.montant) }}</ion-note>
              </ion-item>
              <ion-item>
                <ion-label>
                  <strong>Total</strong>
                </ion-label>
                <ion-note slot="end" color="primary">
                  <strong>{{ formatMontant(activite.montant) }}</strong>
                </ion-note>
              </ion-item>
            </ion-list>

            <!-- Actions selon statut budget et activité -->
            <div class="ion-margin-top">
              <!-- Budget "Créé" - Modifier/Supprimer (seulement pour l'année de l'utilisateur) -->
              <div v-if="canEditBudget() && isBudgetEditable()">
                <ion-button size="small" fill="outline" @click="openActiviteModal(activite)">
                  <ion-icon :icon="createOutline" slot="start"></ion-icon>
                  Modifier
                </ion-button>
                <ion-button size="small" color="danger" fill="outline" @click="deleteActivite(activite.id, activite.nom)">
                  <ion-icon :icon="trashOutline" slot="start"></ion-icon>
                  Supprimer
                </ion-button>
              </div>

              <!-- Budget "Approuvé comité" (toutes les années) -->
              <div v-if="canManageBudget() && isBudgetApprouveComite()">
                <!-- Activité non terminée/annulée -->
                <div v-if="activite.status !== 'Terminé' && activite.status !== 'Annulé'">
                  <ion-button size="small" color="warning" fill="outline" @click="annulerActivite(activite.id, activite.nom)">
                    <ion-icon :icon="closeOutline" slot="start"></ion-icon>
                    Annuler activité
                  </ion-button>
                  <ion-button size="small" color="success" @click="openPresenceModal(activite)">
                    <ion-icon :icon="checkmarkOutline" slot="start"></ion-icon>
                    Faire présence
                  </ion-button>
                </div>

                <!-- Activité terminée -->
                <ion-button
                  v-if="activite.status === 'Terminé'"
                  size="small"
                  color="primary"
                  fill="outline"
                  @click="openParticipantsModal(activite)"
                >
                  <ion-icon :icon="peopleOutline" slot="start"></ion-icon>
                  Voir participants
                </ion-button>
              </div>
            </div>
          </ion-card-content>
        </ion-card>
      </div>

      <!-- Modal Activité -->
      <ion-modal :is-open="showActiviteModal" @didDismiss="closeActiviteModal">
        <ion-header>
          <ion-toolbar>
            <ion-title>{{ editMode ? 'Modifier' : 'Nouvelle' }} activité</ion-title>
            <ion-buttons slot="end">
              <ion-button @click="closeActiviteModal">Fermer</ion-button>
            </ion-buttons>
          </ion-toolbar>
        </ion-header>
        <ion-content class="ion-padding">
          <ion-list>
            <ion-item>
              <ion-label position="stacked">Nom *</ion-label>
              <ion-input v-model="activiteForm.nom" placeholder="Nom de l'activité"></ion-input>
            </ion-item>

            <ion-item>
              <ion-label position="stacked">Description</ion-label>
              <ion-textarea v-model="activiteForm.description" :rows="3" placeholder="Description"></ion-textarea>
            </ion-item>

            <ion-item>
              <ion-label position="stacked">Date début</ion-label>
              <ion-input type="date" v-model="activiteForm.dateDebut"></ion-input>
            </ion-item>

            <ion-item>
              <ion-label position="stacked">Date fin</ion-label>
              <ion-input type="date" v-model="activiteForm.dateFin"></ion-input>
            </ion-item>

            <ion-list-header>
              <ion-label>Détails du budget</ion-label>
              <ion-button size="small" @click="addDetail">
                <ion-icon :icon="addOutline"></ion-icon>
              </ion-button>
            </ion-list-header>

            <ion-item v-for="(detail, index) in activiteForm.details" :key="index">
              <ion-label position="stacked">Description</ion-label>
              <ion-input v-model="detail.details" placeholder="Ex: Transport"></ion-input>
              <ion-label position="stacked">Montant</ion-label>
              <ion-input type="number" v-model.number="detail.montant" placeholder="0"></ion-input>
              <ion-button slot="end" fill="clear" color="danger" @click="removeDetail(index)">
                <ion-icon :icon="trashOutline"></ion-icon>
              </ion-button>
            </ion-item>
          </ion-list>

          <ion-button expand="block" @click="saveActivite" :disabled="!isActiviteFormValid()" class="ion-margin-top">
            {{ editMode ? 'Mettre à jour' : 'Créer' }}
          </ion-button>
        </ion-content>
      </ion-modal>

      <!-- Modal Présence -->
      <ion-modal :is-open="showPresenceModal" @didDismiss="closePresenceModal">
        <ion-header>
          <ion-toolbar>
            <ion-title>Présence - {{ selectedActivite?.nom }}</ion-title>
            <ion-buttons slot="end">
              <ion-button @click="closePresenceModal">Fermer</ion-button>
            </ion-buttons>
          </ion-toolbar>
          <ion-toolbar>
            <ion-segment :value="presenceTab" @ionChange="presenceTab = $event.detail.value as string">
              <ion-segment-button value="enfants">
                <ion-label>🏕️ Explorateurs ({{ selectedEnfants.length }}/{{ personnesDisponibles?.enfants.length || 0 }})</ion-label>
              </ion-segment-button>
              <ion-segment-button value="staff">
                <ion-label>👨‍🏫 Staff ({{ selectedStaff.length }}/{{ personnesDisponibles?.staff.length || 0 }})</ion-label>
              </ion-segment-button>
            </ion-segment>
          </ion-toolbar>
        </ion-header>
        <ion-content class="ion-padding">
          <div v-if="loadingPresence" class="ion-text-center ion-padding">
            <ion-spinner></ion-spinner>
          </div>

          <div v-else>
            <!-- Tab Enfants -->
            <div v-if="presenceTab === 'enfants'">
              <ion-button expand="block" @click="toggleAllEnfants" fill="outline" class="ion-margin-bottom">
                {{ selectedEnfants.length === personnesDisponibles?.enfants.length ? 'Tout désélectionner' : 'Tout sélectionner' }}
              </ion-button>

              <ion-list>
                <ion-item v-for="enfant in personnesDisponibles?.enfants" :key="enfant.inscriptionId">
                  <ion-checkbox
                    slot="start"
                    :checked="selectedEnfants.includes(enfant.inscriptionId)"
                    @ionChange="toggleEnfant(enfant.inscriptionId)"
                  ></ion-checkbox>
                  <ion-label>
                    <h3>{{ enfant.nom }} {{ enfant.prenom }}</h3>
                    <p>
                      <ion-chip v-if="enfant.classeNom" size="small">{{ enfant.classeNom }}</ion-chip>
                      <ion-chip size="small">{{ enfant.genre }}</ion-chip>
                    </p>
                  </ion-label>
                </ion-item>
              </ion-list>
            </div>

            <!-- Tab Staff -->
            <div v-if="presenceTab === 'staff'">
              <ion-button expand="block" @click="toggleAllStaff" fill="outline" class="ion-margin-bottom">
                {{ selectedStaff.length === personnesDisponibles?.staff.length ? 'Tout désélectionner' : 'Tout sélectionner' }}
              </ion-button>

              <ion-list>
                <ion-item v-for="staff in personnesDisponibles?.staff" :key="staff.staffId">
                  <ion-checkbox
                    slot="start"
                    :checked="selectedStaff.includes(staff.staffId)"
                    @ionChange="toggleStaff(staff.staffId)"
                  ></ion-checkbox>
                  <ion-label>
                    <h3>{{ staff.nom }} {{ staff.prenom }}</h3>
                    <p>
                      <ion-chip v-if="staff.totem" size="small">{{ staff.totem }}</ion-chip>
                      <ion-chip size="small">{{ staff.role }}</ion-chip>
                    </p>
                  </ion-label>
                </ion-item>
              </ion-list>
            </div>
          </div>

          <ion-button
            expand="block"
            color="success"
            @click="enregistrerPresence"
            :disabled="loadingPresence || (selectedEnfants.length === 0 && selectedStaff.length === 0)"
            class="ion-margin-top"
          >
            Enregistrer ({{ selectedEnfants.length + selectedStaff.length }})
          </ion-button>
        </ion-content>
      </ion-modal>

      <!-- Modal Participants -->
      <ion-modal :is-open="showParticipantsModal" @didDismiss="closeParticipantsModal">
        <ion-header>
          <ion-toolbar>
            <ion-title>Participants - {{ selectedActivite?.nom }}</ion-title>
            <ion-buttons slot="end">
              <ion-button @click="closeParticipantsModal">Fermer</ion-button>
            </ion-buttons>
          </ion-toolbar>
        </ion-header>
        <ion-content class="ion-padding">
          <!-- Filtres -->
          <ion-card>
            <ion-card-content>
              <ion-item>
                <ion-checkbox v-model="filtreEnfant"></ion-checkbox>
                <ion-label class="ion-margin-start">🏕️ Explorateurs</ion-label>
              </ion-item>
              <ion-item>
                <ion-checkbox v-model="filtreStaff"></ion-checkbox>
                <ion-label class="ion-margin-start">👨‍🏫 Staff</ion-label>
              </ion-item>
              <ion-item v-if="filtreEnfant && classesParticipants.length > 0">
                <ion-label>Classe</ion-label>
                <ion-select v-model="classeIdFilter" placeholder="Toutes">
                  <ion-select-option :value="undefined">Toutes</ion-select-option>
                  <ion-select-option v-for="classe in classesParticipants" :key="classe.id" :value="classe.id">
                    {{ classe.nom }}
                  </ion-select-option>
                </ion-select>
              </ion-item>
              <ion-text color="primary">
                <p>Total: <strong>{{ (participants?.enfants?.length || 0) + (participants?.staff?.length || 0) }}</strong> participant(s)</p>
              </ion-text>
            </ion-card-content>
          </ion-card>

          <!-- Liste des participants -->
          <div v-if="filtreEnfant && participants?.enfants && participants.enfants.length > 0">
            <ion-text color="primary">
              <h4>Explorateurs ({{ participants.enfants.length }})</h4>
            </ion-text>
            <ion-list>
              <ion-item v-for="enfant in participants.enfants" :key="enfant.inscriptionId">
                <ion-label>
                  <h3>{{ enfant.nom }} {{ enfant.prenom }}</h3>
                  <p>
                    <ion-chip v-if="enfant.classeNom" :color="getClasseColor(enfant.classeNom)" size="small">
                      {{ enfant.classeNom }}
                    </ion-chip>
                    <ion-chip size="small">{{ enfant.genre }}</ion-chip>
                  </p>
                </ion-label>
              </ion-item>
            </ion-list>
          </div>

          <div v-if="filtreStaff && participants?.staff && participants.staff.length > 0">
            <ion-text color="success">
              <h4>Staff ({{ participants.staff.length }})</h4>
            </ion-text>
            <ion-list>
              <ion-item v-for="staff in participants.staff" :key="staff.staffId">
                <ion-label>
                  <h3>{{ staff.nom }} {{ staff.prenom }}</h3>
                  <p>
                    <ion-chip v-if="staff.totem" size="small">{{ staff.totem }}</ion-chip>
                    <ion-chip size="small" color="success">{{ staff.role }}</ion-chip>
                  </p>
                </ion-label>
              </ion-item>
            </ion-list>
          </div>
        </ion-content>
      </ion-modal>

      <!-- Modal Export PDF -->
      <ion-modal :is-open="showExportModal" @didDismiss="closeExportModal">
        <ion-header>
          <ion-toolbar>
            <ion-title>Exporter en PDF</ion-title>
            <ion-buttons slot="end">
              <ion-button @click="closeExportModal">Fermer</ion-button>
            </ion-buttons>
          </ion-toolbar>
        </ion-header>
        <ion-content class="ion-padding">
          <ion-text color="medium">
            <p>Sélectionnez les colonnes à inclure dans l'export PDF :</p>
          </ion-text>
          
          <ion-list>
            <ion-item>
              <ion-checkbox v-model="exportColumns.includeDate">
                <ion-label>Date (début & fin)</ion-label>
              </ion-checkbox>
            </ion-item>
            
            <ion-item>
              <ion-checkbox v-model="exportColumns.includeNomActivite">
                <ion-label>Nom de l'activité</ion-label>
              </ion-checkbox>
            </ion-item>
            
            <ion-item>
              <ion-checkbox v-model="exportColumns.includeCoutActivite">
                <ion-label>Coût de l'activité</ion-label>
              </ion-checkbox>
            </ion-item>
            
            <ion-item>
              <ion-checkbox v-model="exportColumns.includeDescriptionActivite">
                <ion-label>Description</ion-label>
              </ion-checkbox>
            </ion-item>
            
            <ion-item>
              <ion-checkbox v-model="exportColumns.includeDetailsActivite">
                <ion-label>Détails de l'activité</ion-label>
              </ion-checkbox>
            </ion-item>
            
            <ion-item>
              <ion-checkbox v-model="exportColumns.includeCoutDetails">
                <ion-label>Coût des détails</ion-label>
              </ion-checkbox>
            </ion-item>
            
            <ion-item>
              <ion-checkbox v-model="exportColumns.includeStatutActivite">
                <ion-label>Statut</ion-label>
              </ion-checkbox>
            </ion-item>
          </ion-list>

          <ion-button expand="block" @click="confirmExport" class="ion-margin-top">
            Exporter
          </ion-button>
        </ion-content>
      </ion-modal>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import {
  IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButtons, IonButton,
  IonIcon, IonCard, IonCardHeader, IonCardTitle, IonCardSubtitle, IonCardContent,
  IonItem, IonLabel, IonSelect, IonSelectOption, IonText, IonChip, IonGrid,
  IonRow, IonCol, IonNote, IonList, IonListHeader, IonSpinner, IonToast,
  IonModal, IonInput, IonTextarea, IonCheckbox, IonSegment, IonSegmentButton,
  alertController
} from '@ionic/vue';
import {
  syncOutline, addOutline, createOutline, trashOutline, closeOutline,
  checkmarkOutline, peopleOutline, documentTextOutline
} from 'ionicons/icons';
import { useAuthStore } from '@/stores/auth.store';
import budgetService from '@/services/budget-global.service';
import activiteService from '@/services/activite.service';
import anneeExerciceService from '@/services/annee-exercice.service';
import participantService from '@/services/participant-activite.service';
import { PdfService } from '@/services/pdf.service';
import type {
  BudgetGlobalResponse, ActiviteResponse, AnneeExercice, CreateActiviteRequest,
  UpdateActiviteRequest, DetailActiviteDto, PersonnesDisponiblesResponse,
  ParticipantsResponse, ExportColumnsDto
} from '@/types';

const authStore = useAuthStore();

// États
const loading = ref(false);
const error = ref<string | null>(null);
const success = ref<string | null>(null);
const budget = ref<BudgetGlobalResponse | null>(null);
const activites = ref<ActiviteResponse[]>([]);
const anneesExercice = ref<AnneeExercice[]>([]);
const selectedAnneeId = ref<number | null>(null);

// Modal Activité
const showActiviteModal = ref(false);
const editMode = ref(false);
const selectedActivite = ref<ActiviteResponse | null>(null);
const activiteForm = ref<CreateActiviteRequest | UpdateActiviteRequest>({
  nom: '',
  description: '',
  dateDebut: '',
  dateFin: '',
  budgetGlobalId: 0,
  statusId: 1,
  details: [{ details: '', montant: 0 }],
});

// Modal Présence
const showPresenceModal = ref(false);
const presenceTab = ref('enfants');
const loadingPresence = ref(false);
const personnesDisponibles = ref<PersonnesDisponiblesResponse | null>(null);
const selectedEnfants = ref<number[]>([]);
const selectedStaff = ref<number[]>([]);

// Modal Participants
const showParticipantsModal = ref(false);
const participants = ref<ParticipantsResponse | null>(null);
const filtreEnfant = ref(true);
const filtreStaff = ref(true);
const classeIdFilter = ref<number | undefined>(undefined);
const classesParticipants = ref<{ id: number; nom: string }[]>([]);

// Modal Export PDF
const showExportModal = ref(false);
const exportColumns = ref({
  includeDate: true,
  includeNomActivite: true,
  includeCoutActivite: true,
  includeDescriptionActivite: true,
  includeDetailsActivite: true,
  includeCoutDetails: true,
  includeStatutActivite: true
});

// Montage
onMounted(async () => {
  await loadData();
});

// Watchers
watch([filtreEnfant, filtreStaff, classeIdFilter], () => {
  if (selectedActivite.value) {
    loadParticipants();
  }
});

// Méthodes
const loadData = async () => {
  try {
    loading.value = true;
    error.value = null;
    anneesExercice.value = await anneeExerciceService.getAllAnneesExercice();

    // Sélectionner l'année de l'utilisateur
    if (authStore.user?.anneeExercice && anneesExercice.value.length > 0) {
      const userAnnee = anneesExercice.value.find(a => a.annee === authStore.user?.anneeExercice);
      if (userAnnee) {
        selectedAnneeId.value = userAnnee.id;
      } else {
        selectedAnneeId.value = anneesExercice.value[0].id;
      }
    } else if (anneesExercice.value.length > 0) {
      selectedAnneeId.value = anneesExercice.value[0].id;
    }

    if (selectedAnneeId.value) {
      await loadBudgetAndActivites();
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors du chargement';
  } finally {
    loading.value = false;
  }
};

const loadBudgetAndActivites = async () => {
  if (!selectedAnneeId.value) return;

  try {
    loading.value = true;
    budget.value = await budgetService.getBudgetByAnneeExercice(selectedAnneeId.value);
    activites.value = await activiteService.getActivitesByAnneeExercice(selectedAnneeId.value);
  } catch (err: any) {
    if (err.response?.status === 404) {
      budget.value = null;
      activites.value = [];
    } else {
      error.value = err.response?.data?.message || 'Erreur lors du chargement du budget';
    }
  } finally {
    loading.value = false;
  }
};

const updateBudgetStatus = async () => {
  if (!budget.value) return;

  try {
    loading.value = true;
    await budgetService.updateBudgetStatus(budget.value.id, { statusId: budget.value.statusId });
    await loadBudgetAndActivites();
    success.value = 'Statut du budget mis à jour';
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors de la mise à jour';
  } finally {
    loading.value = false;
  }
};

// Activité CRUD
const openActiviteModal = (activite?: ActiviteResponse) => {
  editMode.value = !!activite;
  selectedActivite.value = activite || null;

  if (activite) {
    activiteForm.value = {
      nom: activite.nom,
      description: activite.description,
      dateDebut: activite.dateDebut,
      dateFin: activite.dateFin,
      statusId: activite.statusId,
      details: [...activite.details],
    };
  } else {
    activiteForm.value = {
      nom: '',
      description: '',
      dateDebut: '',
      dateFin: '',
      budgetGlobalId: budget.value?.id || 0,
      statusId: 1,
      details: [{ details: '', montant: 0 }],
    };
  }

  showActiviteModal.value = true;
};

const closeActiviteModal = () => {
  showActiviteModal.value = false;
  editMode.value = false;
  selectedActivite.value = null;
};

const saveActivite = async () => {
  try {
    loading.value = true;

    // Nettoyer les dates vides (convertir en undefined)
    const formData = {
      ...activiteForm.value,
      dateDebut: activiteForm.value.dateDebut || undefined,
      dateFin: activiteForm.value.dateFin || undefined,
    };

    if (editMode.value && selectedActivite.value) {
      await activiteService.updateActivite(selectedActivite.value.id, formData as UpdateActiviteRequest);
      success.value = 'Activité mise à jour';
    } else {
      await activiteService.createActivite(formData as CreateActiviteRequest);
      success.value = 'Activité créée';
    }

    closeActiviteModal();
    await loadBudgetAndActivites();
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors de l\'enregistrement';
  } finally {
    loading.value = false;
  }
};

const deleteActivite = async (id: number, nom: string) => {
  const alert = await alertController.create({
    header: 'Confirmer',
    message: `Supprimer l'activité "${nom}" ?`,
    buttons: [
      { text: 'Annuler', role: 'cancel' },
      {
        text: 'Supprimer',
        role: 'destructive',
        handler: async () => {
          try {
            loading.value = true;
            await activiteService.deleteActivite(id);
            await loadBudgetAndActivites();
            success.value = 'Activité supprimée';
          } catch (err: any) {
            error.value = err.response?.data?.message || 'Erreur lors de la suppression';
          } finally {
            loading.value = false;
          }
        }
      }
    ]
  });
  await alert.present();
};

const annulerActivite = async (id: number, nom: string) => {
  const alert = await alertController.create({
    header: 'Confirmer',
    message: `Annuler l'activité "${nom}" ?`,
    buttons: [
      { text: 'Non', role: 'cancel' },
      {
        text: 'Oui',
        handler: async () => {
          try {
            loading.value = true;
            await participantService.annulerActivite(id);
            await loadBudgetAndActivites();
            success.value = 'Activité annulée';
          } catch (err: any) {
            error.value = err.response?.data?.message || 'Erreur lors de l\'annulation';
          } finally {
            loading.value = false;
          }
        }
      }
    ]
  });
  await alert.present();
};

// Présence
const openPresenceModal = async (activite: ActiviteResponse) => {
  selectedActivite.value = activite;
  selectedEnfants.value = [];
  selectedStaff.value = [];
  presenceTab.value = 'enfants';
  showPresenceModal.value = true;

  try {
    loadingPresence.value = true;
    personnesDisponibles.value = await participantService.getPersonnesDisponibles(activite.id);
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors du chargement';
  } finally {
    loadingPresence.value = false;
  }
};

const closePresenceModal = () => {
  showPresenceModal.value = false;
  selectedActivite.value = null;
  personnesDisponibles.value = null;
};

const toggleEnfant = (inscriptionId: number) => {
  const index = selectedEnfants.value.indexOf(inscriptionId);
  if (index > -1) {
    selectedEnfants.value.splice(index, 1);
  } else {
    selectedEnfants.value.push(inscriptionId);
  }
};

const toggleStaff = (staffId: number) => {
  const index = selectedStaff.value.indexOf(staffId);
  if (index > -1) {
    selectedStaff.value.splice(index, 1);
  } else {
    selectedStaff.value.push(staffId);
  }
};

const toggleAllEnfants = () => {
  if (selectedEnfants.value.length === personnesDisponibles.value?.enfants.length) {
    selectedEnfants.value = [];
  } else {
    selectedEnfants.value = personnesDisponibles.value?.enfants.map(e => e.inscriptionId) || [];
  }
};

const toggleAllStaff = () => {
  if (selectedStaff.value.length === personnesDisponibles.value?.staff.length) {
    selectedStaff.value = [];
  } else {
    selectedStaff.value = personnesDisponibles.value?.staff.map(s => s.staffId) || [];
  }
};

const enregistrerPresence = async () => {
  if (!selectedActivite.value) return;

  try {
    loadingPresence.value = true;
    await participantService.enregistrerPresence({
      activiteId: selectedActivite.value.id,
      enfantsPresents: selectedEnfants.value,
      staffPresents: selectedStaff.value,
    });
    success.value = 'Présence enregistrée';
    closePresenceModal();
    await loadBudgetAndActivites();
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors de l\'enregistrement';
  } finally {
    loadingPresence.value = false;
  }
};

// Participants
const openParticipantsModal = async (activite: ActiviteResponse) => {
  selectedActivite.value = activite;
  filtreEnfant.value = true;
  filtreStaff.value = true;
  classeIdFilter.value = undefined;
  showParticipantsModal.value = true;

  // Charger classes disponibles
  try {
    const data = await participantService.getParticipants(activite.id, true, false, undefined);
    if (data.enfants) {
      classesParticipants.value = Array.from(
        new Map(
          data.enfants
            .filter(e => e.classeId && e.classeNom)
            .map(e => [e.classeId, { id: e.classeId!, nom: e.classeNom! }])
        ).values()
      ).sort((a, b) => a.nom.localeCompare(b.nom));
    }
  } catch (err: any) {
    console.error('Erreur chargement classes:', err);
  }

  await loadParticipants();
};

const closeParticipantsModal = () => {
  showParticipantsModal.value = false;
  selectedActivite.value = null;
  participants.value = null;
};

const loadParticipants = async () => {
  if (!selectedActivite.value) return;

  try {
    participants.value = await participantService.getParticipants(
      selectedActivite.value.id,
      filtreEnfant.value,
      filtreStaff.value,
      classeIdFilter.value
    );
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors du chargement';
  }
};

// Export PDF
const exportPdf = () => {
  if (!budget.value || !selectedAnneeId.value) return;
  showExportModal.value = true;
};

const closeExportModal = () => {
  showExportModal.value = false;
};

const confirmExport = async () => {
  if (!selectedAnneeId.value) return;
  
  try {
    loading.value = true;
    await PdfService.downloadBudgetPdf({
      anneeExerciceId: selectedAnneeId.value,
      ...exportColumns.value
    });
    success.value = 'PDF téléchargé et ouvert avec succès!';
    closeExportModal();
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erreur lors de l\'export PDF';
  } finally {
    loading.value = false;
  }
};

// Détails activité
const addDetail = () => {
  if (!Array.isArray(activiteForm.value.details)) {
    activiteForm.value.details = [];
  }
  activiteForm.value.details.push({ details: '', montant: 0 });
};

const removeDetail = (index: number) => {
  if (Array.isArray(activiteForm.value.details)) {
    activiteForm.value.details.splice(index, 1);
  }
};

// Helpers
const canManageBudget = () => {
  return authStore.user?.role === 'Directeur' || authStore.user?.role === 'Co_Directeur';
};

const canEditBudget = () => {
  const hasRole = authStore.user?.role === 'Directeur' || authStore.user?.role === 'Co_Directeur';
  if (!hasRole || !authStore.user?.anneeExerciceId || !budget.value?.anneeExerciceId) {
    return false;
  }
  
  // Vérifier que l'année sélectionnée correspond à l'année de l'utilisateur
  return authStore.user.anneeExerciceId === selectedAnneeId.value;
};

const isBudgetEditable = () => {
  return budget.value && budget.value.status === 'Créé';
};

const isBudgetApprouveComite = () => {
  return budget.value && budget.value.status === 'Approuvé comité';
};

const getUserYear = () => {
  if (!authStore.user?.anneeExercice) return 'Non défini';
  return new Date(authStore.user.anneeExercice).getFullYear().toString();
};

const isActiviteFormValid = () => {
  return activiteForm.value.nom && activiteForm.value.nom.trim().length > 0;
};

const formatMontant = (montant?: number | null) => {
  if (!montant && montant !== 0) return '0 Ar';
  return montant.toLocaleString('fr-FR') + ' Ar';
};

const formatDate = (dateStr?: string) => {
  if (!dateStr) return 'Non défini';
  return new Date(dateStr).toLocaleDateString('fr-FR');
};

const getBudgetStatusColor = (status: string) => {
  if (status === 'Créé') return 'warning';
  if (status === 'Approuvé comité') return 'success';
  return 'medium';
};

const getActiviteStatusColor = (status: string) => {
  if (status === 'Créé') return 'warning';
  if (status === 'Terminé') return 'success';
  if (status === 'Annulé') return 'danger';
  return 'medium';
};

const getClasseColor = (classeNom: string) => {
  const nom = classeNom.toLowerCase();
  if (nom.includes('ami')) return 'primary';
  if (nom.includes('compagnon')) return 'danger';
  if (nom.includes('eclaireur') || nom.includes('éclaireur')) return 'success';
  if (nom.includes('pionnier')) return 'medium';
  if (nom.includes('voyageur')) return 'tertiary';
  if (nom.includes('guide')) return 'warning';
  return 'success';
};
</script>

<style scoped>
.header-logo {
  height: 40px;
  margin-left: 10px;
}

.rotate-up {
  transform: rotate(-90deg);
  transition: transform 0.3s;
}

.rotate-down {
  transform: rotate(0deg);
  transition: transform 0.3s;
}
</style>
