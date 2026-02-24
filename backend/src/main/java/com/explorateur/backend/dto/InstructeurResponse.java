package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour la réponse d'un instructeur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructeurResponse {
    
    private Long id;
    private String nom;
    private String prenom;
    private String genre;
    private String totem;
    private String telephone;
    private Boolean estChefGuide;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
