package com.explorateur.backendbeta.repository;

import com.explorateur.backendbeta.entity.MouvementBudgetaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MouvementBudgetaireRepository extends JpaRepository<MouvementBudgetaire, Long> {

    @Query("""
            SELECT m
            FROM MouvementBudgetaire m
            WHERE (:recherche IS NULL OR :recherche = '' OR LOWER(COALESCE(m.description, '')) LIKE LOWER(CONCAT('%', :recherche, '%')))
              AND (:dateDebut IS NULL OR FUNCTION('date', m.createdAt) >= :dateDebut)
              AND (:dateFin IS NULL OR FUNCTION('date', m.createdAt) <= :dateFin)
              AND (:typeId IS NULL OR m.type.id = :typeId)
              AND (:anneeExerciceId IS NULL OR m.anneeExercice.id = :anneeExerciceId)
            """)
    Page<MouvementBudgetaire> findByFilters(
            @Param("recherche") String recherche,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("typeId") Long typeId,
            @Param("anneeExerciceId") Long anneeExerciceId,
            Pageable pageable
    );

    @Query("""
            SELECT m
            FROM MouvementBudgetaire m
            WHERE (:recherche IS NULL OR :recherche = '' OR LOWER(COALESCE(m.description, '')) LIKE LOWER(CONCAT('%', :recherche, '%')))
              AND (:dateDebut IS NULL OR FUNCTION('date', m.createdAt) >= :dateDebut)
              AND (:dateFin IS NULL OR FUNCTION('date', m.createdAt) <= :dateFin)
              AND (:typeId IS NULL OR m.type.id = :typeId)
              AND (:anneeExerciceId IS NULL OR m.anneeExercice.id = :anneeExerciceId)
            """)
    List<MouvementBudgetaire> findByFilters(
            @Param("recherche") String recherche,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("typeId") Long typeId,
            @Param("anneeExerciceId") Long anneeExerciceId,
            Sort sort
    );

    @Query("""
            SELECT COALESCE(SUM(m.montant), 0)
            FROM MouvementBudgetaire m
            WHERE (:anneeExerciceId IS NULL OR m.anneeExercice.id = :anneeExerciceId)
              AND UPPER(m.type.type) = UPPER(:typeName)
            """)
    BigDecimal sumMontantByType(@Param("typeName") String typeName, @Param("anneeExerciceId") Long anneeExerciceId);
}
