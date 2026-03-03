<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button default-href="/tabs/classes-progressives"></ion-back-button>
          <img src="/assets/logo.png" alt="Logo" class="header-logo" style="margin-left: 8px;" />
        </ion-buttons>
        <ion-title v-if="cp">{{ formatDate(cp.dateCp) }}</ion-title>
        <ion-buttons slot="end">
          <ion-button v-if="canModify" @click="openAddProgrammeModal">
            <ion-icon :icon="addOutline"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-refresher slot="fixed" @ionRefresh="handleRefresh($event)">
        <ion-refresher-content></ion-refresher-content>
      </ion-refresher>

      <!-- Info CP -->
      <div v-if="cp" class="ion-padding">
        <ion-card>
          <ion-card-header>
            <ion-card-title>{{ formatDate(cp.dateCp) }}</ion-card-title>
            <ion-card-subtitle>
              {{ cp.heureDebut }} - {{ cp.heureFin }}
              <span v-if="cp.niveau"> • {{ cp.niveau }}</span>
            </ion-card-subtitle>
          </ion-card-header>
          <ion-card-content>
            {{ cp.nombreProgrammes }} programme(s) planifié(s)
          </ion-card-content>
        </ion-card>
      </div>

      <!-- Liste des programmes -->
      <ion-list v-if="!loading">
        <ion-list-header>
          <ion-label>Programmes planifiés</ion-label>
        </ion-list-header>

        <ion-item 
          v-for="detail in cpDetails" 
          :key="detail.id"
        >
          <ion-label>
            <h2>{{ detail.programmeNom || detail.description }}</h2>
            <p v-if="detail.programmeDescription" style="font-size: 0.85rem; color: var(--ion-color-medium);">
              {{ detail.programmeDescription }}
            </p>
            <p style="font-size: 0.85rem; color: var(--ion-color-medium-shade);">
              <ion-icon :icon="people"></ion-icon>
              <span v-if="detail.instructeurs.length > 0">
                {{ detail.instructeurs.map(i => i.nom + ' ' + i.prenom).join(', ') }}
              </span>
              <span v-else>Aucun instructeur assigné</span>
            </p>
            <p v-if="detail.statusNom" style="font-size: 0.8rem;">
              <ion-badge :color="getStatusColor(detail.statusNom)">
                {{ detail.statusNom }}
              </ion-badge>
            </p>
          </ion-label>
          <ion-buttons slot="end">
            <ion-button 
              v-if="canModify"
              @click.stop="openEditInstructeursModal(detail)"
            >
              <ion-icon :icon="createOutline"></ion-icon>
            </ion-button>
            <ion-button 
              v-if="canModify && detail.statusNom && detail.statusNom !== 'Terminé'"
              color="primary"
              @click.stop="openChangeStatusModal(detail)"
            >
              <ion-icon :icon="checkmarkOutline"></ion-icon>
            </ion-button>
            <ion-button 
              v-if="canModify && detail.statusNom !== 'Terminé'"
              color="danger" 
              @click.stop="confirmDelete(detail.id)"
            >
              <ion-icon :icon="trashOutline"></ion-icon>
            </ion-button>
          </ion-buttons>
        </ion-item>

        <!-- Message si aucun programme -->
        <div v-if="cpDetails.length === 0" class="ion-padding ion-text-center">
          <ion-note>Aucun programme planifié pour cette CP</ion-note>
        </div>
      </ion-list>

      <!-- Skeleton lors du chargement -->
      <ion-list v-else>
        <ion-item v-for="i in 3" :key="i">
          <ion-label>
            <ion-skeleton-text animated style="width: 80%"></ion-skeleton-text>
            <ion-skeleton-text animated style="width: 60%"></ion-skeleton-text>
            <ion-skeleton-text animated style="width: 40%"></ion-skeleton-text>
          </ion-label>
        </ion-item>
      </ion-list>
    </ion-content>

    <!-- Modal sélection de programme avec filtres -->
    <ion-modal :is-open="showProgrammeModal">
      <ion-page>
        <ion-header>
          <ion-toolbar>
            <ion-title>Sélectionner un programme</ion-title>
            <ion-buttons slot="end">
              <ion-button @click="cancelProgrammeSelection">
                <ion-icon :icon="closeOutline"></ion-icon>
              </ion-button>
            </ion-buttons>
          </ion-toolbar>
        </ion-header>
        <ion-content>
          <div class="ion-padding">
            <!-- Filtres -->
            <ion-item>
              <ion-label>Catégorie</ion-label>
              <ion-select 
                v-model="selectedCategorieId" 
                placeholder="Toutes" 
                @ionChange="filterProgrammesList"
              >
                <ion-select-option :value="undefined">Toutes</ion-select-option>
                <ion-select-option 
                  v-for="cat in categories" 
                  :key="cat.id" 
                  :value="cat.id"
                >
                  {{ cat.nom }}
                </ion-select-option>
              </ion-select>
            </ion-item>

            <ion-item>
              <ion-label>Classe</ion-label>
              <ion-select 
                v-model="selectedClasseId" 
                placeholder="Toutes" 
                @ionChange="filterProgrammesList"
              >
                <ion-select-option :value="undefined">Toutes</ion-select-option>
                <ion-select-option 
                  v-for="classe in classes" 
                  :key="classe.id" 
                  :value="classe.id"
                >
                  {{ classe.nom }}
                </ion-select-option>
              </ion-select>
            </ion-item>

            <!-- Liste des programmes filtrés -->
            <ion-list>
              <ion-list-header>
                <ion-label>Programmes ({{ filteredProgrammes.length }})</ion-label>
              </ion-list-header>
              <ion-item 
                v-for="prog in filteredProgrammes" 
                :key="prog.id"
                button
                @click="selectProgrammeFromModal(prog)"
              >
                <ion-label>
                  <h2>{{ prog.nom }}</h2>
                  <p v-if="prog.description">{{ prog.description }}</p>
                  <p style="font-size: 0.85rem; color: var(--ion-color-medium);">
                    {{ prog.categorieNom }} • {{ prog.classeNom }}
                  </p>
                </ion-label>
              </ion-item>
              <div v-if="filteredProgrammes.length === 0" class="ion-padding ion-text-center">
                <ion-note>Aucun programme trouvé</ion-note>
              </div>
            </ion-list>
          </div>
        </ion-content>
      </ion-page>
    </ion-modal>

    <!-- Modal sélection instructeurs avec auto-complétion -->
    <ion-modal :is-open="showInstructeurModal">
      <ion-page>
        <ion-header>
          <ion-toolbar>
            <ion-title>Instructeurs</ion-title>
            <ion-buttons slot="end">
              <ion-button @click="cancelInstructeurSelection">
                <ion-icon :icon="closeOutline"></ion-icon>
              </ion-button>
            </ion-buttons>
          </ion-toolbar>
        </ion-header>
        <ion-content>
          <div class="ion-padding">
            <!-- Titre selon le contexte -->
            <h3 v-if="selectedProgramme">
              {{ selectedProgramme.nom }}
            </h3>
            <h3 v-else-if="isActiviteLibre">
              {{ activiteDescription }}
            </h3>

            <!-- Instructeurs déjà sélectionnés -->
            <div v-if="getSelectedInstructeursDetails().length > 0" class="ion-margin-bottom">
              <ion-label><strong>Instructeurs sélectionnés:</strong></ion-label>
              <div style="display: flex; flex-wrap: wrap; gap: 8px; margin-top: 8px;">
                <ion-chip 
                  v-for="inst in getSelectedInstructeursDetails()" 
                  :key="inst.id"
                  @click="removeInstructeur(inst.id)"
                >
                  <ion-label>{{ inst.prenom }} {{ inst.nom }}</ion-label>
                  <ion-icon :icon="closeOutline"></ion-icon>
                </ion-chip>
              </div>
            </div>

            <!-- Barre de recherche avec auto-complétion -->
            <ion-searchbar
              v-model="instructeurSearchText"
              placeholder="Rechercher un instructeur..."
              @ionInput="filterInstructeursList"
            ></ion-searchbar>

            <!-- Liste des instructeurs filtrés -->
            <ion-list>
              <ion-item 
                v-for="inst in filteredInstructeurs" 
                :key="inst.id"
                button
                @click="addInstructeurToSelection(inst)"
              >
                <ion-label>
                  <h3>{{ inst.prenom }} {{ inst.nom }}</h3>
                  <p v-if="inst.totem">{{ inst.totem }}</p>
                </ion-label>
              </ion-item>
              <div v-if="filteredInstructeurs.length === 0 && instructeurSearchText" class="ion-padding ion-text-center">
                <ion-note>Aucun instructeur trouvé</ion-note>
              </div>
            </ion-list>

            <!-- Bouton valider -->
            <div class="ion-padding-top">
              <ion-button 
                expand="block" 
                @click="confirmInstructeurSelection"
              >
                Valider{{ selectedInstructeurs.length > 0 ? ` (${selectedInstructeurs.length} instructeur${selectedInstructeurs.length > 1 ? 's' : ''})` : ' sans instructeur' }}
              </ion-button>
            </div>
          </div>
        </ion-content>
      </ion-page>
    </ion-modal>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
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
  IonList,
  IonListHeader,
  IonItem,
  IonLabel,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardSubtitle,
  IonCardContent,
  IonBadge,
  IonRefresher,
  IonRefresherContent,
  IonSkeletonText,
  IonNote,
  IonModal,
  IonSearchbar,
  IonSelect,
  IonSelectOption,
  IonChip,
  actionSheetController,
  alertController,
  toastController
} from '@ionic/vue';
import { addOutline, createOutline, trashOutline, people, checkmarkOutline, closeOutline } from 'ionicons/icons';
import { useAuthStore } from '@/stores/auth.store';
import classeProgressiveService from '@/services/classe-progressive.service';
import cpDetailsService from '@/services/cp-details.service';
import programmeService from '@/services/programme.service';
import instructeurService from '@/services/instructeur.service';
import programmeStatusService from '@/services/programme-status.service';
import categorieProgrammeService from '@/services/categorie-programme.service';
import classeService from '@/services/classe.service';
import type { ClasseProgressive, CpDetails, Programme, Instructeur, ProgrammeStatus, CategorieProgramme, Classe } from '@/types';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const loading = ref(false);
const cp = ref<ClasseProgressive | null>(null);
const cpDetails = ref<CpDetails[]>([]);
const programmes = ref<Programme[]>([]);
const instructeurs = ref<Instructeur[]>([]);
const statuts = ref<ProgrammeStatus[]>([]);
const categories = ref<CategorieProgramme[]>([]);
const classes = ref<Classe[]>([]);

// États pour les modales
const showProgrammeModal = ref(false);
const showInstructeurModal = ref(false);
const selectedCategorieId = ref<number | undefined>();
const selectedClasseId = ref<number | undefined>();
const filteredProgrammes = ref<Programme[]>([]);
const selectedProgramme = ref<Programme | null>(null);
const isActiviteLibre = ref(false);
const activiteDescription = ref('');

// États pour auto-complétion instructeurs
const instructeurSearchText = ref('');
const selectedInstructeurs = ref<number[]>([]);
const filteredInstructeurs = ref<Instructeur[]>([]);

const canModify = computed(() => {
  const role = authStore.user?.role;
  return role === 'Directeur' || role === 'Co_Directeur';
});

onMounted(() => {
  loadData();
});

// Watchers
watch(instructeurSearchText, () => {
  filterInstructeursList();
});

async function loadData() {
  try {
    loading.value = true;
    const cpId = parseInt(route.params.id as string);
    await Promise.all([
      loadCP(cpId),
      loadCPDetails(cpId),
      loadProgrammes(),
      loadInstructeurs(),
      loadStatuts(),
      loadCategories(),
      loadClasses()
    ]);
  } finally {
    loading.value = false;
  }
}

async function loadCP(cpId: number) {
  try {
    cp.value = await classeProgressiveService.getCPById(cpId);
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors du chargement de la CP',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  }
}

async function loadCPDetails(cpId: number) {
  try {
    cpDetails.value = await cpDetailsService.getProgrammesByCP(cpId);
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors du chargement des détails',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  }
}

async function loadProgrammes() {
  try {
    programmes.value = await programmeService.getAllProgrammes();
  } catch (error: any) {
    console.error('Erreur chargement programmes:', error);
  }
}

async function loadInstructeurs() {
  try {
    instructeurs.value = await instructeurService.getAllInstructeurs();
  } catch (error: any) {
    console.error('Erreur chargement instructeurs:', error);
  }
}

async function loadStatuts() {
  try {
    statuts.value = await programmeStatusService.getAllStatuts();
  } catch (error: any) {
    console.error('Erreur chargement statuts:', error);
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

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return date.toLocaleDateString('fr-FR', { 
    weekday: 'long', 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  });
}

function getStatusColor(status: string): string {
  switch (status) {
    case 'En attente': return 'warning';
    case 'En cours': return 'primary';
    case 'Terminé': return 'success';
    default: return 'medium';
  }
}

async function openAddProgrammeModal() {
  const actionSheet = await actionSheetController.create({
    header: 'Ajouter à la CP',
    buttons: [
      {
        text: 'Programme',
        handler: () => openProgrammeSelectionModal()
      },
      {
        text: 'Activité libre',
        handler: () => showActiviteLibreForm()
      },
      {
        text: 'Annuler',
        role: 'cancel'
      }
    ]
  });
  await actionSheet.present();
}

function openProgrammeSelectionModal() {
  selectedCategorieId.value = undefined;
  selectedClasseId.value = undefined;
  filteredProgrammes.value = programmes.value;
  selectedProgramme.value = null;
  isActiviteLibre.value = false;
  showProgrammeModal.value = true;
}

async function filterProgrammesList() {
  try {
    if (selectedCategorieId.value || selectedClasseId.value) {
      filteredProgrammes.value = await programmeService.filterProgrammes(
        selectedCategorieId.value,
        selectedClasseId.value
      );
    } else {
      filteredProgrammes.value = programmes.value;
    }
  } catch (error: any) {
    console.error('Erreur filtrage programmes:', error);
    filteredProgrammes.value = programmes.value;
  }
}

function selectProgrammeFromModal(programme: Programme) {
  selectedProgramme.value = programme;
  isActiviteLibre.value = false;
  showProgrammeModal.value = false;
  openInstructeurSelectionModal();
}

function cancelProgrammeSelection() {
  showProgrammeModal.value = false;
  selectedCategorieId.value = undefined;
  selectedClasseId.value = undefined;
  selectedProgramme.value = null;
}

function closeProgrammeModal() {
  // Juste fermer sans reset - utilisé après sélection
  showProgrammeModal.value = false;
}

async function showActiviteLibreForm() {
  const alert = await alertController.create({
    header: 'Activité libre',
    inputs: [
      {
        name: 'description',
        type: 'textarea',
        placeholder: 'Description de l\'activité'
      }
    ],
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Suivant',
        handler: (data) => {
          if (!data.description?.trim()) {
            return false;
          }
          activiteDescription.value = data.description;
          isActiviteLibre.value = true;
          selectedProgramme.value = null;
          openInstructeurSelectionModal();
          return true;
        }
      }
    ]
  });
  await alert.present();
}

function openInstructeurSelectionModal() {
  selectedInstructeurs.value = [];
  instructeurSearchText.value = '';
  filteredInstructeurs.value = instructeurs.value;
  showInstructeurModal.value = true;
}

function filterInstructeursList() {
  const searchLower = instructeurSearchText.value.toLowerCase();
  
  if (!searchLower) {
    // Exclure les instructeurs déjà sélectionnés
    filteredInstructeurs.value = instructeurs.value.filter(
      inst => !selectedInstructeurs.value.includes(inst.id)
    );
    return;
  }
  
  filteredInstructeurs.value = instructeurs.value.filter(inst => {
    // Exclure les déjà sélectionnés
    if (selectedInstructeurs.value.includes(inst.id)) return false;
    
    // Filtrer par recherche
    const fullName = `${inst.prenom} ${inst.nom} ${inst.totem || ''}`.toLowerCase();
    return fullName.includes(searchLower);
  });
}

function addInstructeurToSelection(instructeur: Instructeur) {
  if (!selectedInstructeurs.value.includes(instructeur.id)) {
    selectedInstructeurs.value.push(instructeur.id);
    instructeurSearchText.value = '';
    filterInstructeursList();
  }
}

function removeInstructeur(instructeurId: number) {
  selectedInstructeurs.value = selectedInstructeurs.value.filter(id => id !== instructeurId);
  filterInstructeursList();
}

function getSelectedInstructeursDetails() {
  return instructeurs.value.filter(inst => selectedInstructeurs.value.includes(inst.id));
}

async function confirmInstructeurSelection() {
  // Sauvegarder les valeurs AVANT de fermer
  const programmeToAdd = selectedProgramme.value;
  const descriptionToAdd = activiteDescription.value;
  const isActivite = isActiviteLibre.value;
  const instructeursToAdd = [...selectedInstructeurs.value];
  
  console.log('Confirmation - Instructeurs sélectionnés:', instructeursToAdd);
  console.log('Programme:', programmeToAdd);
  console.log('Activité libre:', isActivite, descriptionToAdd);
  
  // Fermer la modale
  closeInstructeurModal();
  
  // Réinitialiser les variables APRÈS la fermeture
  selectedInstructeurs.value = [];
  instructeurSearchText.value = '';
  isActiviteLibre.value = false;
  activiteDescription.value = '';
  selectedProgramme.value = null;
  selectedCategorieId.value = undefined;
  selectedClasseId.value = undefined;
  
  try {
    // Utiliser les valeurs sauvegardées
    if (isActivite) {
      await addProgrammeToCPWithInstructeurs(undefined, descriptionToAdd, instructeursToAdd);
    } else if (programmeToAdd) {
      await addProgrammeToCPWithInstructeurs(programmeToAdd.id, undefined, instructeursToAdd);
    } else {
      console.error('Aucun programme ni activité libre sélectionné');
      const toast = await toastController.create({
        message: 'Erreur: aucun programme sélectionné',
        duration: 3000,
        color: 'danger'
      });
      await toast.present();
    }
  } catch (error) {
    console.error('Erreur lors de la confirmation:', error);
  }
}

function cancelInstructeurSelection() {
  showInstructeurModal.value = false;
  selectedInstructeurs.value = [];
  instructeurSearchText.value = '';
  isActiviteLibre.value = false;
  activiteDescription.value = '';
  selectedProgramme.value = null;
}

function closeInstructeurModal() {
  // Juste fermer sans reset - utilisé après confirmation
  showInstructeurModal.value = false;
}

async function addProgrammeToCPWithInstructeurs(programmeId: number | undefined, description: string | undefined, instructeurIds: number[]) {
  try {
    const request = {
      classeProgressiveId: parseInt(route.params.id as string),
      programmeId,
      description,
      instructeurIds: instructeurIds
    };
    
    console.log('Envoi requête addProgrammeToCP:', request);
    
    const result = await cpDetailsService.addProgrammeToCP(request);
    console.log('Résultat addProgrammeToCP:', result);
    
    const toast = await toastController.create({
      message: 'Ajouté avec succès',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadCPDetails(parseInt(route.params.id as string));
  } catch (error: any) {
    console.error('Erreur addProgrammeToCP:', error);
    const toast = await toastController.create({
      message: error.message || 'Erreur lors de l\'ajout',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  }
}

async function openEditInstructeursModal(detail: CpDetails) {
  // Multi-select pour instructeurs - simplifiée pour mobile
  const currentIds = detail.instructeurs.map(i => i.id);
  selectedInstructeurs.value = [...currentIds];
  
  const alert = await alertController.create({
    header: 'Modifier instructeurs',
    message: 'Instructeurs actuels: ' + detail.instructeurs.map(i => i.prenom + ' ' + i.nom).join(', '),
    buttons: [
      {
        text: 'Réinitialiser',
        handler: () => {
          selectedInstructeurs.value = [];
          return false;
        }
      },
      {
        text: 'Valider',
        handler: async () => {
          await updateInstructeurs(detail.id);
        }
      },
      {
        text: 'Annuler',
        role: 'cancel'
      }
    ]
  });
  await alert.present();
}

async function updateInstructeurs(cpDetailsId: number) {
  try {
    await cpDetailsService.updateInstructeur(cpDetailsId, {
      instructeurIds: selectedInstructeurs.value
    });
    const toast = await toastController.create({
      message: 'Instructeurs modifiés',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    selectedInstructeurs.value = [];
    await loadCPDetails(parseInt(route.params.id as string));
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors de la modification',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  }
}

async function openChangeStatusModal(detail: CpDetails) {
  if (!detail.programmeId) return;
  
  const buttons = statuts.value.map(status => ({
    text: status.nom,
    handler: () => changeStatus(detail.programmeId!, status.id)
  }));
  
  buttons.push({
    text: 'Annuler',
    handler: async () => {}
  });

  const actionSheet = await actionSheetController.create({
    header: 'Changer le statut',
    buttons
  });
  await actionSheet.present();
}

async function changeStatus(programmeId: number, statusId: number) {
  try {
    const cpId = parseInt(route.params.id as string);
    await programmeStatusService.updateStatus(programmeId, cpId, { statusId });
    const toast = await toastController.create({
      message: 'Statut modifié',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadCPDetails(cpId);
  } catch (error: any) {
    const toast = await toastController.create({
      message: error.message || 'Erreur lors de la modification du statut',
      duration: 3000,
      color: 'danger'
    });
    await toast.present();
  }
}

async function confirmDelete(cpDetailsId: number) {
  const alert = await alertController.create({
    header: 'Confirmer la suppression',
    message: 'Voulez-vous vraiment retirer ce programme de la CP ?',
    buttons: [
      {
        text: 'Annuler',
        role: 'cancel'
      },
      {
        text: 'Supprimer',
        role: 'destructive',
        handler: () => deleteCPDetail(cpDetailsId)
      }
    ]
  });
  await alert.present();
}

async function deleteCPDetail(cpDetailsId: number) {
  try {
    await cpDetailsService.removeProgrammeFromCP(cpDetailsId);
    const toast = await toastController.create({
      message: 'Programme retiré de la CP',
      duration: 2000,
      color: 'success'
    });
    await toast.present();
    await loadCPDetails(parseInt(route.params.id as string));
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
