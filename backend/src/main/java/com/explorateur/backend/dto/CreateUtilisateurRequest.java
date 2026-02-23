package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO pour la création d'un utilisateur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUtilisateurRequest {
    
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String username;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
    
    @NotNull(message = "Le rôle est obligatoire")
    private Long roleId;
    
    @NotNull(message = "L'année d'exercice est obligatoire")
    private Long anneeExerciceId;
}
