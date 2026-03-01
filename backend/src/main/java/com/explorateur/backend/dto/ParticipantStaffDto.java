package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantStaffDto {
    private Long staffId;
    private Long instructeurId;
    private String nom;
    private String prenom;
    private String totem;
    private String role;
}
