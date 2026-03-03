package com.explorateur.backend.service;

import com.explorateur.backend.dto.ClasseProgressiveResponse;
import com.explorateur.backend.dto.CreateClasseProgressiveRequest;
import com.explorateur.backend.dto.UpdateClasseProgressiveRequest;
import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.entity.ClasseProgressive;
import com.explorateur.backend.entity.Utilisateur;
import com.explorateur.backend.repository.AnneeExerciceRepository;
import com.explorateur.backend.repository.ClasseProgressiveRepository;
import com.explorateur.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des Classes Progressives (CP)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClasseProgressiveService {
    
    private final ClasseProgressiveRepository cpRepository;
    private final AnneeExerciceRepository anneeExerciceRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JournalService journalService;
    
    /**
     * Créer une nouvelle CP
     */
    @Transactional
    public ClasseProgressiveResponse createClasseProgressive(CreateClasseProgressiveRequest request) {
        log.info("Création d'une nouvelle CP pour la date: {}", request.getDateCp());
        
        // Validation: Heure début < Heure fin
        validateHeures(request.getHeureDebut(), request.getHeureFin());
        
        // Récupérer l'année d'exercice
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(request.getAnneeExerciceId())
                .orElseThrow(() -> new RuntimeException("Année d'exercice non trouvée avec l'ID: " + request.getAnneeExerciceId()));
        
        // Validation: L'utilisateur ne peut créer que dans son année d'exercice
        validateUserAnneeExercice(anneeExercice);
        
        // Validation: Une CP d'une année clôturée est verrouillée
        validateAnneeNonCloturee(anneeExercice);
        
        // Validation optionnelle: Une CP ne peut pas être créée dans le passé
        // (commentée car optionnelle selon les besoins)
        // validateDateNonPassee(request.getDateCp());
        
        // Validation: Deux CP ne doivent pas avoir le même horaire exact dans la même année
        if (cpRepository.existsByDateCpAndHeureDebutAndHeureFinAndAnneeExerciceId(
                request.getDateCp(), request.getHeureDebut(), request.getHeureFin(), 
                request.getAnneeExerciceId())) {
            throw new RuntimeException("Une CP avec le même horaire existe déjà pour cette année d'exercice");
        }
        
        // Créer la CP
        ClasseProgressive cp = ClasseProgressive.builder()
                .dateCp(request.getDateCp())
                .heureDebut(request.getHeureDebut())
                .heureFin(request.getHeureFin())
                .niveau(request.getNiveau())
                .anneeExercice(anneeExercice)
                .build();
        
        ClasseProgressive savedCp = cpRepository.save(cp);
        log.info("CP créée avec succès avec l'ID: {}", savedCp.getId());
        
        // Journalisation
        journalService.logAction("Enregistrement de la classe progressive le " + 
                savedCp.getDateCp() + " a " + savedCp.getHeureDebut() + " - " + savedCp.getHeureFin());
        
        return mapToResponse(savedCp);
    }
    
    /**
     * Modifier une CP existante
     */
    @Transactional
    public ClasseProgressiveResponse updateClasseProgressive(Long id, UpdateClasseProgressiveRequest request) {
        log.info("Modification de la CP ID: {}", id);
        
        ClasseProgressive cp = cpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CP non trouvée avec l'ID: " + id));
        
        // Validation: Heure début < Heure fin
        validateHeures(request.getHeureDebut(), request.getHeureFin());
        
        // Récupérer l'année d'exercice
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(request.getAnneeExerciceId())
                .orElseThrow(() -> new RuntimeException("Année d'exercice non trouvée avec l'ID: " + request.getAnneeExerciceId()));
        
        // Validation: L'utilisateur ne peut modifier que dans son année d'exercice
        validateUserAnneeExercice(cp.getAnneeExercice());
        
        // Validation: Une CP d'une année clôturée est verrouillée
        validateAnneeNonCloturee(cp.getAnneeExercice());
        
        // Validation: Deux CP ne doivent pas avoir le même horaire exact dans la même année
        if (cpRepository.existsByDateCpAndHeureDebutAndHeureFinAndAnneeExerciceIdAndIdNot(
                request.getDateCp(), request.getHeureDebut(), request.getHeureFin(), 
                request.getAnneeExerciceId(), id)) {
            throw new RuntimeException("Une CP avec le même horaire existe déjà pour cette année d'exercice");
        }
        
        // Mettre à jour les champs
        cp.setDateCp(request.getDateCp());
        cp.setHeureDebut(request.getHeureDebut());
        cp.setHeureFin(request.getHeureFin());
        cp.setNiveau(request.getNiveau());
        cp.setAnneeExercice(anneeExercice);
        
        ClasseProgressive updatedCp = cpRepository.save(cp);
        log.info("CP mise à jour avec succès: {}", updatedCp.getId());
        
        // Journalisation
        journalService.logAction("Modification de la classe progressive du " + 
                updatedCp.getDateCp() + " a " + updatedCp.getHeureDebut() + " - " + updatedCp.getHeureFin());
        
        return mapToResponse(updatedCp);
    }
    
    /**
     * Supprimer une CP
     */
    @Transactional
    public void deleteClasseProgressive(Long id) {
        log.info("Suppression de la CP ID: {}", id);
        
        ClasseProgressive cp = cpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CP non trouvée avec l'ID: " + id));
        
        // Validation: L'utilisateur ne peut supprimer que dans son année d'exercice
        validateUserAnneeExercice(cp.getAnneeExercice());
        
        // Validation: Une CP d'une année clôturée est verrouillée
        validateAnneeNonCloturee(cp.getAnneeExercice());
        
        // Validation: Une CP ne peut pas être supprimée si présences enregistrées
        if (cpRepository.hasPresences(id)) {
            throw new RuntimeException("Impossible de supprimer cette CP: des présences sont enregistrées");
        }
        
        // Validation: Une CP ne peut pas être supprimée si programmes terminés
        if (cpRepository.hasProgrammesTermines(id)) {
            throw new RuntimeException("Impossible de supprimer cette CP: des programmes sont déjà terminés");
        }
        
        // Journalisation avant suppression
        journalService.logAction("Suppression de la classe progressive du " + cp.getDateCp());
        
        cpRepository.delete(cp);
        log.info("CP supprimée avec succès: {}", id);
    }
    
    /**
     * Récupérer toutes les CP
     */
    @Transactional(readOnly = true)
    public List<ClasseProgressiveResponse> getAllClassesProgressives() {
        log.info("Récupération de toutes les CP");
        return cpRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer une CP par ID
     */
    @Transactional(readOnly = true)
    public ClasseProgressiveResponse getClasseProgressiveById(Long id) {
        log.info("Récupération de la CP ID: {}", id);
        ClasseProgressive cp = cpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CP non trouvée avec l'ID: " + id));
        return mapToResponse(cp);
    }
    
    /**
     * Filtrer les CP par plage de dates
     */
    @Transactional(readOnly = true)
    public List<ClasseProgressiveResponse> filterByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Filtrage des CP entre {} et {}", dateDebut, dateFin);
        return cpRepository.findByDateCpBetween(dateDebut, dateFin)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Filtrer les CP par année d'exercice
     */
    @Transactional(readOnly = true)
    public List<ClasseProgressiveResponse> filterByAnneeExercice(Long anneeExerciceId) {
        log.info("Filtrage des CP par année d'exercice ID: {}", anneeExerciceId);
        return cpRepository.findByAnneeExerciceId(anneeExerciceId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Filtrer les CP par plage de dates ET année d'exercice
     */
    @Transactional(readOnly = true)
    public List<ClasseProgressiveResponse> filterByDateRangeAndAnneeExercice(
            LocalDate dateDebut, LocalDate dateFin, Long anneeExerciceId) {
        log.info("Filtrage des CP entre {} et {} pour l'année d'exercice ID: {}", 
                 dateDebut, dateFin, anneeExerciceId);
        return cpRepository.findByDateCpBetweenAndAnneeExerciceId(dateDebut, dateFin, anneeExerciceId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    // ========== MÉTHODES DE VALIDATION ==========
    
    /**
     * Valider que l'heure de début est avant l'heure de fin
     */
    private void validateHeures(java.time.LocalTime heureDebut, java.time.LocalTime heureFin) {
        if (heureDebut.isAfter(heureFin) || heureDebut.equals(heureFin)) {
            throw new RuntimeException("L'heure de début doit être avant l'heure de fin");
        }
    }
    
    /**
     * Valider qu'une CP n'est pas créée dans le passé (optionnel)
     */
    @SuppressWarnings("unused")
    private void validateDateNonPassee(LocalDate dateCp) {
        if (dateCp.isBefore(LocalDate.now())) {
            throw new RuntimeException("Une CP ne peut pas être créée dans le passé");
        }
    }
    
    /**
     * Valider que l'utilisateur crée/modifie dans son année d'exercice
     */
    private void validateUserAnneeExercice(AnneeExercice anneeExercice) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + username));
        
        if (utilisateur.getAnneeExercice() == null) {
            throw new RuntimeException("L'utilisateur n'a pas d'année d'exercice associée");
        }
        
        if (!anneeExercice.getId().equals(utilisateur.getAnneeExercice().getId())) {
            throw new RuntimeException("Vous ne pouvez créer/modifier/supprimer que des données de votre année d'exercice");
        }
    }
    
    /**
     * Valider que l'année d'exercice n'est pas clôturée
     */
    private void validateAnneeNonCloturee(AnneeExercice anneeExercice) {
        if (anneeExercice.getDateFin().isBefore(LocalDate.now())) {
            throw new RuntimeException("Une CP d'une année clôturée est verrouillée");
        }
    }
    
    // ========== MÉTHODE DE MAPPING ==========
    
    /**
     * Mapper une entité ClasseProgressive vers un DTO de réponse
     */
    private ClasseProgressiveResponse mapToResponse(ClasseProgressive cp) {
        return ClasseProgressiveResponse.builder()
                .id(cp.getId())
                .dateCp(cp.getDateCp())
                .heureDebut(cp.getHeureDebut())
                .heureFin(cp.getHeureFin())
                .niveau(cp.getNiveau())
                .anneeExerciceId(cp.getAnneeExercice().getId())
                .anneeExercice(cp.getAnneeExercice().getAnnee())
                .createdAt(cp.getCreatedAt())
                .build();
    }
}
