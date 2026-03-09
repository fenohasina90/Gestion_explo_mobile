package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les statistiques d'un enfant
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiqueEnfantResponse {
    
    private Long enfantId;
    private String nom;
    private String prenom;
    private String classe;
    private Long anneeExerciceId;
    private String anneeExercice;
    
    // Pourcentage de programmes complétés
    private Long nombreProgrammesCompletes;
    private Long totalProgrammesClasse;
    private Double pourcentageProgrammes;
    
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
