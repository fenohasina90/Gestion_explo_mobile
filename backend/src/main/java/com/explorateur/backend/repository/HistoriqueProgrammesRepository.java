package com.explorateur.backend.repository;

import com.explorateur.backend.entity.HistoriqueProgrammes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour HistoriqueProgrammes
 */
@Repository
public interface HistoriqueProgrammesRepository extends JpaRepository<HistoriqueProgrammes, Long> {
    
    /**
     * Obtenir l'historique d'un programme dans une CP
     */
    List<HistoriqueProgrammes> findByProgrammeIdAndClasseProgressiveIdOrderByCreatedAtDesc(
        Long programmeId, 
        Long classeProgressiveId
    );
    
    /**
     * Obtenir le dernier statut d'un programme dans une CP
     */
    @Query("SELECT h FROM HistoriqueProgrammes h WHERE h.programme.id = :programmeId " +
           "AND h.classeProgressiveId = :classeProgressiveId " +
           "ORDER BY h.createdAt DESC LIMIT 1")
    Optional<HistoriqueProgrammes> findLatestByProgrammeAndCP(
        @Param("programmeId") Long programmeId,
        @Param("classeProgressiveId") Long classeProgressiveId
    );
    
    /**
     * Obtenir tout l'historique d'un programme (toutes CP confondues)
     */
    List<HistoriqueProgrammes> findByProgrammeIdOrderByCreatedAtDesc(Long programmeId);
    
    /**
     * Obtenir l'historique d'une CP spécifique
     */
    List<HistoriqueProgrammes> findByClasseProgressiveIdOrderByCreatedAtDesc(Long classeProgressiveId);
}
