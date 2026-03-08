package com.explorateur.backend.repository;

import com.explorateur.backend.entity.AnneeExercice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnneeExerciceRepository extends JpaRepository<AnneeExercice, Long> {
    
    /**
     * Trouve l'année d'exercice par date
     */
    Optional<AnneeExercice> findByAnnee(LocalDate annee);
    
    /**
     * Trouve toutes les années d'exercice commençant à une date donnée
     */
    List<AnneeExercice> findAllByAnnee(LocalDate annee);
    
    /**
     * Trouve l'année d'exercice la plus récente
     */
    Optional<AnneeExercice> findFirstByOrderByAnneeDesc();
    
    /**
     * Trouve toutes les années d'exercice dépassées (dateFin < date donnée)
     */
    List<AnneeExercice> findByDateFinBefore(LocalDate date);
}
