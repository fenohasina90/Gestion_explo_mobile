package com.explorateur.backend.dto;

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
public class StatistiquesAnnuellesDto {
    
    private String anneeExercice;
    private Long totalProgrammesTravailles;
    private Long programmesTermines;
    private Long programmesEnCours;
    private Long programmesEnAttente;
    private Long totalChangements;
    private Long nombreCPs;
    private Double tauxCompletion;
}
