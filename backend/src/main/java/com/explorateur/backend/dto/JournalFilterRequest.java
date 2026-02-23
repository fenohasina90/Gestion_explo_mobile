package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour filtrer les entrées du journal
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Critères de filtrage pour le journal d'audit")
public class JournalFilterRequest {

    @Schema(description = "Date de début (format: yyyy-MM-ddTHH:mm:ss)", example = "2026-01-01T00:00:00")
    private LocalDateTime dateDebut;

    @Schema(description = "Date de fin (format: yyyy-MM-ddTHH:mm:ss)", example = "2026-12-31T23:59:59")
    private LocalDateTime dateFin;

    @Schema(description = "ID de l'utilisateur pour filtrer par utilisateur")
    private Long utilisateurId;

    @Schema(description = "Recherche textuelle dans l'action")
    private String searchText;
}
