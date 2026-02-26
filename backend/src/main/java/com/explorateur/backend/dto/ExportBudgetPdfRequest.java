package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requête pour l'export PDF du budget avec colonnes sélectionnables
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête d'export PDF du budget")
public class ExportBudgetPdfRequest {
    
    @Schema(description = "ID de l'année d'exercice", example = "1")
    private Long anneeExerciceId;
    
    // Colonnes optionnelles
    @Schema(description = "Inclure la date des activités", example = "true")
    private Boolean includeDate;
    
    @Schema(description = "Inclure le nom des activités", example = "true")
    private Boolean includeNomActivite;
    
    @Schema(description = "Inclure le coût des activités", example = "true")
    private Boolean includeCoutActivite;
    
    @Schema(description = "Inclure la description des activités", example = "false")
    private Boolean includeDescriptionActivite;
    
    @Schema(description = "Inclure les détails des activités", example = "true")
    private Boolean includeDetailsActivite;
    
    @Schema(description = "Inclure le coût des détails", example = "true")
    private Boolean includeCoutDetails;
    
    @Schema(description = "Inclure le statut des activités", example = "true")
    private Boolean includeStatutActivite;
}
