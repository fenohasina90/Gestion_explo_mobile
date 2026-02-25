package com.explorateur.backend.controller;

import com.explorateur.backend.dto.ActiviteResponse;
import com.explorateur.backend.dto.ActiviteStatusResponse;
import com.explorateur.backend.dto.CreateActiviteRequest;
import com.explorateur.backend.service.ActiviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour la gestion des activités
 */
@RestController
@RequestMapping("/api/activites")
@RequiredArgsConstructor
@Tag(name = "Activités", description = "API de gestion des activités")
@SecurityRequirement(name = "bearerAuth")
public class ActiviteController {
    
    private final ActiviteService activiteService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur')")
    @Operation(summary = "Créer une nouvelle activité",
               description = "Crée une nouvelle activité avec ses détails (Directeur et Co-Directeur uniquement)")
    public ResponseEntity<ActiviteResponse> createActivite(
            @Valid @RequestBody CreateActiviteRequest request,
            Authentication authentication) {
        ActiviteResponse response = activiteService.createActivite(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/annee/{anneeExerciceId}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Obtenir les activités d'une année",
               description = "Récupère toutes les activités pour une année d'exercice spécifique")
    public ResponseEntity<List<ActiviteResponse>> getActivitesByAnnee(@PathVariable Long anneeExerciceId) {
        List<ActiviteResponse> activites = activiteService.getActivitesByAnneeExercice(anneeExerciceId);
        return ResponseEntity.ok(activites);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Obtenir une activité par ID",
               description = "Récupère une activité spécifique avec tous ses détails")
    public ResponseEntity<ActiviteResponse> getActiviteById(@PathVariable Long id) {
        ActiviteResponse activite = activiteService.getActiviteById(id);
        return ResponseEntity.ok(activite);
    }
    
    @GetMapping("/statuts")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Obtenir tous les statuts d'activités",
               description = "Récupère la liste de tous les statuts possibles pour une activité")
    public ResponseEntity<List<ActiviteStatusResponse>> getAllStatuts() {
        List<ActiviteStatusResponse> statuts = activiteService.getAllStatuts();
        return ResponseEntity.ok(statuts);
    }
}
