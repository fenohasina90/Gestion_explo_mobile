package com.explorateur.backendbeta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MouvementBudgetaireFilterRequest {
    private String recherche;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long typeId;
    private Long anneeExerciceId;
}
