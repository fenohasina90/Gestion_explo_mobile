package com.explorateur.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mouvement_budgetaire")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entité représentant un mouvement budgétaire (recette ou dépense)")
public class MouvementBudgetaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique du mouvement budgétaire", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "annee_exercice_id")
    @Schema(description = "Année d'exercice associée au mouvement", required = true)
    private AnneeExercice anneeExercice;

    @ManyToOne
    @JoinColumn(name = "type_id")
    @Schema(description = "Type de mouvement (RECETTE ou DEPENSE)", required = true)
    private Type type;

    @Column(nullable = false)
    @Schema(description = "Montant du mouvement budgétaire", example = "50000.00", required = true)
    private BigDecimal montant;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Description détaillée du mouvement budgétaire", example = "Cotisation mensuelle janvier 2026")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Date de création du mouvement", example = "2026-03-09T10:15:30", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
}
