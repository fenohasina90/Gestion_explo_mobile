package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour CpDetails
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse détail CP (affectation programme à CP)")
public class CpDetailsResponse {
    
    @Schema(description = "ID du détail", example = "1")
    private Long id;
    
    @Schema(description = "ID de la classe progressive", example = "1")
    private Long classeProgressiveId;
    
    @Schema(description = "ID du programme", example = "1")
    private Long programmeId;
    
    @Schema(description = "Nom du programme", example = "Étude biblique")
    private String programmeName;
    
    @Schema(description = "ID de la catégorie du programme", example = "1")
    private Long categorieId;
    
    @Schema(description = "Nom de la catégorie", example = "Lovan' ny fiangonana")
    private String categorieName;
    
    @Schema(description = "ID de l'instructeur assigné", example = "1")
    private Long instructeurId;
    
    @Schema(description = "Nom de l'instructeur", example = "Jean Dupont")
    private String instructeurName;
    
    @Schema(description = "Statut actuel du programme", example = "En attente")
    private String statutActuel;
    
    @Schema(description = "Date de création", example = "2026-03-01T10:00:00")
    private LocalDateTime createdAt;
}
