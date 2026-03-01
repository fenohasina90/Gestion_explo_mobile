package com.explorateur.backend.service;

import com.explorateur.backend.dto.CreateProgrammeRequest;
import com.explorateur.backend.dto.ProgrammeResponse;
import com.explorateur.backend.dto.UpdateProgrammeRequest;
import com.explorateur.backend.entity.CategorieProgramme;
import com.explorateur.backend.entity.Classe;
import com.explorateur.backend.entity.Programme;
import com.explorateur.backend.repository.CategorieProgrammeRepository;
import com.explorateur.backend.repository.ClasseRepository;
import com.explorateur.backend.repository.ProgrammeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des programmes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProgrammeService {
    
    private final ProgrammeRepository programmeRepository;
    private final CategorieProgrammeRepository categorieProgrammeRepository;
    private final ClasseRepository classeRepository;
    
    /**
     * Créer un nouveau programme
     */
    @Transactional
    public ProgrammeResponse createProgramme(CreateProgrammeRequest request) {
        log.info("Création d'un nouveau programme: {}", request.getNom());
        
        // Vérifier que la catégorie existe
        CategorieProgramme categorie = categorieProgrammeRepository.findById(request.getCategorieId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
        
        // Vérifier que la classe existe
        Classe classe = classeRepository.findById(request.getClasseId())
                .orElseThrow(() -> new RuntimeException("Classe introuvable"));
        
        Programme programme = Programme.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .categorie(categorie)
                .classe(classe)
                .build();
        
        Programme saved = programmeRepository.save(programme);
        
        return mapToResponse(saved);
    }
    
    /**
     * Modifier un programme
     */
    @Transactional
    public ProgrammeResponse updateProgramme(Long id, UpdateProgrammeRequest request) {
        log.info("Modification du programme ID: {}", id);
        
        Programme programme = programmeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programme introuvable"));
        
        // Vérifier que la catégorie existe
        CategorieProgramme categorie = categorieProgrammeRepository.findById(request.getCategorieId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
        
        // Vérifier que la classe existe
        Classe classe = classeRepository.findById(request.getClasseId())
                .orElseThrow(() -> new RuntimeException("Classe introuvable"));
        
        programme.setNom(request.getNom());
        programme.setDescription(request.getDescription());
        programme.setCategorie(categorie);
        programme.setClasse(classe);
        // updatedAt sera mis à jour automatiquement par @PreUpdate
        
        Programme updated = programmeRepository.save(programme);
        
        return mapToResponse(updated);
    }
    
    /**
     * Supprimer un programme
     */
    @Transactional
    public void deleteProgramme(Long id) {
        log.info("Suppression du programme ID: {}", id);
        
        Programme programme = programmeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programme introuvable"));
        
        // Vérifier que le programme n'est pas utilisé dans une CP
        // TODO: Activer cette vérification quand l'entité CpDetails sera créée
        // Note: Pour l'instant, la suppression est autorisée car CpDetails n'existe pas encore
        /*
        boolean isUsed = programmeRepository.isProgrammeUsedInCP(id);
        if (isUsed) {
            throw new RuntimeException("Impossible de supprimer ce programme car il est utilisé dans une Classe Progressive");
        }
        */
        
        programmeRepository.delete(programme);
    }
    
    /**
     * Obtenir tous les programmes
     */
    @Transactional(readOnly = true)
    public List<ProgrammeResponse> getAllProgrammes() {
        log.info("Récupération de tous les programmes");
        
        return programmeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtenir un programme par ID
     */
    @Transactional(readOnly = true)
    public ProgrammeResponse getProgrammeById(Long id) {
        log.info("Récupération du programme ID: {}", id);
        
        Programme programme = programmeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programme introuvable"));
        
        return mapToResponse(programme);
    }
    
    /**
     * Filtrer les programmes
     */
    @Transactional(readOnly = true)
    public List<ProgrammeResponse> filterProgrammes(Long categorieId, Long classeId, String nom) {
        log.info("Filtrage des programmes - Catégorie: {}, Classe: {}, Nom: {}", categorieId, classeId, nom);
        
        return programmeRepository.filterProgrammes(categorieId, classeId, nom)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Rechercher des programmes par nom
     */
    @Transactional(readOnly = true)
    public List<ProgrammeResponse> searchByNom(String nom) {
        log.info("Recherche de programmes par nom: {}", nom);
        
        return programmeRepository.searchByNom(nom)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtenir les programmes d'une catégorie
     */
    @Transactional(readOnly = true)
    public List<ProgrammeResponse> getProgrammesByCategorie(Long categorieId) {
        log.info("Récupération des programmes de la catégorie ID: {}", categorieId);
        
        return programmeRepository.findByCategorieId(categorieId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtenir les programmes d'une classe
     */
    @Transactional(readOnly = true)
    public List<ProgrammeResponse> getProgrammesByClasse(Long classeId) {
        log.info("Récupération des programmes de la classe ID: {}", classeId);
        
        return programmeRepository.findByClasseId(classeId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Mapper une entité vers un DTO de réponse
     */
    private ProgrammeResponse mapToResponse(Programme programme) {
        return ProgrammeResponse.builder()
                .id(programme.getId())
                .nom(programme.getNom())
                .description(programme.getDescription())
                .categorieId(programme.getCategorie() != null ? programme.getCategorie().getId() : null)
                .categorieNom(programme.getCategorie() != null ? programme.getCategorie().getNom() : null)
                .classeId(programme.getClasse() != null ? programme.getClasse().getId() : null)
                .classeNom(programme.getClasse() != null ? programme.getClasse().getNom() : null)
                .createdAt(programme.getCreatedAt())
                .updatedAt(programme.getUpdatedAt())
                .build();
    }
}
