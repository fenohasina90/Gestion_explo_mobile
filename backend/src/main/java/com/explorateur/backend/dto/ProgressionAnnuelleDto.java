package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour la progression annuelle d'un programme
 * Résumé du statut final d'un programme pour une année
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressionAnnuelleDto {
    
    private Long id;
    private Long programmeId;
    private String programmeNom;
    private Long categorieId;
    private String categorieNom;
    private Long classeId;
    private String classeNom;
    private String anneeExercice;
    private Long statutFinalId;
    private String statutFinalNom;
    private Long nombreChangements;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
