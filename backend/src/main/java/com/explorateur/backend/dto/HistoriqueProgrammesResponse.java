package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour HistoriqueProgrammes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Historique de changement de statut d'un programme")
public class HistoriqueProgrammesResponse {
    
    @Schema(description = "ID de l'historique", example = "1")
    private Long id;
    
    @Schema(description = "ID du programme", example = "1")
    private Long programmeId;
    
    @Schema(description = "Nom du programme", example = "Étude biblique")
    private String programmeNom;
    
    @Schema(description = "ID de la classe progressive", example = "1")
    private Long classeProgressiveId;
    
    @Schema(description = "ID du statut", example = "1")
    private Long statusId;
    
    @Schema(description = "Nom du statut", example = "En cours")
    private String statusNom;
    
    @Schema(description = "Date du changement de statut")
    private LocalDateTime createdAt;
}
