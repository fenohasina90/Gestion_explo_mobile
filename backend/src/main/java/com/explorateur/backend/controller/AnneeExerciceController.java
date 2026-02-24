package com.explorateur.backend.controller;

import com.explorateur.backend.dto.AnneeExerciceResponse;
import com.explorateur.backend.dto.CreateAnneeExerciceRequest;
import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.service.AnneeExerciceInitService;
import com.explorateur.backend.service.AnneeExerciceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des années d'exercice
 */
@RestController
@RequestMapping("/api/annee-exercice")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
@Tag(name = "Année d'Exercice", description = "API de gestion des années d'exercice du Club des Explorateurs")
public class AnneeExerciceController {

    private final AnneeExerciceInitService anneeExerciceInitService;
    private final AnneeExerciceService anneeExerciceService;

    /**
     * Obtenir l'année d'exercice en cours
     */
    @GetMapping("/courante")
    @Operation(
        summary = "Obtenir l'année d'exercice en cours",
        description = "Retourne l'année d'exercice correspondant à l'année en cours (basée sur la date système)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Année d'exercice trouvée ou créée avec succès"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<AnneeExercice> getAnneeExerciceCourante() {
        AnneeExercice annee = anneeExerciceInitService.getOrCreateAnneeExerciceCourante();
        return ResponseEntity.ok(annee);
    }

    /**
     * Obtenir l'année d'exercice la plus récente
     */
    @GetMapping("/recente")
    @Operation(
        summary = "Obtenir l'année d'exercice la plus récente",
        description = "Retourne l'année d'exercice la plus récente enregistrée dans la base de données"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Année d'exercice trouvée avec succès"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<AnneeExercice> getAnneeExerciceRecente() {
        AnneeExercice annee = anneeExerciceInitService.getAnneeExerciceRecente();
        return ResponseEntity.ok(annee);
    }
    
    /**
     * Créer une nouvelle année d'exercice
     */
    @PostMapping
    @PreAuthorize("hasRole('Directeur')")
    @Operation(summary = "Créer une nouvelle année d'exercice",
               description = "Seul un Directeur peut créer une nouvelle année d'exercice")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Année d'exercice créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<AnneeExerciceResponse> createAnneeExercice(
            @Valid @RequestBody CreateAnneeExerciceRequest request) {
        AnneeExerciceResponse response = anneeExerciceService.createAnneeExercice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Récupérer toutes les années d'exercice
     */
    @GetMapping
    @Operation(summary = "Récupérer toutes les années d'exercice",
               description = "Retourne la liste de toutes les années d'exercice")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    public ResponseEntity<List<AnneeExerciceResponse>> getAllAnneesExercice() {
        List<AnneeExerciceResponse> annees = anneeExerciceService.getAllAnneesExercice();
        return ResponseEntity.ok(annees);
    }
    
    /**
     * Récupérer une année d'exercice par ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une année d'exercice par ID",
               description = "Retourne les informations d'une année d'exercice spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Année d'exercice trouvée"),
        @ApiResponse(responseCode = "404", description = "Année d'exercice non trouvée")
    })
    public ResponseEntity<AnneeExerciceResponse> getAnneeExerciceById(@PathVariable Long id) {
        AnneeExerciceResponse annee = anneeExerciceService.getAnneeExerciceById(id);
        return ResponseEntity.ok(annee);
    }
    
    /**
     * Supprimer une année d'exercice
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Directeur')")
    @Operation(summary = "Supprimer une année d'exercice",
               description = "Seul un Directeur peut supprimer une année d'exercice")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Année d'exercice supprimée avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "404", description = "Année d'exercice non trouvée")
    })
    public ResponseEntity<Void> deleteAnneeExercice(@PathVariable Long id) {
        anneeExerciceService.deleteAnneeExercice(id);
        return ResponseEntity.noContent().build();
    }
}
