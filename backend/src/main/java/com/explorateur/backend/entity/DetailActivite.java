package com.explorateur.backend.entity;

import com.explorateur.backend.config.LocalDateTimeConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "details_activites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailActivite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "activite_id", nullable = false)
    private Activite activite;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    @Column
    private Double montant;
    
    @Column(name = "created_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @Convert(converter = LocalDateTimeConverter.class)
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
