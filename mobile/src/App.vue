<template>
  <ion-app>
    <div class="app-background"></div>
    <ion-router-outlet />
  </ion-app>
</template>

<script setup lang="ts">
import { IonApp, IonRouterOutlet } from '@ionic/vue';
</script>

<style scoped>
.app-background {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0.8)), url('/assets/pathfinder-flag.png');
  /* background-image: url('/assets/pathfinder-flag.png'); */
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  z-index: -2;
}

.app-background::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  z-index: 1;
}
</style>
