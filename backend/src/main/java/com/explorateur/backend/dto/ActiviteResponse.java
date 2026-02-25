package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour la réponse d'une activité
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Activité avec ses détails")
public class ActiviteResponse {
    
    @Schema(description = "ID de l'activité", example = "1")
    private Long id;
    
    @Schema(description = "Nom de l'activité", example = "Campement d'hiver")
    private String nom;
    
    @Schema(description = "Description", example = "Campement de 3 jours")
    private String description;
    
    @Schema(description = "Date de début", example = "2026-03-15")
    private String dateDebut;
    
    @Schema(description = "Date de fin", example = "2026-03-17")
    private String dateFin;
    
    @Schema(description = "Montant total (somme des détails)", example = "500000")
    private Double montant;
    
    @Schema(description = "ID du budget global", example = "1")
    private Long budgetGlobalId;
    
    @Schema(description = "Année d'exercice", example = "2026")
    private String anneeExercice;
    
    @Schema(description = "Statut", example = "En attente")
    private String status;
    
    @Schema(description = "ID du statut", example = "1")
    private Long statusId;
    
    @Schema(description = "Liste des détails")
    private List<DetailActiviteResponse> details;
    
    @Schema(description = "Date de création", example = "2026-02-26T10:00:00")
    private String createdAt;
}
