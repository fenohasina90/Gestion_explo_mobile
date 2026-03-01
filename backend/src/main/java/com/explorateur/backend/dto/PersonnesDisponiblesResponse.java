package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonnesDisponiblesResponse {
    private List<ParticipantEnfantDto> enfants;
    private List<ParticipantStaffDto> staff;
}
