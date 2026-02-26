package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour la mise à jour d'une activité
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de mise à jour d'une activité")
public class UpdateActiviteRequest {
    
    @NotBlank(message = "Le nom de l'activité est requis")
    @Schema(description = "Nom de l'activité", example = "Campement d'hiver")
    private String nom;
    
    @Schema(description = "Description de l'activité", example = "Campement de 3 jours en montagne")
    private String description;
    
    @Schema(description = "Date de début de l'activité", example = "2026-03-15")
    private LocalDate dateDebut;
    
    @Schema(description = "Date de fin de l'activité", example = "2026-03-17")
    private LocalDate dateFin;
    
    @NotNull(message = "Le statut est requis")
    @Schema(description = "ID du statut de l'activité", example = "1")
    private Long statusId;
    
    @NotEmpty(message = "Au moins un détail est requis")
    @Valid
    @Schema(description = "Liste des détails de l'activité")
    private List<DetailActiviteDto> details;
}
