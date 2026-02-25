package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour la réponse d'un staff
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffResponse {
    
    private Long id;
    
    // Informations de l'instructeur
    private Long instructeurId;
    private String instructeurNom;
    private String instructeurPrenom;
    private String instructeurTelephone;
    private String instructeurGenre;
    private String instructeurTotem;
    private Boolean instructeurEstChefGuide;
    
    // Informations du staff
    private String role;
    private Long roleId;
    
    // Année d'exercice
    private Long anneeExerciceId;
    private String anneeExercice;
    
    private Integer etat; // 1 = actif, 11 = supprimé
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
