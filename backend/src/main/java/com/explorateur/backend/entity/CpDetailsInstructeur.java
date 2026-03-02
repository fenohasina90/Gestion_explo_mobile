package com.explorateur.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité CpDetailsInstructeur
 * Représente l'affectation d'un ou plusieurs instructeurs à un programme dans une CP
 */
@Entity
@Table(name = "cp_details_instructeurs", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cp_details_id", "instructeur_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CpDetailsInstructeur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cp_details_id", nullable = false)
    private CpDetails cpDetails;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructeur_id", nullable = false)
    private Instructeur instructeur;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
