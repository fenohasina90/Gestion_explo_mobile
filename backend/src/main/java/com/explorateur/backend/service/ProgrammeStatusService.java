package com.explorateur.backend.service;

import com.explorateur.backend.dto.ChangeProgrammeStatusRequest;
import com.explorateur.backend.dto.HistoriqueProgrammesResponse;
import com.explorateur.backend.dto.ProgrammeStatusResponse;
import com.explorateur.backend.entity.HistoriqueProgrammes;
import com.explorateur.backend.entity.Programme;
import com.explorateur.backend.entity.ProgrammeStatus;
import com.explorateur.backend.repository.HistoriqueProgrammesRepository;
import com.explorateur.backend.repository.ProgrammeRepository;
import com.explorateur.backend.repository.ProgrammeStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des statuts de programme
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProgrammeStatusService {
    
    private final ProgrammeStatusRepository programmeStatusRepository;
    private final HistoriqueProgrammesRepository historiqueProgrammesRepository;
    private final ProgrammeRepository programmeRepository;
    
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
     */
    @Transactional
    public HistoriqueProgrammesResponse changeProgrammeStatus(ChangeProgrammeStatusRequest request) {
        log.info("Changement de statut du programme ID: {} dans CP ID: {} vers statut ID: {}", 
                 request.getProgrammeId(), request.getClasseProgressiveId(), request.getNewStatusId());
        
        // Vérifier que le programme existe
        Programme programme = programmeRepository.findById(request.getProgrammeId())
                .orElseThrow(() -> new RuntimeException("Programme introuvable"));
        
        // Vérifier que le nouveau statut existe
        ProgrammeStatus newStatus = programmeStatusRepository.findById(request.getNewStatusId())
                .orElseThrow(() -> new RuntimeException("Statut introuvable"));
        
        // Récupérer le statut actuel du programme dans cette CP
        Optional<HistoriqueProgrammes> currentHistorique = historiqueProgrammesRepository
                .findLatestByProgrammeAndCP(request.getProgrammeId(), request.getClasseProgressiveId());
        
        String currentStatusName = currentHistorique
                .map(h -> h.getStatus().getStatus())
                .orElse(null);
        
        // Appliquer les règles métier
        validateStatusTransition(currentStatusName, newStatus.getStatus());
        
        // Créer l'entrée dans l'historique
        HistoriqueProgrammes historique = HistoriqueProgrammes.builder()
                .programme(programme)
                .classeProgressiveId(request.getClasseProgressiveId())
                .status(newStatus)
                .build();
        
        HistoriqueProgrammes saved = historiqueProgrammesRepository.save(historique);
        
        return mapHistoriqueToResponse(saved);
    }
    
    /**
     * Initialiser le statut d'un programme dans une CP (statut "En attente")
     */
    @Transactional
    public HistoriqueProgrammesResponse initializeProgrammeStatus(Long programmeId, Long classeProgressiveId) {
        log.info("Initialisation du statut du programme ID: {} dans CP ID: {}", programmeId, classeProgressiveId);
        
        // Vérifier que le programme existe
        Programme programme = programmeRepository.findById(programmeId)
                .orElseThrow(() -> new RuntimeException("Programme introuvable"));
        
        // Récupérer le statut "En attente"
        ProgrammeStatus statusEnAttente = programmeStatusRepository.findByStatus(STATUS_EN_ATTENTE)
                .orElseThrow(() -> new RuntimeException("Statut 'En attente' introuvable"));
        
        // Vérifier qu'aucun statut n'existe déjà pour ce programme dans cette CP
        Optional<HistoriqueProgrammes> existing = historiqueProgrammesRepository
                .findLatestByProgrammeAndCP(programmeId, classeProgressiveId);
        
        if (existing.isPresent()) {
            throw new RuntimeException("Ce programme a déjà un statut dans cette CP");
        }
        
        // Créer l'entrée initiale dans l'historique
        HistoriqueProgrammes historique = HistoriqueProgrammes.builder()
                .programme(programme)
                .classeProgressiveId(classeProgressiveId)
                .status(statusEnAttente)
                .build();
        
        HistoriqueProgrammes saved = historiqueProgrammesRepository.save(historique);
        
        return mapHistoriqueToResponse(saved);
    }
    
    /**
     * Obtenir l'historique d'un programme dans une CP
     */
    @Transactional(readOnly = true)
    public List<HistoriqueProgrammesResponse> getHistoriqueByProgrammeAndCP(Long programmeId, Long classeProgressiveId) {
        log.info("Récupération de l'historique du programme ID: {} dans CP ID: {}", programmeId, classeProgressiveId);
        
        return historiqueProgrammesRepository
                .findByProgrammeIdAndClasseProgressiveIdOrderByCreatedAtDesc(programmeId, classeProgressiveId)
                .stream()
                .map(this::mapHistoriqueToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtenir l'historique complet d'un programme (toutes CP)
     */
    @Transactional(readOnly = true)
    public List<HistoriqueProgrammesResponse> getHistoriqueByProgramme(Long programmeId) {
        log.info("Récupération de l'historique complet du programme ID: {}", programmeId);
        
        return historiqueProgrammesRepository
                .findByProgrammeIdOrderByCreatedAtDesc(programmeId)
                .stream()
                .map(this::mapHistoriqueToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtenir le statut actuel d'un programme dans une CP
     */
    @Transactional(readOnly = true)
    public HistoriqueProgrammesResponse getCurrentStatus(Long programmeId, Long classeProgressiveId) {
        log.info("Récupération du statut actuel du programme ID: {} dans CP ID: {}", programmeId, classeProgressiveId);
        
        HistoriqueProgrammes current = historiqueProgrammesRepository
                .findLatestByProgrammeAndCP(programmeId, classeProgressiveId)
                .orElseThrow(() -> new RuntimeException("Aucun statut trouvé pour ce programme dans cette CP"));
        
        return mapHistoriqueToResponse(current);
    }
    
    /**
     * Valider la transition de statut selon les règles métier
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        // Si c'est le premier statut (pas de statut actuel), il doit être "En attente"
        if (currentStatus == null) {
            if (!STATUS_EN_ATTENTE.equals(newStatus)) {
                throw new RuntimeException("Le statut initial d'un programme doit être 'En attente'");
            }
            return;
        }
        
        // Une fois "Terminé", on ne peut plus changer de statut
        if (STATUS_TERMINE.equals(currentStatus)) {
            throw new RuntimeException("Un programme terminé ne peut plus changer de statut");
        }
        
        // Pour passer à "Terminé", il faut d'abord être "En cours"
        if (STATUS_TERMINE.equals(newStatus) && !STATUS_EN_COURS.equals(currentStatus)) {
            throw new RuntimeException("Un programme ne peut passer à 'Terminé' que s'il est 'En cours'");
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
     * Mapper un historique vers un DTO de réponse
     */
    private HistoriqueProgrammesResponse mapHistoriqueToResponse(HistoriqueProgrammes historique) {
        return HistoriqueProgrammesResponse.builder()
                .id(historique.getId())
                .programmeId(historique.getProgramme() != null ? historique.getProgramme().getId() : null)
                .programmeNom(historique.getProgramme() != null ? historique.getProgramme().getNom() : null)
                .classeProgressiveId(historique.getClasseProgressiveId())
                .statusId(historique.getStatus() != null ? historique.getStatus().getId() : null)
                .statusNom(historique.getStatus() != null ? historique.getStatus().getStatus() : null)
                .createdAt(historique.getCreatedAt())
                .build();
    }
}
