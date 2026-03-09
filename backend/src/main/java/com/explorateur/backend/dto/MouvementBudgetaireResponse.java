package com.explorateur.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Réponse pour un mouvement budgétaire")
public class MouvementBudgetaireResponse {

    @Schema(description = "Identifiant unique du mouvement", example = "1")
    private Long id;

    @Schema(description = "Année d'exercice associée au mouvement")
    private AnneeExerciceResponse anneeExercice;

    @Schema(description = "Type de mouvement (RECETTE ou DEPENSE)")
    private TypeResponse type;

    @Schema(description = "Montant du mouvement budgétaire", example = "50000.00")
    private BigDecimal montant;

    @Schema(description = "Description détaillée du mouvement", example = "Cotisation mensuelle janvier 2026")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date de création du mouvement", example = "2026-03-09T10:15:30")
    private LocalDateTime createdAt;
}
