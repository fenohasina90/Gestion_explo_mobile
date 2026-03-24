package com.explorateur.backendbeta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "annee_exercice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnneeExercice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate annee;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
