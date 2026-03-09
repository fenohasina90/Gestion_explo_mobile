package com.explorateur.backend.repository;

import com.explorateur.backend.entity.ParticipantActiviteStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantActiviteStaffRepository extends JpaRepository<ParticipantActiviteStaff, Long> {
    
    List<ParticipantActiviteStaff> findByActiviteId(Long activiteId);
    
    /**
     * Compte le nombre de participations aux activités pour un staff donnée
     */
    @Query("SELECT COUNT(p) FROM ParticipantActiviteStaff p WHERE p.staff.id = :staffId")
    Long countByStaffId(@Param("staffId") Long staffId);
    
    /**
     * Compte le nombre de participations aux activités pour un staff dans une année d'exercice
     */
    @Query("SELECT COUNT(p) FROM ParticipantActiviteStaff p WHERE p.staff.id = :staffId AND p.activite.budgetGlobal.anneeExercice.id = :anneeExerciceId")
    Long countByStaffIdAndAnneeExerciceId(@Param("staffId") Long staffId, @Param("anneeExerciceId") Long anneeExerciceId);
    
    void deleteByActiviteId(Long activiteId);
}
