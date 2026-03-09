package com.explorateur.backend.repository;

import com.explorateur.backend.entity.ClasseProgressive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository pour l'entité ClasseProgressive
 */
@Repository
public interface ClasseProgressiveRepository extends JpaRepository<ClasseProgressive, Long> {
    
    /**
     * Trouver les CP par année d'exercice
     */
    List<ClasseProgressive> findByAnneeExerciceId(Long anneeExerciceId);
    
    /**
     * Compter le nombre total de CP pour une année d'exercice
     */
    @Query("SELECT COUNT(cp) FROM ClasseProgressive cp WHERE cp.anneeExercice.id = :anneeExerciceId")
    Long countByAnneeExerciceId(@Param("anneeExerciceId") Long anneeExerciceId);
    
    /**
     * Filtrer les CP par plage de dates
     */
    @Query("SELECT cp FROM ClasseProgressive cp WHERE cp.dateCp BETWEEN :dateDebut AND :dateFin")
    List<ClasseProgressive> findByDateCpBetween(@Param("dateDebut") LocalDate dateDebut, 
                                                  @Param("dateFin") LocalDate dateFin);
    
    /**
     * Filtrer les CP par plage de dates et année d'exercice
     */
    @Query("SELECT cp FROM ClasseProgressive cp WHERE cp.dateCp BETWEEN :dateDebut AND :dateFin " +
           "AND cp.anneeExercice.id = :anneeExerciceId")
    List<ClasseProgressive> findByDateCpBetweenAndAnneeExerciceId(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("anneeExerciceId") Long anneeExerciceId);
    
    /**
     * Vérifier si une CP existe avec le même horaire (date, heure_debut, heure_fin)
     * dans la même année d'exercice
     */
    @Query("SELECT COUNT(cp) > 0 FROM ClasseProgressive cp WHERE " +
           "cp.dateCp = :dateCp AND cp.heureDebut = :heureDebut AND cp.heureFin = :heureFin " +
           "AND cp.anneeExercice.id = :anneeExerciceId")
    boolean existsByDateCpAndHeureDebutAndHeureFinAndAnneeExerciceId(
            @Param("dateCp") LocalDate dateCp,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin,
            @Param("anneeExerciceId") Long anneeExerciceId);
    
    /**
     * Vérifier si une CP existe avec le même horaire (pour update, exclut l'ID actuel)
     */
    @Query("SELECT COUNT(cp) > 0 FROM ClasseProgressive cp WHERE " +
           "cp.dateCp = :dateCp AND cp.heureDebut = :heureDebut AND cp.heureFin = :heureFin " +
           "AND cp.anneeExercice.id = :anneeExerciceId AND cp.id != :cpId")
    boolean existsByDateCpAndHeureDebutAndHeureFinAndAnneeExerciceIdAndIdNot(
            @Param("dateCp") LocalDate dateCp,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin,
            @Param("anneeExerciceId") Long anneeExerciceId,
            @Param("cpId") Long cpId);
    
    /**
     * Vérifier si une CP a des présences enregistrées
     */
    @Query("SELECT COUNT(p) > 0 FROM ClasseProgressive cp " +
           "LEFT JOIN CpPresence p ON p.classeProgressive.id = cp.id " +
           "WHERE cp.id = :cpId")
    boolean hasPresences(@Param("cpId") Long cpId);
    
    /**
     * Vérifier si une CP a des programmes terminés
     */
    @Query("SELECT COUNT(h) > 0 FROM HistoriqueProgrammes h " +
           "JOIN ProgrammeStatus ps ON h.status.id = ps.id " +
           "WHERE h.classeProgressiveId = :cpId AND ps.status = 'Terminé'")
    boolean hasProgrammesTermines(@Param("cpId") Long cpId);
}
