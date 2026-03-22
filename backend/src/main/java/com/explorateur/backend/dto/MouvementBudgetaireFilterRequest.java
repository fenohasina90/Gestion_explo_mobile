package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Filtres de recherche des mouvements budgétaires")
public class MouvementBudgetaireFilterRequest {

    @Schema(description = "Texte de recherche dans la description", example = "cotisation")
    private String recherche;

    @Schema(description = "Date de début", example = "2026-01-01")
    private LocalDate dateDebut;

    @Schema(description = "Date de fin", example = "2026-12-31")
    private LocalDate dateFin;

    @Schema(description = "ID du type de mouvement", example = "1")
    private Long typeId;

    @Schema(description = "ID de l'année d'exercice", example = "1")
    private Long anneeExerciceId;
}
