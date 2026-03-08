package com.explorateur.backend.service;

import com.explorateur.backend.dto.ChangeProgrammeStatusRequest;
import com.explorateur.backend.dto.HistoriqueProgrammesResponse;
import com.explorateur.backend.dto.ProgrammeStatusResponse;
import com.explorateur.backend.entity.*;
import com.explorateur.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des statuts de programme (NOUVEAU SYSTÈME)
 * Utilise HistoriqueProgramme et ProgrammeProgressionAnnuelle
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProgrammeStatusService {
    
    private final ProgrammeStatusRepository programmeStatusRepository;
    private final HistoriqueProgrammeRepository historiqueProgrammeRepository;
    private final ProgrammeProgressionAnnuelleRepository progressionAnnuelleRepository;
    private final ClasseProgressiveRepository classeProgressiveRepository;
    private final ProgrammeRepository programmeRepository;
    private final HistoriqueProgrammeService historiqueProgrammeService;
    
    // Constantes pour les statuts
    private static final String STATUS_EN_ATTENTE = "En attente";
    private static final String STATUS_EN_COURS = "En cours";
    private static final String STATUS_TERMINE = "Terminé";
    
    /**
     * Obtenir tous les statuts disponibles
     */
    @Transactional(readOnly = true)
    public List<ProgrammeStatusResponse> getAllStatuts() {
        log.info("Récupération de tous les statuts de programme");
        
        return programmeStatusRepository.findAll()
                .stream()
                .map(this::mapStatusToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Changer le statut d'un programme dans une CP
     * NOUVELLE VERSION avec validation complète selon les règles métier
     */
    @Transactional
    public HistoriqueProgrammesResponse changeProgrammeStatus(ChangeProgrammeStatusRequest request) {
        log.info("Changement de statut du programme ID: {} dans CP ID: {} vers statut ID: {}", 
                 request.getProgrammeId(), request.getClasseProgressiveId(), request.getNewStatusId());
        
        // Vérifier que le programme existe
        Programme programme = programmeRepository.findById(request.getProgrammeId())
                .orElseThrow(() -> new RuntimeException("Programme introuvable"));
        
        // Vérifier que la CP existe
        ClasseProgressive cp = classeProgressiveRepository.findById(request.getClasseProgressiveId())
                .orElseThrow(() -> new RuntimeException("Classe Progressive introuvable"));
        
        // RÈGLE MÉTIER 1: Vérifier que la CP n'est pas clôturée
        if (cp.getEtat() != null && cp.getEtat() == 1) {
            throw new RuntimeException("Cette Classe Progressive est clôturée. Modification de statut interdite.");
        }
        
        // Vérifier que le nouveau statut existe
        ProgrammeStatus newStatus = programmeStatusRepository.findById(request.getNewStatusId())
                .orElseThrow(() -> new RuntimeException("Statut introuvable"));
        
        Long anneeExerciceId = cp.getAnneeExercice().getId();
        
        // RÈGLE MÉTIER 2: Vérifier que le programme n'est pas déjà TERMINÉ pour cette année
        boolean dejaTermine = historiqueProgrammeRepository
                .isProgrammeTerminePourAnnee(request.getProgrammeId(), anneeExerciceId);
        
        if (dejaTermine) {
            throw new RuntimeException("Ce programme est déjà TERMINÉ pour cette année. Modification interdite.");
        }
        
        // Récupérer le statut actuel du programme pour cette année
        List<HistoriqueProgramme> historiquesAnnee = historiqueProgrammeRepository
                .findLatestByProgrammeAndAnnee(request.getProgrammeId(), anneeExerciceId);
        
        String currentStatusName = historiquesAnnee.isEmpty() ? null 
                : historiquesAnnee.get(0).getStatus().getStatus();
        
        // Valider la transition de statut
        validateStatusTransition(currentStatusName, newStatus.getStatus());
        
        // Enregistrer le changement dans l'historique via le service dédié
        historiqueProgrammeService.enregistrerChangementStatut(
                request.getProgrammeId(), 
                request.getClasseProgressiveId(), 
                request.getNewStatusId()
        );
        
        // Récupérer l'historique créé pour le retour
        List<HistoriqueProgramme> dernierHistorique = historiqueProgrammeRepository
                .findByClasseProgressiveIdOrderByCreatedAtAsc(request.getClasseProgressiveId());
        
        HistoriqueProgramme saved = dernierHistorique.isEmpty() ? null 
                : dernierHistorique.get(dernierHistorique.size() - 1);
        
        return mapHistoriqueToResponseNew(saved);
    }
    
    /**
     * Valider la transition de statut selon les règles métier
     * RÈGLE MÉTIER 3: Un programme TERMINÉ ne peut plus être modifié
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        // Si c'est le premier statut, il peut être n'importe lequel (généralement "En cours" lors de l'ajout)
        if (currentStatus == null) {
            return;
        }
        
        // Une fois "Terminé", on ne peut plus changer de statut
        if (STATUS_TERMINE.equals(currentStatus)) {
            throw new RuntimeException("Un programme terminé ne peut plus changer de statut");
        }
        
        // On ne peut pas revenir à "En attente" si on est déjà "En cours"
        if (STATUS_EN_ATTENTE.equals(newStatus) && STATUS_EN_COURS.equals(currentStatus)) {
            throw new RuntimeException("Un programme 'En cours' ne peut pas revenir à 'En attente'");
        }
    }
    
    /**
     * Mapper un statut vers un DTO de réponse
     */
    private ProgrammeStatusResponse mapStatusToResponse(ProgrammeStatus status) {
        return ProgrammeStatusResponse.builder()
                .id(status.getId())
                .status(status.getStatus())
                .build();
    }
    
    /**
     * Mapper un historique (nouveau système) vers un DTO de réponse
     */
    private HistoriqueProgrammesResponse mapHistoriqueToResponseNew(HistoriqueProgramme historique) {
        if (historique == null) {
            return null;
        }
        
        return HistoriqueProgrammesResponse.builder()
                .id(historique.getId())
                .programmeId(historique.getProgramme() != null ? historique.getProgramme().getId() : null)
                .programmeNom(historique.getProgramme() != null ? historique.getProgramme().getNom() : null)
                .classeProgressiveId(historique.getClasseProgressive() != null ? historique.getClasseProgressive().getId() : null)
                .statusId(historique.getStatus() != null ? historique.getStatus().getId() : null)
                .statusNom(historique.getStatus() != null ? historique.getStatus().getStatus() : null)
                .createdAt(historique.getCreatedAt())
                .build();
    }
}
