package com.explorateur.backend.repository;

import com.explorateur.backend.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByUsername(String username);
    Optional<Utilisateur> findByUsernameAndActiveTrue(String username);
    List<Utilisateur> findByActive(Boolean active);
    List<Utilisateur> findByAnneeExerciceId(Long anneeExerciceId);
    List<Utilisateur> findByRoleRoleNameAndActive(String roleName, Boolean active);
    
    /**
     * Récupère tous les utilisateurs actifs (non supprimés)
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.etat <> 11 ORDER BY u.username ASC")
    List<Utilisateur> findAllActive();
    
    /**
     * Récupère un utilisateur par username (excluant les supprimés)
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.etat <> 11 AND u.username = :username")
    Optional<Utilisateur> findByUsernameActive(@Param("username") String username);
    
    /**
     * Récupère les utilisateurs par année d'exercice (excluant les supprimés)
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.etat <> 11 AND u.anneeExercice.id = :anneeExerciceId")
    List<Utilisateur> findByAnneeExerciceIdActive(@Param("anneeExerciceId") Long anneeExerciceId);
}
