package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO pour la création d'un enfant
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Données pour créer un nouvel enfant")
public class CreateEnfantRequest {
    
    @NotBlank(message = "Le nom est obligatoire")
    @Schema(description = "Nom de l'enfant", example = "Dupont", required = true)
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Schema(description = "Prénom de l'enfant", example = "Sophie", required = true)
    private String prenom;
    
    @NotBlank(message = "Le genre est obligatoire")
    @Schema(description = "Genre de l'enfant", example = "FILLE", allowableValues = {"GARCON", "FILLE"}, required = true)
    private String genre;
    
    @NotNull(message = "La date de naissance est obligatoire")
    @Schema(description = "Date de naissance", example = "2012-05-15", required = true)
    private LocalDate dateNaissance;
    
    @Schema(description = "Adresse de l'enfant", example = "123 Rue de la Paix")
    private String adresse;
    
    @NotNull(message = "L'ID du parent est obligatoire")
    @Schema(description = "ID du parent", example = "1", required = true)
    private Long parentId;
    
    @Schema(description = "Date de baptême", example = "2020-08-15")
    private LocalDate bapteme;
}
