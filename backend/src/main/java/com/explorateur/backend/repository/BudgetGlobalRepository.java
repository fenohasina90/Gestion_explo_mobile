package com.explorateur.backend.repository;

import com.explorateur.backend.entity.BudgetGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetGlobalRepository extends JpaRepository<BudgetGlobal, Long> {
    
    /**
     * Trouver le budget global pour une année d'exercice
     */
    Optional<BudgetGlobal> findByAnneeExerciceId(Long anneeExerciceId);
    
    /**
     * Lister tous les budgets globaux par année d'exercice
     */
    List<BudgetGlobal> findAllByOrderByAnneeExerciceAnneeDesc();
}
