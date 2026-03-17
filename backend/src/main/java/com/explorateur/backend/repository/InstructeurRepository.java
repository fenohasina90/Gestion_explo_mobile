package com.explorateur.backend.repository;

import com.explorateur.backend.entity.Instructeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Instructeur
 */
@Repository
public interface InstructeurRepository extends JpaRepository<Instructeur, Long> {
    
    /**
     * Recherche des instructeurs par nom ou prénom (pour auto-complétion)
     * Utilise une requête native pour compatibilité SQLite
     */
//     @Query(value = "SELECT * FROM instructeur WHERE " +
//            "LOWER(nom) LIKE LOWER('%' || :search || '%') OR " +
//            "LOWER(prenom) LIKE LOWER('%' || :search || '%')", 
//            nativeQuery = true)
//     List<Instructeur> searchByNomOrPrenom(@Param("search") String search);

       @Query(value = "SELECT * FROM instructeur WHERE " +
              "LOWER(nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
              "LOWER(prenom) LIKE LOWER(CONCAT('%', :search, '%'))", 
              nativeQuery = true)
    List<Instructeur> searchByNomOrPrenom(@Param("search") String search);
    
    /**
     * Recherche d'un instructeur par nom et prénom exact (pour éviter les doublons)
     */
    @Query("SELECT i FROM Instructeur i WHERE " +
           "LOWER(i.nom) = LOWER(:nom) AND LOWER(i.prenom) = LOWER(:prenom)")
    Optional<Instructeur> findByNomAndPrenom(@Param("nom") String nom, @Param("prenom") String prenom);
    
    /**
     * Récupère tous les instructeurs triés par nom
     */
    List<Instructeur> findAllByOrderByNomAsc();
}
