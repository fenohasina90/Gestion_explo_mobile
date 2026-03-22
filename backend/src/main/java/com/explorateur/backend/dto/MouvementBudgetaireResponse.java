package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Réponse d'un mouvement budgétaire")
public class MouvementBudgetaireResponse {

    @Schema(description = "Identifiant du mouvement", example = "10")
    private Long id;

    @Schema(description = "Année d'exercice concernée")
    private AnneeExerciceResponse anneeExercice;

    @Schema(description = "Type du mouvement")
    private TypeResponse type;

    @Schema(description = "Montant du mouvement", example = "50000.00")
    private BigDecimal montant;

    @Schema(description = "Description du mouvement", example = "Cotisation mensuelle")
    private String description;

    @Schema(description = "Date de création", example = "2026-03-20T10:15:30")
    private LocalDateTime createdAt;
}
