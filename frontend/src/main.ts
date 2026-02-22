import './assets/main.css'
import 'primevue/resources/themes/lara-light-blue/theme.css'
import 'primevue/resources/primevue.min.css'
import 'primeicons/primeicons.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import PrimeVue from 'primevue/config'

import App from './App.vue'
import router from './router'
import { setupAutoSync } from './services/syncService'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(PrimeVue)

// Configuration de la synchronisation automatique
setupAutoSync()

app.mount('#app')
