package com.explorateur.backend.repository;

import com.explorateur.backend.entity.Enfant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EnfantRepository extends JpaRepository<Enfant, Long> {
    
    /**
     * Recherche d'enfants par nom et/ou prénom avec filtre d'âge (10-15 ans)
     * pour l'auto-complétion
     */
    @Query(value = "SELECT * FROM enfants e " +
           "WHERE (LOWER(e.nom || ' ' || e.prenom) LIKE LOWER(:searchTerm) " +
           "OR LOWER(e.prenom || ' ' || e.nom) LIKE LOWER(:searchTerm)) " +
           "AND e.date_naissance BETWEEN :dateMin AND :dateMax " +
           "ORDER BY e.nom ASC, e.prenom ASC " +
           "LIMIT 10", nativeQuery = true)
    List<Enfant> searchByNomOrPrenomWithAgeRange(
            @Param("searchTerm") String searchTerm,
            @Param("dateMin") String dateMin,
            @Param("dateMax") String dateMax
    );
    
    /**
     * Trouve les enfants par nom et prénom (vérification doublon)
     */
    @Query("SELECT e FROM Enfant e WHERE LOWER(e.nom) = LOWER(:nom) AND LOWER(e.prenom) = LOWER(:prenom)")
    List<Enfant> findByNomAndPrenom(@Param("nom") String nom, @Param("prenom") String prenom);
    
    /**
     * Trouve les enfants par parent
     */
    @Query("SELECT e FROM Enfant e WHERE e.parent.id = :parentId ORDER BY e.nom ASC, e.prenom ASC")
    List<Enfant> findByParentId(@Param("parentId") Long parentId);
}
