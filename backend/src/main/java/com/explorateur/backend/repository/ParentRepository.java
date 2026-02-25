package com.explorateur.backend.repository;

import com.explorateur.backend.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    
    /**
     * Recherche de parents par nom et/ou prénom (pour auto-complétion)
     * Utilise LIKE avec l'opérateur || pour la concaténation SQLite
     */
    @Query(value = "SELECT * FROM parents p " +
           "WHERE (LOWER(p.nom || ' ' || p.prenom) LIKE LOWER(:searchTerm) " +
           "OR LOWER(p.prenom || ' ' || p.nom) LIKE LOWER(:searchTerm)) " +
           "ORDER BY p.nom ASC, p.prenom ASC " +
           "LIMIT 10", nativeQuery = true)
    List<Parent> searchByNomOrPrenom(@Param("searchTerm") String searchTerm);
    
    /**
     * Trouve les parents par nom et prénom (vérification doublon)
     */
    @Query("SELECT p FROM Parent p WHERE LOWER(p.nom) = LOWER(:nom) AND LOWER(p.prenom) = LOWER(:prenom)")
    List<Parent> findByNomAndPrenom(@Param("nom") String nom, @Param("prenom") String prenom);
}
