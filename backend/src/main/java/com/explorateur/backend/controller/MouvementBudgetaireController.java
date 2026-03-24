package com.explorateur.backend.controller;

import com.explorateur.backend.dto.*;
import com.explorateur.backend.service.MouvementBudgetaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget-mouvements")
@RequiredArgsConstructor
@Tag(name = "Mouvements Budgétaires", description = "API de gestion des mouvements budgétaires")
@SecurityRequirement(name = "bearerAuth")
public class MouvementBudgetaireController {

    private final MouvementBudgetaireService mouvementBudgetaireService;

    @PostMapping
    @PreAuthorize("hasRole('Directeur')")
    @Operation(summary = "Créer un mouvement", description = "Création d'un mouvement budgétaire (Directeur uniquement)")
    public ResponseEntity<MouvementBudgetaireResponse> createMouvement(@Valid @RequestBody CreateMouvementBudgetaireRequest request) {
        return ResponseEntity.ok(mouvementBudgetaireService.createMouvement(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_Directeur', 'ROLE_Co_Directeur', 'ROLE_Co-Directeur')")
    @Operation(summary = "Modifier un mouvement", description = "Modification d'un mouvement (Directeur et Co-Directeur)")
    public ResponseEntity<MouvementBudgetaireResponse> updateMouvement(@PathVariable Long id,
                                                                       @Valid @RequestBody UpdateMouvementBudgetaireRequest request) {
        return ResponseEntity.ok(mouvementBudgetaireService.updateMouvement(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Directeur')")
    @Operation(summary = "Supprimer un mouvement", description = "Suppression d'un mouvement budgétaire (Directeur uniquement)")
    public ResponseEntity<Void> deleteMouvement(@PathVariable Long id) {
        mouvementBudgetaireService.deleteMouvement(id);
        return ResponseEntity.noContent().build();
    }

}
