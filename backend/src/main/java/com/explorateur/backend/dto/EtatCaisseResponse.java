package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Réponse pour l'état de caisse")
public class EtatCaisseResponse {

    @Schema(description = "Total des recettes", example = "1500000.00")
    private BigDecimal totalRecettes;

    @Schema(description = "Total des dépenses", example = "850000.00")
    private BigDecimal totalDepenses;

    @Schema(description = "Solde actuel (recettes - dépenses)", example = "650000.00")
    private BigDecimal solde;

    @Schema(description = "Année d'exercice concernée")
    private AnneeExerciceResponse anneeExercice;
}
