package com.explorateur.backend.controller;

import com.explorateur.backend.dto.CreateInscriptionRequest;
import com.explorateur.backend.dto.InscriptionResponse;
import com.explorateur.backend.service.InscriptionService;
import com.explorateur.backend.service.PdfExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
@Tag(name = "Inscriptions", description = "API de gestion des inscriptions")
@SecurityRequirement(name = "bearerAuth")
public class InscriptionController {
    
    private final InscriptionService inscriptionService;
    private final PdfExportService pdfExportService;
    
    @PostMapping
    @PreAuthorize("hasRole('Directeur') or hasRole('Co_Directeur')")
    @Operation(
            summary = "Créer une nouvelle inscription",
            description = "Inscrit un enfant pour une année d'exercice. Accessible uniquement au Directeur et Co-Directeur."
    )
    public ResponseEntity<InscriptionResponse> createInscription(
            @Valid @RequestBody CreateInscriptionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inscriptionService.createInscription(request));
    }
    
    @GetMapping
    @Operation(summary = "Récupérer toutes les inscriptions")
    public ResponseEntity<List<InscriptionResponse>> getAllInscriptions() {
        return ResponseEntity.ok(inscriptionService.getAllInscriptions());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une inscription par ID")
    public ResponseEntity<InscriptionResponse> getInscriptionById(
            @Parameter(description = "ID de l'inscription")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(inscriptionService.getInscriptionById(id));
    }
    
    @GetMapping("/filter")
    @Operation(
            summary = "Filtrer les inscriptions",
            description = "Filtre les inscriptions par année d'exercice, classe ou genre d'enfant"
    )
    public ResponseEntity<List<InscriptionResponse>> filterInscriptions(
            @Parameter(description = "ID de l'année d'exercice")
            @RequestParam(required = false) Long anneeExerciceId,
            @Parameter(description = "ID de la classe")
            @RequestParam(required = false) Long classeId,
            @Parameter(description = "Genre de l'enfant")
            @RequestParam(required = false) String genre
    ) {
        if (anneeExerciceId != null) {
            return ResponseEntity.ok(inscriptionService.getInscriptionsByAnneeExerciceId(anneeExerciceId));
        } else if (classeId != null) {
            return ResponseEntity.ok(inscriptionService.getInscriptionsByClasseId(classeId));
        } else if (genre != null) {
            return ResponseEntity.ok(inscriptionService.getInscriptionsByGenre(genre));
        } else {
            return ResponseEntity.ok(inscriptionService.getAllInscriptions());
        }
    }
    
    @PatchMapping("/{id}/assurance")
    @PreAuthorize("hasRole('Directeur') or hasRole('Co_Directeur')")
    @Operation(
            summary = "Mettre à jour le statut d'assurance",
            description = "Met à jour le statut d'assurance d'une inscription. Accessible au Directeur et Co-Directeur."
    )
    public ResponseEntity<InscriptionResponse> updateAssurance(
            @Parameter(description = "ID de l'inscription")
            @PathVariable Long id,
            @Parameter(description = "Nouveau statut d'assurance")
            @RequestParam Boolean estAssurance
    ) {
        return ResponseEntity.ok(inscriptionService.updateAssurance(id, estAssurance));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Directeur')")
    @Operation(
            summary = "Supprimer une inscription",
            description = "Supprime une inscription. Accessible uniquement au Directeur."
    )
    public ResponseEntity<Void> deleteInscription(
            @Parameter(description = "ID de l'inscription")
            @PathVariable Long id
    ) {
        inscriptionService.deleteInscription(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/export/pdf")
    @Operation(
            summary = "Exporter les inscriptions en PDF",
            description = "Génère un fichier PDF des enfants inscrits selon les filtres appliqués"
    )
    public ResponseEntity<byte[]> exportToPdf(
            @Parameter(description = "ID de l'année d'exercice")
            @RequestParam(required = false) Long anneeExerciceId,
            @Parameter(description = "ID de la classe")
            @RequestParam(required = false) Long classeId,
            @Parameter(description = "Genre de l'enfant")
            @RequestParam(required = false) String genre,
            @Parameter(description = "Statut d'assurance")
            @RequestParam(required = false) Boolean estAssurance
    ) throws IOException {
        // Récupérer les inscriptions selon les filtres
        List<InscriptionResponse> inscriptions;
        
        if (anneeExerciceId != null) {
            inscriptions = inscriptionService.getInscriptionsByAnneeExerciceId(anneeExerciceId);
        } else if (classeId != null) {
            inscriptions = inscriptionService.getInscriptionsByClasseId(classeId);
        } else if (genre != null) {
            inscriptions = inscriptionService.getInscriptionsByGenre(genre);
        } else {
            inscriptions = inscriptionService.getAllInscriptions();
        }
        
        // Appliquer les filtres supplémentaires (genre, assurance) si spécifiés
        if (genre != null && anneeExerciceId != null) {
            inscriptions = inscriptions.stream()
                    .filter(i -> genre.equals(i.getEnfantGenre()))
                    .toList();
        }
        
        if (estAssurance != null) {
            inscriptions = inscriptions.stream()
                    .filter(i -> estAssurance.equals(i.getEstAssurance()))
                    .toList();
        }
        
        // Déterminer l'année pour le titre (extraire uniquement l'année, pas la date complète)
        String annee;
        if (inscriptions.isEmpty()) {
            annee = String.valueOf(java.time.Year.now().getValue());
        } else {
            // Extraire l'année depuis la chaîne d'année d'exercice (format peut être "2026" ou "2026-01-01")
            String anneeExercice = inscriptions.get(0).getAnneeExercice();
            // Prendre les 4 premiers caractères qui représentent l'année
            annee = anneeExercice.substring(0, 4);
        }
        
        // Générer le PDF
        byte[] pdfBytes = pdfExportService.generateEnfantsPdf(inscriptions, annee);
        
        // Configurer les en-têtes HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "liste_explorateur_" + annee + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
