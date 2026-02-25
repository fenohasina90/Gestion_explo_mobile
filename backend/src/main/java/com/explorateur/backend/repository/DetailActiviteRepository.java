package com.explorateur.backend.repository;

import com.explorateur.backend.entity.DetailActivite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailActiviteRepository extends JpaRepository<DetailActivite, Long> {
    
    /**
     * Trouver tous les détails d'une activité
     */
    List<DetailActivite> findByActiviteId(Long activiteId);
    
    /**
     * Supprimer tous les détails d'une activité
     */
    void deleteByActiviteId(Long activiteId);
}
