package com.explorateur.backend.controller;

import com.explorateur.backend.dto.BudgetGlobalResponse;
import com.explorateur.backend.dto.ExportBudgetPdfRequest;
import com.explorateur.backend.dto.UpdateBudgetStatusRequest;
import com.explorateur.backend.service.BudgetGlobalService;
import com.explorateur.backend.service.BudgetPdfExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    private final BudgetPdfExportService budgetPdfExportService;
    
    @GetMapping("/annee/{anneeExerciceId}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Obtenir le budget global d'une année",
               description = "Récupère ou crée le budget global pour une année d'exercice spécifique")
    public ResponseEntity<BudgetGlobalResponse> getBudgetByAnneeExercice(@PathVariable Long anneeExerciceId) {
        BudgetGlobalResponse response = budgetGlobalService.getOrCreateBudgetGlobal(anneeExerciceId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Obtenir tous les budgets globaux",
               description = "Récupère tous les budgets globaux triés par année décroissante")
    public ResponseEntity<List<BudgetGlobalResponse>> getAllBudgets() {
        List<BudgetGlobalResponse> budgets = budgetGlobalService.getAllBudgets();
        return ResponseEntity.ok(budgets);
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur')")
    @Operation(summary = "Modifier le statut d'un budget",
               description = "Modifie le statut du budget global (Directeur et Co-Directeur uniquement)")
    public ResponseEntity<BudgetGlobalResponse> updateBudgetStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBudgetStatusRequest request,
            Authentication authentication) {
        BudgetGlobalResponse response = budgetGlobalService.updateBudgetStatus(id, request.getStatusId(), authentication.getName());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/export-pdf")
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Exporter le budget en PDF",
               description = "Génère un PDF du budget avec les colonnes sélectionnées")
    public ResponseEntity<byte[]> exportBudgetPdf(@Valid @RequestBody ExportBudgetPdfRequest request) {
        try {
            byte[] pdfBytes = budgetPdfExportService.generateBudgetPdf(request);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "budget_" + request.getAnneeExerciceId() + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage());
        }
    }
}
