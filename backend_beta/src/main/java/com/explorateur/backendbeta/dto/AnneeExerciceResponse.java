package com.explorateur.backendbeta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnneeExerciceResponse {
    private Long id;
    private LocalDate annee;
    private LocalDate dateFin;
    private LocalDateTime createdAt;
}
