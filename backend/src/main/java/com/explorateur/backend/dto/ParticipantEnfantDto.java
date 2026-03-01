package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantEnfantDto {
    private Long inscriptionId;
    private Long enfantId;
    private String nom;
    private String prenom;
    private String genre;
    private Long classeId;
    private String classeNom;
}
