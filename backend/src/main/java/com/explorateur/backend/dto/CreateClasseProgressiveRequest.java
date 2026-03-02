package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO de requête pour créer une ClasseProgressive
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de création de Classe Progressive (CP)")
public class CreateClasseProgressiveRequest {
    
    @NotNull(message = "La date de la CP est obligatoire")
    @Schema(description = "Date de la CP", example = "2026-03-15", required = true)
    private LocalDate dateCp;
    
    @NotNull(message = "L'heure de début est obligatoire")
    @Schema(description = "Heure de début", example = "14:00:00", required = true)
    private LocalTime heureDebut;
    
    @NotNull(message = "L'heure de fin est obligatoire")
    @Schema(description = "Heure de fin", example = "16:00:00", required = true)
    private LocalTime heureFin;
    
    @Schema(description = "Niveau de la CP", example = "1")
    private Integer niveau;
    
    @NotNull(message = "L'année d'exercice est obligatoire")
    @Schema(description = "ID de l'année d'exercice", example = "1", required = true)
    private Long anneeExerciceId;
}
