import { Filesystem, Directory } from '@capacitor/filesystem';
import { FileOpener } from '@capacitor-community/file-opener';
import apiService from '@/services/api.service';

const apiClient = apiService.getAxiosInstance();

export const PdfService = {
  /**
   * Télécharge et ouvre un PDF des inscriptions avec filtres
   */
  async downloadInscriptionsPdf(
    anneeExerciceId?: number,
    classeId?: number,
    genre?: string,
    estAssurance?: boolean
  ) {
    try {
      console.log('=== DÉBUT EXPORT INSCRIPTIONS PDF ===');
      console.log('Paramètres:', { anneeExerciceId, classeId, genre, estAssurance });
      
      const params: Record<string, any> = {};
      if (anneeExerciceId) params.anneeExerciceId = anneeExerciceId;
      if (classeId) params.classeId = classeId;
      if (genre) params.genre = genre;
      if (estAssurance !== undefined) params.estAssurance = estAssurance;

      // Appel API
      const axiosInstance = apiService.getAxiosInstance();
      console.log('Envoi requête GET /api/inscriptions/export/pdf...');
      
      const response = await axiosInstance.get('/api/inscriptions/export/pdf', {
        params,
        responseType: 'blob' // Important!
      });

      console.log('Réponse reçue:', {
        status: response.status,
        contentType: response.headers['content-type'],
        size: response.data.size
      });

      // Sauvegarder le blob en fichier
      const fileName = `inscriptions_${anneeExerciceId || 'all'}_${Date.now()}.pdf`;
      console.log('Sauvegarde du fichier:', fileName);
      
      await this.savePdfToDevice(response.data, fileName);
      
      console.log('=== EXPORT INSCRIPTIONS PDF RÉUSSI ===');
      return fileName;
    } catch (error: any) {
      console.error('=== ERREUR EXPORT INSCRIPTIONS PDF ===');
      console.error('Type d\'erreur:', error.constructor.name);
      console.error('Message:', error.message);
      if (error.response) {
        console.error('Statut HTTP:', error.response.status);
        console.error('Données:', error.response.data);
      }
      console.error('Stack:', error.stack);
      throw error;
    }
  },

  /**
   * Télécharge et ouvre un PDF du budget
   */
  async downloadBudgetPdf(request: {
    anneeExerciceId: number;
    includeDate?: boolean;
    includeNomActivite?: boolean;
    includeCoutActivite?: boolean;
    includeDescriptionActivite?: boolean;
    includeDetailsActivite?: boolean;
    includeCoutDetails?: boolean;
    includeStatutActivite?: boolean;
  }) {
    try {
      console.log('=== DÉBUT EXPORT BUDGET PDF ===');
      console.log('Requête:', request);
      
      const axiosInstance = apiService.getAxiosInstance();
      console.log('Envoi requête POST /api/budget-global/export-pdf...');
      
      const response = await axiosInstance.post('/api/budget-global/export-pdf', request, {
        responseType: 'blob'
      });

      console.log('Réponse reçue:', {
        status: response.status,
        contentType: response.headers['content-type'],
        size: response.data.size
      });

      const fileName = `budget_${request.anneeExerciceId}_${Date.now()}.pdf`;
      console.log('Sauvegarde du fichier:', fileName);
      
      await this.savePdfToDevice(response.data, fileName);
      
      console.log('=== EXPORT BUDGET PDF RÉUSSI ===');
      return fileName;
    } catch (error: any) {
      console.error('=== ERREUR EXPORT BUDGET PDF ===');
      console.error('Type d\'erreur:', error.constructor.name);
      console.error('Message:', error.message);
      if (error.response) {
        console.error('Statut HTTP:', error.response.status);
        console.error('Données:', error.response.data);
      }
      console.error('Stack:', error.stack);
      throw error;
    }
  },

  /**
   * Sauvegarde le blob PDF sur l'appareil et l'ouvre
   */
  async savePdfToDevice(blob: Blob, fileName: string) {
    try {
      console.log('Conversion Blob en base64...', { size: blob.size, type: blob.type });
      
      // Convertir Blob en base64
      const base64Data = await this.blobToBase64(blob);
      console.log('Conversion réussie, taille base64:', base64Data.length);
      
      // Sauvegarder dans le système de fichiers
      console.log('Écriture fichier dans Documents/...');
      const savedFile = await Filesystem.writeFile({
        path: fileName,
        data: base64Data,
        directory: Directory.Documents,
        recursive: true
      });

      console.log('✅ PDF sauvegardé:', savedFile.uri);

      // Ouvrir le PDF
      console.log('Ouverture du PDF...');
      await FileOpener.open({
        filePath: savedFile.uri,
        contentType: 'application/pdf'
      });

      console.log('✅ PDF ouvert avec succès');
      return savedFile.uri;
    } catch (error: any) {
      console.error('❌ Erreur savePdfToDevice:', {
        name: error.constructor.name,
        message: error.message,
        stack: error.stack
      });
      throw error;
    }
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