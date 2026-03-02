package com.explorateur.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité ProgrammeStatus
 * Table de référence pour les statuts de programme (En attente, En cours, Terminé)
 */
@Entity
@Table(name = "programme_status")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgrammeStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String status;
}
