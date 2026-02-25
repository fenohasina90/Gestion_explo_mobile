package com.explorateur.backend.entity;

import com.explorateur.backend.config.LocalDateAttributeConverter;
import com.explorateur.backend.config.LocalDateTimeConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "enfants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enfant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String prenom;
    
    @Column(nullable = false)
    private String genre;
    
    @Column(name = "date_naissance")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate dateNaissance;
    
    @Column
    private String adresse;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Parent parent;
    
    @Column
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate bapteme;
    
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
