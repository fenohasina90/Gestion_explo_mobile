package com.explorateur.backend.repository;

import com.explorateur.backend.entity.CpDetailsInstructeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité CpDetailsInstructeur
 */
@Repository
public interface CpDetailsInstructeurRepository extends JpaRepository<CpDetailsInstructeur, Long> {
    
    /**
     * Trouver tous les instructeurs d'un cp_details
     */
    List<CpDetailsInstructeur> findByCpDetailsId(Long cpDetailsId);
    
    /**
     * Supprimer tous les instructeurs d'un cp_details
     */
    void deleteByCpDetailsId(Long cpDetailsId);
    
    /**
     * Vérifier si un instructeur est déjà affecté à un cp_details
     */
    @Query("SELECT COUNT(cdi) > 0 FROM CpDetailsInstructeur cdi WHERE " +
           "cdi.cpDetails.id = :cpDetailsId AND cdi.instructeur.id = :instructeurId")
    boolean existsByCpDetailsIdAndInstructeurId(
            @Param("cpDetailsId") Long cpDetailsId,
            @Param("instructeurId") Long instructeurId);
    
    /**
     * Supprimer un instructeur spécifique d'un cp_details
     */
    void deleteByCpDetailsIdAndInstructeurId(Long cpDetailsId, Long instructeurId);
}
