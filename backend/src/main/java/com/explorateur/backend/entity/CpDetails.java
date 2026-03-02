package com.explorateur.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité CpDetails
 * Représente l'affectation d'un programme à une CP avec un instructeur assigné
 */
@Entity
@Table(name = "cp_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CpDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_progressive_id", nullable = false)
    private ClasseProgressive classeProgressive;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id", nullable = false)
    private Programme programme;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructeur_id")
    private Instructeur instructeur;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
