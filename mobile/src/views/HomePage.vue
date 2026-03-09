<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
        <ion-buttons slot="start">
          <img src="/assets/logo.png" alt="Logo" class="header-logo" />
        </ion-buttons>
        <ion-title>Tableau de bord</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-header collapse="condense">
        <ion-toolbar>
          <ion-title size="large">Tableau de bord</ion-title>
        </ion-toolbar>
      </ion-header>

      <div class="ion-padding">
        <!-- Carte de bienvenue -->
        <ion-card class="welcome-card">
          <ion-card-header>
            <ion-card-subtitle>Bienvenue</ion-card-subtitle>
            <ion-card-title>{{ authStore.user?.username }}</ion-card-title>
          </ion-card-header>
          <ion-card-content>
            <div class="user-info">
              <div class="info-item">
                <span class="info-label">Rôle:</span>
                <span class="info-value">{{ authStore.user?.role }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Année:</span>
                <span class="info-value">{{ authStore.user?.anneeExercice ? new Date(authStore.user.anneeExercice).getFullYear() : 'N/A' }}</span>
              </div>
            </div>
            <ion-badge :color="authStore.user?.active ? 'success' : 'danger'" class="status-badge">
              {{ authStore.user?.active ? 'Actif' : 'Inactif' }}
            </ion-badge>
          </ion-card-content>
        </ion-card>

        <!-- Actions principales -->
        <h2 class="section-title">Actions rapides</h2>
        
        <ion-grid>
          <ion-row>
            <ion-col size="6">
              <ion-card button @click="$router.push('/tabs/enfants')" class="action-card-wrapper">
                <ion-card-content class="action-card">
                  <ion-icon :icon="peopleOutline" class="action-icon"></ion-icon>
                  <h3>Enfants</h3>
                </ion-card-content>
              </ion-card>
            </ion-col>
            <ion-col size="6">
              <ion-card button @click="$router.push('/tabs/activites')" class="action-card-wrapper">
                <ion-card-content class="action-card">
                  <ion-icon :icon="calendarOutline" class="action-icon"></ion-icon>
                  <h3>Activités</h3>
                </ion-card-content>
              </ion-card>
            </ion-col>
          </ion-row>
        </ion-grid>

        <!-- Section Gestion du Staff (Directeur et Co-Directeur) -->
        <div v-if="canManageStaff">
          <h2 class="section-title">Gestion du Staff</h2>
          
          <ion-grid>
            <ion-row>
              <ion-col size="12">
                <ion-card button @click="$router.push('/staffs')" class="action-card-wrapper">
                  <ion-card-content class="action-card staff-card">
                    <ion-icon :icon="personCircleOutline" class="action-icon"></ion-icon>
                    <h3>Staffs & Instructeurs</h3>
                    <p>Gérer les membres du staff</p>
                  </ion-card-content>
                </ion-card>
              </ion-col>
            </ion-row>
          </ion-grid>
        </div>

        <!-- Section Gestion des Programmes (Directeur et Co-Directeur) -->
        <div v-if="canManageStaff">
          <h2 class="section-title">Gestion des Programmes</h2>
          
          <ion-grid>
            <ion-row>
              <ion-col size="6">
                <ion-card button @click="$router.push('/categories')" class="action-card-wrapper">
                  <ion-card-content class="action-card programme-card">
                    <ion-icon :icon="documentTextOutline" class="action-icon"></ion-icon>
                    <h3>Catégories</h3>
                  </ion-card-content>
                </ion-card>
              </ion-col>
              <ion-col size="6">
                <ion-card button @click="$router.push('/programmes')" class="action-card-wrapper">
                  <ion-card-content class="action-card programme-card">
                    <ion-icon :icon="listOutline" class="action-icon"></ion-icon>
                    <h3>Programmes</h3>
                  </ion-card-content>
                </ion-card>
              </ion-col>
            </ion-row>
            <ion-row>
              <ion-col size="12">
                <ion-card button @click="$router.push('/classes-progressives')" class="action-card-wrapper">
                  <ion-card-content class="action-card programme-card-large">
                    <ion-icon :icon="checkmarkOutline" class="action-icon"></ion-icon>
                    <h3>Classes Progressives (CP)</h3>
                    <p>Planifier et gérer les séances</p>
                  </ion-card-content>
                </ion-card>
              </ion-col>
            </ion-row>
            <ion-row>
              <ion-col size="12">
                <ion-card button @click="$router.push('/historique-programmes')" class="action-card-wrapper">
                  <ion-card-content class="action-card programme-card-large">
                    <ion-icon :icon="statsChartOutline" class="action-icon"></ion-icon>
                    <h3>Historique des Programmes</h3>
                    <p>Statistiques et suivi détaillé</p>
                  </ion-card-content>
                </ion-card>
              </ion-col>
            </ion-row>
          </ion-grid>
        </div>

        <!-- Section Budget (Tous les utilisateurs) -->
        <h2 class="section-title">Budget</h2>
        
        <ion-grid>
          <ion-row>
            <ion-col size="6">
              <ion-card button @click="$router.push('/tabs/budget')" class="action-card-wrapper">
                <ion-card-content class="action-card">
                  <ion-icon :icon="walletOutline" class="action-icon"></ion-icon>
                  <h3>Budget Global</h3>
                </ion-card-content>
              </ion-card>
            </ion-col>
            <ion-col size="6">
              <ion-card button @click="$router.push('/mouvements-budgetaires')" class="action-card-wrapper">
                <ion-card-content class="action-card">
                  <ion-icon :icon="cashOutline" class="action-icon"></ion-icon>
                  <h3>Mouvements</h3>
                </ion-card-content>
              </ion-card>
            </ion-col>
          </ion-row>
        </ion-grid>

        <!-- Section Administration (Directeur uniquement) -->
        <div v-if="authStore.isDirecteur">
          <h2 class="section-title">Administration</h2>
          
          <ion-grid>
            <ion-row>
              <ion-col size="6">
                <ion-card button @click="$router.push('/utilisateurs')">
                  <ion-card-content class="action-card admin-card">
                    <ion-icon :icon="personOutline" class="action-icon"></ion-icon>
                    <h3>Utilisateurs</h3>
                  </ion-card-content>
                </ion-card>
              </ion-col>
              <ion-col size="6">
                <ion-card button @click="$router.push('/annees-exercice')">
                  <ion-card-content class="action-card admin-card">
                    <ion-icon :icon="documentTextOutline" class="action-icon"></ion-icon>
                    <h3>Années</h3>
                  </ion-card-content>
                </ion-card>
              </ion-col>
            </ion-row>
          </ion-grid>
        </div>

        <!-- Section Audit (Tous les utilisateurs) -->
        <h2 class="section-title">Audit</h2>
        
        <ion-grid>
          <ion-row>
            <ion-col size="12">
              <ion-card button @click="$router.push('/journal')">
                <ion-card-content class="action-card audit-card">
                  <ion-icon :icon="documentTextOutline" class="action-icon"></ion-icon>
                  <h3>Journal d'Audit</h3>
                  <p>Consulter l'historique des actions</p>
                </ion-card-content>
              </ion-card>
            </ion-col>
          </ion-row>
        </ion-grid>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import {
  IonContent,
  IonHeader,
  IonPage,
  IonTitle,
  IonToolbar,
  IonButtons,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardSubtitle,
  IonCardContent,
  IonBadge,
  IonGrid,
  IonRow,
  IonCol,
  IonIcon
} from '@ionic/vue';
import {
  peopleOutline,
  calendarOutline,
  personOutline,
  documentTextOutline,
  personCircleOutline,
  listOutline,
  checkmarkOutline,
  statsChartOutline,
  walletOutline,
  cashOutline
} from 'ionicons/icons';
import { useAuthStore } from '@/stores/auth.store';

const authStore = useAuthStore();

const canManageStaff = computed(() => {
  return authStore.user?.role === 'Directeur' || authStore.user?.role === 'Co_Directeur';
});
</script>

<style scoped>
.section-title {
  color: var(--ion-color-primary);
  font-size: 1rem;
  font-weight: 600;
  margin: 1.25rem 0 0.75rem;
  padding-left: 0.25rem;
}

.welcome-card {
  background: linear-gradient(135deg, var(--ion-color-primary-tint) 0%, var(--ion-color-primary) 100%);
  color: white;
}

.welcome-card ion-card-subtitle,
.welcome-card ion-card-title {
  color: white;
}

.user-info {
  margin: 0.75rem 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin: 0.5rem 0;
  font-size: 0.875rem;
}

.info-label {
  opacity: 0.9;
}

.info-value {
  font-weight: 600;
}

.status-badge {
  margin-top: 0.5rem;
  font-size: 0.75rem;
}

.action-card-wrapper {
  margin: 0;
}

.action-card {
  text-align: center;
  padding: 1.25rem 1rem;
  min-height: 100px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.action-card h3 {
  font-size: 0.875rem;
  font-weight: 600;
  margin: 0;
}

.action-icon {
  font-size: 36px;
  color: var(--ion-color-primary);
  margin-bottom: 0.5rem;
}

.admin-card .action-icon {
  color: var(--ion-color-warning);
}

.staff-card {
  padding: 1rem;
}

.staff-card .action-icon {
  color: var(--ion-color-tertiary);
  font-size: 32px;
}

.staff-card h3 {
  font-size: 0.9375rem;
  margin: 0.5rem 0 0.25rem;
}

.staff-card p {
  margin: 0;
  font-size: 0.75rem;
  color: var(--ion-color-medium);
}

.audit-card {
  padding: 1rem;
}

.audit-card .action-icon {
  color: var(--ion-color-secondary);
  font-size: 32px;
}

.audit-card h3 {
  font-size: 0.9375rem;
  margin: 0.5rem 0 0.25rem;
}

.audit-card p {
  margin: 0;
  font-size: 0.75rem;
  color: var(--ion-color-medium);
}

.programme-card .action-icon {
  color: var(--ion-color-success);
}

.programme-card-large {
  padding: 1rem;
}

.programme-card-large .action-icon {
  color: var(--ion-color-success);
  font-size: 32px;
}

.programme-card-large h3 {
  font-size: 0.9375rem;
  margin: 0.5rem 0 0.25rem;
}

.programme-card-large p {
  margin: 0;
  font-size: 0.75rem;
  color: var(--ion-color-medium);
}

.action-card h3 {
  margin: 0;
  font-size: 1rem;
  font-weight: 500;
  color: var(--ion-color-dark);
}
</style>
