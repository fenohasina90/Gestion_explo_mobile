package com.explorateur.backend.scheduler;

import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.entity.Programme;
import com.explorateur.backend.entity.ProgrammeProgressionAnnuelle;
import com.explorateur.backend.entity.ProgrammeStatus;
import com.explorateur.backend.entity.HistoriqueProgramme;
import com.explorateur.backend.repository.AnneeExerciceRepository;
import com.explorateur.backend.repository.ProgrammeProgressionAnnuelleRepository;
import com.explorateur.backend.repository.ProgrammeRepository;
import com.explorateur.backend.repository.ProgrammeStatusRepository;
import com.explorateur.backend.repository.HistoriqueProgrammeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Tâches planifiées pour la gestion automatique des programmes
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProgrammeScheduler {

    private final ProgrammeRepository programmeRepository;
    private final AnneeExerciceRepository anneeExerciceRepository;
    private final ProgrammeProgressionAnnuelleRepository progressionAnnuelleRepository;
    private final ProgrammeStatusRepository programmeStatusRepository;
    private final HistoriqueProgrammeRepository historiqueProgrammeRepository;

    /**
     * Initialise automatiquement tous les programmes à "EN ATTENTE" 
     * au premier jour de chaque année d'exercice.
     * S'exécute tous les jours à 00:01
     */
    @Scheduled(cron = "0 1 0 * * *") // Tous les jours à 00h01
    @Transactional
    public void initialiserStatutsProgrammesNouvelleAnnee() {
        LocalDate aujourdhui = LocalDate.now();
        
        log.info("🔄 Vérification initialisation des statuts de programmes pour la date: {}", aujourdhui);
        
        // Vérifier si c'est le premier jour d'une année d'exercice
        List<AnneeExercice> anneesCommencantAujourdhui = anneeExerciceRepository
                .findAllByAnnee(aujourdhui);
        
        if (anneesCommencantAujourdhui.isEmpty()) {
            log.debug("Aucune année d'exercice ne commence aujourd'hui");
            return;
        }
        
        // Récupérer le statut "En attente"
        ProgrammeStatus statutEnAttente = programmeStatusRepository.findByStatus("En attente")
                .orElseThrow(() -> new IllegalStateException("Le statut 'En attente' n'existe pas dans la base de données"));
        
        // Pour chaque nouvelle année d'exercice
        for (AnneeExercice anneeExercice : anneesCommencantAujourdhui) {
            log.info("📅 Initialisation des programmes pour l'année d'exercice: {}", anneeExercice.getAnnee().getYear());
            
            // Récupérer tous les programmes actifs
            List<Programme> tousLesProgrammes = programmeRepository.findAll();
            
            int compteurInitialisations = 0;
            
            for (Programme programme : tousLesProgrammes) {
                // Vérifier si la progression existe déjà pour cette année
                boolean dejaInitialise = progressionAnnuelleRepository
                        .existsByProgrammeIdAndAnneeExerciceId(programme.getId(), anneeExercice.getId());
                
                if (!dejaInitialise) {
                    // Créer la progression avec statut "En attente"
                    ProgrammeProgressionAnnuelle progression = ProgrammeProgressionAnnuelle.builder()
                            .programme(programme)
                            .anneeExercice(anneeExercice)
                            .statutFinal(statutEnAttente)
                            .build();
                    
                    progressionAnnuelleRepository.save(progression);
                    compteurInitialisations++;
                }
            }
            
            log.info("✅ {} programmes initialisés à 'EN ATTENTE' pour l'année {}", 
                    compteurInitialisations, anneeExercice.getAnnee().getYear());
        }
    }
    
    /**
     * Initialisation manuelle des statuts pour une année d'exercice donnée
     * Utile pour les années déjà commencées ou pour la correction de données
     */
    @Transactional
    public void initialiserStatutsPourAnnee(Long anneeExerciceId) {
        log.info("🔧 Initialisation manuelle des statuts pour l'année ID: {}", anneeExerciceId);
        
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(anneeExerciceId)
                .orElseThrow(() -> new IllegalArgumentException("Année d'exercice non trouvée: " + anneeExerciceId));
        
        ProgrammeStatus statutEnAttente = programmeStatusRepository.findByStatus("En attente")
                .orElseThrow(() -> new IllegalStateException("Le statut 'En attente' n'existe pas dans la base de données"));
        
        List<Programme> tousLesProgrammes = programmeRepository.findAll();
        int compteurInitialisations = 0;
        
        for (Programme programme : tousLesProgrammes) {
            boolean dejaInitialise = progressionAnnuelleRepository
                    .existsByProgrammeIdAndAnneeExerciceId(programme.getId(), anneeExercice.getId());
            
            if (!dejaInitialise) {
                ProgrammeProgressionAnnuelle progression = ProgrammeProgressionAnnuelle.builder()
                        .programme(programme)
                        .anneeExercice(anneeExercice)
                        .statutFinal(statutEnAttente)
                        .build();
                
                progressionAnnuelleRepository.save(progression);
                
                // Enregistrer aussi dans l'historique (classe_progressive_id = NULL)
                HistoriqueProgramme historique = HistoriqueProgramme.builder()
                        .programme(programme)
                        .classeProgressive(null)  // NULL = initialisation système
                        .status(statutEnAttente)
                        .anneeExercice(anneeExercice)
                        .build();
                
                historiqueProgrammeRepository.save(historique);
                compteurInitialisations++;
            }
        }
        
        log.info("✅ Initialisation manuelle terminée: {} programmes initialisés", compteurInitialisations);
    }
}
