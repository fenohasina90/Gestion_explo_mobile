package com.explorateur.backend.repository;

import com.explorateur.backend.entity.BudgetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetStatusRepository extends JpaRepository<BudgetStatus, Long> {
}
