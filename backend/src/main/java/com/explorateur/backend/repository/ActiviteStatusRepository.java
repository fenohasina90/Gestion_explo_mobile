package com.explorateur.backend.repository;

import com.explorateur.backend.entity.ActiviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiviteStatusRepository extends JpaRepository<ActiviteStatus, Long> {
}
