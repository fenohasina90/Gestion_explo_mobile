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
@Schema(description = "Informations de l'utilisateur connecté")
public class UserInfoResponse {
    
    @Schema(description = "ID de l'utilisateur", example = "1")
    private Long id;
    
    @Schema(description = "Nom d'utilisateur", example = "directeur")
    private String username;
    
    @Schema(description = "Rôle de l'utilisateur", example = "Directeur")
    private String role;
    
    @Schema(description = "Statut actif", example = "true")
    private Boolean active;
    
    @Schema(description = "Année d'exercice", example = "2026-01-01")
    private String anneeExercice;
}
