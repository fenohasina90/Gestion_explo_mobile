package com.explorateur.backendbeta.repository;

import com.explorateur.backendbeta.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JournalRepository extends JpaRepository<Journal, Long> {

    @Query("""
            SELECT j
            FROM Journal j
            WHERE (:searchText = '' OR LOWER(COALESCE(j.action, '')) LIKE LOWER(CONCAT('%', :searchText, '%')))
              AND j.timestamp >= :dateDebut
              AND j.timestamp <= :dateFin
            ORDER BY j.timestamp DESC
            """)
    List<Journal> findByFilters(
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            @Param("searchText") String searchText
    );

    List<Journal> findAllByOrderByTimestampDesc();
}
