package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour une année d'exercice
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Réponse contenant les informations d'une année d'exercice")
public class AnneeExerciceResponse {
    
    @Schema(description = "Identifiant unique de l'année d'exercice", example = "1")
    private Long id;
    
    @Schema(description = "Date de début de l'année d'exercice", example = "2026-01-01")
    private LocalDate annee;
    
    @Schema(description = "Date de fin de l'année d'exercice", example = "2026-12-31")
    private LocalDate dateFin;
    
    @Schema(description = "Date de création de l'enregistrement", example = "2026-02-23T03:36:52")
    private LocalDateTime createdAt;
}
