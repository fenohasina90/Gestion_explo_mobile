package com.explorateur.backend.controller;

import com.explorateur.backend.dto.CategorieProgrammeResponse;
import com.explorateur.backend.dto.CreateCategorieProgrammeRequest;
import com.explorateur.backend.dto.UpdateCategorieProgrammeRequest;
import com.explorateur.backend.service.CategorieProgrammeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour la gestion des catégories de programme
 */
@RestController
@RequestMapping("/api/categories-programme")
@RequiredArgsConstructor
@Tag(name = "Catégories de Programme", description = "API de gestion des catégories de programme")
@SecurityRequirement(name = "bearerAuth")
public class CategorieProgrammeController {
    
    private final CategorieProgrammeService categorieProgrammeService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur')")
    @Operation(summary = "Créer une catégorie de programme", 
               description = "Crée une nouvelle catégorie de programme (Directeur et Co-Directeur uniquement)")
    public ResponseEntity<CategorieProgrammeResponse> createCategorie(
            @Valid @RequestBody CreateCategorieProgrammeRequest request) {
        CategorieProgrammeResponse response = categorieProgrammeService.createCategorie(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur')")
    @Operation(summary = "Modifier une catégorie de programme",
               description = "Modifie une catégorie de programme existante (Directeur et Co-Directeur uniquement)")
    public ResponseEntity<CategorieProgrammeResponse> updateCategorie(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategorieProgrammeRequest request) {
        CategorieProgrammeResponse response = categorieProgrammeService.updateCategorie(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur')")
    @Operation(summary = "Supprimer une catégorie de programme",
               description = "Supprime une catégorie de programme si elle n'est pas utilisée (Directeur et Co-Directeur uniquement)")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        categorieProgrammeService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_Directeur', 'ROLE_Co-Directeur', 'ROLE_Co_Directeur', 'ROLE_Secrétaire', 'ROLE_Instructeur')")
    @Operation(summary = "Lister toutes les catégories de programme",
               description = "Récupère la liste de toutes les catégories de programme")
    public ResponseEntity<List<CategorieProgrammeResponse>> getAllCategories() {
        List<CategorieProgrammeResponse> categories = categorieProgrammeService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_Directeur', 'ROLE_Co-Directeur', 'ROLE_Co_Directeur', 'ROLE_Secrétaire', 'ROLE_Instructeur')")
    @Operation(summary = "Obtenir une catégorie par ID",
               description = "Récupère les détails d'une catégorie de programme")
    public ResponseEntity<CategorieProgrammeResponse> getCategorieById(@PathVariable Long id) {
        CategorieProgrammeResponse response = categorieProgrammeService.getCategorieById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/count-programmes")
    @PreAuthorize("hasAnyAuthority('ROLE_Directeur', 'ROLE_Co-Directeur', 'ROLE_Co_Directeur', 'ROLE_Secrétaire', 'ROLE_Instructeur')")
    @Operation(summary = "Compter les programmes d'une catégorie",
               description = "Compte le nombre de programmes associés à une catégorie")
    public ResponseEntity<Long> countProgrammesByCategorie(@PathVariable Long id) {
        Long count = categorieProgrammeService.countProgrammesByCategorie(id);
        return ResponseEntity.ok(count);
    }
}
