package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requête pour changer le statut d'un programme
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de changement de statut d'un programme")
public class ChangeProgrammeStatusRequest {
    
    @NotNull(message = "L'ID du programme est obligatoire")
    @Schema(description = "ID du programme", example = "1", required = true)
    private Long programmeId;
    
    @NotNull(message = "L'ID de la classe progressive est obligatoire")
    @Schema(description = "ID de la classe progressive", example = "1", required = true)
    private Long classeProgressiveId;
    
    @NotNull(message = "L'ID du nouveau statut est obligatoire")
    @Schema(description = "ID du nouveau statut (1=En attente, 2=En cours, 3=Terminé)", example = "2", required = true)
    private Long newStatusId;
}
