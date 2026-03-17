package com.explorateur.backend.controller;

import com.explorateur.backend.dto.*;
import com.explorateur.backend.service.MouvementBudgetaireService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller pour la gestion des mouvements budgétaires
 */
@RestController
@RequestMapping("/api/mouvements-budgetaires")
@RequiredArgsConstructor
@Tag(name = "Mouvements Budgétaires", description = "API de gestion des mouvements budgétaires (recettes et dépenses)")
@SecurityRequirement(name = "bearerAuth")
public class MouvementBudgetaireController {
    
    private final MouvementBudgetaireService mouvementBudgetaireService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur')")
    @Operation(summary = "Créer un mouvement budgétaire", 
               description = "Crée un nouveau mouvement budgétaire (recette ou dépense) - Directeur et Co-Directeur")
    public ResponseEntity<MouvementBudgetaireResponse> createMouvement(
            @Valid @RequestBody CreateMouvementBudgetaireRequest request,
            Authentication authentication) {
        MouvementBudgetaireResponse response = mouvementBudgetaireService.createMouvement(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur')")
    @Operation(summary = "Modifier un mouvement budgétaire",
               description = "Modifie un mouvement budgétaire existant - Directeur et Co-Directeur")
    public ResponseEntity<MouvementBudgetaireResponse> updateMouvement(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMouvementBudgetaireRequest request) {
        MouvementBudgetaireResponse response = mouvementBudgetaireService.updateMouvement(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur')")
    @Operation(summary = "Supprimer un mouvement budgétaire",
               description = "Supprime un mouvement budgétaire - Directeur et Co-Directeur")
    public ResponseEntity<Void> deleteMouvement(@PathVariable Long id) {
        mouvementBudgetaireService.deleteMouvement(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/etat-caisse")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtenir l'état de caisse",
               description = "Calcule l'état de caisse (total recettes, total dépenses, solde) pour une année d'exercice")
    public ResponseEntity<EtatCaisseResponse> getEtatCaisse(
            @Parameter(description = "ID de l'année d'exercice (si non spécifié, prend l'année active)") 
            @RequestParam(required = false) Long anneeExerciceId) {
        EtatCaisseResponse etatCaisse = mouvementBudgetaireService.getEtatCaisse(anneeExerciceId);
        return ResponseEntity.ok(etatCaisse);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtenir un mouvement par ID",
               description = "Récupère les détails d'un mouvement budgétaire")
    public ResponseEntity<MouvementBudgetaireResponse> getMouvementById(@PathVariable Long id) {
        MouvementBudgetaireResponse response = mouvementBudgetaireService.getMouvementById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulter l'état de caisse avec filtres et pagination",
               description = "Récupère la liste paginée des mouvements budgétaires avec filtres optionnels")
    public ResponseEntity<PageResponse<MouvementBudgetaireResponse>> getMouvementsWithFilters(
            @Parameter(description = "Recherche par description") @RequestParam(required = false) String recherche,
            @Parameter(description = "Date de début") @RequestParam(required = false) LocalDate dateDebut,
            @Parameter(description = "Date de fin") @RequestParam(required = false) LocalDate dateFin,
            @Parameter(description = "ID du type de mouvement (1=RECETTE, 2=DEPENSE)") @RequestParam(required = false) Long typeId,
            @Parameter(description = "ID de l'année d'exercice") @RequestParam(required = false) Long anneeExerciceId,
            @Parameter(description = "Numéro de page (commence à 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "Direction du tri (asc ou desc)") @RequestParam(defaultValue = "desc") String direction) {
        
        MouvementBudgetaireFilterRequest filters = MouvementBudgetaireFilterRequest.builder()
                .recherche(recherche)
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .typeId(typeId)
                .anneeExerciceId(anneeExerciceId)
                .build();
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        PageResponse<MouvementBudgetaireResponse> mouvementsPage = mouvementBudgetaireService.getMouvementsWithFiltersPaginated(filters, pageable);
        return ResponseEntity.ok(mouvementsPage);
    }
}
