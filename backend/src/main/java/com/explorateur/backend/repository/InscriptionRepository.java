package com.explorateur.backend.repository;

import com.explorateur.backend.entity.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    
    /**
     * Trouve toutes les inscriptions par année d'exercice
     */
    @Query("SELECT i FROM Inscription i WHERE i.anneeExercice.id = :anneeExerciceId ORDER BY i.enfant.nom ASC")
    List<Inscription> findByAnneeExerciceId(@Param("anneeExerciceId") Long anneeExerciceId);
    
    /**
     * Trouve toutes les inscriptions par classe
     */
    @Query("SELECT i FROM Inscription i WHERE i.classe.id = :classeId ORDER BY i.enfant.nom ASC")
    List<Inscription> findByClasseId(@Param("classeId") Long classeId);
    
    /**
     * Trouve les inscriptions par année d'exercice et classe
     */
    @Query("SELECT i FROM Inscription i WHERE i.anneeExercice.id = :anneeExerciceId AND i.classe.id = :classeId ORDER BY i.enfant.nom ASC")
    List<Inscription> findByAnneeExerciceIdAndClasseId(
            @Param("anneeExerciceId") Long anneeExerciceId,
            @Param("classeId") Long classeId
    );
    
    /**
     * Trouve les inscriptions par genre d'enfant
     */
    @Query("SELECT i FROM Inscription i WHERE i.enfant.genre = :genre ORDER BY i.enfant.nom ASC")
    List<Inscription> findByGenre(@Param("genre") String genre);
    
    /**
     * Vérifie si un enfant est déjà inscrit pour une année d'exercice donnée
     */
    @Query("SELECT i FROM Inscription i WHERE i.enfant.id = :enfantId AND i.anneeExercice.id = :anneeExerciceId")
    Optional<Inscription> findByEnfantIdAndAnneeExerciceId(
            @Param("enfantId") Long enfantId,
            @Param("anneeExerciceId") Long anneeExerciceId
    );
    
    /**
     * Trouve toutes les inscriptions d'un enfant
     */
    @Query("SELECT i FROM Inscription i WHERE i.enfant.id = :enfantId ORDER BY i.anneeExercice.annee DESC")
    List<Inscription> findByEnfantId(@Param("enfantId") Long enfantId);
    
    /**
     * Vérifie si un enfant est déjà inscrit pour une année d'exercice donnée
     */
    boolean existsByEnfantIdAndAnneeExerciceId(Long enfantId, Long anneeExerciceId);
}
