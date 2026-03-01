package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour Programme
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse programme")
public class ProgrammeResponse {
    
    @Schema(description = "ID du programme", example = "1")
    private Long id;
    
    @Schema(description = "Nom du programme", example = "Étude biblique")
    private String nom;
    
    @Schema(description = "Description du programme", example = "Étude approfondie des écritures")
    private String description;
    
    @Schema(description = "ID de la catégorie", example = "1")
    private Long categorieId;
    
    @Schema(description = "Nom de la catégorie", example = "Lovan' ny fiangonana")
    private String categorieNom;
    
    @Schema(description = "ID de la classe", example = "1")
    private Long classeId;
    
    @Schema(description = "Nom de la classe", example = "Ami")
    private String classeNom;
    
    @Schema(description = "Date de création")
    private LocalDateTime createdAt;
    
    @Schema(description = "Date de dernière modification")
    private LocalDateTime updatedAt;
}
