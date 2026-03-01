package com.explorateur.backend.repository;

import com.explorateur.backend.entity.ProgrammeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour ProgrammeStatus
 */
@Repository
public interface ProgrammeStatusRepository extends JpaRepository<ProgrammeStatus, Long> {
    
    /**
     * Trouver un statut par son nom
     */
    Optional<ProgrammeStatus> findByStatus(String status);
}
