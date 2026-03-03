package com.explorateur.backend.service;

import com.explorateur.backend.dto.HistoriqueProgrammeDto;
import com.explorateur.backend.dto.ProgressionAnnuelleDto;
import com.explorateur.backend.dto.StatistiquesAnnuellesDto;
import com.explorateur.backend.entity.*;
import com.explorateur.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service pour gérer l'historique des programmes.
 * Règles métier strictes:
 * - L'historique est IMMUABLE (pas de modification/suppression)
 * - Chaque changement de statut génère une ligne
 * - L'ordre chronologique est respecté
 * - Isolation par année d'exercice
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HistoriqueProgrammeService {
    
    private final HistoriqueProgrammeRepository historiqueProgrammeRepository;
    private final ProgrammeProgressionAnnuelleRepository progressionAnnuelleRepository;
    private final ProgrammeRepository programmeRepository;
    private final ClasseProgressiveRepository classeProgressiveRepository;
    private final ProgrammeStatusRepository programmeStatusRepository;
    private final JournalService journalService;
    
    /**
     * Enregistre un changement de statut dans l'historique.
     * IMPORTANT: Cette méthode est appelée automatiquement lors de chaque changement de statut.
     * 
     * @param programmeId ID du programme
     * @param classeProgressiveId ID de la CP
     * @param nouveauStatusId ID du nouveau statut
     */
    @Transactional
    public void enregistrerChangementStatut(Long programmeId, Long classeProgressiveId, Long nouveauStatusId) {
        log.info("Enregistrement changement statut: programme={}, cp={}, statut={}", 
                programmeId, classeProgressiveId, nouveauStatusId);
        
        Programme programme = programmeRepository.findById(programmeId)
                .orElseThrow(() -> new IllegalArgumentException("Programme non trouvé: " + programmeId));
        
        ClasseProgressive cp = classeProgressiveRepository.findById(classeProgressiveId)
                .orElseThrow(() -> new IllegalArgumentException("CP non trouvée: " + classeProgressiveId));
        
        ProgrammeStatus status = programmeStatusRepository.findById(nouveauStatusId)
                .orElseThrow(() -> new IllegalArgumentException("Statut non trouvé: " + nouveauStatusId));
        
        // Créer l'entrée d'historique (IMMUABLE)
        HistoriqueProgramme historique = HistoriqueProgramme.builder()
                .programme(programme)
                .classeProgressive(cp)
                .status(status)
                .build();
        
        historiqueProgrammeRepository.save(historique);
        log.info("Historique enregistré avec succès: ID={}", historique.getId());
        
        // Mettre à jour la progression annuelle si statut = "Terminé"
        if ("Terminé".equals(status.getStatus())) {
            mettreAJourProgressionAnnuelle(programme, cp.getAnneeExercice(), status);
        }
        
        // Journaliser l'action
        journalService.logAction(
                String.format("Changement de statut du programme '%s' vers '%s' dans la CP du %s",
                        programme.getNom(), status.getStatus(), cp.getDateCp())
        );
    }
    
    /**
     * Met à jour ou crée la progression annuelle d'un programme
     */
    @Transactional
    protected void mettreAJourProgressionAnnuelle(Programme programme, AnneeExercice anneeExercice, ProgrammeStatus statutFinal) {
        var progressionOpt = progressionAnnuelleRepository
                .findByProgrammeIdAndAnneeExerciceId(programme.getId(), anneeExercice.getId());
        
        if (progressionOpt.isPresent()) {
            // Mise à jour du statut final
            ProgrammeProgressionAnnuelle progression = progressionOpt.get();
            progression.setStatutFinal(statutFinal);
            progressionAnnuelleRepository.save(progression);
            log.info("Progression annuelle mise à jour: programme={}, année={}, statut={}",
                    programme.getNom(), anneeExercice.getAnnee(), statutFinal.getStatus());
        } else {
            // Création de la progression
            ProgrammeProgressionAnnuelle progression = ProgrammeProgressionAnnuelle.builder()
                    .programme(programme)
                    .anneeExercice(anneeExercice)
                    .statutFinal(statutFinal)
                    .build();
            progressionAnnuelleRepository.save(progression);
            log.info("Progression annuelle créée: programme={}, année={}",
                    programme.getNom(), anneeExercice.getAnnee());
        }
    }
    
    /**
     * Récupère l'historique complet d'un programme (toutes années)
     */
    @Transactional(readOnly = true)
    public List<HistoriqueProgrammeDto> getHistoriqueProgramme(Long programmeId) {
        log.info("Récupération historique du programme: {}", programmeId);
        
        List<HistoriqueProgramme> historiques = historiqueProgrammeRepository
                .findByProgrammeIdOrderByCreatedAtAsc(programmeId);
        
        return historiques.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère l'historique d'un programme pour une année spécifique
     */
    @Transactional(readOnly = true)
    public List<HistoriqueProgrammeDto> getHistoriqueProgrammeParAnnee(Long programmeId, Long anneeExerciceId) {
        log.info("Récupération historique du programme {} pour l'année {}", programmeId, anneeExerciceId);
        
        List<HistoriqueProgramme> historiques = historiqueProgrammeRepository
                .findByProgrammeIdAndAnneeExerciceId(programmeId, anneeExerciceId);
        
        return historiques.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère l'historique d'une CP
     */
    @Transactional(readOnly = true)
    public List<HistoriqueProgrammeDto> getHistoriqueCP(Long cpId) {
        log.info("Récupération historique de la CP: {}", cpId);
        
        List<HistoriqueProgramme> historiques = historiqueProgrammeRepository
                .findByClasseProgressiveIdOrderByCreatedAtAsc(cpId);
        
        return historiques.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les progressions annuelles d'un programme
     */
    @Transactional(readOnly = true)
    public List<ProgressionAnnuelleDto> getProgressionsProgramme(Long programmeId) {
        log.info("Récupération progressions du programme: {}", programmeId);
        
        List<ProgrammeProgressionAnnuelle> progressions = progressionAnnuelleRepository
                .findByProgrammeIdOrderByAnneeExerciceAnneeDesc(programmeId);
        
        return progressions.stream()
                .map(this::mapProgressionToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Calcule les statistiques annuelles des programmes
     */
    @Transactional(readOnly = true)
    public StatistiquesAnnuellesDto getStatistiquesAnnuelles(Long anneeExerciceId) {
        log.info("Calcul statistiques pour l'année: {}", anneeExerciceId);
        
        // Récupérer tous les historiques de l'année
        List<HistoriqueProgramme> historiques = historiqueProgrammeRepository
                .findByAnneeExerciceIdOrderByCreatedAtAsc(anneeExerciceId);
        
        // Récupérer toutes les progressions de l'année
        List<ProgrammeProgressionAnnuelle> progressions = progressionAnnuelleRepository
                .findByAnneeExerciceId(anneeExerciceId);
        
        // Compter les programmes uniques travaillés
        long totalProgrammes = historiques.stream()
                .map(h -> h.getProgramme().getId())
                .distinct()
                .count();
        
        // Compter par statut final
        Map<String, Long> comptesParStatut = new HashMap<>();
        for (ProgrammeProgressionAnnuelle prog : progressions) {
            if (prog.getStatutFinal() != null) {
                String statut = prog.getStatutFinal().getStatus();
                comptesParStatut.put(statut, comptesParStatut.getOrDefault(statut, 0L) + 1);
            }
        }
        
        long programmesTermines = comptesParStatut.getOrDefault("Terminé", 0L);
        long programmesEnCours = comptesParStatut.getOrDefault("En cours", 0L);
        long programmesEnAttente = comptesParStatut.getOrDefault("En attente", 0L);
        
        // Compter les CPs uniques
        long nombreCPs = historiques.stream()
                .map(h -> h.getClasseProgressive().getId())
                .distinct()
                .count();
        
        // Calculer le taux de complétion
        double tauxCompletion = totalProgrammes > 0 
                ? (programmesTermines * 100.0) / totalProgrammes 
                : 0.0;
        
        // Récupérer l'année d'exercice (format string)
        String anneeExercice = historiques.isEmpty() 
                ? "" 
                : historiques.get(0).getClasseProgressive().getAnneeExercice().getAnnee().toString();
        
        return StatistiquesAnnuellesDto.builder()
                .anneeExercice(anneeExercice)
                .totalProgrammesTravailles(totalProgrammes)
                .programmesTermines(programmesTermines)
                .programmesEnCours(programmesEnCours)
                .programmesEnAttente(programmesEnAttente)
                .totalChangements((long) historiques.size())
                .nombreCPs(nombreCPs)
                .tauxCompletion(Math.round(tauxCompletion * 100.0) / 100.0)
                .build();
    }
    
    /**
     * Mapper de HistoriqueProgramme vers DTO
     */
    private HistoriqueProgrammeDto mapToDto(HistoriqueProgramme historique) {
        return HistoriqueProgrammeDto.builder()
                .id(historique.getId())
                .programmeId(historique.getProgramme().getId())
                .programmeNom(historique.getProgramme().getNom())
                .classeProgressiveId(historique.getClasseProgressive().getId())
                .classeProgressiveDate(historique.getClasseProgressive().getDateCp().toString())
                .statusId(historique.getStatus().getId())
                .statusNom(historique.getStatus().getStatus())
                .anneeExercice(historique.getClasseProgressive().getAnneeExercice().getAnnee().toString())
                .dateChangement(historique.getCreatedAt())
                .build();
    }
    
    /**
     * Mapper de ProgrammeProgressionAnnuelle vers DTO
     */
    private ProgressionAnnuelleDto mapProgressionToDto(ProgrammeProgressionAnnuelle progression) {
        // Compter le nombre de changements pour ce programme/année
        Long nombreChangements = historiqueProgrammeRepository
                .findByProgrammeIdAndAnneeExerciceId(
                        progression.getProgramme().getId(),
                        progression.getAnneeExercice().getId()
                ).size();
        
        return ProgressionAnnuelleDto.builder()
                .id(progression.getId())
                .programmeId(progression.getProgramme().getId())
                .programmeNom(progression.getProgramme().getNom())
                .anneeExercice(progression.getAnneeExercice().getAnnee().toString())
                .statutFinalId(progression.getStatutFinal() != null ? progression.getStatutFinal().getId() : null)
                .statutFinalNom(progression.getStatutFinal() != null ? progression.getStatutFinal().getStatus() : null)
                .nombreChangements(nombreChangements)
                .createdAt(progression.getCreatedAt())
                .updatedAt(progression.getUpdatedAt())
                .build();
    }
}
