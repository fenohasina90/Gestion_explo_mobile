package com.explorateur.backend.service;

import com.explorateur.backend.dto.HistoriqueProgrammeDto;
import com.explorateur.backend.dto.ProgressionAnnuelleDto;
import com.explorateur.backend.dto.StatistiquesAnnuellesDto;
import com.explorateur.backend.dto.ProgrammeAvancementDto;
import com.explorateur.backend.entity.*;
import com.explorateur.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final AnneeExerciceRepository anneeExerciceRepository;
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
                .anneeExercice(cp.getAnneeExercice())
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
        
        // Récupérer TOUS les programmes (pas seulement ceux travaillés)
        List<Programme> tousProgrammes = programmeRepository.findAll();
        long totalProgrammes = tousProgrammes.size();
        
        // Récupérer tous les historiques de l'année
        List<HistoriqueProgramme> historiques = historiqueProgrammeRepository
                .findByAnneeExerciceIdOrderByCreatedAtAsc(anneeExerciceId);
        
        // Calculer le statut final de chaque programme (dernier statut dans l'historique)
        Map<Long, String> statutsFinaux = new HashMap<>();
        
        // Initialiser tous les programmes à "En attente"
        for (Programme p : tousProgrammes) {
            statutsFinaux.put(p.getId(), "En attente");
        }
        
        // Mettre à jour avec les statuts réels depuis l'historique
        for (HistoriqueProgramme h : historiques) {
            Long programmeId = h.getProgramme().getId();
            // Le dernier statut écrase les précédents (historiques triés par date croissante)
            if (h.getStatus() != null) {
                statutsFinaux.put(programmeId, h.getStatus().getStatus());
            }
        }
        
        // Compter par statut final
        Map<String, Long> comptesParStatut = new HashMap<>();
        for (String statut : statutsFinaux.values()) {
            comptesParStatut.put(statut, comptesParStatut.getOrDefault(statut, 0L) + 1);
        }
        
        long programmesTermines = comptesParStatut.getOrDefault("Terminé", 0L);
        long programmesEnCours = comptesParStatut.getOrDefault("En cours", 0L);
        long programmesEnAttente = comptesParStatut.getOrDefault("En attente", 0L);
        
        // Compter les CPs uniques
        long nombreCPs = historiques.stream()
                .map(h -> h.getClasseProgressive() != null ? h.getClasseProgressive().getId() : null)
                .filter(id -> id != null)
                .distinct()
                .count();
        
        // Calculer le taux de complétion
        double tauxCompletion = totalProgrammes > 0 
                ? (programmesTermines * 100.0) / totalProgrammes 
                : 0.0;
        
        // Récupérer l'année d'exercice (format string)
        String anneeExercice = "";
        if (!historiques.isEmpty() && historiques.get(0).getAnneeExercice() != null) {
            anneeExercice = historiques.get(0).getAnneeExercice().getAnnee().toString();
        }
        
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
     * Récupère l'avancement de tous les programmes pour une année d'exercice
     * avec possibilité de filtrer par classe et catégorie
     */
    @Transactional(readOnly = true)
    public List<ProgrammeAvancementDto> getAvancementProgrammes(
            Long anneeExerciceId, 
            Long classeId, 
            Long categorieId) {
        log.info("Récupération avancement programmes: année={}, classe={}, catégorie={}", 
                anneeExerciceId, classeId, categorieId);
        
        // Récupérer tous les programmes avec filtres
        List<Programme> programmes = programmeRepository.findAll();
        
        // Appliquer les filtres
        if (classeId != null) {
            programmes = programmes.stream()
                    .filter(p -> p.getClasse().getId().equals(classeId))
                    .collect(Collectors.toList());
        }
        
        if (categorieId != null) {
            programmes = programmes.stream()
                    .filter(p -> p.getCategorie().getId().equals(categorieId))
                    .collect(Collectors.toList());
        }
        
        // Pour chaque programme, récupérer son statut actuel pour l'année donnée
        return programmes.stream()
                .map(programme -> calculerAvancement(programme, anneeExerciceId))
                .collect(Collectors.toList());
    }
    
    /**
     * Calcule l'avancement d'un programme pour une année donnée
     */
    private ProgrammeAvancementDto calculerAvancement(Programme programme, Long anneeExerciceId) {
        // Récupérer l'historique du programme pour cette année
        List<HistoriqueProgramme> historiques = historiqueProgrammeRepository
                .findLatestByProgrammeAndAnnee(programme.getId(), anneeExerciceId);
        
        // Statut actuel = dernier enregistrement
        HistoriqueProgramme dernierHistorique = historiques.isEmpty() ? null : historiques.get(0);
        
        // Compter les changements
        Long nombreChangements = historiqueProgrammeRepository
                .countByProgrammeAndAnnee(programme.getId(), anneeExerciceId);
        
        // Récupérer toutes les entrées de l'année pour calculer dates min/max
        List<HistoriqueProgramme> tousHistoriques = historiqueProgrammeRepository
                .findByProgrammeIdAndAnneeExerciceId(programme.getId(), anneeExerciceId);
        
        // Séparer les historiques avec et sans CP
        List<HistoriqueProgramme> historiquesAvecCP = tousHistoriques.stream()
                .filter(h -> h.getClasseProgressive() != null)
                .collect(Collectors.toList());
        
        LocalDate datePremiereCP = historiquesAvecCP.isEmpty() ? null 
                : historiquesAvecCP.get(0).getClasseProgressive().getDateCp();
        LocalDate dateDerniereCP = historiquesAvecCP.isEmpty() ? null 
                : historiquesAvecCP.get(historiquesAvecCP.size() - 1).getClasseProgressive().getDateCp();
        
        // Analyser l'évolution
        boolean estDemarre = tousHistoriques.stream()
                .anyMatch(h -> "En cours".equals(h.getStatus().getStatus()) || 
                              "Terminé".equals(h.getStatus().getStatus()));
        boolean estTermine = dernierHistorique != null && 
                            "Terminé".equals(dernierHistorique.getStatus().getStatus());
        
        // Calculer le pourcentage d'avancement
        int pourcentage = 0;
        if (dernierHistorique != null) {
            String statut = dernierHistorique.getStatus().getStatus();
            if ("En attente".equals(statut)) {
                pourcentage = 0;
            } else if ("En cours".equals(statut)) {
                pourcentage = 50;
            } else if ("Terminé".equals(statut)) {
                pourcentage = 100;
            }
        }
        
        // Convertir TOUS les historiques en DTOs (y compris initialisation)
        List<HistoriqueProgrammeDto> historiqueDtos = tousHistoriques.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        // Récupérer l'année d'exercice en string
        String anneeExerciceStr = dernierHistorique != null 
                ? dernierHistorique.getAnneeExercice().getAnnee().toString() 
                : (!tousHistoriques.isEmpty() 
                    ? tousHistoriques.get(0).getAnneeExercice().getAnnee().toString() 
                    : null);
        
        return ProgrammeAvancementDto.builder()
                .programmeId(programme.getId())
                .programmeNom(programme.getNom())
                .categorieId(programme.getCategorie().getId())
                .categorieNom(programme.getCategorie().getNom())
                .classeId(programme.getClasse().getId())
                .classeNom(programme.getClasse().getNom())
                .anneeExerciceId(anneeExerciceId)
                .anneeExercice(anneeExerciceStr)
                .statutActuelId(dernierHistorique != null ? dernierHistorique.getStatus().getId() : 1L)
                .statutActuelNom(dernierHistorique != null ? dernierHistorique.getStatus().getStatus() : "EN ATTENTE")
                .nombreChangements(nombreChangements.intValue())
                .datePremiereCP(datePremiereCP != null ? datePremiereCP.toString() : null)
                .dateDerniereCP(dateDerniereCP != null ? dateDerniereCP.toString() : null)
                .dateChangement(dernierHistorique != null ? dernierHistorique.getCreatedAt().toString() : null)
                .estDemarre(estDemarre)
                .estTermine(estTermine)
                .pourcentageAvancement(pourcentage)
                .historique(historiqueDtos)
                .build();
    }
    
    /**
     * Récupère la progression annuelle de tous les programmes
     * Si anneeExerciceId est null, retourne pour toutes les années
     */
    @Transactional(readOnly = true)
    public List<ProgressionAnnuelleDto> getProgressionAnnuelle(Long anneeExerciceId) {
        log.info("Récupération progression annuelle: année={}", anneeExerciceId);
        
        // Récupérer tous les programmes
        List<Programme> tousProgrammes = programmeRepository.findAll();
        
        // Récupérer tous les historiques de l'année
        List<HistoriqueProgramme> historiques;
        if (anneeExerciceId != null) {
            historiques = historiqueProgrammeRepository.findByAnneeExerciceIdOrderByCreatedAtAsc(anneeExerciceId);
        } else {
            historiques = historiqueProgrammeRepository.findAll();
        }
        
        // Récupérer l'année d'exercice
        AnneeExercice anneeExercice = null;
        if (anneeExerciceId != null) {
            anneeExercice = anneeExerciceRepository.findById(anneeExerciceId)
                    .orElse(null);
        }
        
        // Calculer le statut final de chaque programme
        Map<Long, ProgrammeStatus> statutsFinaux = new HashMap<>();
        Map<Long, Long> nombreChangementsMap = new HashMap<>();
        
        // Récupérer le statut "En attente" par défaut
        ProgrammeStatus statutEnAttente = programmeStatusRepository.findByStatus("En attente")
                .orElseThrow(() -> new RuntimeException("Statut 'En attente' introuvable"));
        
        // Initialiser tous les programmes à "En attente"
        for (Programme p : tousProgrammes) {
            statutsFinaux.put(p.getId(), statutEnAttente);
            nombreChangementsMap.put(p.getId(), 0L);
        }
        
        // Mettre à jour avec les statuts réels depuis l'historique
        for (HistoriqueProgramme h : historiques) {
            Long programmeId = h.getProgramme().getId();
            if (h.getStatus() != null) {
                statutsFinaux.put(programmeId, h.getStatus());
                nombreChangementsMap.put(programmeId, 
                    nombreChangementsMap.getOrDefault(programmeId, 0L) + 1);
            }
        }
        
        // Construire les DTOs
        final AnneeExercice annee = anneeExercice;
        return tousProgrammes.stream()
                .map(programme -> {
                    ProgrammeStatus statutFinal = statutsFinaux.get(programme.getId());
                    Long nbChangements = nombreChangementsMap.get(programme.getId());
                    
                    return ProgressionAnnuelleDto.builder()
                            .programmeId(programme.getId())
                            .programmeNom(programme.getNom())
                            .categorieId(programme.getCategorie().getId())
                            .categorieNom(programme.getCategorie().getNom())
                            .classeId(programme.getClasse().getId())
                            .classeNom(programme.getClasse().getNom())
                            .anneeExercice(annee != null ? annee.getAnnee().toString() : "")
                            .statutFinalId(statutFinal.getId())
                            .statutFinalNom(statutFinal.getStatus())
                            .nombreChangements(nbChangements)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les statistiques pour toutes les années ou une année spécifique
     */
    @Transactional(readOnly = true)
    public List<StatistiquesAnnuellesDto> getToutesStatistiques(Long anneeExerciceId) {
        log.info("Récupération statistiques: année={}", anneeExerciceId);
        
        if (anneeExerciceId != null) {
            // Retourner les stats d'une seule année
            return List.of(getStatistiquesAnnuelles(anneeExerciceId));
        } else {
            // Retourner les stats de toutes les années
            List<ProgrammeProgressionAnnuelle> toutesProgressions = progressionAnnuelleRepository.findAll();
            
            // Grouper par année
            Map<Long, List<ProgrammeProgressionAnnuelle>> parAnnee = toutesProgressions.stream()
                    .collect(Collectors.groupingBy(p -> p.getAnneeExercice().getId()));
            
            return parAnnee.keySet().stream()
                    .map(this::getStatistiquesAnnuelles)
                    .collect(Collectors.toList());
        }
    }
    
    /**
     * Récupère l'avancement de tous les programmes
     * Si anneeExerciceId est null, retourne pour toutes les années
     */
    @Transactional(readOnly = true)
    public List<ProgrammeAvancementDto> getTousAvancementProgrammes(
            Long anneeExerciceId, 
            Long classeId, 
            Long categorieId) {
        log.info("Récupération tous avancements: année={}, classe={}, catégorie={}", 
                anneeExerciceId, classeId, categorieId);
        
        if (anneeExerciceId != null) {
            // Retourner l'avancement pour une année spécifique
            return getAvancementProgrammes(anneeExerciceId, classeId, categorieId);
        } else {
            // Récupérer tous les programmes avec filtres
            List<Programme> programmes = programmeRepository.findAll();
            
            // Appliquer les filtres
            if (classeId != null) {
                programmes = programmes.stream()
                        .filter(p -> p.getClasse().getId().equals(classeId))
                        .collect(Collectors.toList());
            }
            
            if (categorieId != null) {
                programmes = programmes.stream()
                        .filter(p -> p.getCategorie().getId().equals(categorieId))
                        .collect(Collectors.toList());
            }
            
            // Pour chaque programme, récupérer son avancement pour toutes les années
            return programmes.stream()
                    .flatMap(programme -> {
                        // Trouver toutes les années où ce programme a été utilisé
                        List<HistoriqueProgramme> historiques = historiqueProgrammeRepository
                                .findByProgrammeIdOrderByCreatedAtAsc(programme.getId());
                        
                        return historiques.stream()
                                .map(h -> h.getAnneeExercice().getId())
                                .distinct()
                                .map(anneeId -> calculerAvancement(programme, anneeId));
                    })
                    .collect(Collectors.toList());
        }
    }
    
    /**
     * Mapper de HistoriqueProgramme vers DTO
     */
    private HistoriqueProgrammeDto mapToDto(HistoriqueProgramme historique) {
        // Gérer le cas où classe_progressive_id est NULL (initialisation annuelle)
        Long cpId = null;
        String cpDate = null;
        String anneeExercice = null;
        
        if (historique.getClasseProgressive() != null) {
            // Historique lié à une CP spécifique
            cpId = historique.getClasseProgressive().getId();
            cpDate = historique.getClasseProgressive().getDateCp().toString();
            anneeExercice = historique.getClasseProgressive().getAnneeExercice().getAnnee().toString();
        } else {
            // Historique d'initialisation (début d'année)
            // Utiliser la date de début de l'année d'exercice
            cpDate = historique.getAnneeExercice().getAnnee().toString();
            anneeExercice = historique.getAnneeExercice().getAnnee().toString();
        }
        
        return HistoriqueProgrammeDto.builder()
                .id(historique.getId())
                .programmeId(historique.getProgramme().getId())
                .programmeNom(historique.getProgramme().getNom())
                .classeProgressiveId(cpId)
                .classeProgressiveDate(cpDate)
                .statusId(historique.getStatus().getId())
                .statusNom(historique.getStatus().getStatus())
                .anneeExercice(anneeExercice)
                .dateChangement(historique.getCreatedAt())
                .build();
    }
    
    /**
     * Mapper de ProgrammeProgressionAnnuelle vers DTO
     */
    private ProgressionAnnuelleDto mapProgressionToDto(ProgrammeProgressionAnnuelle progression) {
        // Compter le nombre de changements pour ce programme/année
        Long nombreChangements = (long) historiqueProgrammeRepository
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
    
    /**
     * Initialise tous les programmes au statut "EN ATTENTE" pour une année d'exercice.
     * Cette méthode ne peut être appelée qu'une seule fois par année.
     * Seul le Directeur peut effectuer cette action.
     * 
     * @param anneeExerciceId ID de l'année d'exercice
     * @return Nombre de programmes initialisés
     * @throws IllegalArgumentException si l'année n'existe pas
     * @throws IllegalStateException si les statuts ont déjà été initialisés
     */
    @Transactional
    public int initialiserStatutsAnnuels(Long anneeExerciceId) {
        log.info("Initialisation des statuts pour l'année d'exercice ID={}", anneeExerciceId);
        
        // Vérifier que l'année existe
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(anneeExerciceId)
                .orElseThrow(() -> new IllegalArgumentException("Année d'exercice non trouvée: " + anneeExerciceId));
        
        // Vérifier que les statuts n'ont pas déjà été initialisés
        if (Boolean.TRUE.equals(anneeExercice.getStatutsInitialises())) {
            throw new IllegalStateException("Les statuts ont déjà été initialisés pour l'année " + anneeExercice.getAnnee().getYear());
        }
        
        // Récupérer le statut "En attente" (ID = 1)
        ProgrammeStatus statutEnAttente = programmeStatusRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Statut 'En attente' non trouvé"));
        
        // Récupérer tous les programmes actifs
        List<Programme> programmes = programmeRepository.findAll();
        
        log.info("Initialisation de {} programmes au statut 'En attente'", programmes.size());
        
        // Créer une entrée d'historique pour chaque programme
        int count = 0;
        for (Programme programme : programmes) {
            HistoriqueProgramme historique = HistoriqueProgramme.builder()
                    .programme(programme)
                    .classeProgressive(null) // Pas de CP spécifique pour l'initialisation
                    .status(statutEnAttente)
                    .anneeExercice(anneeExercice)
                    .build();
            
            historiqueProgrammeRepository.save(historique);
            count++;
        }
        
        // Marquer l'année comme initialisée
        anneeExercice.setStatutsInitialises(true);
        anneeExerciceRepository.save(anneeExercice);
        
        // Journaliser l'action
        journalService.logAction(
                String.format("Initialisation des statuts pour l'année %d - %d programmes initialisés à 'En attente'",
                        anneeExercice.getAnnee().getYear(), count)
        );
        
        log.info("Initialisation terminée: {} programmes initialisés", count);
        return count;
    }
}
