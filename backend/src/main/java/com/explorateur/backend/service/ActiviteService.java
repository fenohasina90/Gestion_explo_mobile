package com.explorateur.backend.service;

import com.explorateur.backend.dto.*;
import com.explorateur.backend.entity.*;
import com.explorateur.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActiviteService {
    
    private final ActiviteRepository activiteRepository;
    private final DetailActiviteRepository detailActiviteRepository;
    private final BudgetGlobalRepository budgetGlobalRepository;
    private final ActiviteStatusRepository activiteStatusRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JournalService journalService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    /**
     * Créer une activité avec ses détails
     */
    @Transactional
    public ActiviteResponse createActivite(CreateActiviteRequest request, String currentUsername) {
        log.info("Création d'une activité: {}", request.getNom());
        
        // Vérifier l'utilisateur actuel
        Utilisateur currentUser = utilisateurRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Vérifier que le budget global existe
        BudgetGlobal budgetGlobal = budgetGlobalRepository.findById(request.getBudgetGlobalId())
            .orElseThrow(() -> new RuntimeException("Budget global introuvable"));
        
        // Vérifier que l'utilisateur appartient à la même année d'exercice
        if (!currentUser.getAnneeExercice().getId().equals(budgetGlobal.getAnneeExercice().getId())) {
            throw new RuntimeException("Vous ne pouvez créer une activité que pour votre année d'exercice");
        }
        
        // Récupérer le statut (par défaut : "En attente" = ID 1)
        ActiviteStatus status = null;
        if (request.getStatusId() != null) {
            status = activiteStatusRepository.findById(request.getStatusId())
                .orElseThrow(() -> new RuntimeException("Statut introuvable"));
        } else {
            // Statut par défaut: En attente (ID 1)
            status = activiteStatusRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Statut par défaut introuvable"));
        }
        
        // Calculer le montant total des détails
        Double montantTotal = request.getDetails().stream()
            .mapToDouble(DetailActiviteDto::getMontant)
            .sum();
        
        // Créer l'activité
        Activite activite = Activite.builder()
            .nom(request.getNom())
            .description(request.getDescription())
            .dateDebut(request.getDateDebut())
            .dateFin(request.getDateFin())
            .montant(montantTotal)
            .budgetGlobal(budgetGlobal)
            .status(status)
            .build();
        
        Activite savedActivite = activiteRepository.save(activite);
        
        // Créer les détails
        List<DetailActivite> details = request.getDetails().stream()
            .map(dto -> DetailActivite.builder()
                .activite(savedActivite)
                .details(dto.getDetails())
                .montant(dto.getMontant())
                .build())
            .collect(Collectors.toList());
        
        detailActiviteRepository.saveAll(details);
        
        // Mettre à jour le montant du budget global (somme des montants des activités)
        updateBudgetGlobalMontant(budgetGlobal.getId());
        
        // Log l'action
        journalService.logAction("Création de l'activité " + activite.getNom() + 
            " pour un montant de " + montantTotal + " Ar");
        
        return mapToResponse(savedActivite, details);
    }
    
    /**
     * Récupérer toutes les activités d'une année d'exercice
     */
    @Transactional(readOnly = true)
    public List<ActiviteResponse> getActivitesByAnneeExercice(Long anneeExerciceId) {
        List<Activite> activites = activiteRepository.findByAnneeExerciceId(anneeExerciceId);
        
        return activites.stream()
            .map(activite -> {
                List<DetailActivite> details = detailActiviteRepository.findByActiviteId(activite.getId());
                return mapToResponse(activite, details);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Récupérer une activité par son ID
     */
    @Transactional(readOnly = true)
    public ActiviteResponse getActiviteById(Long id) {
        Activite activite = activiteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Activité introuvable"));
        
        List<DetailActivite> details = detailActiviteRepository.findByActiviteId(id);
        
        return mapToResponse(activite, details);
    }
    
    /**
     * Récupérer tous les statuts d'activités
     */
    @Transactional(readOnly = true)
    public List<ActiviteStatusResponse> getAllStatuts() {
        return activiteStatusRepository.findAll().stream()
            .map(status -> ActiviteStatusResponse.builder()
                .id(status.getId())
                .status(status.getStatus())
                .build())
            .collect(Collectors.toList());
    }
    
    /**
     * Mettre à jour une activité
     */
    @Transactional
    public ActiviteResponse updateActivite(Long id, UpdateActiviteRequest request, String currentUsername) {
        log.info("Mise à jour de l'activité ID: {}", id);
        
        // Vérifier l'utilisateur actuel
        Utilisateur currentUser = utilisateurRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Récupérer l'activité existante
        Activite activite = activiteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Activité introuvable"));
        
        // Vérifier que l'utilisateur appartient à la même année d'exercice
        if (!currentUser.getAnneeExercice().getId().equals(activite.getBudgetGlobal().getAnneeExercice().getId())) {
            throw new RuntimeException("Vous ne pouvez modifier une activité que de votre année d'exercice");
        }
        
        // Vérifier que le budget n'est pas encore approuvé
        if (activite.getBudgetGlobal().getStatus() != null && 
            !"Créé".equals(activite.getBudgetGlobal().getStatus().getNom())) {
            throw new RuntimeException("Impossible de modifier une activité d'un budget déjà approuvé");
        }
        
        // Vérifier que l'activité n'est pas terminée
        if (activite.getStatus() != null && "Terminé".equals(activite.getStatus().getStatus())) {
            throw new RuntimeException("Impossible de modifier une activité dont le statut est 'Terminé'");
        }
        
        // Récupérer le statut
        ActiviteStatus status = activiteStatusRepository.findById(request.getStatusId())
            .orElseThrow(() -> new RuntimeException("Statut introuvable"));
        
        // Calculer le nouveau montant total
        Double montantTotal = request.getDetails().stream()
            .mapToDouble(DetailActiviteDto::getMontant)
            .sum();
        
        // Mettre à jour l'activité
        activite.setNom(request.getNom());
        activite.setDescription(request.getDescription());
        activite.setDateDebut(request.getDateDebut());
        activite.setDateFin(request.getDateFin());
        activite.setMontant(montantTotal);
        activite.setStatus(status);
        
        Activite updatedActivite = activiteRepository.save(activite);
        
        // Supprimer les anciens détails et créer les nouveaux
        detailActiviteRepository.deleteByActiviteId(id);
        
        List<DetailActivite> newDetails = request.getDetails().stream()
            .map(dto -> DetailActivite.builder()
                .activite(updatedActivite)
                .details(dto.getDetails())
                .montant(dto.getMontant())
                .build())
            .collect(Collectors.toList());
        
        detailActiviteRepository.saveAll(newDetails);
        
        // Mettre à jour le montant du budget global
        updateBudgetGlobalMontant(activite.getBudgetGlobal().getId());
        
        // Log l'action
        journalService.logAction("Modification de l'activité " + activite.getNom() + 
            " - Nouveau montant: " + montantTotal + " Ar");
        
        return mapToResponse(updatedActivite, newDetails);
    }
    
    /**
     * Supprimer une activité
     */
    @Transactional
    public void deleteActivite(Long id, String currentUsername) {
        log.info("Suppression de l'activité ID: {}", id);
        
        // Vérifier l'utilisateur actuel
        Utilisateur currentUser = utilisateurRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Récupérer l'activité
        Activite activite = activiteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Activité introuvable"));
        
        // Vérifier que l'utilisateur appartient à la même année d'exercice
        if (!currentUser.getAnneeExercice().getId().equals(activite.getBudgetGlobal().getAnneeExercice().getId())) {
            throw new RuntimeException("Vous ne pouvez supprimer une activité que de votre année d'exercice");
        }
        
        // Vérifier que le budget n'est pas encore approuvé
        if (activite.getBudgetGlobal().getStatus() != null && 
            !"Créé".equals(activite.getBudgetGlobal().getStatus().getNom())) {
            throw new RuntimeException("Impossible de supprimer une activité d'un budget déjà approuvé");
        }
        
        Long budgetGlobalId = activite.getBudgetGlobal().getId();
        String nomActivite = activite.getNom();
        
        // Supprimer les détails puis l'activité
        detailActiviteRepository.deleteByActiviteId(id);
        activiteRepository.deleteById(id);
        
        // Mettre à jour le montant du budget global
        updateBudgetGlobalMontant(budgetGlobalId);
        
        // Log l'action
        journalService.logAction("Suppression de l'activité " + nomActivite);
    }
    
    /**
     * Mettre à jour le montant du budget global (somme des montants des activités)
     */
    private void updateBudgetGlobalMontant(Long budgetGlobalId) {
        List<Activite> activites = activiteRepository.findByBudgetGlobalId(budgetGlobalId);
        
        Double montantTotal = activites.stream()
            .mapToDouble(a -> a.getMontant() != null ? a.getMontant() : 0.0)
            .sum();
        
        BudgetGlobal budgetGlobal = budgetGlobalRepository.findById(budgetGlobalId)
            .orElseThrow(() -> new RuntimeException("Budget global introuvable"));
        
        budgetGlobal.setMontant(montantTotal);
        budgetGlobalRepository.save(budgetGlobal);
    }
    
    /**
     * Mapper une entité vers un DTO de réponse
     */
    private ActiviteResponse mapToResponse(Activite activite, List<DetailActivite> details) {
        List<DetailActiviteResponse> detailsResponse = details.stream()
            .map(detail -> DetailActiviteResponse.builder()
                .id(detail.getId())
                .details(detail.getDetails())
                .montant(detail.getMontant())
                .createdAt(detail.getCreatedAt() != null ? detail.getCreatedAt().format(DATETIME_FORMATTER) : null)
                .build())
            .collect(Collectors.toList());
        
        return ActiviteResponse.builder()
            .id(activite.getId())
            .nom(activite.getNom())
            .description(activite.getDescription())
            .dateDebut(activite.getDateDebut() != null ? activite.getDateDebut().format(DATE_FORMATTER) : null)
            .dateFin(activite.getDateFin() != null ? activite.getDateFin().format(DATE_FORMATTER) : null)
            .montant(activite.getMontant())
            .budgetGlobalId(activite.getBudgetGlobal().getId())
            .anneeExercice(activite.getBudgetGlobal().getAnneeExercice().getAnnee().getYear() + "")
            .status(activite.getStatus() != null ? activite.getStatus().getStatus() : null)
            .statusId(activite.getStatus() != null ? activite.getStatus().getId() : null)
            .details(detailsResponse)
            .createdAt(activite.getCreatedAt() != null ? activite.getCreatedAt().format(DATETIME_FORMATTER) : null)
            .build();
    }
}
