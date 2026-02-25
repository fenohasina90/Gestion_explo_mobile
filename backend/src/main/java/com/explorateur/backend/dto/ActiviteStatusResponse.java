package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse d'un statut d'activité
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Statut d'activité")
public class ActiviteStatusResponse {
    
    @Schema(description = "ID", example = "1")
    private Long id;
    
    @Schema(description = "Libellé", example = "En attente")
    private String status;
}
