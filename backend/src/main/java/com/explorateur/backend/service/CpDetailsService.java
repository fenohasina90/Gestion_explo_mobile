package com.explorateur.backend.service;

import com.explorateur.backend.dto.AddProgrammeToCpRequest;
import com.explorateur.backend.dto.CpDetailsResponse;
import com.explorateur.backend.dto.UpdateCpDetailsInstructeurRequest;
import com.explorateur.backend.entity.*;
import com.explorateur.backend.repository.*;
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
    private final ProgrammeStatusService programmeStatusService;
    private final HistoriqueProgrammesRepository historiqueProgrammesRepository;
    
    /**
     * Ajouter un programme à une CP
     */
    @Transactional
    public CpDetailsResponse addProgrammeToCP(AddProgrammeToCpRequest request) {
        log.info("Ajout du programme ID: {} à la CP ID: {}", request.getProgrammeId(), request.getClasseProgressiveId());
        
        // Vérifier que la CP existe
        ClasseProgressive cp = cpRepository.findById(request.getClasseProgressiveId())
                .orElseThrow(() -> new RuntimeException("CP non trouvée avec l'ID: " + request.getClasseProgressiveId()));
        
        // Vérifier que le programme existe
        Programme programme = programmeRepository.findById(request.getProgrammeId())
                .orElseThrow(() -> new RuntimeException("Programme non trouvé avec l'ID: " + request.getProgrammeId()));
        
        // Règle métier: Un même programme ne peut être ajouté qu'une seule fois dans la même CP
        if (cpDetailsRepository.existsByClasseProgressiveIdAndProgrammeId(
                request.getClasseProgressiveId(), request.getProgrammeId())) {
            throw new RuntimeException("Ce programme est déjà ajouté à cette CP");
        }
        
        // Vérifier l'instructeur si spécifié
        Instructeur instructeur = null;
        if (request.getInstructeurId() != null) {
            instructeur = instructeurRepository.findById(request.getInstructeurId())
                    .orElseThrow(() -> new RuntimeException("Instructeur non trouvé avec l'ID: " + request.getInstructeurId()));
        }
        
        // Créer l'affectation
        CpDetails cpDetails = CpDetails.builder()
                .classeProgressive(cp)
                .programme(programme)
                .instructeur(instructeur)
                .build();
        
        CpDetails savedCpDetails = cpDetailsRepository.save(cpDetails);
        log.info("Programme ajouté à la CP avec l'ID: {}", savedCpDetails.getId());
        
        // Règle métier: Lors de l'ajout, le statut est automatiquement "En attente"
        programmeStatusService.initializeProgrammeStatus(
                request.getProgrammeId(), 
                request.getClasseProgressiveId());
        
        return mapToResponse(savedCpDetails);
    }
    
    /**
     * Modifier l'instructeur d'un programme dans une CP
     */
    @Transactional
    public CpDetailsResponse updateInstructeur(Long cpDetailsId, UpdateCpDetailsInstructeurRequest request) {
        log.info("Modification de l'instructeur pour le cp_details ID: {}", cpDetailsId);
        
        CpDetails cpDetails = cpDetailsRepository.findById(cpDetailsId)
                .orElseThrow(() -> new RuntimeException("Affectation non trouvée avec l'ID: " + cpDetailsId));
        
        // Vérifier l'instructeur si spécifié
        Instructeur instructeur = null;
        if (request.getInstructeurId() != null) {
            instructeur = instructeurRepository.findById(request.getInstructeurId())
                    .orElseThrow(() -> new RuntimeException("Instructeur non trouvé avec l'ID: " + request.getInstructeurId()));
        }
        
        cpDetails.setInstructeur(instructeur);
        CpDetails updatedCpDetails = cpDetailsRepository.save(cpDetails);
        log.info("Instructeur modifié avec succès pour cp_details ID: {}", cpDetailsId);
        
        return mapToResponse(updatedCpDetails);
    }
    
    /**
     * Supprimer un programme d'une CP
     */
    @Transactional
    public void removeProgrammeFromCP(Long cpDetailsId) {
        log.info("Suppression du programme de la CP, cp_details ID: {}", cpDetailsId);
        
        CpDetails cpDetails = cpDetailsRepository.findById(cpDetailsId)
                .orElseThrow(() -> new RuntimeException("Affectation non trouvée avec l'ID: " + cpDetailsId));
        
        // Règle métier: Suppression impossible si statut = Terminé
        if (cpDetailsRepository.isProgrammeTermine(
                cpDetails.getClasseProgressive().getId(), 
                cpDetails.getProgramme().getId())) {
            throw new RuntimeException("Impossible de supprimer ce programme: il est déjà terminé");
        }
        
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
        
        return cpDetailsRepository.findByClasseProgressiveId(classeProgressiveId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer un détail par ID
     */
    @Transactional(readOnly = true)
    public CpDetailsResponse getCpDetailsById(Long id) {
        log.info("Récupération du cp_details ID: {}", id);
        CpDetails cpDetails = cpDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation non trouvée avec l'ID: " + id));
        return mapToResponse(cpDetails);
    }
    
    // ========== MÉTHODE DE MAPPING ==========
    
    /**
     * Mapper une entité CpDetails vers un DTO de réponse
     */
    private CpDetailsResponse mapToResponse(CpDetails cpDetails) {
        // Récupérer le statut actuel du programme dans cette CP
        String statutActuel = getStatutActuel(
                cpDetails.getProgramme().getId(), 
                cpDetails.getClasseProgressive().getId());
        
        return CpDetailsResponse.builder()
                .id(cpDetails.getId())
                .classeProgressiveId(cpDetails.getClasseProgressive().getId())
                .programmeId(cpDetails.getProgramme().getId())
                .programmeName(cpDetails.getProgramme().getNom())
                .categorieId(cpDetails.getProgramme().getCategorie().getId())
                .categorieName(cpDetails.getProgramme().getCategorie().getNom())
                .instructeurId(cpDetails.getInstructeur() != null ? cpDetails.getInstructeur().getId() : null)
                .instructeurName(cpDetails.getInstructeur() != null ? 
                        cpDetails.getInstructeur().getNom() + " " + cpDetails.getInstructeur().getPrenom() : null)
                .statutActuel(statutActuel)
                .createdAt(cpDetails.getCreatedAt())
                .build();
    }
    
    /**
     * Récupérer le statut actuel d'un programme dans une CP
     */
    private String getStatutActuel(Long programmeId, Long cpId) {
        return historiqueProgrammesRepository.findLatestByProgrammeAndCP(programmeId, cpId)
                .map(historique -> historique.getStatus().getStatus())
                .orElse("Non défini");
    }
}
