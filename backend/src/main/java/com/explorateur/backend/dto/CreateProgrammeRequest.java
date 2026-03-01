package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requête pour créer un programme
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de création d'un programme")
public class CreateProgrammeRequest {
    
    @NotBlank(message = "Le nom du programme est obligatoire")
    @Schema(description = "Nom du programme", example = "Étude biblique", required = true)
    private String nom;
    
    @Schema(description = "Description du programme", example = "Étude approfondie des écritures")
    private String description;
    
    @NotNull(message = "La catégorie est obligatoire")
    @Schema(description = "ID de la catégorie", example = "1", required = true)
    private Long categorieId;
    
    @NotNull(message = "La classe est obligatoire")
    @Schema(description = "ID de la classe", example = "1", required = true)
    private Long classeId;
}
