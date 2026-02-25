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
     * Recherche des staffs avec filtres multiples (excluant les supprimés)
     */
    @Query("SELECT s FROM Staff s WHERE s.etat <> 11 AND " +
           "(:anneeExerciceId IS NULL OR s.anneeExercice.id = :anneeExerciceId) AND " +
           "(:roleId IS NULL OR s.role.id = :roleId) AND " +
           "(:estChefGuide IS NULL OR s.instructeur.estChefGuide = :estChefGuide) " +
           "ORDER BY s.instructeur.nom ASC, s.instructeur.prenom ASC")
    List<Staff> findByFilters(
        @Param("anneeExerciceId") Long anneeExerciceId,
        @Param("roleId") Long roleId,
        @Param("estChefGuide") Boolean estChefGuide
    );
    
    /**
     * Récupère les staffs par année d'exercice (excluant les supprimés)
     */
    @Query("SELECT s FROM Staff s WHERE s.etat <> 11 AND s.anneeExercice.id = :anneeExerciceId")
    List<Staff> findByAnneeExerciceId(@Param("anneeExerciceId") Long anneeExerciceId);
    
    /**
     * Récupère les staffs par instructeur (excluant les supprimés)
     */
    @Query("SELECT s FROM Staff s WHERE s.etat <> 11 AND s.instructeur.id = :instructeurId")
    List<Staff> findByInstructeurId(@Param("instructeurId") Long instructeurId);
    
    /**
     * Vérifie si un instructeur est déjà staff pour une année d'exercice donnée (excluant les supprimés)
     */
    @Query("SELECT s FROM Staff s WHERE s.etat <> 11 AND s.instructeur.id = :instructeurId AND s.anneeExercice.id = :anneeExerciceId")
    Optional<Staff> findByInstructeurAndAnneeExercice(
        @Param("instructeurId") Long instructeurId,
        @Param("anneeExerciceId") Long anneeExerciceId
    );
    
    /**
     * Récupère tous les staffs actifs (non supprimés)
     */
    @Query("SELECT s FROM Staff s WHERE s.etat <> 11 ORDER BY s.instructeur.nom ASC, s.instructeur.prenom ASC")
    List<Staff> findAllActive();
}
