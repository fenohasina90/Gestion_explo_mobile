package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour la réponse d'un parent
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
