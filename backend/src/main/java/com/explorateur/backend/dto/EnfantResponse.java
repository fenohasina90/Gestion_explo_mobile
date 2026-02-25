package com.explorateur.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO pour la réponse d'un enfant
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnfantResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String genre;
    private LocalDate dateNaissance;
    private Integer age;
    private String adresse;
    private Long parentId;
    private String parentNom;
    private String parentPrenom;
    private LocalDate bapteme;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
