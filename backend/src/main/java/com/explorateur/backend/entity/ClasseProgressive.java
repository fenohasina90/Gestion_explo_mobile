package com.explorateur.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entité ClasseProgressive (CP)
 * Représente une séance officielle de classe progressive
 */
@Entity
@Table(name = "classe_progressive")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClasseProgressive {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "date_cp", nullable = false)
    private LocalDate dateCp;
    
    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;
    
    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;
    
    @Column(name = "niveau")
    private Integer niveau;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_exercice_id", nullable = false)
    private AnneeExercice anneeExercice;
    
    /**
     * État de la CP
     * 0 = ouverte (saisie présences et modification statuts autorisées)
     * 1 = clôturée (tout verrouillé)
     */
    @Column(name = "etat", nullable = false)
    @Builder.Default
    private Integer etat = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (etat == null) {
            etat = 0;
        }
    }
}
