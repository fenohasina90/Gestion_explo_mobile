package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour la création d'une activité avec ses détails
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Données pour créer une nouvelle activité")
public class CreateActiviteRequest {
    
    @NotBlank(message = "Le nom est obligatoire")
    @Schema(description = "Nom de l'activité", example = "Campement d'hiver", required = true)
    private String nom;
    
    @Schema(description = "Description de l'activité", example = "Campement de 3 jours en montagne")
    private String description;
    
    @NotNull(message = "La date de début est obligatoire")
    @Schema(description = "Date de début", example = "2026-03-15", required = true)
    private LocalDate dateDebut;
    
    @NotNull(message = "La date de fin est obligatoire")
    @Schema(description = "Date de fin", example = "2026-03-17", required = true)
    private LocalDate dateFin;
    
    @NotNull(message = "L'ID du budget global est obligatoire")
    @Schema(description = "ID du budget global", example = "1", required = true)
    private Long budgetGlobalId;
    
    @Schema(description = "ID du statut", example = "1")
    private Long statusId;
    
    @NotEmpty(message = "Au moins un détail d'activité est requis")
    @Valid
    @Schema(description = "Liste des détails d'activité", required = true)
    private List<DetailActiviteDto> details;
}
