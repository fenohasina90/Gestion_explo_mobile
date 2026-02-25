package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO pour les suggestions d'enfants lors de l'auto-complétion
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnfantSuggestion {
    private Long id;
    private String nom;
    private String prenom;
    private String genre;
    private LocalDate dateNaissance;
    private Integer age;
    private String parentNom;
    private String parentPrenom;
    
    /**
     * Nom complet pour l'affichage
     */
    public String getNomComplet() {
        return nom + " " + prenom;
    }
    
    /**
     * Nom complet du parent
     */
    public String getParentNomComplet() {
        if (parentNom != null && parentPrenom != null) {
            return parentNom + " " + parentPrenom;
        }
        return null;
    }
}
