package com.explorateur.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité pour tracer l'historique des changements de statut des programmes.
 * IMMUABLE - Ne peut jamais être modifié ou supprimé.
 */
@Entity
@Table(name = "historique_programmes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueProgramme {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id", nullable = false)
    private Programme programme;
    
    /**
     * CP où le changement a eu lieu
     * NULL = initialisation automatique/système
     * non-NULL = changement dans une CP spécifique
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_progressive_id", nullable = true)
    private ClasseProgressive classeProgressive;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private ProgrammeStatus status;
    
    /**
     * Année d'exercice (dénormalisé pour faciliter les requêtes)
     * Correspond à classeProgressive.anneeExercice (si CP non null)
     * ou directement renseigné lors de l'initialisation automatique
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_exercice_id", nullable = false)
    private AnneeExercice anneeExercice;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
