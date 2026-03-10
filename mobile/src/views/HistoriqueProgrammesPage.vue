<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button default-href="/tabs/home"></ion-back-button>
          <!-- <img src="/assets/logo.png" alt="Logo" class="header-logo" style="margin-left: 8px;" /> -->
        </ion-buttons>
        <ion-title>Historique</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <!-- <ion-header collapse="condense">
        <ion-toolbar>
          <ion-title size="large">Historique des Programmes</ion-title>
        </ion-toolbar>
      </ion-header> -->

      <div class="historique-container">
        <!-- Sélecteur d'année -->
        <ion-card>
          <ion-card-content>
            <ion-item>
              <ion-label>Année</ion-label>
              <ion-select v-model="selectedAnneeId" placeholder="Toutes les années">
                <ion-select-option :value="null">Toutes les années</ion-select-option>
                <ion-select-option 
                  v-for="annee in annees" 
                  :key="annee.id" 
                  :value="annee.id"
                >
                  {{ formatAnneeExercice(annee.annee) }}{{ annee.estActif ? ' (Actif)' : '' }}
                </ion-select-option>
              </ion-select>
            </ion-item>
          </ion-card-content>
        </ion-card>

        <!-- Onglets de vues -->
        <ion-segment v-model="viewMode" @ionChange="handleSegmentChange">
          <ion-segment-button value="statistiques">
            <ion-label>Statistiques</ion-label>
          </ion-segment-button>
          <ion-segment-button value="progression">
            <ion-label>Progression</ion-label>
          </ion-segment-button>
          <ion-segment-button value="avancement">
            <ion-label>Avancement</ion-label>
          </ion-segment-button>
        </ion-segment>

        <!-- Chargement -->
        <div v-if="loading" class="loading-container">
          <ion-spinner name="crescent"></ion-spinner>
          <p>Chargement...</p>
        </div>

        <!-- Erreur -->
        <ion-card v-if="error && !loading" color="danger">
          <ion-card-content>
            {{ error }}
          </ion-card-content>
        </ion-card>

        <!-- Contenu des vues -->
        <div v-if="!loading && !error">
          <!-- Vue Statistiques -->
          <div v-if="viewMode === 'statistiques'">
            <!-- Filtres -->
            <ion-card>
              <ion-card-header>
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <ion-card-title>Filtres</ion-card-title>
                  <ion-button fill="clear" @click="showStatFilters = !showStatFilters">
                    <ion-icon :icon="chevronBackOutline" :class="{ 'rotate-up': showStatFilters, 'rotate-down': !showStatFilters }"></ion-icon>
                  </ion-button>
                </div>
              </ion-card-header>
              <ion-card-content v-if="showStatFilters">
                <ion-item>
                  <ion-label>Catégorie</ion-label>
                  <ion-select v-model="filterStatCategorie" placeholder="Toutes">
                    <ion-select-option :value="''">Toutes les catégories</ion-select-option>
                    <ion-select-option v-for="cat in categories" :key="cat" :value="cat">
                      {{ cat }}
                    </ion-select-option>
                  </ion-select>
                </ion-item>
                
                <ion-item>
                  <ion-label>Classe</ion-label>
                  <ion-select v-model="filterStatClasse" placeholder="Toutes">
                    <ion-select-option :value="''">Toutes les classes</ion-select-option>
                    <ion-select-option v-for="cls in classes" :key="cls" :value="cls">
                      {{ cls }}
                    </ion-select-option>
                  </ion-select>
                </ion-item>
                
                <ion-item>
                  <ion-label>Statut</ion-label>
                  <ion-select v-model="filterStatStatut" placeholder="Tous">
                    <ion-select-option :value="''">Tous les statuts</ion-select-option>
                    <ion-select-option value="En attente">En attente</ion-select-option>
                    <ion-select-option value="En cours">En cours</ion-select-option>
                    <ion-select-option value="Terminé">Terminé</ion-select-option>
                  </ion-select>
                </ion-item>
              </ion-card-content>
            </ion-card>

            <div v-if="filteredStatistiques.length === 0">
              <ion-card>
                <ion-card-content>
                  <p class="ion-text-center">Aucune statistique disponible</p>
                </ion-card-content>
              </ion-card>
            </div>
            <div v-else>
              <ion-card v-for="(stat, index) in filteredStatistiques" :key="index" class="stat-card">
                <ion-card-header>
                  <ion-card-title>
                    📅 Année {{ formatAnneeExercice(stat.anneeExercice) }}
                  </ion-card-title>
                </ion-card-header>
                <ion-card-content>
                  <!-- Métriques principales -->
                  <ion-grid>
                    <ion-row>
                      <ion-col size="6">
                        <div class="metric-box metric-total">
                          <div class="metric-icon">📚</div>
                          <div class="metric-value">{{ stat.totalProgrammesTravailles }}</div>
                          <div class="metric-label">Total</div>
                        </div>
                      </ion-col>
                      <ion-col size="6">
                        <div class="metric-box metric-termine">
                          <div class="metric-icon">✅</div>
                          <div class="metric-value">{{ stat.programmesTermines }}</div>
                          <div class="metric-label">Terminés</div>
                          <!-- <div class="metric-percent">{{ stat.tauxCompletion.toFixed(1) }}%</div> -->
                        </div>
                      </ion-col>
                    </ion-row>
                    <ion-row>
                      <ion-col size="6">
                        <div class="metric-box metric-cours">
                          <div class="metric-icon">▶️</div>
                          <div class="metric-value">{{ stat.programmesEnCours }}</div>
                          <div class="metric-label">En cours</div>
                          <!-- <div class="metric-percent">
                            {{ stat.totalProgrammesTravailles > 0 ? ((stat.programmesEnCours / stat.totalProgrammesTravailles) * 100).toFixed(1) : 0 }}%
                          </div> -->
                        </div>
                      </ion-col>
                      <ion-col size="6">
                        <div class="metric-box metric-attente">
                          <div class="metric-icon">⏳</div>
                          <div class="metric-value">{{ stat.programmesEnAttente }}</div>
                          <div class="metric-label">En attente</div>
                          <!-- <div class="metric-percent">
                            {{ stat.totalProgrammesTravailles > 0 ? ((stat.programmesEnAttente / stat.totalProgrammesTravailles) * 100).toFixed(1) : 0 }}%
                          </div> -->
                        </div>
                      </ion-col>
                    </ion-row>
                  </ion-grid>

                  <!-- Progression circulaire -->
                  <div class="progress-section">
                    <h4>Taux de complétion</h4>
                    <div class="circular-progress-container">
                      <svg class="circular-progress" viewBox="0 0 200 200">
                        <!-- Cercle de fond -->
                        <circle
                          class="circle-bg"
                          cx="100"
                          cy="100"
                          r="85"
                          fill="none"
                          stroke="#e9ecef"
                          stroke-width="20"
                        />
                        <!-- Cercle de progression -->
                        <circle
                          class="circle-progress"
                          cx="100"
                          cy="100"
                          r="85"
                          fill="none"
                          stroke="url(#gradient-complete)"
                          stroke-width="20"
                          stroke-linecap="round"
                          :style="{
                            strokeDasharray: `${(stat.tauxCompletion / 100) * 534} 534`,
                            transform: 'rotate(-90deg)',
                            transformOrigin: '100px 100px'
                          }"
                        />
                        <!-- Gradient pour le cercle -->
                        <defs>
                          <linearGradient id="gradient-complete" x1="0%" y1="0%" x2="100%" y2="100%">
                            <stop offset="0%" style="stop-color:#84fab0;stop-opacity:1" />
                            <stop offset="100%" style="stop-color:#8fd3f4;stop-opacity:1" />
                          </linearGradient>
                        </defs>
                      </svg>
                      <!-- Texte au centre -->
                      <div class="circular-progress-text">
                        <div class="progress-percentage">{{ stat.tauxCompletion.toFixed(1) }}%</div>
                        <div class="progress-label-small">Complétés</div>
                      </div>
                    </div>
                    
                    <!-- Légende -->
                    <!-- <div class="legend-mobile">
                      <div class="legend-item">
                        <div class="legend-color-termine"></div>
                        <span>Terminés ({{ stat.programmesTermines }})</span>
                      </div>
                      <div class="legend-item">
                        <div class="legend-color-cours"></div>
                        <span>En cours ({{ stat.programmesEnCours }})</span>
                      </div>
                      <div class="legend-item">
                        <div class="legend-color-attente"></div>
                        <span>En attente ({{ stat.programmesEnAttente }})</span>
                      </div>
                    </div> -->
                  </div>
                </ion-card-content>
              </ion-card>
            </div>
          </div>

          <!-- Vue Progression -->
          <div v-if="viewMode === 'progression'">
            <!-- Filtres -->
            <ion-card>
              <ion-card-header>
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <ion-card-title>Filtres</ion-card-title>
                  <ion-button fill="clear" @click="showProgressionFilters = !showProgressionFilters">
                    <ion-icon :icon="chevronBackOutline" :class="{ 'rotate-up': showProgressionFilters, 'rotate-down': !showProgressionFilters }"></ion-icon>
                  </ion-button>
                </div>
              </ion-card-header>
              <ion-card-content v-if="showProgressionFilters">
                <ion-searchbar 
                  v-model="filterSearch" 
                  placeholder="Rechercher un programme..."
                  animated
                  show-clear-button="focus"
                ></ion-searchbar>
                
                <ion-item>
                  <ion-label>Catégorie</ion-label>
                  <ion-select v-model="filterCategorie" placeholder="Toutes">
                    <ion-select-option :value="''">Toutes les catégories</ion-select-option>
                    <ion-select-option v-for="cat in categories" :key="cat" :value="cat">
                      {{ cat }}
                    </ion-select-option>
                  </ion-select>
                </ion-item>
                
                <ion-item>
                  <ion-label>Classe</ion-label>
                  <ion-select v-model="filterClasse" placeholder="Toutes">
                    <ion-select-option :value="''">Toutes les classes</ion-select-option>
                    <ion-select-option v-for="cls in classes" :key="cls" :value="cls">
                      {{ cls }}
                    </ion-select-option>
                  </ion-select>
                </ion-item>
                
                <ion-item>
                  <ion-label>Statut</ion-label>
                  <ion-select v-model="filterStatut" placeholder="Tous">
                    <ion-select-option :value="''">Tous les statuts</ion-select-option>
                    <ion-select-option value="En attente">En attente</ion-select-option>
                    <ion-select-option value="En cours">En cours</ion-select-option>
                    <ion-select-option value="Terminé">Terminé</ion-select-option>
                  </ion-select>
                </ion-item>
              </ion-card-content>
            </ion-card>

            <!-- Liste des programmes -->
            <div v-if="filteredProgression.length === 0">
              <ion-card>
                <ion-card-content>
                  <p class="ion-text-center">Aucun programme trouvé</p>
                </ion-card-content>
              </ion-card>
            </div>
            <ion-card v-for="prog in filteredProgression" :key="prog.programmeId" class="programme-card">
              <ion-card-header>
                <ion-card-title>{{ prog.programmeNom }}</ion-card-title>
              </ion-card-header>
              <ion-card-content>
                <div class="programme-info">
                  <ion-chip :color="getClasseColor(prog.classeNom)">
                    <ion-label>{{ prog.classeNom }}</ion-label>
                  </ion-chip>
                  <ion-chip color="medium">
                    <ion-label>{{ prog.categorieNom }}</ion-label>
                  </ion-chip>
                  <ion-chip color="light">
                    <ion-label>📅 {{ formatAnneeExercice(prog.anneeExercice) }}</ion-label>
                  </ion-chip>
                </div>
                <div class="programme-status">
                  <ion-badge :color="getStatutColor(prog.statutFinalNom)">
                    {{ getStatutLabel(prog.statutFinalNom) }}
                  </ion-badge>
                </div>
              </ion-card-content>
            </ion-card>
          </div>

          <!-- Vue Avancement -->
          <div v-if="viewMode === 'avancement'">
            <!-- Filtres -->
            <ion-card>
              <ion-card-header>
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <ion-card-title>Filtres</ion-card-title>
                  <ion-button fill="clear" @click="showAvancementFilters = !showAvancementFilters">
                    <ion-icon :icon="chevronBackOutline" :class="{ 'rotate-up': showAvancementFilters, 'rotate-down': !showAvancementFilters }"></ion-icon>
                  </ion-button>
                </div>
              </ion-card-header>
              <ion-card-content v-if="showAvancementFilters">
                <ion-item>
                  <ion-label>Catégorie</ion-label>
                  <ion-select v-model="filterAvancementCategorie" placeholder="Toutes">
                    <ion-select-option :value="''">Toutes les catégories</ion-select-option>
                    <ion-select-option v-for="cat in categoriesAvancement" :key="cat" :value="cat">
                      {{ cat }}
                    </ion-select-option>
                  </ion-select>
                </ion-item>
                
                <ion-item>
                  <ion-label>Classe</ion-label>
                  <ion-select v-model="filterAvancementClasse" placeholder="Toutes">
                    <ion-select-option :value="''">Toutes les classes</ion-select-option>
                    <ion-select-option v-for="cls in classesAvancement" :key="cls" :value="cls">
                      {{ cls }}
                    </ion-select-option>
                  </ion-select>
                </ion-item>
                
                <ion-item>
                  <ion-label>Statut</ion-label>
                  <ion-select v-model="filterAvancementStatut" placeholder="Tous">
                    <ion-select-option :value="''">Tous les statuts</ion-select-option>
                    <ion-select-option value="En attente">En attente</ion-select-option>
                    <ion-select-option value="En cours">En cours</ion-select-option>
                    <ion-select-option value="Terminé">Terminé</ion-select-option>
                  </ion-select>
                </ion-item>
              </ion-card-content>
            </ion-card>

            <!-- Liste des programmes avec historique -->
            <div v-if="filteredAvancement.length === 0">
              <ion-card>
                <ion-card-content>
                  <p class="ion-text-center">Aucun programme trouvé</p>
                </ion-card-content>
              </ion-card>
            </div>
            <ion-card v-for="prog in filteredAvancement" :key="prog.programmeId" class="avancement-card">
              <ion-card-header @click="toggleExpansion(prog.programmeId)">
                <ion-card-title>
                  <ion-icon :icon="chevronForwardOutline" :style="{ transform: expandedProgrammes.has(prog.programmeId) ? 'rotate(90deg)' : 'rotate(0deg)', transition: 'transform 0.3s' }" />
                  {{ prog.programmeNom }}
                </ion-card-title>
                <ion-card-subtitle>
                  <ion-chip :color="getClasseColor(prog.classeNom)" size="small">
                    <ion-label>{{ prog.classeNom }}</ion-label>
                  </ion-chip>
                  <ion-chip color="medium" size="small">
                    <ion-label>{{ prog.categorieNom }}</ion-label>
                  </ion-chip>
                  <ion-chip color="light" size="small">
                    <ion-label>📅 {{ formatAnneeExercice(prog.anneeExercice) }}</ion-label>
                  </ion-chip>
                </ion-card-subtitle>
              </ion-card-header>
              <ion-card-content>
                <div class="avancement-header-info">
                  <ion-badge :color="getStatutColor(prog.statutActuelNom)">
                    {{ getStatutLabel(prog.statutActuelNom) }}
                  </ion-badge>
                  <ion-badge color="light">
                    📊 {{ prog.nombreChangements }} changement(s)
                  </ion-badge>
                </div>

                <!-- Détails expandables -->
                <div v-if="expandedProgrammes.has(prog.programmeId)" class="avancement-details">
                  <div v-if="prog.datePremiereCP" class="detail-row">
                    <strong>Première CP:</strong> {{ formatDate(prog.datePremiereCP) }}
                  </div>
                  <div v-if="prog.dateDerniereCP" class="detail-row">
                    <strong>Dernière CP:</strong> {{ formatDate(prog.dateDerniereCP) }}
                  </div>

                  <!-- Historique -->
                  <h4 class="timeline-title">📜 Historique des changements</h4>
                  
                  <!-- État initial si nécessaire -->
                  <div 
                    v-if="prog.historique.length > 0 && prog.historique[0].statusNom !== 'En attente'" 
                    class="timeline-item"
                  >
                    <div class="timeline-line"></div>
                    <div class="timeline-dot"></div>
                    <div class="timeline-content">
                      <div class="timeline-date">Période initiale</div>
                      <ion-badge :color="getStatutColor('En attente')">
                        {{ getStatutLabel('En attente') }}
                      </ion-badge>
                      <div class="timeline-note">
                        <em>État initial du programme</em>
                      </div>
                    </div>
                  </div>

                  <!-- Historique des changements -->
                  <div 
                    v-for="(hist, index) in prog.historique" 
                    :key="hist.id" 
                    class="timeline-item"
                  >
                    <div v-if="index < prog.historique.length - 1" class="timeline-line"></div>
                    <div class="timeline-dot"></div>
                    <div class="timeline-content">
                      <div class="timeline-date">{{ formatDate(hist.dateChangement) }}</div>
                      <div class="timeline-change">
                        <ion-badge 
                          v-if="index > 0 || prog.historique[0].statusNom !== 'En attente'"
                          :color="getStatutColor(index > 0 ? prog.historique[index - 1].statusNom : 'En attente')"
                        >
                          {{ getStatutLabel(index > 0 ? prog.historique[index - 1].statusNom : 'En attente') }}
                        </ion-badge>
                        <span v-if="index > 0 || prog.historique[0].statusNom !== 'En attente'" class="arrow">→</span>
                        <ion-badge :color="getStatutColor(hist.statusNom)">
                          {{ getStatutLabel(hist.statusNom) }}
                        </ion-badge>
                      </div>
                      <div v-if="hist.classeProgressiveDate" class="timeline-cp">
                        CP du {{ new Date(hist.classeProgressiveDate).toLocaleDateString('fr-FR') }}
                      </div>
                      <div v-if="!hist.classeProgressiveId" class="timeline-note">
                        <em>Initialisation automatique</em>
                      </div>
                    </div>
                  </div>
                </div>
              </ion-card-content>
            </ion-card>
          </div>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import {
  IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButtons, IonBackButton,
  IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonCardSubtitle,
  IonItem, IonLabel, IonSelect, IonSelectOption, IonSegment, IonSegmentButton,
  IonSpinner, IonGrid, IonRow, IonCol, IonSearchbar, IonChip, IonBadge, IonIcon, IonButton
} from '@ionic/vue';
import { chevronForwardOutline, chevronBackOutline } from 'ionicons/icons';
import historiqueProgrammeService from '@/services/historique-programme.service';
import anneeExerciceService from '@/services/annee-exercice.service';
import type { 
  StatistiquesAnnuelles,
  ProgressionAnnuelle,
  ProgrammeAvancement,
  AnneeExercice
} from '@/types';

// État
const viewMode = ref<'statistiques' | 'progression' | 'avancement'>('statistiques');
const annees = ref<AnneeExercice[]>([]);
const selectedAnneeId = ref<number | null>(null);
const loading = ref(true);
const error = ref<string | null>(null);

const statistiques = ref<StatistiquesAnnuelles[]>([]);
const progression = ref<ProgressionAnnuelle[]>([]);
const avancement = ref<ProgrammeAvancement[]>([]);

// Filtres Statistiques
const filterStatCategorie = ref('');
const filterStatClasse = ref('');
const filterStatStatut = ref('');
const showStatFilters = ref(true);

// Filtres Progression
const filterSearch = ref('');
const filterCategorie = ref('');
const filterClasse = ref('');
const filterStatut = ref('');
const showProgressionFilters = ref(true);

// Filtres Avancement
const filterAvancementCategorie = ref('');
const filterAvancementClasse = ref('');
const filterAvancementStatut = ref('');
const showAvancementFilters = ref(true);

// État d'expansion
const expandedProgrammes = ref<Set<number>>(new Set());

// Chargement initial
const loadAnnees = async () => {
  try {
    const data = await anneeExerciceService.getAllAnneesExercice();
    annees.value = data;
    
    // Sélectionner l'année active par défaut
    const activeAnnee = data.find((a: AnneeExercice) => a.estActif);
    if (activeAnnee) {
      selectedAnneeId.value = activeAnnee.id;
    }
  } catch (err: any) {
    error.value = err.message || 'Erreur lors du chargement des années';
  }
};

const loadStatistiques = async () => {
  try {
    loading.value = true;
    // Charger à la fois les statistiques et la progression pour le filtrage
    const [statsData, progData] = await Promise.all([
      historiqueProgrammeService.getStatistiquesAnnuelles(
        selectedAnneeId.value || undefined
      ),
      historiqueProgrammeService.getProgressionAnnuelle(
        selectedAnneeId.value || undefined
      )
    ]);
    statistiques.value = statsData;
    progression.value = progData;
    error.value = null;
  } catch (err: any) {
    error.value = err.message || 'Erreur lors du chargement des statistiques';
  } finally {
    loading.value = false;
  }
};

const loadProgression = async () => {
  try {
    loading.value = true;
    const data = await historiqueProgrammeService.getProgressionAnnuelle(
      selectedAnneeId.value || undefined
    );
    progression.value = data;
    error.value = null;
  } catch (err: any) {
    error.value = err.message || 'Erreur lors du chargement de la progression';
  } finally {
    loading.value = false;
  }
};

const loadAvancement = async () => {
  try {
    loading.value = true;
    const data = await historiqueProgrammeService.getAvancementProgrammes(
      selectedAnneeId.value || undefined
    );
    avancement.value = data;
    error.value = null;
  } catch (err: any) {
    error.value = err.message || 'Erreur lors du chargement de l\'avancement';
  } finally {
    loading.value = false;
  }
};

// Computed
const categories = computed(() => {
  return Array.from(new Set(progression.value.map(p => p.categorieNom))).sort();
});

const classes = computed(() => {
  return Array.from(new Set(progression.value.map(p => p.classeNom))).sort();
});

const categoriesAvancement = computed(() => {
  return Array.from(new Set(avancement.value.map(p => p.categorieNom))).sort();
});

const classesAvancement = computed(() => {
  return Array.from(new Set(avancement.value.map(p => p.classeNom))).sort();
});
// Statistiques filtrées calculées à partir de la progression
const filteredStatistiques = computed(() => {
  // Filtrer la progression selon les critères
  const filtered = progression.value.filter((prog) => {
    const matchCategorie = !filterStatCategorie.value || prog.categorieNom === filterStatCategorie.value;
    const matchClasse = !filterStatClasse.value || prog.classeNom === filterStatClasse.value;
    const matchStatut = !filterStatStatut.value || prog.statutFinalNom === filterStatStatut.value;
    return matchCategorie && matchClasse && matchStatut;
  });

  // Grouper par année et calculer les statistiques
  const statsMap = new Map<string, any>();
  
  filtered.forEach(prog => {
    const annee = prog.anneeExercice;
    if (!statsMap.has(annee)) {
      statsMap.set(annee, {
        anneeExercice: annee,
        totalProgrammesTravailles: 0,
        programmesTermines: 0,
        programmesEnCours: 0,
        programmesEnAttente: 0,
        tauxCompletion: 0
      });
    }
    
    const stat = statsMap.get(annee);
    stat.totalProgrammesTravailles++;
    
    const statutNorm = prog.statutFinalNom.toLowerCase();
    if (statutNorm.includes('termin')) {
      stat.programmesTermines++;
    } else if (statutNorm.includes('cours')) {
      stat.programmesEnCours++;
    } else if (statutNorm.includes('attente')) {
      stat.programmesEnAttente++;
    }
  });

  // Calculer le taux de complétion
  statsMap.forEach(stat => {
    if (stat.totalProgrammesTravailles > 0) {
      stat.tauxCompletion = (stat.programmesTermines / stat.totalProgrammesTravailles) * 100;
    }
  });

  return Array.from(statsMap.values());
});

const filteredProgression = computed(() => {
  return progression.value.filter((prog) => {
    const matchStatut = !filterStatut.value || prog.statutFinalNom === filterStatut.value;
    const matchCategorie = !filterCategorie.value || prog.categorieNom === filterCategorie.value;
    const matchClasse = !filterClasse.value || prog.classeNom === filterClasse.value;
    const matchSearch = !filterSearch.value || 
      prog.programmeNom.toLowerCase().includes(filterSearch.value.toLowerCase()) ||
      prog.categorieNom.toLowerCase().includes(filterSearch.value.toLowerCase()) ||
      prog.classeNom.toLowerCase().includes(filterSearch.value.toLowerCase());
    return matchStatut && matchCategorie && matchClasse && matchSearch;
  });
});

const filteredAvancement = computed(() => {
  return avancement.value.filter((prog) => {
    const matchCategorie = !filterAvancementCategorie.value || prog.categorieNom === filterAvancementCategorie.value;
    const matchClasse = !filterAvancementClasse.value || prog.classeNom === filterAvancementClasse.value;
    const matchStatut = !filterAvancementStatut.value || prog.statutActuelNom === filterAvancementStatut.value;
    return matchCategorie && matchClasse && matchStatut;
  });
});

// Fonctions utilitaires
const formatAnneeExercice = (anneeExercice: string): string => {
  if (!anneeExercice) return '';
  const date = new Date(anneeExercice);
  return date.getFullYear().toString();
};

const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const getStatutLabel = (statut: string): string => {
  if (!statut) return '';
  const statutNormalized = statut.trim().toLowerCase();
  
  if (statutNormalized.includes('attente')) return 'EN ATTENTE';
  if (statutNormalized.includes('cours')) return 'EN COURS';
  if (statutNormalized.includes('termin')) return 'TERMINÉ';
  return statut;
};

const getStatutColor = (statut: string): string => {
  if (!statut) return 'medium';
  const statutNormalized = statut.trim().toLowerCase();
  
  if (statutNormalized.includes('attente')) return 'warning';
  if (statutNormalized.includes('cours')) return 'primary';
  if (statutNormalized.includes('termin')) return 'success';
  return 'medium';
};

const getClasseColor = (classe: string): string => {
  if (!classe) return 'medium';
  const classeNormalized = classe.trim().toLowerCase();
  
  if (classeNormalized.includes('ami')) return 'primary';
  if (classeNormalized.includes('compagnon')) return 'danger';
  if (classeNormalized.includes('eclaireur') || classeNormalized.includes('éclaireur')) return 'success';
  if (classeNormalized.includes('pionnier')) return 'medium';
  if (classeNormalized.includes('voyageur')) return 'tertiary';
  if (classeNormalized.includes('guide')) return 'warning';
  return 'medium';
};

const toggleExpansion = (programmeId: number) => {
  const newExpanded = new Set(expandedProgrammes.value);
  if (newExpanded.has(programmeId)) {
    newExpanded.delete(programmeId);
  } else {
    newExpanded.add(programmeId);
  }
  expandedProgrammes.value = newExpanded;
};

const handleSegmentChange = () => {
  if (viewMode.value === 'statistiques') {
    loadStatistiques();
  } else if (viewMode.value === 'progression') {
    loadProgression();
  } else if (viewMode.value === 'avancement') {
    loadAvancement();
  }
};

// Watchers
watch(selectedAnneeId, () => {
  handleSegmentChange();
});

// Initialisation
loadAnnees();
loadStatistiques();
</script>

<style scoped>
.historique-container {
  padding: 0;
}

/* Métriques */
.metric-box {
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.metric-icon {
  font-size: 2rem;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 2rem;
  font-weight: bold;
  margin: 8px 0;
}

.metric-label {
  font-size: 0.9rem;
  opacity: 0.8;
}

.metric-percent {
  font-size: 0.85rem;
  font-weight: 600;
  margin-top: 4px;
}

.metric-total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.metric-termine {
  background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
  color: #0f5132;
}

.metric-cours {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: #084298;
}

.metric-attente {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  color: #000;
}

/* Progression */
.progress-section {
  margin-top: 20px;
}

.progress-section h4 {
  margin: 0 0 16px 0;
  font-size: 1rem;
  font-weight: 600;
  text-align: center;
}

/* Progression circulaire */
.circular-progress-container {
  position: relative;
  width: 200px;
  height: 200px;
  margin: 0 auto 24px;
}

.circular-progress {
  width: 100%;
  height: 100%;
}

.circle-progress {
  transition: stroke-dasharray 0.8s ease;
}

.circular-progress-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.progress-percentage {
  font-size: 2.5rem;
  font-weight: bold;
  background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
}

.progress-label-small {
  font-size: 0.9rem;
  color: #6c757d;
  margin-top: 8px;
  font-weight: 500;
}

/* Légende */
.legend-mobile {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.85rem;
}

.legend-color-termine,
.legend-color-cours,
.legend-color-attente {
  width: 20px;
  height: 20px;
  border-radius: 4px;
}

.legend-color-termine {
  background: linear-gradient(90deg, #84fab0 0%, #8fd3f4 100%);
}

.legend-color-cours {
  background: linear-gradient(90deg, #fa709a 0%, #fee140 100%);
}

.legend-color-attente {
  background: linear-gradient(90deg, #ffecd2 0%, #fcb69f 100%);
}

/* Programme cards */
.programme-card,
.avancement-card {
  margin: 12px 16px;
}

.programme-info,
.avancement-header-info {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.programme-status {
  margin-top: 12px;
}

/* Détails avancement */
.avancement-details {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ddd;
}

.detail-row {
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.timeline-title {
  margin: 16px 0 12px 0;
  font-size: 1rem;
  font-weight: 600;
}

/* Timeline */
.timeline-item {
  position: relative;
  padding-left: 30px;
  padding-bottom: 20px;
}

.timeline-line {
  position: absolute;
  left: 9px;
  top: 20px;
  bottom: 0;
  width: 2px;
  background: #ddd;
}

.timeline-dot {
  position: absolute;
  left: 5px;
  top: 5px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--ion-color-primary);
  border: 2px solid white;
  box-shadow: 0 0 0 2px var(--ion-color-primary);
}

.timeline-content {
  background: #f8f9fa;
  padding: 12px;
  border-radius: 8px;
}

.timeline-date {
  font-size: 0.85rem;
  color: #6c757d;
  margin-bottom: 8px;
}

.timeline-change {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.arrow {
  font-weight: bold;
  color: #6c757d;
}

.timeline-cp {
  font-size: 0.85rem;
  color: #495057;
  margin-top: 4px;
}

.timeline-note {
  font-size: 0.85rem;
  color: #6c757d;
  margin-top: 4px;
}

/* Loading */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.loading-container p {
  margin-top: 16px;
  color: #6c757d;
}

/* Card header clickable */
ion-card-header {
  cursor: pointer;
}

ion-card-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* Animation chevron pour filtres */
.rotate-up {
  transform: rotate(-90deg);
  transition: transform 0.3s ease;
}

.header-logo {
  height: 40px;
  width: auto;
  margin-left: 10px;
  margin-right: 10px;
}

.rotate-down {
  transform: rotate(90deg);
  transition: transform 0.3s ease;
}
</style>
