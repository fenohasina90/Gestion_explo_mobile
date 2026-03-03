package com.explorateur.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité pour suivre la progression annuelle d'un programme.
 * Maintient le statut final d'un programme pour chaque année d'exercice.
 */
@Entity
@Table(name = "programme_progression_annuelle",
       uniqueConstraints = @UniqueConstraint(columnNames = {"programme_id", "annee_exercice_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgrammeProgressionAnnuelle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id", nullable = false)
    private Programme programme;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_exercice_id", nullable = false)
    private AnneeExercice anneeExercice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statut_final_id")
    private ProgrammeStatus statutFinal;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
