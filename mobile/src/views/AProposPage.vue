<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button default-href="/tabs/profil"></ion-back-button>
        </ion-buttons>
        <ion-title>À propos</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <div class="ion-padding">
        <!-- Logo et nom de l'application -->
        <div class="app-header">
          <img src="/assets/logo.png" alt="Logo" class="app-logo" />
          <h1>Gestion Explorations</h1>
          <p class="version">Version 1.0.0</p>
        </div>

        <!-- Description de l'application -->
        <ion-card>
          <ion-card-header>
            <ion-card-title>
              <ion-icon :icon="informationCircleOutline" class="title-icon"></ion-icon>
              À propos de l'application
            </ion-card-title>
          </ion-card-header>
          <ion-card-content>
            <p>
              Gestion Explorateur est une application mobile pour la gestion complète 
              des activités des explorateurs. Elle permet de gérer les inscriptions, 
              les staffs, les budgets, les programmes et le suivi des participations.
            </p>
            <p class="features-title">Fonctionnalités principales :</p>
            <ul class="features-list">
              <li>Gestion des explorateurs et inscriptions</li>
              <li>Gestion du personnel (staffs)</li>
              <li>Gestion budgétaire complète</li>
              <li>Suivi des programmes et activités</li>
              <li>Classes progressives</li>
              <li>Statistiques de participation détaillées</li>
            </ul>
          </ion-card-content>
        </ion-card>

        <!-- Développeur -->
        <ion-card>
          <ion-card-header>
            <ion-card-title>
              <ion-icon :icon="personCircleOutline" class="title-icon"></ion-icon>
              Développeur
            </ion-card-title>
          </ion-card-header>
          <ion-card-content>
            <div class="developer-info">
              <div class="info-row">
                <ion-icon :icon="personOutline" class="info-icon"></ion-icon>
                <div>
                  <p class="info-label">Nom complet</p>
                  <p class="info-value">MANGALAHY Fenohasina</p>
                </div>
              </div>

              <div class="info-row">
                <ion-icon :icon="mailOutline" class="info-icon"></ion-icon>
                <div>
                  <p class="info-label">Email</p>
                  <a :href="'mailto:mangalahyfenohasina@gmail.com'" class="info-value link">
                    mangalahyfenohasina@gmail.com
                  </a>
                </div>
              </div>

              <div class="info-row">
                <ion-icon :icon="logoWhatsapp" class="info-icon"></ion-icon>
                <div>
                  <p class="info-label">WhatsApp</p>
                  <a :href="'https://wa.me/261384337119'" class="info-value link" target="_blank">
                    +261 38 43 371 19
                  </a>
                </div>
              </div>
            </div>
          </ion-card-content>
        </ion-card>

        <!-- Technologies -->
        <ion-card>
          <ion-card-header>
            <ion-card-title>
              <ion-icon :icon="codeSlashOutline" class="title-icon"></ion-icon>
              Technologies utilisées
            </ion-card-title>
          </ion-card-header>
          <ion-card-content>
            <div class="tech-grid">
              <ion-chip color="primary">
                <ion-icon :icon="logoVue"></ion-icon>
                <ion-label>Vue 3</ion-label>
              </ion-chip>
              <ion-chip color="primary">
                <ion-icon :icon="phonePortraitOutline"></ion-icon>
                <ion-label>Ionic 7</ion-label>
              </ion-chip>
              <ion-chip color="success">
                <ion-icon :icon="serverOutline"></ion-icon>
                <ion-label>Spring Boot</ion-label>
              </ion-chip>
              <ion-chip color="warning">
                <ion-icon :icon="logoJavascript"></ion-icon>
                <ion-label>TypeScript</ion-label>
              </ion-chip>
            </div>
          </ion-card-content>
        </ion-card>

        <!-- Copyright -->
        <div class="copyright">
          <p>© 2026 MANGALAHY Fenohasina</p>
          <p>Tous droits réservés</p>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonButtons,
  IonBackButton,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardContent,
  IonIcon,
  IonChip,
  IonLabel,
} from '@ionic/vue';
import {
  informationCircleOutline,
  personCircleOutline,
  personOutline,
  mailOutline,
  logoWhatsapp,
  codeSlashOutline,
  logoVue,
  phonePortraitOutline,
  serverOutline,
  logoJavascript,
} from 'ionicons/icons';
</script>

<style scoped>
.app-header {
  text-align: center;
  padding: 2rem 0;
}

.app-logo {
  width: 120px;
  height: 120px;
  margin: 0 auto 1rem;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.app-header h1 {
  font-size: 1.75rem;
  font-weight: bold;
  margin: 1rem 0 0.5rem;
  color: var(--ion-color-primary);
}

.version {
  color: var(--ion-color-medium);
  font-size: 0.875rem;
}

.title-icon {
  margin-right: 8px;
  vertical-align: middle;
  font-size: 1.25rem;
}

ion-card-title {
  display: flex;
  align-items: center;
  font-size: 1.125rem;
}

ion-card-content p {
  margin-bottom: 1rem;
  line-height: 1.6;
  color: var(--ion-color-dark);
}

.features-title {
  font-weight: 600;
  margin-top: 1.5rem;
  margin-bottom: 0.5rem;
  color: var(--ion-color-primary);
}

.features-list {
  padding-left: 1.5rem;
  margin: 0;
}

.features-list li {
  margin-bottom: 0.5rem;
  line-height: 1.5;
}

.developer-info {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
}

.info-icon {
  font-size: 1.5rem;
  color: var(--ion-color-primary);
  margin-top: 0.25rem;
  flex-shrink: 0;
}

.info-label {
  font-size: 0.875rem;
  color: var(--ion-color-medium);
  margin: 0 0 0.25rem;
}

.info-value {
  font-size: 1rem;
  font-weight: 500;
  color: var(--ion-color-dark);
  margin: 0;
}

.link {
  color: var(--ion-color-primary);
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

.tech-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tech-grid ion-chip {
  margin: 0;
}

.tech-grid ion-icon {
  margin-right: 4px;
}

.copyright {
  text-align: center;
  padding: 2rem 0 1rem;
  color: var(--ion-color-medium);
}

.copyright p {
  margin: 0.25rem 0;
  font-size: 0.875rem;
}
</style>
