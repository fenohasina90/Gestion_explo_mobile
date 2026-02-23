package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse pour un rôle
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Réponse contenant les informations d'un rôle")
public class RoleResponse {
    
    @Schema(description = "Identifiant unique du rôle", example = "1")
    private Long id;
    
    @Schema(description = "Nom du rôle", example = "Directeur")
    private String roleName;
}
