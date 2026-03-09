package com.explorateur.backend.repository;

import com.explorateur.backend.entity.CpPresenceStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CpPresenceStaffRepository extends JpaRepository<CpPresenceStaff, Long> {
    
    List<CpPresenceStaff> findByClasseProgressiveId(Long classeProgressiveId);
    
    /**
     * Compte le nombre de présences aux CP pour un staff donné
     */
    @Query("SELECT COUNT(p) FROM CpPresenceStaff p WHERE p.staff.id = :staffId")
    Long countByStaffId(@Param("staffId") Long staffId);
    
    /**
     * Compte le nombre de présences aux CP pour un staff dans une année d'exercice
     */
    @Query("SELECT COUNT(p) FROM CpPresenceStaff p WHERE p.staff.id = :staffId AND p.classeProgressive.anneeExercice.id = :anneeExerciceId")
    Long countByStaffIdAndAnneeExerciceId(@Param("staffId") Long staffId, @Param("anneeExerciceId") Long anneeExerciceId);
    
    void deleteByClasseProgressiveId(Long classeProgressiveId);
    
    boolean existsByClasseProgressiveIdAndStaffId(Long classeProgressiveId, Long staffId);
}
