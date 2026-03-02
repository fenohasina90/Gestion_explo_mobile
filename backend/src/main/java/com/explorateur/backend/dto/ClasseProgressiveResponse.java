package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO de réponse pour ClasseProgressive
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse Classe Progressive (CP)")
public class ClasseProgressiveResponse {
    
    @Schema(description = "ID de la CP", example = "1")
    private Long id;
    
    @Schema(description = "Date de la CP", example = "2026-03-15")
    private LocalDate dateCp;
    
    @Schema(description = "Heure de début", example = "14:00:00")
    private LocalTime heureDebut;
    
    @Schema(description = "Heure de fin", example = "16:00:00")
    private LocalTime heureFin;
    
    @Schema(description = "Niveau de la CP", example = "1")
    private Integer niveau;
    
    @Schema(description = "ID de l'année d'exercice", example = "1")
    private Long anneeExerciceId;
    
    @Schema(description = "Année d'exercice", example = "2026-01-01")
    private LocalDate anneeExercice;
    
    @Schema(description = "Date de création", example = "2026-03-01T10:00:00")
    private LocalDateTime createdAt;
}
