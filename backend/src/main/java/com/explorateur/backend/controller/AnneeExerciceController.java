package com.explorateur.backend.controller;

import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.service.AnneeExerciceInitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
