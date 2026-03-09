package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Réponse pour un type de mouvement budgétaire")
public class TypeResponse {

    @Schema(description = "Identifiant unique du type", example = "1")
    private Long id;

    @Schema(description = "Nom du type (RECETTE ou DEPENSE)", example = "RECETTE")
    private String type;
}
