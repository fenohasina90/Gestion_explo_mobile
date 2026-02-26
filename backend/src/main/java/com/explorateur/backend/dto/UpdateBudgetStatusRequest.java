package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la mise à jour du statut du budget global
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de mise à jour du statut du budget")
public class UpdateBudgetStatusRequest {
    
    @NotNull(message = "Le statut est requis")
    @Schema(description = "ID du nouveau statut", example = "2")
    private Long statusId;
}
