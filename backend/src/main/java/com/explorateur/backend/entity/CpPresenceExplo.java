package com.explorateur.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cp_presence_explo", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"classe_progressive_id", "enfant_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CpPresenceExplo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "classe_progressive_id", nullable = false)
    private ClasseProgressive classeProgressive;
    
    @ManyToOne
    @JoinColumn(name = "enfant_id", nullable = false)
    private Inscription inscription;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
