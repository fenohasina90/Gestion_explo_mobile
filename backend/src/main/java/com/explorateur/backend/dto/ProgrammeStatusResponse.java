package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse pour ProgrammeStatus
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse statut de programme")
public class ProgrammeStatusResponse {
    
    @Schema(description = "ID du statut", example = "1")
    private Long id;
    
    @Schema(description = "Nom du statut", example = "En attente")
    private String status;
}
