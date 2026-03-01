package com.explorateur.backend.repository;

import com.explorateur.backend.entity.CategorieProgramme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour CategorieProgramme
 */
@Repository
public interface CategorieProgrammeRepository extends JpaRepository<CategorieProgramme, Long> {
    
    /**
     * Vérifier si une catégorie avec ce nom existe déjà
     */
    boolean existsByNom(String nom);
    
    /**
     * Vérifier si une catégorie avec ce nom existe (hors ID donné)
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CategorieProgramme c WHERE c.nom = :nom AND c.id != :id")
    boolean existsByNomAndIdNot(@Param("nom") String nom, @Param("id") Long id);
    
    /**
     * Trouver une catégorie par nom
     */
    Optional<CategorieProgramme> findByNom(String nom);
    
    /**
     * Compter le nombre de programmes d'une catégorie
     */
    @Query("SELECT COUNT(p) FROM Programme p WHERE p.categorie.id = :categorieId")
    long countProgrammesByCategorie(@Param("categorieId") Long categorieId);
}
