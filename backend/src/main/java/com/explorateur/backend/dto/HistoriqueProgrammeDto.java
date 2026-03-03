package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour une entrée dans l'historique des programmes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entrée de l'historique d'un programme")
public class HistoriqueProgrammeDto {
    
    @Schema(description = "ID de l'historique", example = "1")
    private Long id;
    
    @Schema(description = "ID du programme", example = "5")
    private Long programmeId;
    
    @Schema(description = "Nom du programme", example = "Étude biblique")
    private String programmeNom;
    
    @Schema(description = "ID de la classe progressive", example = "12")
    private Long classeProgressiveId;
    
    @Schema(description = "Date de la CP", example = "2026-03-03")
    private String classeProgressiveDate;
    
    @Schema(description = "ID du statut", example = "2")
    private Long statusId;
    
    @Schema(description = "Nom du statut", example = "En cours")
    private String statusNom;
    
    @Schema(description = "Année d'exercice", example = "2026")
    private String anneeExercice;
    
    @Schema(description = "Date du changement de statut", example = "2026-03-03T14:30:00")
    private LocalDateTime dateChangement;
}
