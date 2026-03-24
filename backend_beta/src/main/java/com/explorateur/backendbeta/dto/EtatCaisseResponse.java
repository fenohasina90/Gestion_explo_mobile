package com.explorateur.backendbeta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtatCaisseResponse {
    private BigDecimal totalRecettes;
    private BigDecimal totalDepenses;
    private BigDecimal solde;
    private AnneeExerciceResponse anneeExercice;
}
