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
public class EnregistrerPresenceCpRequest {
    private Long classeProgressiveId;
    private List<Long> enfantsPresents;
    private List<Long> staffPresents;
}
