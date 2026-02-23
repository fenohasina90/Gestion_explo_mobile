package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO pour la création d'une nouvelle année d'exercice
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour créer une nouvelle année d'exercice")
public class CreateAnneeExerciceRequest {
    
    @NotNull(message = "L'année d'exercice est requise")
    @Schema(description = "Date de début de l'année d'exercice (format: YYYY-MM-DD)", 
            example = "2027-01-01", 
            required = true)
    private LocalDate annee;
}
