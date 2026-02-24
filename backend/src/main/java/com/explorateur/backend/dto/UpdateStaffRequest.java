package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la mise à jour d'un staff (avec infos instructeur)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStaffRequest {
    
    private Long roleId;
    
    // Informations de l'instructeur (optionnelles)
    private String nom;
    private String prenom;
    private String genre;
    private String totem;
    private String telephone;
    private Boolean estChefGuide;
}
