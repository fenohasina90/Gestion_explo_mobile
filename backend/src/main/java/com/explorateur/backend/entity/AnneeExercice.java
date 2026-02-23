package com.explorateur.backend.entity;

import com.explorateur.backend.config.LocalDateAttributeConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "annee_exercice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entité représentant une année d'exercice du Club des Explorateurs")
public class AnneeExercice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique de l'année d'exercice", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Convert(converter = LocalDateAttributeConverter.class)
    @Schema(description = "Date de début de l'année d'exercice (1er janvier)", example = "2026-01-01", required = true)
    private LocalDate annee;

    @Column(name = "date_fin", nullable = false)
    @Convert(converter = LocalDateAttributeConverter.class)
    @Schema(description = "Date de fin de l'année d'exercice (31 décembre)", example = "2026-12-31", required = true)
    private LocalDate dateFin;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Date de création de l'enregistrement", example = "2026-02-22T19:04:35", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
}
