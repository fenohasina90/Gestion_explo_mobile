package com.explorateur.backend.service;

import com.explorateur.backend.dto.CreateInscriptionRequest;
import com.explorateur.backend.dto.InscriptionResponse;
import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.entity.Classe;
import com.explorateur.backend.entity.Enfant;
import com.explorateur.backend.entity.Inscription;
import com.explorateur.backend.repository.AnneeExerciceRepository;
import com.explorateur.backend.repository.ClasseRepository;
import com.explorateur.backend.repository.EnfantRepository;
import com.explorateur.backend.repository.InscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InscriptionService {
    
    private final InscriptionRepository inscriptionRepository;
    private final EnfantRepository enfantRepository;
    private final AnneeExerciceRepository anneeExerciceRepository;
    private final ClasseRepository classeRepository;
    private final JournalService journalService;
    
    /**
     * Créer une nouvelle inscription
     */
    @Transactional
    public InscriptionResponse createInscription(CreateInscriptionRequest request) {
        // Vérifier que l'enfant existe
        Enfant enfant = enfantRepository.findById(request.getEnfantId())
                .orElseThrow(() -> new RuntimeException("Enfant introuvable"));
        
        // Vérifier que l'année d'exercice existe
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(request.getAnneeExerciceId())
                .orElseThrow(() -> new RuntimeException("Année d'exercice introuvable"));
        
        // Vérifier que la classe existe
        Classe classe = classeRepository.findById(request.getClasseId())
                .orElseThrow(() -> new RuntimeException("Classe introuvable"));
        
        // Vérifier si l'enfant est déjà inscrit pour cette année
        Optional<Inscription> existante = inscriptionRepository.findByEnfantIdAndAnneeExerciceId(
                request.getEnfantId(),
                request.getAnneeExerciceId()
        );
        if (existante.isPresent()) {
            throw new RuntimeException("Cet enfant est déjà inscrit pour cette année d'exercice");
        }
        
        // Vérifier l'âge de l'enfant (10-15 ans)
        int age = calculateAge(enfant.getDateNaissance(), anneeExercice.getAnnee());
        if (age < 10 || age > 15) {
            throw new RuntimeException("L'enfant doit avoir entre 10 et 15 ans pour l'année d'exercice " + 
                    anneeExercice.getAnnee().getYear());
        }
        
        Inscription inscription = new Inscription();
        inscription.setEnfant(enfant);
        inscription.setAnneeExercice(anneeExercice);
        inscription.setClasse(classe);
        inscription.setEstAssurance(request.getEstAssurance() != null ? request.getEstAssurance() : false);
        
        Inscription saved = inscriptionRepository.save(inscription);
        
        journalService.logAction("Inscription de l'enfant " + enfant.getNom() + " " + enfant.getPrenom() + 
                " pour l'année " + anneeExercice.getAnnee().getYear());
        
        return mapToResponse(saved);
    }
    
    /**
     * Récupérer toutes les inscriptions
     */
    @Transactional(readOnly = true)
    public List<InscriptionResponse> getAllInscriptions() {
        return inscriptionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les inscriptions par année d'exercice
     */
    @Transactional(readOnly = true)
    public List<InscriptionResponse> getInscriptionsByAnneeExerciceId(Long anneeExerciceId) {
        return inscriptionRepository.findByAnneeExerciceId(anneeExerciceId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les inscriptions par classe
     */
    @Transactional(readOnly = true)
    public List<InscriptionResponse> getInscriptionsByClasseId(Long classeId) {
        return inscriptionRepository.findByClasseId(classeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les inscriptions par genre
     */
    @Transactional(readOnly = true)
    public List<InscriptionResponse> getInscriptionsByGenre(String genre) {
        return inscriptionRepository.findByGenre(genre).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer une inscription par ID
     */
    @Transactional(readOnly = true)
    public InscriptionResponse getInscriptionById(Long id) {
        Inscription inscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));
        return mapToResponse(inscription);
    }
    
    /**
     * Mettre à jour le statut d'assurance
     */
    @Transactional
    public InscriptionResponse updateAssurance(Long id, Boolean estAssurance) {
        Inscription inscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));
        
        inscription.setEstAssurance(estAssurance);
        Inscription updated = inscriptionRepository.save(inscription);
        
        journalService.logAction("Mise à jour assurance pour l'inscription de " + 
                inscription.getEnfant().getNom() + " " + inscription.getEnfant().getPrenom());
        
        return mapToResponse(updated);
    }
    
    /**
     * Supprimer une inscription
     */
    @Transactional
    public void deleteInscription(Long id) {
        Inscription inscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));
        
        String nomEnfant = inscription.getEnfant().getNom() + " " + inscription.getEnfant().getPrenom();
        inscriptionRepository.delete(inscription);
        
        journalService.logAction("Suppression de l'inscription de " + nomEnfant);
    }
    
    // Méthodes utilitaires
    
    /**
     * Calcule l'âge d'un enfant dans une année donnée
     * Par exemple: enfant né en 2012, âge en 2026 = 2026 - 2012 = 14 ans
     */
    private int calculateAge(LocalDate dateNaissance, LocalDate dateReference) {
        if (dateNaissance == null) {
            return 0;
        }
        // Calculer l'âge dans l'année de référence (année complète)
        return dateReference.getYear() - dateNaissance.getYear();
    }
    
    // Méthode de mapping
    
    private InscriptionResponse mapToResponse(Inscription inscription) {
        Enfant enfant = inscription.getEnfant();
        int age = calculateAge(enfant.getDateNaissance(), inscription.getAnneeExercice().getAnnee());
        
        return InscriptionResponse.builder()
                .id(inscription.getId())
                // Enfant
                .enfantId(enfant.getId())
                .enfantNom(enfant.getNom())
                .enfantPrenom(enfant.getPrenom())
                .enfantGenre(enfant.getGenre())
                .enfantDateNaissance(enfant.getDateNaissance())
                .enfantAge(age)
                // Parent
                .parentId(enfant.getParent() != null ? enfant.getParent().getId() : null)
                .parentNom(enfant.getParent() != null ? enfant.getParent().getNom() : null)
                .parentPrenom(enfant.getParent() != null ? enfant.getParent().getPrenom() : null)
                .parentTelephone(enfant.getParent() != null ? enfant.getParent().getTelephone() : null)
                // Inscription
                .anneeExerciceId(inscription.getAnneeExercice().getId())
                .anneeExercice(inscription.getAnneeExercice().getAnnee().toString())
                .classeId(inscription.getClasse().getId())
                .classeNom(inscription.getClasse().getNom())
                .estAssurance(inscription.getEstAssurance())
                .createdAt(inscription.getCreatedAt())
                .build();
    }
}
