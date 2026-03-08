package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour l'historique d'un programme
 * Représente un changement de statut d'un programme
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueProgrammeDto {
    
    private Long id;
    private Long programmeId;
    private String programmeNom;
    private Long classeProgressiveId;
    private String classeProgressiveDate;
    private Long statusId;
    private String statusNom;
    private String anneeExercice;
    private LocalDateTime dateChangement;
}
