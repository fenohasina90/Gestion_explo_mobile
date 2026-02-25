package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la création d'une inscription
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Données pour créer une nouvelle inscription")
public class CreateInscriptionRequest {
    
    @NotNull(message = "L'ID de l'enfant est obligatoire")
    @Schema(description = "ID de l'enfant à inscrire", example = "1", required = true)
    private Long enfantId;
    
    @NotNull(message = "L'ID de l'année d'exercice est obligatoire")
    @Schema(description = "ID de l'année d'exercice", example = "1", required = true)
    private Long anneeExerciceId;
    
    @NotNull(message = "L'ID de la classe est obligatoire")
    @Schema(description = "ID de la classe", example = "1", required = true)
    private Long classeId;
    
    @Schema(description = "Indique si l'enfant a une assurance", example = "true")
    private Boolean estAssurance = false;
}
