package com.explorateur.backend.repository;

import com.explorateur.backend.entity.ProgrammeProgressionAnnuelle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la progression annuelle des programmes
 */
@Repository
public interface ProgrammeProgressionAnnuelleRepository extends JpaRepository<ProgrammeProgressionAnnuelle, Long> {
    
    /**
     * Trouve la progression d'un programme pour une année donnée
     */
    Optional<ProgrammeProgressionAnnuelle> findByProgrammeIdAndAnneeExerciceId(
            Long programmeId, Long anneeExerciceId);
    
    /**
     * Récupère toutes les progressions pour une année d'exercice
     */
    List<ProgrammeProgressionAnnuelle> findByAnneeExerciceId(Long anneeExerciceId);
    
    /**
     * Récupère toutes les progressions d'un programme (toutes années)
     */
    List<ProgrammeProgressionAnnuelle> findByProgrammeIdOrderByAnneeExerciceAnneeDesc(Long programmeId);
    
    /**
     * Compte les programmes terminés pour une année
     */
    @Query("SELECT COUNT(p) FROM ProgrammeProgressionAnnuelle p " +
           "WHERE p.anneeExercice.id = :anneeExerciceId " +
           "AND p.statutFinal.status = 'Terminé'")
    Long countProgrammesTerminesParAnnee(@Param("anneeExerciceId") Long anneeExerciceId);
    
    /**
     * Compte les programmes en cours pour une année
     */
    @Query("SELECT COUNT(p) FROM ProgrammeProgressionAnnuelle p " +
           "WHERE p.anneeExercice.id = :anneeExerciceId " +
           "AND p.statutFinal.status = 'En cours'")
    Long countProgrammesEnCoursParAnnee(@Param("anneeExerciceId") Long anneeExerciceId);
    
    /**
     * Vérifie si une progression existe déjà
     */
    boolean existsByProgrammeIdAndAnneeExerciceId(Long programmeId, Long anneeExerciceId);
}
