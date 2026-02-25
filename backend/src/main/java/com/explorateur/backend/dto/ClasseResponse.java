package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse d'une classe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClasseResponse {
    private Long id;
    private String nom;
    private String logo;
    private Integer age;
}
