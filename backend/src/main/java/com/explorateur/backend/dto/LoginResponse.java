package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse d'authentification")
public class LoginResponse {
    
    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Builder.Default
    @Schema(description = "Type de token", example = "Bearer")
    private String type = "Bearer";
    
    @Schema(description = "ID de l'utilisateur", example = "1")
    private Long userId;
    
    @Schema(description = "Nom d'utilisateur", example = "directeur")
    private String username;
    
    @Schema(description = "Rôle de l'utilisateur", example = "Directeur")
    private String role;
    
    @Schema(description = "ID de l'année d'exercice", example = "1")
    private Long anneeExerciceId;
    
    @Schema(description = "Année d'exercice", example = "2026-01-01")
    private String anneeExercice;
}
