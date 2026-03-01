package com.explorateur.backend.service;

import com.explorateur.backend.dto.CategorieProgrammeResponse;
import com.explorateur.backend.dto.CreateCategorieProgrammeRequest;
import com.explorateur.backend.dto.UpdateCategorieProgrammeRequest;
import com.explorateur.backend.entity.CategorieProgramme;
import com.explorateur.backend.repository.CategorieProgrammeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des catégories de programme
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategorieProgrammeService {
    
    private final CategorieProgrammeRepository categorieProgrammeRepository;
    
    /**
     * Créer une nouvelle catégorie de programme
     */
    @Transactional
    public CategorieProgrammeResponse createCategorie(CreateCategorieProgrammeRequest request) {
        log.info("Création d'une nouvelle catégorie: {}", request.getNom());
        
        // Vérifier que le nom n'existe pas déjà
        if (categorieProgrammeRepository.existsByNom(request.getNom())) {
            throw new RuntimeException("Une catégorie avec ce nom existe déjà");
        }
        
        CategorieProgramme categorie = CategorieProgramme.builder()
                .nom(request.getNom())
                .build();
        
        CategorieProgramme saved = categorieProgrammeRepository.save(categorie);
        
        return mapToResponse(saved);
    }
    
    /**
     * Modifier une catégorie de programme
     */
    @Transactional
    public CategorieProgrammeResponse updateCategorie(Long id, UpdateCategorieProgrammeRequest request) {
        log.info("Modification de la catégorie ID: {} vers nom: {}", id, request.getNom());
        
        CategorieProgramme categorie = categorieProgrammeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
        
        // Vérifier que le nouveau nom n'existe pas déjà (sauf pour cette catégorie)
        if (categorieProgrammeRepository.existsByNomAndIdNot(request.getNom(), id)) {
            throw new RuntimeException("Une catégorie avec ce nom existe déjà");
        }
        
        categorie.setNom(request.getNom());
        CategorieProgramme updated = categorieProgrammeRepository.save(categorie);
        
        return mapToResponse(updated);
    }
    
    /**
     * Supprimer une catégorie de programme
     */
    @Transactional
    public void deleteCategorie(Long id) {
        log.info("Suppression de la catégorie ID: {}", id);
        
        CategorieProgramme categorie = categorieProgrammeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
        
        // Vérifier qu'aucun programme n'utilise cette catégorie
        long nombreProgrammes = categorieProgrammeRepository.countProgrammesByCategorie(id);
        if (nombreProgrammes > 0) {
            throw new RuntimeException("Impossible de supprimer cette catégorie car elle est utilisée par " 
                    + nombreProgrammes + " programme(s)");
        }
        
        categorieProgrammeRepository.delete(categorie);
    }
    
    /**
     * Obtenir toutes les catégories
     */
    @Transactional(readOnly = true)
    public List<CategorieProgrammeResponse> getAllCategories() {
        log.info("Récupération de toutes les catégories");
        
        return categorieProgrammeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtenir une catégorie par ID
     */
    @Transactional(readOnly = true)
    public CategorieProgrammeResponse getCategorieById(Long id) {
        log.info("Récupération de la catégorie ID: {}", id);
        
        CategorieProgramme categorie = categorieProgrammeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
        
        return mapToResponse(categorie);
    }
    
    /**
     * Compter les programmes par catégorie
     */
    @Transactional(readOnly = true)
    public Long countProgrammesByCategorie(Long categorieId) {
        log.info("Comptage des programmes pour la catégorie ID: {}", categorieId);
        
        return categorieProgrammeRepository.countProgrammesByCategorie(categorieId);
    }
    
    /**
     * Mapper une entité vers un DTO de réponse
     */
    private CategorieProgrammeResponse mapToResponse(CategorieProgramme categorie) {
        long nombreProgrammes = categorieProgrammeRepository.countProgrammesByCategorie(categorie.getId());
        
        return CategorieProgrammeResponse.builder()
                .id(categorie.getId())
                .nom(categorie.getNom())
                .nombreProgrammes(nombreProgrammes)
                .build();
    }
}
