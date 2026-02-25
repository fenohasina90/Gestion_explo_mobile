package com.explorateur.backend.repository;

import com.explorateur.backend.entity.Activite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActiviteRepository extends JpaRepository<Activite, Long> {
    
    /**
     * Trouver toutes les activités d'un budget global
     */
    List<Activite> findByBudgetGlobalId(Long budgetGlobalId);
    
    /**
     * Trouver toutes les activités par année d'exercice
     */
    @Query("SELECT a FROM Activite a WHERE a.budgetGlobal.anneeExercice.id = :anneeExerciceId ORDER BY a.dateDebut DESC")
    List<Activite> findByAnneeExerciceId(@Param("anneeExerciceId") Long anneeExerciceId);
}
