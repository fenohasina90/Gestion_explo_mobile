package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les statistiques annuelles des programmes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Statistiques annuelles des programmes")
public class StatistiquesAnnuellesDto {
    
    @Schema(description = "Année d'exercice", example = "2026")
    private String anneeExercice;
    
    @Schema(description = "Nombre total de programmes différents travaillés", example = "25")
    private Long totalProgrammesTravailles;
    
    @Schema(description = "Nombre de programmes terminés", example = "15")
    private Long programmesTermines;
    
    @Schema(description = "Nombre de programmes en cours", example = "8")
    private Long programmesEnCours;
    
    @Schema(description = "Nombre de programmes en attente", example = "2")
    private Long programmesEnAttente;
    
    @Schema(description = "Nombre total de changements de statut", example = "47")
    private Long totalChangements;
    
    @Schema(description = "Nombre de CPs organisées", example = "18")
    private Long nombreCPs;
    
    @Schema(description = "Pourcentage de complétion", example = "60.0")
    private Double tauxCompletion;
}
