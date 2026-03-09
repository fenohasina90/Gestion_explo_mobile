package com.explorateur.backend.service;

import com.explorateur.backend.dto.*;
import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.entity.MouvementBudgetaire;
import com.explorateur.backend.entity.Type;
import com.explorateur.backend.repository.AnneeExerciceRepository;
import com.explorateur.backend.repository.MouvementBudgetaireRepository;
import com.explorateur.backend.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des mouvements budgétaires
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MouvementBudgetaireService {
    
    private final MouvementBudgetaireRepository mouvementBudgetaireRepository;
    private final AnneeExerciceRepository anneeExerciceRepository;
    private final TypeRepository typeRepository;
    private final JournalService journalService;
    private final AnneeExerciceService anneeExerciceService;
    
    /**
     * Créer un nouveau mouvement budgétaire (Directeur uniquement)
     */
    @Transactional
    public MouvementBudgetaireResponse createMouvement(CreateMouvementBudgetaireRequest request) {
        log.info("Création d'un mouvement budgétaire de type {} pour le montant {}", 
                request.getTypeId(), request.getMontant());
        
        // Récupérer l'année d'exercice
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(request.getAnneeExerciceId())
                .orElseThrow(() -> new RuntimeException("Année d'exercice introuvable"));
        
        // Récupérer le type
        Type type = typeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new RuntimeException("Type de mouvement introuvable"));
        
        // Créer le mouvement
        MouvementBudgetaire mouvement = MouvementBudgetaire.builder()
                .anneeExercice(anneeExercice)
                .type(type)
                .montant(request.getMontant())
                .description(request.getDescription())
                .build();
        
        MouvementBudgetaire saved = mouvementBudgetaireRepository.save(mouvement);
        
        // Journalisation
        String description = request.getDescription() != null ? request.getDescription() : "Sans description";
        journalService.logAction(String.format("Enregistrement d'un mouvement budgétaire de type %s d'un montant de %.2f Ar (%s)", 
                type.getType(), 
                request.getMontant(),
                description));
        
        return mapToResponse(saved);
    }
    
    /**
     * Modifier un mouvement budgétaire (Directeur et Co-directeur)
     */
    @Transactional
    public MouvementBudgetaireResponse updateMouvement(Long id, UpdateMouvementBudgetaireRequest request) {
        log.info("Modification du mouvement budgétaire ID: {}", id);
        
        // Récupérer le mouvement
        MouvementBudgetaire mouvement = mouvementBudgetaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement budgétaire introuvable"));
        
        // Récupérer le type
        Type type = typeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new RuntimeException("Type de mouvement introuvable"));
        
        // Sauvegarder les anciennes valeurs pour le journal
        String ancienType = mouvement.getType().getType();
        BigDecimal ancienMontant = mouvement.getMontant();
        String ancienneDescription = mouvement.getDescription();
        
        // Mettre à jour le mouvement
        mouvement.setType(type);
        mouvement.setMontant(request.getMontant());
        mouvement.setDescription(request.getDescription());
        
        MouvementBudgetaire updated = mouvementBudgetaireRepository.save(mouvement);
        
        // Journalisation
        String description = request.getDescription() != null ? request.getDescription() : "Sans description";
        journalService.logAction(String.format("Modification d'un mouvement budgétaire (Ancien: %s %.2f Ar - Nouveau: %s %.2f Ar - %s)", 
                ancienType, 
                ancienMontant,
                type.getType(),
                request.getMontant(),
                description));
        
        return mapToResponse(updated);
    }
    
    /**
     * Supprimer un mouvement budgétaire (Directeur uniquement)
     */
    @Transactional
    public void deleteMouvement(Long id) {
        log.info("Suppression du mouvement budgétaire ID: {}", id);
        
        MouvementBudgetaire mouvement = mouvementBudgetaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement budgétaire introuvable"));
        
        String description = mouvement.getDescription() != null ? mouvement.getDescription() : "Sans description";
        
        // Journalisation avant suppression
        journalService.logAction(String.format("Suppression d'un mouvement budgétaire de type %s d'un montant de %.2f Ar (%s)", 
                mouvement.getType().getType(), 
                mouvement.getMontant(),
                description));
        
        mouvementBudgetaireRepository.delete(mouvement);
    }
    
    /**
     * Obtenir tous les mouvements budgétaires avec filtres
     */
    @Transactional(readOnly = true)
    public List<MouvementBudgetaireResponse> getMouvementsWithFilters(MouvementBudgetaireFilterRequest filters) {
        log.info("Récupération des mouvements budgétaires avec filtres");
        
        LocalDateTime dateDebut = null;
        LocalDateTime dateFin = null;
        
        if (filters.getDateDebut() != null) {
            dateDebut = filters.getDateDebut().atStartOfDay();
        }
        
        if (filters.getDateFin() != null) {
            dateFin = filters.getDateFin().atTime(LocalTime.MAX);
        }
        
        List<MouvementBudgetaire> mouvements = mouvementBudgetaireRepository.findWithFilters(
                filters.getAnneeExerciceId(),
                filters.getTypeId(),
                filters.getRecherche(),
                dateDebut,
                dateFin
        );
        
        return mouvements.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtenir l'état de caisse pour une année d'exercice
     */
    @Transactional(readOnly = true)
    public EtatCaisseResponse getEtatCaisse(Long anneeExerciceId) {
        log.info("Calcul de l'état de caisse pour l'année d'exercice ID: {}", anneeExerciceId);
        
        // Si aucune année n'est spécifiée, prendre l'année active
        Long finalAnneeId = anneeExerciceId;
        if (finalAnneeId == null) {
            AnneeExercice anneeActive = anneeExerciceRepository.findFirstByOrderByAnneeDesc()
                    .orElseThrow(() -> new RuntimeException("Aucune année d'exercice trouvée"));
            finalAnneeId = anneeActive.getId();
        }
        
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(finalAnneeId)
                .orElseThrow(() -> new RuntimeException("Année d'exercice introuvable"));
        
        // Calculer les totaux
        BigDecimal totalRecettes = mouvementBudgetaireRepository.calculateTotalRecettes(finalAnneeId);
        BigDecimal totalDepenses = mouvementBudgetaireRepository.calculateTotalDepenses(finalAnneeId);
        BigDecimal solde = totalRecettes.subtract(totalDepenses);
        
        return EtatCaisseResponse.builder()
                .totalRecettes(totalRecettes)
                .totalDepenses(totalDepenses)
                .solde(solde)
                .anneeExercice(anneeExerciceService.mapToResponse(anneeExercice))
                .build();
    }
    
    /**
     * Obtenir un mouvement par son ID
     */
    @Transactional(readOnly = true)
    public MouvementBudgetaireResponse getMouvementById(Long id) {
        log.info("Récupération du mouvement budgétaire ID: {}", id);
        MouvementBudgetaire mouvement = mouvementBudgetaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement budgétaire introuvable"));
        return mapToResponse(mouvement);
    }
    
    /**
     * Mapper MouvementBudgetaire vers MouvementBudgetaireResponse
     */
    private MouvementBudgetaireResponse mapToResponse(MouvementBudgetaire mouvement) {
        return MouvementBudgetaireResponse.builder()
                .id(mouvement.getId())
                .anneeExercice(anneeExerciceService.mapToResponse(mouvement.getAnneeExercice()))
                .type(TypeResponse.builder()
                        .id(mouvement.getType().getId())
                        .type(mouvement.getType().getType())
                        .build())
                .montant(mouvement.getMontant())
                .description(mouvement.getDescription())
                .createdAt(mouvement.getCreatedAt())
                .build();
    }
}
