package com.explorateur.backend.repository;

import com.explorateur.backend.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Staff
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    
    /**
     * Recherche des staffs avec filtres multiples
     */
    @Query("SELECT s FROM Staff s WHERE " +
           "(:anneeExerciceId IS NULL OR s.anneeExercice.id = :anneeExerciceId) AND " +
           "(:roleId IS NULL OR s.role.id = :roleId) " +
           "ORDER BY s.instructeur.nom ASC, s.instructeur.prenom ASC")
    List<Staff> findByFilters(
        @Param("anneeExerciceId") Long anneeExerciceId,
        @Param("roleId") Long roleId
    );
    
    /**
     * Récupère les staffs par année d'exercice
     */
    List<Staff> findByAnneeExerciceId(Long anneeExerciceId);
    
    /**
     * Récupère les staffs par instructeur
     */
    List<Staff> findByInstructeurId(Long instructeurId);
    
    /**
     * Vérifie si un instructeur est déjà staff pour une année d'exercice donnée
     */
    @Query("SELECT s FROM Staff s WHERE s.instructeur.id = :instructeurId AND s.anneeExercice.id = :anneeExerciceId")
    Optional<Staff> findByInstructeurAndAnneeExercice(
        @Param("instructeurId") Long instructeurId,
        @Param("anneeExerciceId") Long anneeExerciceId
    );
}
