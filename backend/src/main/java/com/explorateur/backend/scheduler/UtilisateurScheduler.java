package com.explorateur.backend.scheduler;

import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.entity.Utilisateur;
import com.explorateur.backend.repository.AnneeExerciceRepository;
import com.explorateur.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Tâches planifiées pour la gestion automatique des utilisateurs
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UtilisateurScheduler {

    private final AnneeExerciceRepository anneeExerciceRepository;
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Désactive automatiquement les utilisateurs (sauf Directeurs) 
     * dont l'année d'exercice est dépassée.
     * S'exécute tous les jours à 01:00
     */
    @Scheduled(cron = "0 0 1 * * *") // Tous les jours à 01h00
    @Transactional
    public void desactiverUtilisateursAnneesDepassees() {
        LocalDate aujourdhui = LocalDate.now();
        
        // Trouver toutes les années d'exercice dépassées
        List<AnneeExercice> anneesDepassees = anneeExerciceRepository.findByDateFinBefore(aujourdhui);
        
        if (anneesDepassees.isEmpty()) {
            log.debug("Aucune année d'exercice dépassée trouvée");
            return;
        }
        
        int compteurDesactivations = 0;
        
        for (AnneeExercice anneeDepassee : anneesDepassees) {
            log.info("🕒 Traitement de l'année d'exercice dépassée: {} (fin: {})", 
                anneeDepassee.getAnnee().getYear(), 
                anneeDepassee.getDateFin());
            
            // Récupérer tous les utilisateurs actifs de cette année
            List<Utilisateur> utilisateurs = utilisateurRepository.findByAnneeExerciceId(anneeDepassee.getId());
            
            for (Utilisateur utilisateur : utilisateurs) {
                // Ne pas désactiver les Directeurs
                if ("Directeur".equals(utilisateur.getRole().getRoleName())) {
                    log.debug("  ⏭️  Directeur {} ignoré (non désactivé)", utilisateur.getUsername());
                    continue;
                }
                
                // Désactiver uniquement si l'utilisateur est actif
                if (Boolean.TRUE.equals(utilisateur.getActive())) {
                    utilisateur.setActive(false);
                    utilisateur.setUpdatedAt(LocalDateTime.now());
                    utilisateurRepository.save(utilisateur);
                    
                    log.info("  🔒 Utilisateur {} ({}) désactivé (année {} terminée)", 
                        utilisateur.getUsername(),
                        utilisateur.getRole().getRoleName(),
                        anneeDepassee.getAnnee().getYear());
                    
                    compteurDesactivations++;
                }
            }
        }
        
        if (compteurDesactivations > 0) {
            log.info("✅ Désactivation automatique terminée: {} utilisateur(s) désactivé(s)", 
                compteurDesactivations);
        } else {
            log.debug("Aucun utilisateur à désactiver");
        }
    }
}
