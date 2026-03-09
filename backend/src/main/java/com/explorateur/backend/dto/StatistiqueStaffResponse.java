package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les statistiques d'un staff
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiqueStaffResponse {
    
    private Long staffId;
    private String nom;
    private String prenom;
    private String role;
    private Long anneeExerciceId;
    private String anneeExercice;
    
    // Pourcentage de participation aux activités
    private Long nombreParticipationsActivites;
    private Long totalActivites;
    private Double pourcentageActivites;
    
    // Pourcentage de présence aux classes progressives
    private Long nombrePresencesCP;
    private Long totalCP;
    private Double pourcentageCP;
    
    // Classement (sera calculé séparément)
    private Integer rangActivites;
    private Integer rangCP;
}
