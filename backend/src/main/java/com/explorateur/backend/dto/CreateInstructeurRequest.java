package com.explorateur.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la création d'un instructeur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInstructeurRequest {
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String prenom;
    
    @NotBlank(message = "Le genre est obligatoire")
    @Size(max = 10, message = "Le genre ne doit pas dépasser 10 caractères")
    private String genre;
    
    @Size(max = 50, message = "Le totem ne doit pas dépasser 50 caractères")
    private String totem;
    
    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    private String telephone;
    
    private Boolean estChefGuide = false;
}
