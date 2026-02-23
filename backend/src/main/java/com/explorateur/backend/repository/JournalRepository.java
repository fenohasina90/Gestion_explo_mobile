package com.explorateur.backend.repository;

import com.explorateur.backend.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion du journal d'audit
 */
@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {

    /**
     * Recherche avec filtres multiples
     */
    @Query("SELECT j FROM Journal j WHERE " +
           "(:dateDebut IS NULL OR j.timestamp >= :dateDebut) AND " +
           "(:dateFin IS NULL OR j.timestamp <= :dateFin) AND " +
           "(:utilisateurId IS NULL OR j.utilisateur.id = :utilisateurId) AND " +
           "(:searchText IS NULL OR LOWER(j.action) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
           "ORDER BY j.timestamp DESC")
    List<Journal> findByFilters(
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            @Param("utilisateurId") Long utilisateurId,
            @Param("searchText") String searchText
    );

    /**
     * Recherche par période
     */
    List<Journal> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime dateDebut, LocalDateTime dateFin);

    /**
     * Recherche par utilisateur
     */
    List<Journal> findByUtilisateurIdOrderByTimestampDesc(Long utilisateurId);

    /**
     * Récupère toutes les entrées triées par date décroissante
     */
    List<Journal> findAllByOrderByTimestampDesc();
}
