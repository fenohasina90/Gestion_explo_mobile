package com.explorateur.backend.controller;

import com.explorateur.backend.dto.CreateInscriptionRequest;
import com.explorateur.backend.dto.InscriptionResponse;
import com.explorateur.backend.service.InscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
@Tag(name = "Inscriptions", description = "API de gestion des inscriptions")
@SecurityRequirement(name = "bearerAuth")
public class InscriptionController {
    
    private final InscriptionService inscriptionService;
    
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
}
