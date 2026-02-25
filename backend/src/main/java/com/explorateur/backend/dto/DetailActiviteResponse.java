package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse d'un détail d'activité
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Détail d'une activité")
public class DetailActiviteResponse {
    
    @Schema(description = "ID du détail", example = "1")
    private Long id;
    
    @Schema(description = "Description du détail", example = "Achat de tentes")
    private String details;
    
    @Schema(description = "Montant", example = "150000")
    private Double montant;
    
    @Schema(description = "Date de création", example = "2026-02-26T10:00:00")
    private String createdAt;
}
