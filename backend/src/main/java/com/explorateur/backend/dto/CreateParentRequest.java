package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la création d'un parent
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Données pour créer un nouveau parent")
public class CreateParentRequest {
    
    @NotBlank(message = "Le nom est obligatoire")
    @Schema(description = "Nom du parent", example = "Dupont", required = true)
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Schema(description = "Prénom du parent", example = "Jean", required = true)
    private String prenom;
    
    @Schema(description = "Adresse du parent", example = "123 Rue de la Paix")
    private String adresse;
    
    @Schema(description = "Numéro de téléphone", example = "+261 34 12 345 67")
    private String telephone;
}
