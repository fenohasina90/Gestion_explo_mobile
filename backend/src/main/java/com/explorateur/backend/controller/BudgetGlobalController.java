package com.explorateur.backend.controller;

import com.explorateur.backend.dto.BudgetGlobalResponse;
import com.explorateur.backend.service.BudgetGlobalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour la gestion du budget global
 */
@RestController
@RequestMapping("/api/budget-global")
@RequiredArgsConstructor
@Tag(name = "Budget Global", description = "API de gestion du budget global")
@SecurityRequirement(name = "bearerAuth")
public class BudgetGlobalController {
    
    private final BudgetGlobalService budgetGlobalService;
    
    @GetMapping("/annee/{anneeExerciceId}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Obtenir le budget global d'une année",
               description = "Récupère ou crée le budget global pour une année d'exercice spécifique")
    public ResponseEntity<BudgetGlobalResponse> getBudgetByAnneeExercice(@PathVariable Long anneeExerciceId) {
        BudgetGlobalResponse response = budgetGlobalService.getOrCreateBudgetGlobal(anneeExerciceId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Obtenir tous les budgets globaux",
               description = "Récupère tous les budgets globaux triés par année décroissante")
    public ResponseEntity<List<BudgetGlobalResponse>> getAllBudgets() {
        List<BudgetGlobalResponse> budgets = budgetGlobalService.getAllBudgets();
        return ResponseEntity.ok(budgets);
    }
}
