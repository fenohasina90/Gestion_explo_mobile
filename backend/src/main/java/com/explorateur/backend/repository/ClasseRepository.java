package com.explorateur.backend.repository;

import com.explorateur.backend.entity.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClasseRepository extends JpaRepository<Classe, Long> {
    
    /**
     * Trouve toutes les classes ordonnées par nom
     */
    List<Classe> findAllByOrderByNomAsc();
}
