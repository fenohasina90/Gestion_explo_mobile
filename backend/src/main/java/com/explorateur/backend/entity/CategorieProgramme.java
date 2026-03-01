package com.explorateur.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entité CategorieProgramme
 */
@Entity
@Table(name = "categorie_programme")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorieProgramme {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nom;
    
    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL)
    private List<Programme> programmes;
}
