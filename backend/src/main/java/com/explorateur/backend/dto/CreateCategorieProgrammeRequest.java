package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requête pour créer une catégorie de programme
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de création d'une catégorie de programme")
public class CreateCategorieProgrammeRequest {
    
    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Schema(description = "Nom de la catégorie", example = "Lovan' ny fiangonana", required = true)
    private String nom;
}
