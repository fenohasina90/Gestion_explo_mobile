package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requête pour modifier un mouvement budgétaire")
public class UpdateMouvementBudgetaireRequest {

    @NotNull(message = "Le type de mouvement est obligatoire")
    @Schema(description = "ID du type de mouvement (1=RECETTE, 2=DEPENSE)", example = "1", required = true)
    private Long typeId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    @Schema(description = "Montant du mouvement budgétaire", example = "50000.00", required = true)
    private BigDecimal montant;

    @Schema(description = "Description détaillée du mouvement", example = "Cotisation mensuelle janvier 2026")
    private String description;
}
