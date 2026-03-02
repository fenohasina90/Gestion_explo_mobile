package com.explorateur.backend.repository;

import com.explorateur.backend.entity.CpDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité CpDetails
 */
@Repository
public interface CpDetailsRepository extends JpaRepository<CpDetails, Long> {
    
    /**
     * Trouver tous les programmes d'une CP
     */
    List<CpDetails> findByClasseProgressiveId(Long classeProgressiveId);
    
    /**
     * Vérifier si un programme existe déjà dans une CP
     */
    @Query("SELECT COUNT(cd) > 0 FROM CpDetails cd WHERE " +
           "cd.classeProgressive.id = :cpId AND cd.programme.id = :programmeId")
    boolean existsByClasseProgressiveIdAndProgrammeId(
            @Param("cpId") Long cpId,
            @Param("programmeId") Long programmeId);
    
    /**
     * Trouver un programme dans une CP
     */
    @Query("SELECT cd FROM CpDetails cd WHERE " +
           "cd.classeProgressive.id = :cpId AND cd.programme.id = :programmeId")
    Optional<CpDetails> findByClasseProgressiveIdAndProgrammeId(
            @Param("cpId") Long cpId,
            @Param("programmeId") Long programmeId);
    
    /**
     * Vérifier si un programme dans une CP est terminé
     */
    @Query("SELECT COUNT(h) > 0 FROM HistoriqueProgrammes h " +
           "JOIN ProgrammeStatus ps ON h.status.id = ps.id " +
           "WHERE h.classeProgressiveId = :cpId AND h.programme.id = :programmeId " +
           "AND ps.status = 'Terminé'")
    boolean isProgrammeTermine(
            @Param("cpId") Long cpId,
            @Param("programmeId") Long programmeId);
    
    /**
     * Trouver tous les programmes d'un instructeur
     */
    List<CpDetails> findByInstructeurId(Long instructeurId);
    
    /**
     * Trouver tous les détails d'une CP par ID de programme
     */
    List<CpDetails> findByProgrammeId(Long programmeId);
}
