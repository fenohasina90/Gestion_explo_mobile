package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requête pour filtrer les mouvements budgétaires")
public class MouvementBudgetaireFilterRequest {

    @Schema(description = "Recherche par description", example = "cotisation")
    private String recherche;

    @Schema(description = "Date de début du filtre", example = "2026-01-01")
    private LocalDate dateDebut;

    @Schema(description = "Date de fin du filtre", example = "2026-12-31")
    private LocalDate dateFin;

    @Schema(description = "ID du type de mouvement (1=RECETTE, 2=DEPENSE)", example = "1")
    private Long typeId;

    @Schema(description = "ID de l'année d'exercice", example = "1")
    private Long anneeExerciceId;
}
