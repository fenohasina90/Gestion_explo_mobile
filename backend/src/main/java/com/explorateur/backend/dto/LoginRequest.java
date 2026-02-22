package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête d'authentification")
public class LoginRequest {
    
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Schema(description = "Nom d'utilisateur", example = "directeur")
    private String username;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Schema(description = "Mot de passe", example = "directeur123")
    private String password;
}
