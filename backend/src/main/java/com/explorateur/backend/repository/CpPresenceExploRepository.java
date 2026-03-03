package com.explorateur.backend.repository;

import com.explorateur.backend.entity.CpPresenceExplo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CpPresenceExploRepository extends JpaRepository<CpPresenceExplo, Long> {
    
    List<CpPresenceExplo> findByClasseProgressiveId(Long classeProgressiveId);
    
    @Query("SELECT p FROM CpPresenceExplo p WHERE p.classeProgressive.id = :cpId AND p.inscription.classe.id = :classeId")
    List<CpPresenceExplo> findByClasseProgressiveIdAndClasseId(@Param("cpId") Long cpId, @Param("classeId") Long classeId);
    
    void deleteByClasseProgressiveId(Long classeProgressiveId);
    
    boolean existsByClasseProgressiveIdAndInscriptionId(Long classeProgressiveId, Long inscriptionId);
}
