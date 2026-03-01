package com.explorateur.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité HistoriqueProgrammes
 * Enregistre les changements de statut des programmes dans les CP
 */
@Entity
@Table(name = "historique_programmes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueProgrammes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "programme_id")
    private Programme programme;
    
    @Column(name = "classe_progressive_id")
    private Long classeProgressiveId;
    
    @ManyToOne
    @JoinColumn(name = "status_id")
    private ProgrammeStatus status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
