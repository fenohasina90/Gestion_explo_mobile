package com.explorateur.backend.controller;

import com.explorateur.backend.dto.CreateProgrammeRequest;
import com.explorateur.backend.dto.ProgrammeResponse;
import com.explorateur.backend.dto.UpdateProgrammeRequest;
import com.explorateur.backend.service.ProgrammeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * Controller pour la gestion des programmes
 */
@RestController
@RequestMapping("/api/programmes")
@RequiredArgsConstructor
@Tag(name = "Programmes", description = "API de gestion des programmes pédagogiques")
@SecurityRequirement(name = "bearerAuth")
public class ProgrammeController {
    
    private final ProgrammeService programmeService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur')")
    @Operation(summary = "Créer un programme", 
               description = "Crée un nouveau programme pédagogique (Directeur et Co-Directeur uniquement)")
    public ResponseEntity<ProgrammeResponse> createProgramme(
            @Valid @RequestBody CreateProgrammeRequest request) {
        ProgrammeResponse response = programmeService.createProgramme(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur')")
    @Operation(summary = "Modifier un programme",
               description = "Modifie un programme existant (Directeur et Co-Directeur uniquement)")
    public ResponseEntity<ProgrammeResponse> updateProgramme(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProgrammeRequest request) {
        ProgrammeResponse response = programmeService.updateProgramme(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur')")
    @Operation(summary = "Supprimer un programme",
               description = "Supprime un programme s'il n'est pas utilisé dans une CP (Directeur et Co-Directeur uniquement)")
    public ResponseEntity<Void> deleteProgramme(@PathVariable Long id) {
        programmeService.deleteProgramme(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Lister tous les programmes",
               description = "Récupère la liste de tous les programmes")
    public ResponseEntity<List<ProgrammeResponse>> getAllProgrammes() {
        List<ProgrammeResponse> programmes = programmeService.getAllProgrammes();
        return ResponseEntity.ok(programmes);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Obtenir un programme par ID",
               description = "Récupère les détails complets d'un programme")
    public ResponseEntity<ProgrammeResponse> getProgrammeById(@PathVariable Long id) {
        ProgrammeResponse response = programmeService.getProgrammeById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Filtrer les programmes",
               description = "Filtre les programmes par catégorie, classe et/ou nom")
    public ResponseEntity<List<ProgrammeResponse>> filterProgrammes(
            @Parameter(description = "ID de la catégorie (optionnel)")
            @RequestParam(required = false) Long categorieId,
            @Parameter(description = "ID de la classe (optionnel)")
            @RequestParam(required = false) Long classeId,
            @Parameter(description = "Nom du programme (recherche partielle, optionnel)")
            @RequestParam(required = false) String nom) {
        List<ProgrammeResponse> programmes = programmeService.filterProgrammes(categorieId, classeId, nom);
        return ResponseEntity.ok(programmes);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Rechercher/Filtrer des programmes",
               description = "Recherche et filtre les programmes par catégorie, classe et/ou nom (tous les paramètres sont optionnels)")
    public ResponseEntity<List<ProgrammeResponse>> searchByNom(
            @Parameter(description = "ID de la catégorie (optionnel)")
            @RequestParam(required = false) Long categorieId,
            @Parameter(description = "ID de la classe (optionnel)")
            @RequestParam(required = false) Long classeId,
            @Parameter(description = "Texte à rechercher dans le nom (optionnel)")
            @RequestParam(required = false) String nom) {
        List<ProgrammeResponse> programmes = programmeService.filterProgrammes(categorieId, classeId, nom);
        return ResponseEntity.ok(programmes);
    }
    
    @GetMapping("/categorie/{categorieId}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Obtenir les programmes d'une catégorie",
               description = "Récupère tous les programmes d'une catégorie spécifique")
    public ResponseEntity<List<ProgrammeResponse>> getProgrammesByCategorie(
            @PathVariable Long categorieId) {
        List<ProgrammeResponse> programmes = programmeService.getProgrammesByCategorie(categorieId);
        return ResponseEntity.ok(programmes);
    }
    
    @GetMapping("/classe/{classeId}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Obtenir les programmes d'une classe",
               description = "Récupère tous les programmes d'une classe spécifique")
    public ResponseEntity<List<ProgrammeResponse>> getProgrammesByClasse(
            @PathVariable Long classeId) {
        List<ProgrammeResponse> programmes = programmeService.getProgrammesByClasse(classeId);
        return ResponseEntity.ok(programmes);
    }
}
