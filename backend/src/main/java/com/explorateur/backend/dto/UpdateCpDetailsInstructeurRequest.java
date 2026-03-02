package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requête pour modifier l'instructeur d'un programme dans une CP
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de modification d'instructeur")
public class UpdateCpDetailsInstructeurRequest {
    
    @Schema(description = "ID de l'instructeur à assigner (null pour retirer)", example = "1")
    private Long instructeurId;
}
