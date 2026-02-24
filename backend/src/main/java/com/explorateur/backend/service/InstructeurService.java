package com.explorateur.backend.service;

import com.explorateur.backend.dto.CreateInstructeurRequest;
import com.explorateur.backend.dto.InstructeurResponse;
import com.explorateur.backend.dto.InstructeurSuggestion;
import com.explorateur.backend.entity.Instructeur;
import com.explorateur.backend.repository.InstructeurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des instructeurs
 */
@Service
@RequiredArgsConstructor
public class InstructeurService {
    
    private final InstructeurRepository instructeurRepository;
    private final JournalService journalService;
    
    /**
     * Crée un nouvel instructeur
     */
    @Transactional
    public InstructeurResponse createInstructeur(CreateInstructeurRequest request) {
        // Vérifier si l'instructeur existe déjà
        instructeurRepository.findByNomAndPrenom(request.getNom(), request.getPrenom())
            .ifPresent(i -> {
                throw new RuntimeException("Un instructeur avec ce nom et prénom existe déjà");
            });
        
        Instructeur instructeur = Instructeur.builder()
            .nom(request.getNom())
            .prenom(request.getPrenom())
            .genre(request.getGenre())
            .totem(request.getTotem())
            .telephone(request.getTelephone())
            .estChefGuide(request.getEstChefGuide() != null ? request.getEstChefGuide() : false)
            .build();
        
        Instructeur saved = instructeurRepository.save(instructeur);
        
        // Log l'action
        journalService.logAction("Création de l'instructeur " + saved.getNom() + " " + saved.getPrenom());
        
        return mapToResponse(saved);
    }
    
    /**
     * Met à jour un instructeur
     */
    @Transactional
    public InstructeurResponse updateInstructeur(Long id, CreateInstructeurRequest request) {
        Instructeur instructeur = instructeurRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Instructeur introuvable"));
        
        // Vérifier si le nouveau nom/prénom n'existe pas déjà
        if (!instructeur.getNom().equalsIgnoreCase(request.getNom()) || 
            !instructeur.getPrenom().equalsIgnoreCase(request.getPrenom())) {
            instructeurRepository.findByNomAndPrenom(request.getNom(), request.getPrenom())
                .ifPresent(i -> {
                    if (!i.getId().equals(id)) {
                        throw new RuntimeException("Un instructeur avec ce nom et prénom existe déjà");
                    }
                });
        }
        
        String oldName = instructeur.getNom() + " " + instructeur.getPrenom();
        
        instructeur.setNom(request.getNom());
        instructeur.setPrenom(request.getPrenom());
        instructeur.setGenre(request.getGenre());
        instructeur.setTotem(request.getTotem());
        instructeur.setTelephone(request.getTelephone());
        instructeur.setEstChefGuide(request.getEstChefGuide() != null ? request.getEstChefGuide() : false);
        
        Instructeur updated = instructeurRepository.save(instructeur);
        
        // Log l'action
        String newName = updated.getNom() + " " + updated.getPrenom();
        if (!oldName.equals(newName)) {
            journalService.logAction("Modification de l'instructeur " + oldName + " en " + newName);
        } else {
            journalService.logAction("Modification des informations de l'instructeur " + newName);
        }
        
        return mapToResponse(updated);
    }
    
    /**
     * Supprime un instructeur
     */
    @Transactional
    public void deleteInstructeur(Long id) {
        Instructeur instructeur = instructeurRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Instructeur introuvable"));
        
        String name = instructeur.getNom() + " " + instructeur.getPrenom();
        instructeurRepository.delete(instructeur);
        
        // Log la suppression
        journalService.logAction("Suppression de l'instructeur " + name);
    }
    
    /**
     * Récupère tous les instructeurs
     */
    @Transactional(readOnly = true)
    public List<InstructeurResponse> getAllInstructeurs() {
        return instructeurRepository.findAllByOrderByNomAsc().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère un instructeur par ID
     */
    @Transactional(readOnly = true)
    public InstructeurResponse getInstructeurById(Long id) {
        Instructeur instructeur = instructeurRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Instructeur introuvable"));
        return mapToResponse(instructeur);
    }
    
    /**
     * Recherche des instructeurs pour auto-complétion
     */
    @Transactional(readOnly = true)
    public List<InstructeurSuggestion> searchInstructeurs(String search) {
        if (search == null || search.trim().isEmpty()) {
            return instructeurRepository.findAllByOrderByNomAsc().stream()
                .map(this::mapToSuggestion)
                .collect(Collectors.toList());
        }
        
        return instructeurRepository.searchByNomOrPrenom(search).stream()
            .map(this::mapToSuggestion)
            .collect(Collectors.toList());
    }
    
    /**
     * Mapper Instructeur vers InstructeurResponse
     */
    private InstructeurResponse mapToResponse(Instructeur instructeur) {
        return InstructeurResponse.builder()
            .id(instructeur.getId())
            .nom(instructeur.getNom())
            .prenom(instructeur.getPrenom())
            .genre(instructeur.getGenre())
            .totem(instructeur.getTotem())
            .telephone(instructeur.getTelephone())
            .estChefGuide(instructeur.getEstChefGuide())
            .createdAt(instructeur.getCreatedAt())
            .updatedAt(instructeur.getUpdatedAt())
            .build();
    }
    
    /**
     * Mapper Instructeur vers InstructeurSuggestion
     */
    private InstructeurSuggestion mapToSuggestion(Instructeur instructeur) {
        return InstructeurSuggestion.builder()
            .id(instructeur.getId())
            .nom(instructeur.getNom())
            .prenom(instructeur.getPrenom())
            .nomComplet(instructeur.getNom() + " " + instructeur.getPrenom())
            .build();
    }
}
