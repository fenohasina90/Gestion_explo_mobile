package com.explorateur.backend.repository;

import com.explorateur.backend.entity.HistoriqueProgramme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'historique des programmes.
 * ATTENTION: Aucune méthode de suppression ou modification n'est exposée.
 * L'historique est IMMUABLE.
 */
@Repository
public interface HistoriqueProgrammeRepository extends JpaRepository<HistoriqueProgramme, Long> {
    
    /**
     * Récupère l'historique complet d'un programme (toutes années confondues)
     * Trié par ordre chronologique
     */
    @Query("SELECT h FROM HistoriqueProgramme h " +
           "WHERE h.programme.id = :programmeId " +
           "ORDER BY h.createdAt ASC")
    List<HistoriqueProgramme> findByProgrammeIdOrderByCreatedAtAsc(@Param("programmeId") Long programmeId);
    
    /**
     * Récupère l'historique d'un programme pour une année d'exercice spécifique
     */
    @Query("SELECT h FROM HistoriqueProgramme h " +
           "WHERE h.programme.id = :programmeId " +
           "AND h.classeProgressive.anneeExercice.id = :anneeExerciceId " +
           "ORDER BY h.createdAt ASC")
    List<HistoriqueProgramme> findByProgrammeIdAndAnneeExerciceId(
            @Param("programmeId") Long programmeId,
            @Param("anneeExerciceId") Long anneeExerciceId);
    
    /**
     * Récupère l'historique d'une classe progressive
     */
    @Query("SELECT h FROM HistoriqueProgramme h " +
           "WHERE h.classeProgressive.id = :cpId " +
           "ORDER BY h.createdAt ASC")
    List<HistoriqueProgramme> findByClasseProgressiveIdOrderByCreatedAtAsc(@Param("cpId") Long cpId);
    
    /**
     * Récupère tous les historiques pour une année d'exercice
     */
    @Query("SELECT h FROM HistoriqueProgramme h " +
           "WHERE h.classeProgressive.anneeExercice.id = :anneeExerciceId " +
           "ORDER BY h.createdAt ASC")
    List<HistoriqueProgramme> findByAnneeExerciceIdOrderByCreatedAtAsc(@Param("anneeExerciceId") Long anneeExerciceId);
    
    /**
     * Compte le nombre de changements de statut pour un programme
     */
    @Query("SELECT COUNT(h) FROM HistoriqueProgramme h WHERE h.programme.id = :programmeId")
    Long countByProgrammeId(@Param("programmeId") Long programmeId);
    
    /**
     * Vérifie si un programme a déjà été marqué comme "Terminé" 
     * dans une CP donnée
     */
    @Query("SELECT COUNT(h) > 0 FROM HistoriqueProgramme h " +
           "WHERE h.programme.id = :programmeId " +
           "AND h.classeProgressive.id = :cpId " +
           "AND h.status.status = 'Terminé'")
    boolean existsTermineInCp(@Param("programmeId") Long programmeId, @Param("cpId") Long cpId);
}
