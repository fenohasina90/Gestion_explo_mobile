package com.explorateur.backend.service;

import com.explorateur.backend.dto.AddProgrammeToCpRequest;
import com.explorateur.backend.dto.CpDetailsResponse;
import com.explorateur.backend.dto.UpdateCpDetailsInstructeurRequest;
import com.explorateur.backend.entity.*;
import com.explorateur.backend.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des affectations de programmes aux CP (cp_details)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CpDetailsService {
    
    private final CpDetailsRepository cpDetailsRepository;
    private final ClasseProgressiveRepository cpRepository;
    private final ProgrammeRepository programmeRepository;
    private final InstructeurRepository instructeurRepository;
    private final CpDetailsInstructeurRepository cpDetailsInstructeurRepository;
    private final ProgrammeStatusService programmeStatusService;
    private final HistoriqueProgrammesRepository historiqueProgrammesRepository;
    private final HistoriqueProgrammeRepository historiqueProgrammeRepository;
    private final EntityManager entityManager;
    private final JournalService journalService;
    private final HistoriqueProgrammeService historiqueProgrammeService;
    private final ProgrammeStatusRepository programmeStatusRepository;
    
    /**
     * Ajouter un programme ou une activité libre à une CP avec un ou plusieurs instructeurs
     */
    @Transactional
    public CpDetailsResponse addProgrammeToCP(AddProgrammeToCpRequest request) {
        log.info("Ajout à la CP ID: {} - Programme: {}, Description: '{}', Instructeurs: {}", 
                request.getClasseProgressiveId(), 
                request.getProgrammeId(),
                request.getDescription(),
                request.getInstructeurIds() != null ? request.getInstructeurIds().size() : 0);
        
        // Validation: soit programmeId soit description doit être fourni
        if (request.getProgrammeId() == null && (request.getDescription() == null || request.getDescription().trim().isEmpty())) {
            throw new RuntimeException("Vous devez fournir soit un programme (programmeId) soit une description pour l'activité libre");
        }
        
        // Si les deux sont fournis, on privilégie le programme
        if (request.getProgrammeId() != null && request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
            log.warn("Programme et description fournis - la description sera ignorée");
        }
        
        // Vérifier que la CP existe
        ClasseProgressive cp = cpRepository.findById(request.getClasseProgressiveId())
                .orElseThrow(() -> new RuntimeException("CP non trouvée avec l'ID: " + request.getClasseProgressiveId()));
        
        // RÈGLE MÉTIER 1: Vérifier que la CP n'est pas clôturée
        if (cp.getEtat() != null && cp.getEtat() == 1) {
            throw new RuntimeException("Cette Classe Progressive est clôturée. Ajout de programme interdit.");
        }
        
        Programme programme = null;
        
        // Si programmeId est fourni, vérifier que le programme existe
        if (request.getProgrammeId() != null) {
            programme = programmeRepository.findById(request.getProgrammeId())
                    .orElseThrow(() -> new RuntimeException("Programme non trouvé avec l'ID: " + request.getProgrammeId()));
            
            // Règle métier: Un même programme ne peut être ajouté qu'une seule fois dans la même CP
            if (cpDetailsRepository.existsByClasseProgressiveIdAndProgrammeId(
                    request.getClasseProgressiveId(), request.getProgrammeId())) {
                throw new RuntimeException("Ce programme est déjà ajouté à cette CP");
            }
            
            // RÈGLE MÉTIER 2: Vérifier que le programme n'est pas TERMINÉ pour l'année
            Long anneeExerciceId = cp.getAnneeExercice().getId();
            
            // Utiliser le repository pour vérifier si le programme est terminé
            boolean estTermine = historiqueProgrammeRepository
                    .isProgrammeTerminePourAnnee(request.getProgrammeId(), anneeExerciceId);
            
            if (estTermine) {
                throw new RuntimeException("Ce programme est déjà TERMINÉ pour cette année. Ajout interdit.");
            }
        }
        
        // Créer l'affectation (programme ou activité libre)
        CpDetails cpDetails = CpDetails.builder()
                .classeProgressive(cp)
                .programme(programme)
                .description(request.getProgrammeId() == null ? request.getDescription() : null)
                .build();
        
        CpDetails savedCpDetails = cpDetailsRepository.save(cpDetails);
        log.info("{} ajouté à la CP avec l'ID: {}", 
                programme != null ? "Programme" : "Activité libre", 
                savedCpDetails.getId());
        
        // Ajouter les instructeurs à la collection si spécifiés
        if (request.getInstructeurIds() != null && !request.getInstructeurIds().isEmpty()) {
            for (Long instructeurId : request.getInstructeurIds()) {
                // Vérifier que l'instructeur existe
                Instructeur instructeur = instructeurRepository.findById(instructeurId)
                        .orElseThrow(() -> new RuntimeException("Instructeur non trouvé avec l'ID: " + instructeurId));
                
                // Créer l'association et l'ajouter à la collection
                CpDetailsInstructeur cpDetailsInstructeur = CpDetailsInstructeur.builder()
                        .cpDetails(savedCpDetails)
                        .instructeur(instructeur)
                        .build();
                
                savedCpDetails.getInstructeurs().add(cpDetailsInstructeur);
                log.info("Instructeur ID: {} ajouté à la collection", instructeurId);
            }
            
            // Sauvegarder pour persister les instructeurs (cascade)
            savedCpDetails = cpDetailsRepository.save(savedCpDetails);
        }
        
        // RÈGLE MÉTIER 3: Lors de l'ajout d'un PROGRAMME, le statut passe automatiquement à "En cours"
        // Les activités libres n'ont pas de statut
        if (request.getProgrammeId() != null) {
            // Trouver le statut "En cours"
            ProgrammeStatus statusEnCours = programmeStatusRepository.findByStatus("En cours")
                    .orElseThrow(() -> new RuntimeException("Statut 'En cours' introuvable"));
            
            // Changer le statut vers "En cours" (transition automatique EN_ATTENTE → EN_COURS)
            com.explorateur.backend.dto.ChangeProgrammeStatusRequest statusRequest = 
                    new com.explorateur.backend.dto.ChangeProgrammeStatusRequest();
            statusRequest.setProgrammeId(request.getProgrammeId());
            statusRequest.setClasseProgressiveId(request.getClasseProgressiveId());
            statusRequest.setNewStatusId(statusEnCours.getId());
            
            programmeStatusService.changeProgrammeStatus(statusRequest);
            log.info("Statut du programme {} automatiquement changé à 'En cours'", request.getProgrammeId());
        }
        
        log.info("CpDetails créé avec {} instructeur(s)", 
                savedCpDetails.getInstructeurs() != null ? savedCpDetails.getInstructeurs().size() : 0);
        
        // Journalisation
        int nbInstructeurs = savedCpDetails.getInstructeurs() != null ? savedCpDetails.getInstructeurs().size() : 0;
        String message;
        if (savedCpDetails.getProgramme() != null) {
            message = "Ajout du programme " + savedCpDetails.getProgramme().getNom() + " a la CP du " + cp.getDateCp() + 
                    " avec " + nbInstructeurs + " instructeur(s)";
        } else {
            message = "Ajout de l'activite libre '" + savedCpDetails.getDescription() + "' a la CP du " + cp.getDateCp() + 
                    " avec " + nbInstructeurs + " instructeur(s)";
        }
        journalService.logAction(message);
        
        return mapToResponse(savedCpDetails);
    }
    
    /**
     * Modifier les instructeurs d'un programme dans une CP (remplace tous les instructeurs existants)
     */
    @Transactional
    public CpDetailsResponse updateInstructeur(Long cpDetailsId, UpdateCpDetailsInstructeurRequest request) {
        log.info("Modification des instructeurs pour le cp_details ID: {}", cpDetailsId);
        
        CpDetails cpDetails = cpDetailsRepository.findByIdWithInstructeurs(cpDetailsId)
                .orElseThrow(() -> new RuntimeException("Affectation non trouvée avec l'ID: " + cpDetailsId));
        
        // RÈGLE MÉTIER: Vérifier que la CP n'est pas clôturée
        if (cpDetails.getClasseProgressive().getEtat() != null && cpDetails.getClasseProgressive().getEtat() == 1) {
            throw new RuntimeException("Cette Classe Progressive est clôturée. Modification des instructeurs interdite.");
        }
        
        // Vider la collection (orphanRemoval supprimera automatiquement les entrées en base)
        cpDetails.getInstructeurs().clear();
        
        // Forcer le flush pour exécuter les suppressions avant les insertions
        entityManager.flush();
        log.info("Suppressions des anciens instructeurs effectuées");
        
        // Ajouter les nouveaux instructeurs à la collection
        if (request.getInstructeurIds() != null && !request.getInstructeurIds().isEmpty()) {
            for (Long instructeurId : request.getInstructeurIds()) {
                // Vérifier que l'instructeur existe
                Instructeur instructeur = instructeurRepository.findById(instructeurId)
                        .orElseThrow(() -> new RuntimeException("Instructeur non trouvé avec l'ID: " + instructeurId));
                
                // Créer l'association et l'ajouter à la collection
                CpDetailsInstructeur cpDetailsInstructeur = CpDetailsInstructeur.builder()
                        .cpDetails(cpDetails)
                        .instructeur(instructeur)
                        .build();
                
                cpDetails.getInstructeurs().add(cpDetailsInstructeur);
                log.info("Instructeur ID: {} ajouté à la collection", instructeurId);
            }
        }
        
        // Sauvegarder (cascade persistera les nouvelles associations)
        cpDetailsRepository.save(cpDetails);
        
        log.info("Instructeurs modifiés avec succès pour cp_details ID: {}", cpDetailsId);
        
        // Journalisation
        int nbInstructeurs = cpDetails.getInstructeurs() != null ? cpDetails.getInstructeurs().size() : 0;
        String nomActivite = cpDetails.getProgramme() != null ? 
                cpDetails.getProgramme().getNom() : 
                "l'activite libre '" + cpDetails.getDescription() + "'";
        journalService.logAction("Modification des instructeurs pour " + nomActivite + 
                " (" + nbInstructeurs + " instructeur(s))");
        
        return mapToResponse(cpDetails);
    }
    
    /**
     * Supprimer un programme d'une CP
     */
    @Transactional
    public void removeProgrammeFromCP(Long cpDetailsId) {
        log.info("Suppression du programme de la CP, cp_details ID: {}", cpDetailsId);
        
        CpDetails cpDetails = cpDetailsRepository.findById(cpDetailsId)
                .orElseThrow(() -> new RuntimeException("Affectation non trouvée avec l'ID: " + cpDetailsId));
        
        // RÈGLE MÉTIER 1: Vérifier que la CP n'est pas clôturée
        if (cpDetails.getClasseProgressive().getEtat() != null && cpDetails.getClasseProgressive().getEtat() == 1) {
            throw new RuntimeException("Cette Classe Progressive est clôturée. Suppression de programme interdite.");
        }
        
        // RÈGLE MÉTIER 2: Suppression impossible si statut = Terminé
        if (cpDetailsRepository.isProgrammeTermine(
                cpDetails.getClasseProgressive().getId(), 
                cpDetails.getProgramme().getId())) {
            throw new RuntimeException("Impossible de supprimer ce programme: il est déjà terminé");
        }
        
        // Journalisation avant suppression
        String nomActivite = cpDetails.getProgramme() != null ? 
                cpDetails.getProgramme().getNom() : 
                "l'activite libre '" + cpDetails.getDescription() + "'";
        journalService.logAction("Retrait de " + nomActivite + " de la CP du " + 
                cpDetails.getClasseProgressive().getDateCp());
        
        cpDetailsRepository.delete(cpDetails);
        log.info("Programme retiré de la CP avec succès");
    }
    
    /**
     * Voir la liste des programmes d'une CP
     */
    @Transactional(readOnly = true)
    public List<CpDetailsResponse> getProgrammesByCP(Long classeProgressiveId) {
        log.info("Récupération des programmes de la CP ID: {}", classeProgressiveId);
        
        // Vérifier que la CP existe
        cpRepository.findById(classeProgressiveId)
                .orElseThrow(() -> new RuntimeException("CP non trouvée avec l'ID: " + classeProgressiveId));
        
        List<CpDetails> cpDetailsList = cpDetailsRepository.findByClasseProgressiveIdWithInstructeurs(classeProgressiveId);
        
        return cpDetailsList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer un détail par ID
     */
    @Transactional(readOnly = true)
    public CpDetailsResponse getCpDetailsById(Long id) {
        log.info("Récupération du cp_details ID: {}", id);
        CpDetails cpDetails = cpDetailsRepository.findByIdWithInstructeurs(id)
                .orElseThrow(() -> new RuntimeException("Affectation non trouvée avec l'ID: " + id));
        
        return mapToResponse(cpDetails);
    }
    
    // ========== MÉTHODE DE MAPPING ==========
    
    /**
     * Mapper une entité CpDetails vers un DTO de réponse
     */
    private CpDetailsResponse mapToResponse(CpDetails cpDetails) {
        log.debug("Mapping CpDetails ID: {} - Programme: {} - Nombre d'instructeurs: {}", 
                cpDetails.getId(),
                cpDetails.getProgramme() != null ? cpDetails.getProgramme().getId() : "null (activité libre)",
                cpDetails.getInstructeurs() != null ? cpDetails.getInstructeurs().size() : "null");
        
        // Variables pour les infos du programme (null si activité libre)
        Long programmeId = null;
        String programmeNom = null;
        String programmeDescription = null;
        Long categorieId = null;
        String categorieName = null;
        Long classeId = null;
        String classeNom = null;
        Long statusId = null;
        String statusNom = null;
        
        // Si c'est un programme (pas une activité libre)
        if (cpDetails.getProgramme() != null) {
            programmeId = cpDetails.getProgramme().getId();
            programmeNom = cpDetails.getProgramme().getNom();
            programmeDescription = cpDetails.getProgramme().getDescription();
            categorieId = cpDetails.getProgramme().getCategorie().getId();
            categorieName = cpDetails.getProgramme().getCategorie().getNom();
            
            // Récupérer la classe si elle existe
            if (cpDetails.getProgramme().getClasse() != null) {
                classeId = cpDetails.getProgramme().getClasse().getId();
                classeNom = cpDetails.getProgramme().getClasse().getNom();
            }
            
            // Récupérer le statut actuel du programme dans cette CP
            var statusInfo = getStatutActuel(
                    cpDetails.getProgramme().getId(), 
                    cpDetails.getClasseProgressive().getId());
            statusId = statusInfo.statusId();
            statusNom = statusInfo.statusNom();
        }
        
        // Mapper les instructeurs avec nom et prénom séparés
        List<CpDetailsResponse.InstructeurSimpleDto> instructeurs = cpDetails.getInstructeurs()
                .stream()
                .map(cdi -> CpDetailsResponse.InstructeurSimpleDto.builder()
                        .id(cdi.getInstructeur().getId())
                        .nom(cdi.getInstructeur().getNom())
                        .prenom(cdi.getInstructeur().getPrenom())
                        .nomComplet(cdi.getInstructeur().getNom() + " " + cdi.getInstructeur().getPrenom())
                        .build())
                .collect(Collectors.toList());
        
        log.debug("Instructeurs mappés: {}", instructeurs.size());
        
        return CpDetailsResponse.builder()
                .id(cpDetails.getId())
                .classeProgressiveId(cpDetails.getClasseProgressive().getId())
                .classeProgressiveDate(cpDetails.getClasseProgressive().getDateCp().toString())
                .programmeId(programmeId)
                .programmeNom(programmeNom)
                .programmeDescription(programmeDescription)
                .categorieId(categorieId)
                .categorieName(categorieName)
                .classeId(classeId)
                .classeNom(classeNom)
                .description(cpDetails.getDescription())
                .instructeurs(instructeurs)
                .statusId(statusId)
                .statusNom(statusNom)
                .createdAt(cpDetails.getCreatedAt())
                .build();
    }
    
    /**
     * Récupérer le statut actuel d'un programme dans une CP
     */
    private StatusInfo getStatutActuel(Long programmeId, Long cpId) {
        return historiqueProgrammesRepository.findLatestByProgrammeAndCP(programmeId, cpId)
                .map(historique -> new StatusInfo(
                        historique.getStatus().getId(),
                        historique.getStatus().getStatus()
                ))
                .orElse(new StatusInfo(null, "Non défini"));
    }
    
    /**
     * Record pour retourner statusId et statusNom
     */
    private record StatusInfo(Long statusId, String statusNom) {}
}
