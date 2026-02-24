package com.explorateur.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la création d'un staff
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStaffRequest {
    
    @NotNull(message = "L'instructeur est obligatoire")
    private Long instructeurId;
    
    @NotNull(message = "Le rôle est obligatoire")
    private Long roleId;
    
    @NotNull(message = "L'année d'exercice est obligatoire")
    private Long anneeExerciceId;
}
