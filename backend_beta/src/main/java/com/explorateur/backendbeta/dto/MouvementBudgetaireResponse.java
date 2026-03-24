package com.explorateur.backendbeta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MouvementBudgetaireResponse {
    private Long id;
    private AnneeExerciceResponse anneeExercice;
    private TypeResponse type;
    private BigDecimal montant;
    private String description;
    private LocalDateTime createdAt;
}
