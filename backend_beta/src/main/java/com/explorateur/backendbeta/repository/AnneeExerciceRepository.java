package com.explorateur.backendbeta.repository;

import com.explorateur.backendbeta.entity.AnneeExercice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnneeExerciceRepository extends JpaRepository<AnneeExercice, Long> {
    Optional<AnneeExercice> findFirstByOrderByAnneeDesc();
}
