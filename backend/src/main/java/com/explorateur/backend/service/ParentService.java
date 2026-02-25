package com.explorateur.backend.service;

import com.explorateur.backend.dto.CreateParentRequest;
import com.explorateur.backend.dto.ParentResponse;
import com.explorateur.backend.dto.ParentSuggestion;
import com.explorateur.backend.entity.Parent;
import com.explorateur.backend.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParentService {
    
    private final ParentRepository parentRepository;
    private final JournalService journalService;
    
    /**
     * Recherche de parents pour l'auto-complétion
     */
    @Transactional(readOnly = true)
    public List<ParentSuggestion> searchParents(String query) {
        String searchTerm = "%" + query + "%";
        List<Parent> parents = parentRepository.searchByNomOrPrenom(searchTerm);
        
        return parents.stream()
                .map(this::mapToSuggestion)
                .collect(Collectors.toList());
    }
    
    /**
     * Créer un nouveau parent
     */
    @Transactional
    public ParentResponse createParent(CreateParentRequest request) {
        // Vérifier les doublons
        List<Parent> existants = parentRepository.findByNomAndPrenom(request.getNom(), request.getPrenom());
        if (!existants.isEmpty()) {
            throw new RuntimeException("Un parent avec ce nom et prénom existe déjà");
        }
        
        Parent parent = new Parent();
        parent.setNom(request.getNom());
        parent.setPrenom(request.getPrenom());
        parent.setAdresse(request.getAdresse());
        parent.setTelephone(request.getTelephone());
        
        Parent saved = parentRepository.save(parent);
        
        journalService.logAction("Création du parent " + parent.getNom() + " " + parent.getPrenom());
        
        return mapToResponse(saved);
    }
    
    /**
     * Récupérer tous les parents
     */
    @Transactional(readOnly = true)
    public List<ParentResponse> getAllParents() {
        return parentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer un parent par ID
     */
    @Transactional(readOnly = true)
    public ParentResponse getParentById(Long id) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parent introuvable"));
        return mapToResponse(parent);
    }
    
    /**
     * Supprimer un parent
     */
    @Transactional
    public void deleteParent(Long id) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parent introuvable"));
        
        String nomComplet = parent.getNom() + " " + parent.getPrenom();
        parentRepository.delete(parent);
        
        journalService.logAction("Suppression du parent " + nomComplet);
    }
    
    // Méthodes de mapping
    
    private ParentSuggestion mapToSuggestion(Parent parent) {
        return ParentSuggestion.builder()
                .id(parent.getId())
                .nom(parent.getNom())
                .prenom(parent.getPrenom())
                .telephone(parent.getTelephone())
                .adresse(parent.getAdresse())
                .build();
    }
    
    private ParentResponse mapToResponse(Parent parent) {
        return ParentResponse.builder()
                .id(parent.getId())
                .nom(parent.getNom())
                .prenom(parent.getPrenom())
                .adresse(parent.getAdresse())
                .telephone(parent.getTelephone())
                .createdAt(parent.getCreatedAt())
                .updatedAt(parent.getUpdatedAt())
                .build();
    }
}
