package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour l'avancement d'un programme
 * Vue détaillée de l'état actuel d'un programme pour une année
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgrammeAvancementDto {
    
    private Long programmeId;
    private String programmeNom;
    private Long categorieId;
    private String categorieNom;
    private Long classeId;
    private String classeNom;
    private Long anneeExerciceId;
    private String anneeExercice;
    private Long statutActuelId;
    private String statutActuelNom;
    private Integer nombreChangements;
    private String datePremiereCP;
    private String dateDerniereCP;
    private String dateChangement;
    private Boolean estDemarre;
    private Boolean estTermine;
    private Integer pourcentageAvancement;
    private List<HistoriqueProgrammeDto> historique;
}
