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
    
    void deleteByActiviteId(Long activiteId);
}
