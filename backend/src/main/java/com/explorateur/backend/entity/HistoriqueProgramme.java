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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_progressive_id", nullable = false)
    private ClasseProgressive classeProgressive;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private ProgrammeStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
