package com.explorateur.backend.service;

import com.explorateur.backend.dto.AnneeExerciceResponse;
import com.explorateur.backend.dto.CreateAnneeExerciceRequest;
import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.repository.AnneeExerciceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des années d'exercice
 */
@Service
@RequiredArgsConstructor
public class AnneeExerciceService {
    
    private final AnneeExerciceRepository anneeExerciceRepository;
    private final JournalService journalService;
    
    /**
     * Crée une nouvelle année d'exercice
     */
    @Transactional
    public AnneeExerciceResponse createAnneeExercice(CreateAnneeExerciceRequest request) {
        // Vérifier si l'année existe déjà
        if (anneeExerciceRepository.findByAnnee(request.getAnnee()).isPresent()) {
            throw new RuntimeException("Cette année d'exercice existe déjà");
        }
        
        // Calculer automatiquement la date de fin (31 décembre de l'année)
        var dateFin = request.getAnnee().withMonth(12).withDayOfMonth(31);
        
        AnneeExercice anneeExercice = AnneeExercice.builder()
                .annee(request.getAnnee())
                .dateFin(dateFin)
                .createdAt(LocalDateTime.now())
                .build();
        
        AnneeExercice saved = anneeExerciceRepository.save(anneeExercice);
        
        // Log l'action dans le journal
        journalService.logAction("Création de l'année d'exercice " + saved.getAnnee().getYear());
        
        return mapToResponse(saved);
    }
    
    /**
     * Récupère toutes les années d'exercice
     */
    @Transactional(readOnly = true)
    public List<AnneeExerciceResponse> getAllAnneesExercice() {
        return anneeExerciceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère une année d'exercice par ID
     */
    @Transactional(readOnly = true)
    public AnneeExerciceResponse getAnneeExerciceById(Long id) {
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Année d'exercice introuvable"));
        return mapToResponse(anneeExercice);
    }
    
    /**
     * Récupère l'année d'exercice la plus récente
     */
    @Transactional(readOnly = true)
    public AnneeExerciceResponse getLatestAnneeExercice() {
        AnneeExercice anneeExercice = anneeExerciceRepository.findFirstByOrderByAnneeDesc()
                .orElseThrow(() -> new RuntimeException("Aucune année d'exercice trouvée"));
        return mapToResponse(anneeExercice);
    }
    
    /**
     * Supprime une année d'exercice
     */
    @Transactional
    public void deleteAnneeExercice(Long id) {
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Année d'exercice introuvable"));
        
        int year = anneeExercice.getAnnee().getYear();
        anneeExerciceRepository.deleteById(id);
        
        // Log la suppression
        journalService.logAction("Suppression de l'année d'exercice " + year);
    }
    
    /**
     * Convertit une entité en DTO de réponse
     */
    private AnneeExerciceResponse mapToResponse(AnneeExercice anneeExercice) {
        return AnneeExerciceResponse.builder()
                .id(anneeExercice.getId())
                .annee(anneeExercice.getAnnee())
                .dateFin(anneeExercice.getDateFin())
                .createdAt(anneeExercice.getCreatedAt())
                .build();
    }
}
