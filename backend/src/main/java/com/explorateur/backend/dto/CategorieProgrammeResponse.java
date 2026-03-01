package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse pour CategorieProgramme
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse catégorie de programme")
public class CategorieProgrammeResponse {
    
    @Schema(description = "ID de la catégorie", example = "1")
    private Long id;
    
    @Schema(description = "Nom de la catégorie", example = "Lovan' ny fiangonana")
    private String nom;
    
    @Schema(description = "Nombre de programmes dans cette catégorie", example = "5")
    private Long nombreProgrammes;
}
