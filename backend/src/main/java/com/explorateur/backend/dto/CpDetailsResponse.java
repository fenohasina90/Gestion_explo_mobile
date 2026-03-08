package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de réponse pour CpDetails
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse détail CP (affectation programme ou activité libre à CP)")
public class CpDetailsResponse {
    
    @Schema(description = "ID du détail", example = "1")
    private Long id;
    
    @Schema(description = "ID de la classe progressive", example = "1")
    private Long classeProgressiveId;
    
    @Schema(description = "Date de la classe progressive", example = "2026-03-03")
    private String classeProgressiveDate;
    
    @Schema(description = "ID du programme (null pour activité libre)", example = "1")
    private Long programmeId;
    
    @Schema(description = "Nom du programme (null pour activité libre)", example = "Étude biblique")
    private String programmeNom;
    
    @Schema(description = "Description du programme (null pour activité libre)", example = "Étude des paraboles")
    private String programmeDescription;
    
    @Schema(description = "ID de la catégorie du programme (null pour activité libre)", example = "1")
    private Long categorieId;
    
    @Schema(description = "Nom de la catégorie (null pour activité libre)", example = "Lovan' ny fiangonana")
    private String categorieName;
    
    @Schema(description = "ID de la classe du programme (null pour activité libre)", example = "1")
    private Long classeId;
    
    @Schema(description = "Nom de la classe (null pour activité libre)", example = "6-8 ans")
    private String classeNom;
    
    @Schema(description = "Description de l'activité libre (null pour programme)", 
            example = "Atelier de bricolage pour la fête des pères")
    private String description;
    
    @Schema(description = "Liste des instructeurs assignés")
    private List<InstructeurSimpleDto> instructeurs;
    
    @Schema(description = "ID du statut du programme (null pour activité libre)", example = "1")
    private Long statusId;
    
    @Schema(description = "Nom du statut du programme (null pour activité libre)", example = "En attente")
    private String statusNom;
    
    @Schema(description = "Date de création", example = "2026-03-01T10:00:00")
    private LocalDateTime createdAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstructeurSimpleDto {
        @Schema(description = "ID de l'instructeur", example = "1")
        private Long id;
        
        @Schema(description = "Nom de l'instructeur", example = "Dupont")
        private String nom;
        
        @Schema(description = "Prénom de l'instructeur", example = "Jean")
        private String prenom;
        
        @Schema(description = "Nom complet de l'instructeur", example = "Dupont Jean")
        private String nomComplet;
    }
}
