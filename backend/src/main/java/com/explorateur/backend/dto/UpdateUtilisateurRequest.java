package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la modification d'un utilisateur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUtilisateurRequest {
    
    private String username;
    
    private String password;
    
    private Long roleId;
    
    private Boolean active;
    
    private Long anneeExerciceId;
}
