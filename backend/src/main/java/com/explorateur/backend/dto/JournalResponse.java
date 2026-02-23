package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour la réponse d'une entrée du journal
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Représente une entrée du journal d'audit")
public class JournalResponse {

    @Schema(description = "ID de l'entrée du journal")
    private Long id;

    @Schema(description = "Description de l'action effectuée")
    private String action;

    @Schema(description = "Nom d'utilisateur qui a effectué l'action")
    private String username;

    @Schema(description = "ID de l'utilisateur")
    private Long utilisateurId;

    @Schema(description = "Date et heure de l'action")
    private LocalDateTime timestamp;
}
