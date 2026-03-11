# 📄 Guide des Exports PDF

## ✅ Comment fonctionne l'export PDF

Votre backend génère les PDF **correctement** et les retourne directement dans la réponse HTTP (pas de sauvegarde sur serveur).

**Endpoints PDF disponibles :**

### 1. Export liste des explorateurs
```http
GET /api/inscriptions/export/pdf?anneeExerciceId=1
```

### 2. Export budget
```http
POST /api/budget-global/export-pdf
Body: {
  "anneeExerciceId": 1,
  "colonnes": ["nomActivite", "montant", "date"],
  "titre": "Budget 2026"
}
```

---

## 🌐 Tester depuis Swagger UI

1. Allez sur https://explorateurs-backend.onrender.com/swagger-ui.html

2. Sélectionnez le serveur **Render** (pas localhost)

3. Trouvez l'endpoint PDF (ex: `/api/inscriptions/export/pdf`)

4. Cliquez **Try it out**

5. Remplissez les paramètres

6. Cliquez **Execute**

7. **Résultat** : Vous verrez un message "Success"

8. **Télécharger** : Cliquez sur le bouton **Download** qui apparaît sous la réponse

Le fichier PDF sera téléchargé sur votre ordinateur ! ✅

---

## 📱 Récupérer le PDF depuis l'app mobile Ionic

Le backend retourne le PDF en tant que `byte[]`, mais l'app mobile doit le **sauvegarder localement**.

### Option 1: Télécharger et sauvegarder (Recommandé)

Créez un service PDF dans votre app :

```typescript
// mobile/src/services/pdf.service.ts
import { Filesystem, Directory } from '@capacitor/filesystem';
import { FileOpener } from '@capacitor-community/file-opener';
import apiClient from '@/config/api.config';

export const PdfService = {
  /**
   * Télécharge et ouvre un PDF des inscriptions
   */
  async downloadInscriptionsPdf(anneeExerciceId?: number) {
    try {
      // Appel API
      const response = await apiClient.get('/api/inscriptions/export/pdf', {
        params: { anneeExerciceId },
        responseType: 'blob' // Important!
      });

      // Sauvegarder le blob en fichier
      const fileName = `inscriptions_${anneeExerciceId || 'all'}_${Date.now()}.pdf`;
      await this.savePdfToDevice(response.data, fileName);
      
      return fileName;
    } catch (error) {
      console.error('Erreur export PDF:', error);
      throw error;
    }
  },

  /**
   * Télécharge et ouvre un PDF du budget
   */
  async downloadBudgetPdf(request: {
    anneeExerciceId: number;
    colonnes: string[];
    titre: string;
  }) {
    try {
      const response = await apiClient.post('/api/budget-global/export-pdf', request, {
        responseType: 'blob'
      });

      const fileName = `budget_${request.anneeExerciceId}_${Date.now()}.pdf`;
      await this.savePdfToDevice(response.data, fileName);
      
      return fileName;
    } catch (error) {
      console.error('Erreur export PDF budget:', error);
      throw error;
    }
  },

  /**
   * Sauvegarde le blob PDF sur l'appareil et l'ouvre
   */
  async savePdfToDevice(blob: Blob, fileName: string) {
    // Convertir Blob en base64
    const base64Data = await this.blobToBase64(blob);
    
    // Sauvegarder dans le système de fichiers
    const savedFile = await Filesystem.writeFile({
      path: fileName,
      data: base64Data,
      directory: Directory.Documents,
      recursive: true
    });

    console.log('PDF sauvegardé:', savedFile.uri);

    // Ouvrir le PDF
    await FileOpener.open({
      filePath: savedFile.uri,
      contentType: 'application/pdf'
    });

    return savedFile.uri;
  },

  /**
   * Convertit un Blob en base64
   */
  blobToBase64(blob: Blob): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onloadend = () => {
        const base64 = reader.result as string;
        // Retirer le préfixe "data:application/pdf;base64,"
        resolve(base64.split(',')[1]);
      };
      reader.onerror = reject;
      reader.readAsDataURL(blob);
    });
  },

  /**
   * Liste tous les PDFs sauvegardés
   */
  async listSavedPdfs() {
    try {
      const files = await Filesystem.readdir({
        path: '',
        directory: Directory.Documents
      });
      
      return files.files.filter(f => f.name.endsWith('.pdf'));
    } catch (error) {
      console.error('Erreur lecture PDFs:', error);
      return [];
    }
  }
};
```

### Installer les dépendances nécessaires

```bash
cd mobile

# Filesystem (inclus dans Capacitor)
npm install @capacitor/filesystem

# File Opener (pour ouvrir le PDF)
npm install @capacitor-community/file-opener

# Synchroniser
npx cap sync android
```

### Utiliser dans un composant Vue

```vue
<!-- mobile/src/views/ExportPage.vue -->
<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>Exports PDF</ion-title>
      </ion-toolbar>
    </ion-header>
    
    <ion-content>
      <ion-list>
        <ion-item button @click="exportInscriptions">
          <ion-icon :icon="documentText" slot="start"></ion-icon>
          <ion-label>
            <h2>Exporter liste explorateurs</h2>
            <p>Télécharger la liste complète en PDF</p>
          </ion-label>
        </ion-item>

        <ion-item button @click="exportBudget">
          <ion-icon :icon="wallet" slot="start"></ion-icon>
          <ion-label>
            <h2>Exporter budget</h2>
            <p>Télécharger le budget en PDF</p>
          </ion-label>
        </ion-item>
      </ion-list>

      <ion-loading
        :is-open="isLoading"
        message="Génération du PDF..."
      />
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { 
  IonPage, IonHeader, IonToolbar, IonTitle, IonContent,
  IonList, IonItem, IonLabel, IonIcon, IonLoading,
  alertController, toastController
} from '@ionic/vue';
import { documentText, wallet } from 'ionicons/icons';
import { PdfService } from '@/services/pdf.service';

const isLoading = ref(false);

const exportInscriptions = async () => {
  try {
    isLoading.value = true;
    
    // Télécharger le PDF (sans paramètres = toutes les années)
    const fileName = await PdfService.downloadInscriptionsPdf();
    
    const toast = await toastController.create({
      message: `PDF téléchargé: ${fileName}`,
      duration: 3000,
      color: 'success',
      position: 'bottom'
    });
    await toast.present();
    
  } catch (error) {
    const alert = await alertController.create({
      header: 'Erreur',
      message: 'Impossible de générer le PDF',
      buttons: ['OK']
    });
    await alert.present();
  } finally {
    isLoading.value = false;
  }
};

const exportBudget = async () => {
  try {
    isLoading.value = true;
    
    const fileName = await PdfService.downloadBudgetPdf({
      anneeExerciceId: 1,
      colonnes: ['nomActivite', 'montant', 'date'],
      titre: 'Budget 2026'
    });
    
    const toast = await toastController.create({
      message: `PDF téléchargé: ${fileName}`,
      duration: 3000,
      color: 'success',
      position: 'bottom'
    });
    await toast.present();
    
  } catch (error) {
    const alert = await alertController.create({
      header: 'Erreur',
      message: 'Impossible de générer le PDF',
      buttons: ['OK']
    });
    await alert.present();
  } finally {
    isLoading.value = false;
  }
};
</script>
```

---

## 📂 Où sont sauvegardés les PDF sur Android ?

Les PDF sont sauvegardés dans :
```
/storage/emulated/0/Documents/
```

Vous pouvez les voir dans l'app **Fichiers** (Files) de votre téléphone.

---

## 🔧 Permissions Android nécessaires

Ajoutez dans `android/app/src/main/AndroidManifest.xml` :

```xml
<manifest>
    <!-- Permissions existantes -->
    
    <!-- Permission pour sauvegarder des fichiers -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    
    <!-- Android 13+ -->
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    <uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
    <uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
    
    <application>
        <!-- Provider pour partager des fichiers -->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
    </application>
</manifest>
```

Créez `android/app/src/main/res/xml/file_paths.xml` :

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path name="external_files" path="." />
    <files-path name="files" path="." />
    <cache-path name="cache" path="." />
</paths>
```

---

## 🧪 Tester l'export PDF

### Test 1: Depuis Swagger UI
1. Ouvrir Swagger sur Render
2. Tester `/api/inscriptions/export/pdf`
3. Cliquer **Download**
4. ✅ Le PDF doit s'ouvrir

### Test 2: Depuis curl
```bash
curl -X GET "https://explorateurs-backend.onrender.com/api/inscriptions/export/pdf?anneeExerciceId=1" \
  -H "Authorization: Bearer VOTRE_TOKEN" \
  -o inscriptions.pdf

# Le fichier inscriptions.pdf est créé ✅
```

### Test 3: Depuis l'app mobile
1. Cliquer sur "Exporter liste explorateurs"
2. Voir le loader "Génération du PDF..."
3. Le PDF s'ouvre automatiquement
4. ✅ Le fichier est dans Documents/

---

## ❓ FAQ

### Q: Pourquoi le PDF n'apparaît pas sur Render ?

**R:** C'est normal ! Le PDF n'est **jamais sauvegardé sur le serveur**. Il est généré à la volée et retourné directement dans la réponse HTTP. C'est plus sécurisé et économise de l'espace disque.

### Q: Comment voir les PDF générés ?

**R:** Sur Swagger UI, ils sont téléchargés automatiquement. Sur mobile, utilisez le `PdfService` ci-dessus pour les sauvegarder.

### Q: Puis-je sauvegarder les PDF sur le serveur ?

**R:** Pas recommandé, mais possible. Il faudrait :
1. Modifier le service pour sauvegarder dans `/data/pdfs/`
2. Créer un endpoint pour lister/télécharger les PDF
3. Gérer le nettoyage manuel (disque limité à 1GB)

**Meilleure approche** : Laisser tel quel (retour direct) et gérer la sauvegarde côté mobile.

### Q: Le PDF est vide ou corrompu

**R:** Vérifiez :
1. La réponse API retourne bien `Content-Type: application/pdf`
2. L'app utilise `responseType: 'blob'`
3. La conversion base64 est correcte
4. Les permissions Android sont accordées

---

## 📊 Architecture actuelle (Recommandée)

```
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   Mobile     │  HTTP   │   Backend    │         │   SQLite     │
│   Ionic      │ ──────► │   Render     │ ──────► │   /data/     │
│              │         │              │         │              │
└──────────────┘         └──────────────┘         └──────────────┘
       │                        │
       │    PDF bytes[]         │
       │ ◄──────────────────────┘
       │
       ▼
┌──────────────┐
│  Filesystem  │
│  Documents/  │
│  *.pdf       │
└──────────────┘
```

**Flux** :
1. Mobile demande PDF via API
2. Backend lit SQLite, génère PDF en mémoire
3. Backend retourne bytes[] dans réponse HTTP
4. Mobile reçoit bytes[], convertit en fichier
5. Mobile sauvegarde dans Documents/
6. Mobile ouvre le PDF avec viewer natif

**Avantages** :
- ✅ Pas de stockage serveur (économie disque)
- ✅ PDF toujours à jour (généré en temps réel)
- ✅ Sécurisé (pas de fichiers persistants)
- ✅ Scalable (pas de nettoyage à gérer)

---

## 🔒 Alternative : Sauvegarder temporairement sur serveur

Si vraiment nécessaire, modifiez le service :

```java
// Dans PdfExportService.java
public String generateAndSaveEnfantsPdf(List<InscriptionResponse> inscriptions, String annee) {
    // Générer le PDF
    byte[] pdfBytes = generateEnfantsPdf(inscriptions, annee);
    
    // Créer le dossier /data/pdfs si nécessaire
    Path pdfDir = Paths.get("/data/pdfs");
    if (!Files.exists(pdfDir)) {
        Files.createDirectories(pdfDir);
    }
    
    // Sauvegarder le fichier
    String fileName = "inscriptions_" + annee + "_" + System.currentTimeMillis() + ".pdf";
    Path filePath = pdfDir.resolve(fileName);
    Files.write(filePath, pdfBytes);
    
    return fileName; // Retourner le nom du fichier
}
```

Puis créer un endpoint de téléchargement :

```java
@GetMapping("/download/{fileName}")
public ResponseEntity<Resource> downloadPdf(@PathVariable String fileName) throws IOException {
    Path filePath = Paths.get("/data/pdfs/" + fileName);
    Resource resource = new FileSystemResource(filePath.toFile());
    
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
        .contentType(MediaType.APPLICATION_PDF)
        .body(resource);
}
```

⚠️ **Attention** : N'oubliez pas de nettoyer les vieux PDF régulièrement !

---

**Date** : 10 mars 2026  
**Status** : ✅ Les PDF sont générés correctement et retournés dans la réponse HTTP
