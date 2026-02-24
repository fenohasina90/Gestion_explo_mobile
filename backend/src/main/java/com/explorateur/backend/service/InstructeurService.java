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
        
        StringBuilder logMessage = new StringBuilder("Modification de l'instructeur ");
        logMessage.append(instructeur.getNom()).append(" ").append(instructeur.getPrenom()).append(": ");
        
        boolean hasChanges = false;
        
        // Suivre les changements pour le journal
        if (!instructeur.getNom().equals(request.getNom())) {
            logMessage.append("Nom: ").append(instructeur.getNom()).append(" → ").append(request.getNom()).append("; ");
            instructeur.setNom(request.getNom());
            hasChanges = true;
        }
        if (!instructeur.getPrenom().equals(request.getPrenom())) {
            logMessage.append("Prénom: ").append(instructeur.getPrenom()).append(" → ").append(request.getPrenom()).append("; ");
            instructeur.setPrenom(request.getPrenom());
            hasChanges = true;
        }
        if (!instructeur.getGenre().equals(request.getGenre())) {
            logMessage.append("Genre: ").append(instructeur.getGenre()).append(" → ").append(request.getGenre()).append("; ");
            instructeur.setGenre(request.getGenre());
            hasChanges = true;
        }
        if ((instructeur.getTotem() == null && request.getTotem() != null) ||
            (instructeur.getTotem() != null && !instructeur.getTotem().equals(request.getTotem()))) {
            String oldTotem = instructeur.getTotem() != null ? instructeur.getTotem() : "(vide)";
            String newTotem = request.getTotem() != null ? request.getTotem() : "(vide)";
            logMessage.append("Totem: ").append(oldTotem).append(" → ").append(newTotem).append("; ");
            instructeur.setTotem(request.getTotem());
            hasChanges = true;
        }
        if ((instructeur.getTelephone() == null && request.getTelephone() != null) ||
            (instructeur.getTelephone() != null && !instructeur.getTelephone().equals(request.getTelephone()))) {
            String oldTel = instructeur.getTelephone() != null ? instructeur.getTelephone() : "(vide)";
            String newTel = request.getTelephone() != null ? request.getTelephone() : "(vide)";
            logMessage.append("Téléphone: ").append(oldTel).append(" → ").append(newTel).append("; ");
            instructeur.setTelephone(request.getTelephone());
            hasChanges = true;
        }
        
        Boolean newEstChefGuide = request.getEstChefGuide() != null ? request.getEstChefGuide() : false;
        if (!instructeur.getEstChefGuide().equals(newEstChefGuide)) {
            logMessage.append("Chef Guide: ").append(instructeur.getEstChefGuide()).append(" → ").append(newEstChefGuide).append("; ");
            instructeur.setEstChefGuide(newEstChefGuide);
            hasChanges = true;
        }
        
        Instructeur updated = instructeurRepository.save(instructeur);
        
        // Enregistrer dans le journal si des modifications ont été effectuées
        if (hasChanges) {
            journalService.logAction(logMessage.toString());
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
