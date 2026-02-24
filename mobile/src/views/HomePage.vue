<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
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
        <ion-card>
          <ion-card-header>
            <ion-card-subtitle>Bienvenue</ion-card-subtitle>
            <ion-card-title>{{ authStore.user?.username }}</ion-card-title>
          </ion-card-header>
          <ion-card-content>
            <p><strong>Rôle:</strong> {{ authStore.user?.role }}</p>
            <p><strong>Année d'exercice:</strong> {{ authStore.user?.anneeExercice ? new Date(authStore.user.anneeExercice).getFullYear() : 'N/A' }}</p>
            <ion-badge :color="authStore.user?.active ? 'success' : 'danger'">
              {{ authStore.user?.active ? 'Actif' : 'Inactif' }}
            </ion-badge>
          </ion-card-content>
        </ion-card>

        <!-- Actions principales -->
        <h2 class="section-title">Actions rapides</h2>
        
        <ion-grid>
          <ion-row>
            <ion-col size="6">
              <ion-card button @click="$router.push('/tabs/enfants')">
                <ion-card-content class="action-card">
                  <ion-icon :icon="peopleOutline" class="action-icon"></ion-icon>
                  <h3>Enfants</h3>
                </ion-card-content>
              </ion-card>
            </ion-col>
            <ion-col size="6">
              <ion-card button @click="$router.push('/tabs/activites')">
                <ion-card-content class="action-card">
                  <ion-icon :icon="calendarOutline" class="action-icon"></ion-icon>
                  <h3>Activités</h3>
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
import {
  IonContent,
  IonHeader,
  IonPage,
  IonTitle,
  IonToolbar,
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
  documentTextOutline
} from 'ionicons/icons';
import { useAuthStore } from '@/stores/auth.store';

const authStore = useAuthStore();
</script>

<style scoped>
.section-title {
  color: var(--ion-color-primary);
  font-size: 1.25rem;
  font-weight: 600;
  margin: 24px 0 12px;
}

.action-card {
  text-align: center;
  padding: 24px 16px;
}

.action-icon {
  font-size: 48px;
  color: var(--ion-color-primary);
  margin-bottom: 8px;
}

.admin-card .action-icon {
  color: var(--ion-color-warning);
}

.audit-card {
  padding: 20px;
}

.audit-card .action-icon {
  color: var(--ion-color-secondary);
}

.audit-card p {
  margin-top: 4px;
  font-size: 0.875rem;
  color: var(--ion-color-medium);
}

.action-card h3 {
  margin: 0;
  font-size: 1rem;
  font-weight: 500;
  color: var(--ion-color-dark);
}
</style>
