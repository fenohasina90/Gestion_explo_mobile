package com.explorateur.backend.service;

import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.entity.HistoriqueProgramme;
import com.explorateur.backend.entity.Programme;
import com.explorateur.backend.entity.ProgrammeStatus;
import com.explorateur.backend.repository.AnneeExerciceRepository;
import com.explorateur.backend.repository.HistoriqueProgrammeRepository;
import com.explorateur.backend.repository.ProgrammeRepository;
import com.explorateur.backend.repository.ProgrammeStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service pour l'initialisation annuelle des programmes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InitialisationAnnuelleService {
    
    private final ProgrammeRepository programmeRepository;
    private final HistoriqueProgrammeRepository historiqueProgrammeRepository;
    private final ProgrammeStatusRepository programmeStatusRepository;
    private final AnneeExerciceRepository anneeExerciceRepository;
    private final JournalService journalService;
    
    private static final String STATUS_EN_ATTENTE = "En attente";
    
    /**
     * Initialise tous les programmes à "EN ATTENTE" pour une année d'exercice
     * Cette méthode doit être appelée au début de chaque année d'exercice
     * @return le nombre de programmes initialisés
     */
    @Transactional
    public int initialiserProgrammesAnnee(Long anneeExerciceId) {
        log.info("Initialisation des programmes pour l'année d'exercice ID: {}", anneeExerciceId);
        
        // Récupérer l'année d'exercice
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(anneeExerciceId)
                .orElseThrow(() -> new RuntimeException("Année d'exercice introuvable"));
        
        // Récupérer le statut "En attente"
        ProgrammeStatus statusEnAttente = programmeStatusRepository.findByStatus(STATUS_EN_ATTENTE)
                .orElseThrow(() -> new RuntimeException("Statut 'En attente' introuvable"));
        
        // Récupérer tous les programmes
        List<Programme> programmes = programmeRepository.findAll();
        
        int compteur = 0;
        for (Programme programme : programmes) {
            // Vérifier si ce programme a déjà un historique pour cette année
            List<HistoriqueProgramme> existant = historiqueProgrammeRepository
                    .findByProgrammeIdAndAnneeExerciceId(programme.getId(), anneeExerciceId);
            
            if (existant.isEmpty()) {
                // Créer l'entrée initiale dans l'historique
                HistoriqueProgramme historique = HistoriqueProgramme.builder()
                        .programme(programme)
                        .classeProgressive(null) // Pas de CP associée pour l'initialisation
                        .status(statusEnAttente)
                        .build();
                
                historiqueProgrammeRepository.save(historique);
                compteur++;
                
                log.debug("Programme '{}' initialisé à 'En attente' pour l'année {}", 
                         programme.getNom(), anneeExercice.getAnnee());
            }
        }
        
        // Journaliser l'action
        journalService.logAction(
                "Initialisation annuelle de " + compteur + " programmes à 'En attente' pour l'année " + anneeExercice.getAnnee()
        );
        
        log.info("{} programmes initialisés pour l'année {}", compteur, anneeExercice.getAnnee());
        
        return compteur;
    }
    
    /**
     * Vérifie si l'initialisation a été effectuée pour une année donnée
     */
    @Transactional(readOnly = true)
    public boolean estInitialise(Long anneeExerciceId) {
        // Compter le nombre de programmes
        long nombreProgrammes = programmeRepository.count();
        
        // Compter le nombre d'entrées d'historique pour cette année
        long nombreInitialisations = historiqueProgrammeRepository
                .findByAnneeExerciceIdOrderByCreatedAtAsc(anneeExerciceId)
                .stream()
                .filter(h -> h.getClasseProgressive() == null) // Initialisation sans CP
                .count();
        
        return nombreInitialisations >= nombreProgrammes;
    }
}
