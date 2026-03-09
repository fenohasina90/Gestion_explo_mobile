package com.explorateur.backend.repository;

import com.explorateur.backend.entity.ParticipantActiviteExplo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantActiviteExploRepository extends JpaRepository<ParticipantActiviteExplo, Long> {
    
    List<ParticipantActiviteExplo> findByActiviteId(Long activiteId);
    
    @Query("SELECT p FROM ParticipantActiviteExplo p WHERE p.activite.id = :activiteId AND p.inscription.classe.id = :classeId")
    List<ParticipantActiviteExplo> findByActiviteIdAndClasseId(@Param("activiteId") Long activiteId, @Param("classeId") Long classeId);
    
    /**
     * Compte le nombre de participations aux activités pour une inscription donnée
     */
    @Query("SELECT COUNT(p) FROM ParticipantActiviteExplo p WHERE p.inscription.id = :inscriptionId")
    Long countByInscriptionId(@Param("inscriptionId") Long inscriptionId);
    
    /**
     * Compte le nombre de participations aux activités pour une inscription dans une année d'exercice
     */
    @Query("SELECT COUNT(p) FROM ParticipantActiviteExplo p WHERE p.inscription.id = :inscriptionId AND p.activite.anneeExercice.id = :anneeExerciceId")
    Long countByInscriptionIdAndAnneeExerciceId(@Param("inscriptionId") Long inscriptionId, @Param("anneeExerciceId") Long anneeExerciceId);
    
    void deleteByActiviteId(Long activiteId);
}
