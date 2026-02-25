package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour un détail d'activité dans la création
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Détail d'une activité")
public class DetailActiviteDto {
    
    @NotBlank(message = "Les détails sont obligatoires")
    @Schema(description = "Description du détail", example = "Achat de tentes", required = true)
    private String details;
    
    @NotNull(message = "Le montant est obligatoire")
    @Schema(description = "Montant", example = "150000", required = true)
    private Double montant;
}
