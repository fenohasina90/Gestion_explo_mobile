package com.explorateur.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité CpDetails
 * Représente l'affectation d'un programme à une CP ou d'une activité libre
 * Les instructeurs sont gérés via la table cp_details_instructeurs
 * Le programme peut être NULL pour des activités libres (avec description obligatoire)
 */
@Entity
@Table(name = "cp_details", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"classe_progressive_id", "programme_id"})
})
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
    @JoinColumn(name = "programme_id", nullable = true)
    private Programme programme;
    
    @Column(name = "description")
    private String description;
    
    @OneToMany(mappedBy = "cpDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CpDetailsInstructeur> instructeurs = new ArrayList<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
