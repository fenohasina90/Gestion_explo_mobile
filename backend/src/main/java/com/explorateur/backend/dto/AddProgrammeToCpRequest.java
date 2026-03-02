package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requête pour ajouter un programme à une CP
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête d'ajout de programme à une CP")
public class AddProgrammeToCpRequest {
    
    @NotNull(message = "L'ID de la classe progressive est obligatoire")
    @Schema(description = "ID de la classe progressive", example = "1", required = true)
    private Long classeProgressiveId;
    
    @NotNull(message = "L'ID du programme est obligatoire")
    @Schema(description = "ID du programme à ajouter", example = "1", required = true)
    private Long programmeId;
    
    @Schema(description = "ID de l'instructeur à assigner (optionnel)", example = "1")
    private Long instructeurId;
}
