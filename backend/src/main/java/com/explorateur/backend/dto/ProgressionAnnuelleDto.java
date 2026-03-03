package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour la progression annuelle d'un programme
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Progression annuelle d'un programme")
public class ProgressionAnnuelleDto {
    
    @Schema(description = "ID de la progression", example = "1")
    private Long id;
    
    @Schema(description = "ID du programme", example = "5")
    private Long programmeId;
    
    @Schema(description = "Nom du programme", example = "Étude biblique")
    private String programmeNom;
    
    @Schema(description = "Année d'exercice", example = "2026")
    private String anneeExercice;
    
    @Schema(description = "ID du statut final", example = "3")
    private Long statutFinalId;
    
    @Schema(description = "Nom du statut final", example = "Terminé")
    private String statutFinalNom;
    
    @Schema(description = "Nombre de changements de statut", example = "3")
    private Long nombreChangements;
    
    @Schema(description = "Date de création", example = "2026-01-15T10:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Date de dernière mise à jour", example = "2026-03-03T14:30:00")
    private LocalDateTime updatedAt;
}
