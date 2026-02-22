package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse d'erreur")
public class ErrorResponse {
    
    @Schema(description = "Code de statut HTTP", example = "401")
    private int status;
    
    @Schema(description = "Message d'erreur", example = "Identifiants invalides")
    private String message;
    
    @Schema(description = "Date et heure de l'erreur")
    private LocalDateTime timestamp;
    
    @Schema(description = "Chemin de la requête", example = "/api/auth/login")
    private String path;
}
