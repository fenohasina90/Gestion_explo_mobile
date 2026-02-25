package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse du budget global
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Budget global d'une année")
public class BudgetGlobalResponse {
    
    @Schema(description = "ID du budget", example = "1")
    private Long id;
    
    @Schema(description = "Année d'exercice", example = "2026")
    private String anneeExercice;
    
    @Schema(description = "ID de l'année d'exercice", example = "1")
    private Long anneeExerciceId;
    
    @Schema(description = "Montant total (somme des budgets des activités)", example = "2000000")
    private Double montant;
    
    @Schema(description = "Statut", example = "Créé")
    private String status;
    
    @Schema(description = "ID du statut", example = "1")
    private Long statusId;
    
    @Schema(description = "Nombre d'activités", example = "5")
    private Integer nombreActivites;
    
    @Schema(description = "Date de création", example = "2026-02-26T10:00:00")
    private String createdAt;
}
