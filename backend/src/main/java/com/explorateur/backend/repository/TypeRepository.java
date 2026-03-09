package com.explorateur.backend.repository;

import com.explorateur.backend.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {
    
    /**
     * Trouve un type par son nom
     */
    Optional<Type> findByType(String type);
}
