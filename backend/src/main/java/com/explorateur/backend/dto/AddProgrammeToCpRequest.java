package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de requête pour ajouter un programme ou une activité libre à une CP
 * Soit programmeId est fourni (activité programmée), soit description (activité libre)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête d'ajout de programme ou activité libre à une CP")
public class AddProgrammeToCpRequest {
    
    @NotNull(message = "L'ID de la classe progressive est obligatoire")
    @Schema(description = "ID de la classe progressive", example = "1", required = true)
    private Long classeProgressiveId;
    
    @Schema(description = "ID du programme à ajouter (optionnel si description fournie)", example = "1")
    private Long programmeId;
    
    @Schema(description = "Description de l'activité libre (obligatoire si programmeId non fourni)", 
            example = "Atelier de bricolage pour la fête des pères")
    private String description;
    
    @Schema(description = "Liste des IDs des instructeurs à assigner (optionnel)", example = "[1, 2, 3]")
    private List<Long> instructeurIds;
}
