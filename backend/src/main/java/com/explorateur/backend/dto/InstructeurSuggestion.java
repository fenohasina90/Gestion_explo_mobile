package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les suggestions d'instructeurs (auto-complétion)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructeurSuggestion {
    
    private Long id;
    private String nom;
    private String prenom;
    private String nomComplet; // "Nom Prénom" pour affichage
}
