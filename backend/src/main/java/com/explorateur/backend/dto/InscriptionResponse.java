package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO pour la réponse d'une inscription
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscriptionResponse {
    private Long id;
    
    // Informations de l'enfant
    private Long enfantId;
    private String enfantNom;
    private String enfantPrenom;
    private String enfantGenre;
    private LocalDate enfantDateNaissance;
    private Integer enfantAge;
    
    // Informations du parent
    private Long parentId;
    private String parentNom;
    private String parentPrenom;
    private String parentTelephone;
    
    // Informations de l'inscription
    private Long anneeExerciceId;
    private String anneeExercice;
    private Long classeId;
    private String classeNom;
    private Boolean estAssurance;
    private LocalDateTime createdAt;
}
