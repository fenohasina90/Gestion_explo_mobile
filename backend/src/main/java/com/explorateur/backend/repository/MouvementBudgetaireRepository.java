package com.explorateur.backend.repository;

import com.explorateur.backend.entity.MouvementBudgetaire;
import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.entity.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MouvementBudgetaireRepository extends JpaRepository<MouvementBudgetaire, Long> {
    
    /**
     * Trouve tous les mouvements budgétaires d'une année d'exercice
     */
    List<MouvementBudgetaire> findByAnneeExercice(AnneeExercice anneeExercice);
    
    /**
     * Trouve tous les mouvements budgétaires d'un type
     */
    List<MouvementBudgetaire> findByType(Type type);
    
    /**
     * Trouve tous les mouvements budgétaires d'une année et d'un type
     */
    List<MouvementBudgetaire> findByAnneeExerciceAndType(AnneeExercice anneeExercice, Type type);
    
    /**
     * Recherche avec filtres multiples
     */
    @Query("SELECT m FROM MouvementBudgetaire m WHERE " +
           "(:anneeExerciceId IS NULL OR m.anneeExercice.id = :anneeExerciceId) AND " +
           "(:typeId IS NULL OR m.type.id = :typeId) AND " +
           "(:recherche IS NULL OR LOWER(m.description) LIKE LOWER(CONCAT('%', :recherche, '%'))) AND " +
           "(:dateDebut IS NULL OR m.createdAt >= :dateDebut) AND " +
           "(:dateFin IS NULL OR m.createdAt <= :dateFin) " +
           "ORDER BY m.createdAt DESC")
    List<MouvementBudgetaire> findWithFilters(
            @Param("anneeExerciceId") Long anneeExerciceId,
            @Param("typeId") Long typeId,
            @Param("recherche") String recherche,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );
    
    /**
     * Recherche paginée avec filtres multiples
     */
    @Query("SELECT m FROM MouvementBudgetaire m WHERE " +
           "(:anneeExerciceId IS NULL OR m.anneeExercice.id = :anneeExerciceId) AND " +
           "(:typeId IS NULL OR m.type.id = :typeId) AND " +
           "(:recherche IS NULL OR LOWER(m.description) LIKE LOWER(CONCAT('%', :recherche, '%'))) AND " +
           "(:dateDebut IS NULL OR m.createdAt >= :dateDebut) AND " +
           "(:dateFin IS NULL OR m.createdAt <= :dateFin)")
    Page<MouvementBudgetaire> findWithFiltersPaginated(
            @Param("anneeExerciceId") Long anneeExerciceId,
            @Param("typeId") Long typeId,
            @Param("recherche") String recherche,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            Pageable pageable
    );
    
    /**
     * Calcule le total des recettes pour une année d'exercice
     */
    @Query("SELECT COALESCE(SUM(m.montant), 0) FROM MouvementBudgetaire m " +
           "WHERE m.anneeExercice.id = :anneeExerciceId AND m.type.type = 'RECETTE'")
    BigDecimal calculateTotalRecettes(@Param("anneeExerciceId") Long anneeExerciceId);
    
    /**
     * Calcule le total des dépenses pour une année d'exercice
     */
    @Query("SELECT COALESCE(SUM(m.montant), 0) FROM MouvementBudgetaire m " +
           "WHERE m.anneeExercice.id = :anneeExerciceId AND m.type.type = 'DEPENSE'")
    BigDecimal calculateTotalDepenses(@Param("anneeExerciceId") Long anneeExerciceId);
}
