package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour la réponse d'un utilisateur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurResponse {
    
    private Long id;
    private String username;
    private String role;
    private Boolean active;
    private String anneeExercice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
