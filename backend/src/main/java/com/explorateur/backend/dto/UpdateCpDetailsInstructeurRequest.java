package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de requête pour gérer les instructeurs d'un programme dans une CP
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de gestion des instructeurs")
public class UpdateCpDetailsInstructeurRequest {
    
    @NotNull(message = "La liste des IDs des instructeurs est obligatoire")
    @Schema(description = "Liste des IDs des instructeurs (remplace tous les instructeurs existants)", example = "[1, 2, 3]", required = true)
    private List<Long> instructeurIds;
}
