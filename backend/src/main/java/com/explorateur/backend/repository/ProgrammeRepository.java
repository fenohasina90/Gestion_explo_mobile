package com.explorateur.backend.repository;

import com.explorateur.backend.entity.Programme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour Programme
 */
@Repository
public interface ProgrammeRepository extends JpaRepository<Programme, Long> {
    
    /**
     * Rechercher par catégorie
     */
    List<Programme> findByCategorieId(Long categorieId);
    
    /**
     * Rechercher par classe
     */
    List<Programme> findByClasseId(Long classeId);
    
    /**
     * Rechercher par catégorie et classe
     */
    List<Programme> findByCategorieIdAndClasseId(Long categorieId, Long classeId);
    
    /**
     * Rechercher par nom (contient)
     */
    @Query("SELECT p FROM Programme p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    List<Programme> searchByNom(@Param("nom") String nom);
    
    /**
     * Filtrer par catégorie, classe et nom
     */
    @Query("SELECT p FROM Programme p WHERE " +
           "(:categorieId IS NULL OR p.categorie.id = :categorieId) AND " +
           "(:classeId IS NULL OR p.classe.id = :classeId) AND " +
           "(:nom IS NULL OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%')))")
    List<Programme> filterProgrammes(
        @Param("categorieId") Long categorieId,
        @Param("classeId") Long classeId,
        @Param("nom") String nom
    );
    
    /**
     * Vérifier si un programme est utilisé dans une CP
     * Note: Cette requête sera activée quand l'entité CpDetails sera créée
     * TODO: Décommenter cette méthode quand CpDetails sera implémenté
     */
    // @Query("SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END FROM CpDetails cp WHERE cp.programme.id = :programmeId")
    // boolean isProgrammeUsedInCP(@Param("programmeId") Long programmeId);
}
