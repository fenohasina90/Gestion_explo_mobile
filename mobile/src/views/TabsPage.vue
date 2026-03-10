<template>
  <ion-page>
    <ion-tabs>
      <ion-router-outlet></ion-router-outlet>
      <ion-tab-bar slot="bottom">
        <ion-tab-button tab="home" href="/tabs/home">
          <ion-icon aria-hidden="true" :icon="homeOutline" />
          <ion-label>Accueil</ion-label>
        </ion-tab-button>

        <ion-tab-button tab="enfants" href="/tabs/enfants">
          <ion-icon aria-hidden="true" :icon="peopleOutline" />
          <ion-label>Explorateurs</ion-label>
        </ion-tab-button>

        <ion-tab-button tab="cp" href="/tabs/cp">
          <ion-icon aria-hidden="true" :icon="ribbonOutline" />
          <ion-label>CP</ion-label>
        </ion-tab-button>

        <ion-tab-button tab="budget" href="/tabs/budget">
          <ion-icon aria-hidden="true" :icon="cashOutline" />
          <ion-label>Budget</ion-label>
        </ion-tab-button>

        <ion-tab-button tab="profil" href="/tabs/profil">
          <ion-icon aria-hidden="true" :icon="personOutline" />
          <ion-label>Profil</ion-label>
        </ion-tab-button>
      </ion-tab-bar>
    </ion-tabs>
  </ion-page>
</template>

<script setup lang="ts">
import { IonTabBar, IonTabButton, IonTabs, IonLabel, IonIcon, IonPage, IonRouterOutlet } from '@ionic/vue';
import { homeOutline, peopleOutline, ribbonOutline, cashOutline, personOutline } from 'ionicons/icons';
</script>
