package com.explorateur.backend.controller;

import com.explorateur.backend.dto.CreateEnfantRequest;
import com.explorateur.backend.dto.EnfantResponse;
import com.explorateur.backend.dto.EnfantSuggestion;
import com.explorateur.backend.dto.PageResponse;
import com.explorateur.backend.service.EnfantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enfants")
@RequiredArgsConstructor
@Tag(name = "Enfants", description = "API de gestion des enfants")
@SecurityRequirement(name = "bearerAuth")
public class EnfantController {
    
    private final EnfantService enfantService;
    
    @GetMapping("/search")
    @Operation(
            summary = "Rechercher des enfants",
            description = "Recherche d'enfants par nom/prénom pour l'auto-complétion (10-15 ans uniquement)"
    )
    public ResponseEntity<List<EnfantSuggestion>> searchEnfants(
            @Parameter(description = "Terme de recherche (nom ou prénom)")
            @RequestParam String query,
            @Parameter(description = "ID de l'année d'exercice pour le calcul de l'âge")
            @RequestParam Long anneeExerciceId
    ) {
        return ResponseEntity.ok(enfantService.searchEnfants(query, anneeExerciceId));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('Directeur') or hasRole('Co-Directeur')")
    @Operation(
            summary = "Créer un nouvel enfant",
            description = "Crée un nouvel enfant. L'âge doit être entre 10 et 15 ans. Accessible uniquement au Directeur et Co-Directeur."
    )
    public ResponseEntity<EnfantResponse> createEnfant(
            @Valid @RequestBody CreateEnfantRequest request,
            @Parameter(description = "ID de l'année d'exercice pour la vérification de l'âge")
            @RequestParam Long anneeExerciceId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enfantService.createEnfant(request, anneeExerciceId));
    }
    
    @GetMapping
    @Operation(
            summary = "Récupérer tous les enfants avec pagination",
            description = "Retourne une liste paginée de tous les enfants"
    )
    public ResponseEntity<PageResponse<EnfantResponse>> getAllEnfants(
            @Parameter(description = "Numéro de la page (commence à 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri (ex: nom, prenom)")
            @RequestParam(defaultValue = "nom") String sort,
            @Parameter(description = "Direction du tri (asc ou desc)")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(enfantService.getAllEnfants(pageable));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un enfant par ID")
    public ResponseEntity<EnfantResponse> getEnfantById(
            @Parameter(description = "ID de l'enfant")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(enfantService.getEnfantById(id));
    }
    
    @GetMapping("/parent/{parentId}")
    @Operation(summary = "Récupérer les enfants d'un parent")
    public ResponseEntity<List<EnfantResponse>> getEnfantsByParentId(
            @Parameter(description = "ID du parent")
            @PathVariable Long parentId
    ) {
        return ResponseEntity.ok(enfantService.getEnfantsByParentId(parentId));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Directeur')")
    @Operation(
            summary = "Supprimer un enfant",
            description = "Supprime un enfant. Accessible uniquement au Directeur."
    )
    public ResponseEntity<Void> deleteEnfant(
            @Parameter(description = "ID de l'enfant")
            @PathVariable Long id
    ) {
        enfantService.deleteEnfant(id);
        return ResponseEntity.noContent().build();
    }
}
