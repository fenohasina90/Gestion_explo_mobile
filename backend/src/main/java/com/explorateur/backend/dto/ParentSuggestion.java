package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les suggestions de parents lors de l'auto-complétion
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentSuggestion {
    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    
    /**
     * Nom complet pour l'affichage
     */
    public String getNomComplet() {
        return nom + " " + prenom;
    }
}
