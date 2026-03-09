package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour filtrer les statistiques
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiqueFilterRequest {
    
    private Long anneeExerciceId;
    private Long classeId;  // Pour filtrer les enfants par classe
    private String genre;   // Pour filtrer par genre (M/F)
}
