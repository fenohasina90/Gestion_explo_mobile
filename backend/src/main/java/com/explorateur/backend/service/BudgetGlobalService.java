package com.explorateur.backend.service;

import com.explorateur.backend.dto.BudgetGlobalResponse;
import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.entity.BudgetGlobal;
import com.explorateur.backend.entity.BudgetStatus;
import com.explorateur.backend.repository.AnneeExerciceRepository;
import com.explorateur.backend.repository.ActiviteRepository;
import com.explorateur.backend.repository.BudgetGlobalRepository;
import com.explorateur.backend.repository.BudgetStatusRepository;
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
public class BudgetGlobalService {
    
    private final BudgetGlobalRepository budgetGlobalRepository;
    private final AnneeExerciceRepository anneeExerciceRepository;
    private final BudgetStatusRepository budgetStatusRepository;
    private final ActiviteRepository activiteRepository;
    private final JournalService journalService;
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    /**
     * Créer ou obtenir le budget global pour une année d'exercice
     */
    @Transactional
    public BudgetGlobalResponse getOrCreateBudgetGlobal(Long anneeExerciceId) {
        // Vérifier si un budget existe déjà pour cette année
        BudgetGlobal budgetGlobal = budgetGlobalRepository.findByAnneeExerciceId(anneeExerciceId)
            .orElseGet(() -> {
                // Créer un nouveau budget
                AnneeExercice anneeExercice = anneeExerciceRepository.findById(anneeExerciceId)
                    .orElseThrow(() -> new RuntimeException("Année d'exercice introuvable"));
                
                // Statut par défaut: Créé (ID 1)
                BudgetStatus status = budgetStatusRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Statut par défaut introuvable"));
                
                BudgetGlobal newBudget = BudgetGlobal.builder()
                    .anneeExercice(anneeExercice)
                    .montant(0.0)
                    .status(status)
                    .build();
                
                BudgetGlobal saved = budgetGlobalRepository.save(newBudget);
                
                journalService.logAction("Création du budget global pour l'année " + anneeExercice.getAnnee().getYear());
                
                return saved;
            });
        
        return mapToResponse(budgetGlobal);
    }
    
    /**
     * Récupérer le budget global par année d'exercice
     */
    @Transactional(readOnly = true)
    public BudgetGlobalResponse getBudgetByAnneeExercice(Long anneeExerciceId) {
        BudgetGlobal budgetGlobal = budgetGlobalRepository.findByAnneeExerciceId(anneeExerciceId)
            .orElseThrow(() -> new RuntimeException("Budget global introuvable pour cette année"));
        
        return mapToResponse(budgetGlobal);
    }
    
    /**
     * Récupérer tous les budgets globaux
     */
    @Transactional(readOnly = true)
    public List<BudgetGlobalResponse> getAllBudgets() {
        return budgetGlobalRepository.findAllByOrderByAnneeExerciceAnneeDesc().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Mettre à jour le statut d'un budget global
     */
    @Transactional
    public BudgetGlobalResponse updateBudgetStatus(Long budgetId, Long statusId, String currentUsername) {
        log.info("Mise à jour du statut du budget ID: {} vers statut ID: {}", budgetId, statusId);
        
        BudgetGlobal budgetGlobal = budgetGlobalRepository.findById(budgetId)
            .orElseThrow(() -> new RuntimeException("Budget global introuvable"));
        
        // Vérifier que le budget n'est pas déjà approuvé (ne peut pas revenir en arrière)
        if (budgetGlobal.getStatus() != null && "Approuvé comité".equals(budgetGlobal.getStatus().getNom())) {
            throw new RuntimeException("Impossible de modifier le statut d'un budget déjà approuvé par le comité");
        }
        
        BudgetStatus newStatus = budgetStatusRepository.findById(statusId)
            .orElseThrow(() -> new RuntimeException("Statut introuvable"));
        
        String ancienStatut = budgetGlobal.getStatus() != null ? budgetGlobal.getStatus().getNom() : "N/A";
        
        budgetGlobal.setStatus(newStatus);
        BudgetGlobal updated = budgetGlobalRepository.save(budgetGlobal);
        
        journalService.logAction("Modification du statut du budget " + 
            budgetGlobal.getAnneeExercice().getAnnee().getYear() + 
            " de '" + ancienStatut + "' vers '" + newStatus.getNom() + "'");
        
        return mapToResponse(updated);
    }
    
    /**
     * Mapper une entité vers un DTO de réponse
     */
    private BudgetGlobalResponse mapToResponse(BudgetGlobal budgetGlobal) {
        // Compter le nombre d'activités
        int nombreActivites = activiteRepository.findByBudgetGlobalId(budgetGlobal.getId()).size();
        
        return BudgetGlobalResponse.builder()
            .id(budgetGlobal.getId())
            .anneeExercice(budgetGlobal.getAnneeExercice().getAnnee().getYear() + "")
            .anneeExerciceId(budgetGlobal.getAnneeExercice().getId())
            .montant(budgetGlobal.getMontant())
            .status(budgetGlobal.getStatus() != null ? budgetGlobal.getStatus().getNom() : null)
            .statusId(budgetGlobal.getStatus() != null ? budgetGlobal.getStatus().getId() : null)
            .nombreActivites(nombreActivites)
            .createdAt(budgetGlobal.getCreatedAt() != null ? budgetGlobal.getCreatedAt().format(DATETIME_FORMATTER) : null)
            .build();
    }
}
