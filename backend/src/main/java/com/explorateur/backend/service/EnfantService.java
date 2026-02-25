package com.explorateur.backend.service;

import com.explorateur.backend.dto.CreateEnfantRequest;
import com.explorateur.backend.dto.EnfantResponse;
import com.explorateur.backend.dto.EnfantSuggestion;
import com.explorateur.backend.dto.PageResponse;
import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.entity.Enfant;
import com.explorateur.backend.entity.Parent;
import com.explorateur.backend.repository.AnneeExerciceRepository;
import com.explorateur.backend.repository.EnfantRepository;
import com.explorateur.backend.repository.InscriptionRepository;
import com.explorateur.backend.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnfantService {
    
    private final EnfantRepository enfantRepository;
    private final ParentRepository parentRepository;
    private final AnneeExerciceRepository anneeExerciceRepository;
    private final InscriptionRepository inscriptionRepository;
    private final JournalService journalService;
    
    /**
     * Recherche d'enfants pour l'auto-complétion (10-15 ans uniquement)
     * L'âge est calculé par rapport à l'année d'exercice fournie
     */
    @Transactional(readOnly = true)
    public List<EnfantSuggestion> searchEnfants(String query, Long anneeExerciceId) {
        // Récupérer l'année d'exercice
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(anneeExerciceId)
                .orElseThrow(() -> new RuntimeException("Année d'exercice introuvable"));
        
        // Calculer les dates limites pour 10-15 ans dans l'année d'exercice
        // Pour avoir 10 ans en 2026: né en 2016 (2026 - 10 = 2016)
        // Pour avoir 15 ans en 2026: né en 2011 (2026 - 15 = 2011)
        LocalDate anneeDebut = anneeExercice.getAnnee();
        int annee = anneeDebut.getYear();
        LocalDate dateMax = LocalDate.of(annee - 10, 12, 31);  // Dernier jour de l'année des 10 ans
        LocalDate dateMin = LocalDate.of(annee - 15, 1, 1);    // Premier jour de l'année des 15 ans
        
        String searchTerm = "%" + query + "%";
        List<Enfant> enfants = enfantRepository.searchByNomOrPrenomWithAgeRange(
                searchTerm,
                dateMin.toString(),
                dateMax.toString()
        );
        
        // Filtrer les enfants déjà inscrits pour cette année d'exercice
        return enfants.stream()
                .filter(enfant -> !inscriptionRepository.existsByEnfantIdAndAnneeExerciceId(
                        enfant.getId(), anneeExerciceId))
                .map(enfant -> mapToSuggestion(enfant, anneeDebut))
                .collect(Collectors.toList());
    }
    
    /**
     * Créer un nouvel enfant
     */
    @Transactional
    public EnfantResponse createEnfant(CreateEnfantRequest request, Long anneeExerciceId) {
        // Vérifier que le parent existe
        Parent parent = parentRepository.findById(request.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent introuvable"));
        
        // Vérifier les doublons
        List<Enfant> existants = enfantRepository.findByNomAndPrenom(request.getNom(), request.getPrenom());
        if (!existants.isEmpty()) {
            throw new RuntimeException("Un enfant avec ce nom et prénom existe déjà");
        }
        
        // Vérifier l'âge (10-15 ans par rapport à l'année d'exercice)
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(anneeExerciceId)
                .orElseThrow(() -> new RuntimeException("Année d'exercice introuvable"));
        
        int age = calculateAge(request.getDateNaissance(), anneeExercice.getAnnee());
        if (age < 10 || age > 15) {
            throw new RuntimeException("L'enfant doit avoir entre 10 et 15 ans pour l'année d'exercice " + 
                    anneeExercice.getAnnee().getYear());
        }
        
        Enfant enfant = new Enfant();
        enfant.setNom(request.getNom());
        enfant.setPrenom(request.getPrenom());
        enfant.setGenre(request.getGenre());
        enfant.setDateNaissance(request.getDateNaissance());
        enfant.setAdresse(request.getAdresse());
        enfant.setParent(parent);
        enfant.setBapteme(request.getBapteme());
        
        Enfant saved = enfantRepository.save(enfant);
        
        // Message d'audit avec le nom du parent et "fils de" ou "fille de"
        String relation = "GARCON".equals(enfant.getGenre()) ? "fils de" : "fille de";
        journalService.logAction("Création de l'enfant " + enfant.getNom() + " " + enfant.getPrenom() + 
                " " + relation + " " + parent.getNom() + " " + parent.getPrenom());
        
        return mapToResponse(saved, anneeExercice.getAnnee());
    }
    
    /**
     * Récupérer tous les enfants avec pagination
     */
    @Transactional(readOnly = true)
    public PageResponse<EnfantResponse> getAllEnfants(Pageable pageable) {
        LocalDate now = LocalDate.now();
        Page<Enfant> page = enfantRepository.findAll(pageable);
        
        List<EnfantResponse> content = page.getContent().stream()
                .map(enfant -> mapToResponse(enfant, now))
                .collect(Collectors.toList());
        
        return PageResponse.<EnfantResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
    
    /**
     * Récupérer un enfant par ID
     */
    @Transactional(readOnly = true)
    public EnfantResponse getEnfantById(Long id) {
        Enfant enfant = enfantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enfant introuvable"));
        return mapToResponse(enfant, LocalDate.now());
    }
    
    /**
     * Récupérer les enfants d'un parent
     */
    @Transactional(readOnly = true)
    public List<EnfantResponse> getEnfantsByParentId(Long parentId) {
        LocalDate now = LocalDate.now();
        return enfantRepository.findByParentId(parentId).stream()
                .map(enfant -> mapToResponse(enfant, now))
                .collect(Collectors.toList());
    }
    
    /**
     * Supprimer un enfant
     */
    @Transactional
    public void deleteEnfant(Long id) {
        Enfant enfant = enfantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enfant introuvable"));
        
        String nomComplet = enfant.getNom() + " " + enfant.getPrenom();
        enfantRepository.delete(enfant);
        
        journalService.logAction("Suppression de l'enfant " + nomComplet);
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
    
    // Méthodes de mapping
    
    private EnfantSuggestion mapToSuggestion(Enfant enfant, LocalDate dateReference) {
        return EnfantSuggestion.builder()
                .id(enfant.getId())
                .nom(enfant.getNom())
                .prenom(enfant.getPrenom())
                .genre(enfant.getGenre())
                .dateNaissance(enfant.getDateNaissance())
                .age(calculateAge(enfant.getDateNaissance(), dateReference))
                .parentNom(enfant.getParent() != null ? enfant.getParent().getNom() : null)
                .parentPrenom(enfant.getParent() != null ? enfant.getParent().getPrenom() : null)
                .build();
    }
    
    private EnfantResponse mapToResponse(Enfant enfant, LocalDate dateReference) {
        return EnfantResponse.builder()
                .id(enfant.getId())
                .nom(enfant.getNom())
                .prenom(enfant.getPrenom())
                .genre(enfant.getGenre())
                .dateNaissance(enfant.getDateNaissance())
                .age(calculateAge(enfant.getDateNaissance(), dateReference))
                .adresse(enfant.getAdresse())
                .parentId(enfant.getParent() != null ? enfant.getParent().getId() : null)
                .parentNom(enfant.getParent() != null ? enfant.getParent().getNom() : null)
                .parentPrenom(enfant.getParent() != null ? enfant.getParent().getPrenom() : null)
                .bapteme(enfant.getBapteme())
                .createdAt(enfant.getCreatedAt())
                .updatedAt(enfant.getUpdatedAt())
                .build();
    }
}
