package com.explorateur.backend.controller;

import com.explorateur.backend.dto.ClasseProgressiveResponse;
import com.explorateur.backend.dto.CreateClasseProgressiveRequest;
import com.explorateur.backend.dto.UpdateClasseProgressiveRequest;
import com.explorateur.backend.service.ClasseProgressiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller pour la gestion des Classes Progressives (CP)
 */
@RestController
@RequestMapping("/api/classe-progressive")
@RequiredArgsConstructor
@Tag(name = "Classe Progressive", description = "Endpoints pour la gestion des Classes Progressives (CP)")
public class ClasseProgressiveController {
    
    private final ClasseProgressiveService cpService;
    
    /**
     * Créer une nouvelle CP
     * Accessible uniquement au Directeur et Co-Directeur
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur')")
    @Operation(summary = "Créer une nouvelle CP", 
               description = "Crée une nouvelle Classe Progressive. Réservé au Directeur et Co-Directeur.")
    public ResponseEntity<ClasseProgressiveResponse> createClasseProgressive(
            @Valid @RequestBody CreateClasseProgressiveRequest request) {
        ClasseProgressiveResponse response = cpService.createClasseProgressive(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Modifier une CP existante
     * Accessible uniquement au Directeur et Co-Directeur
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur')")
    @Operation(summary = "Modifier une CP", 
               description = "Modifie une Classe Progressive existante. Réservé au Directeur et Co-Directeur.")
    public ResponseEntity<ClasseProgressiveResponse> updateClasseProgressive(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClasseProgressiveRequest request) {
        ClasseProgressiveResponse response = cpService.updateClasseProgressive(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Supprimer une CP
     * Accessible uniquement au Directeur
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Directeur')")
    @Operation(summary = "Supprimer une CP", 
               description = "Supprime une Classe Progressive. Réservé au Directeur uniquement.")
    public ResponseEntity<Void> deleteClasseProgressive(@PathVariable Long id) {
        cpService.deleteClasseProgressive(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Récupérer toutes les CP
     * Accessible à tous les rôles authentifiés
     */
    @GetMapping
    @Operation(summary = "Récupérer toutes les CP", 
               description = "Récupère la liste de toutes les Classes Progressives")
    public ResponseEntity<List<ClasseProgressiveResponse>> getAllClassesProgressives() {
        List<ClasseProgressiveResponse> response = cpService.getAllClassesProgressives();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Récupérer une CP par ID
     * Accessible à tous les rôles authentifiés
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une CP par ID", 
               description = "Récupère les détails d'une Classe Progressive par son ID")
    public ResponseEntity<ClasseProgressiveResponse> getClasseProgressiveById(@PathVariable Long id) {
        ClasseProgressiveResponse response = cpService.getClasseProgressiveById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Filtrer les CP avec paramètres optionnels
     * Accessible à tous les rôles authentifiés
     * Tous les paramètres sont optionnels
     */
    @GetMapping("/filter")
    @Operation(summary = "Filtrer les CP", 
               description = "Filtre les Classes Progressives avec paramètres optionnels (dates et/ou année)")
    public ResponseEntity<List<ClasseProgressiveResponse>> filterCP(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long anneeExerciceId) {
        
        List<ClasseProgressiveResponse> response;
        
        // Si aucun paramètre, retourner toutes les CP
        if (dateDebut == null && dateFin == null && anneeExerciceId == null) {
            response = cpService.getAllClassesProgressives();
        }
        // Si dates + année
        else if (dateDebut != null && dateFin != null && anneeExerciceId != null) {
            response = cpService.filterByDateRangeAndAnneeExercice(dateDebut, dateFin, anneeExerciceId);
        }
        // Si seulement dates
        else if (dateDebut != null && dateFin != null) {
            response = cpService.filterByDateRange(dateDebut, dateFin);
        }
        // Si seulement année
        else if (anneeExerciceId != null) {
            response = cpService.filterByAnneeExercice(anneeExerciceId);
        }
        // Autres cas : retourner toutes
        else {
            response = cpService.getAllClassesProgressives();
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Filtrer les CP par plage de dates
     * Accessible à tous les rôles authentifiés
     */
    @GetMapping("/filter/dates")
    @Operation(summary = "Filtrer les CP par dates", 
               description = "Filtre les Classes Progressives entre deux dates")
    public ResponseEntity<List<ClasseProgressiveResponse>> filterByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        List<ClasseProgressiveResponse> response = cpService.filterByDateRange(dateDebut, dateFin);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Filtrer les CP par année d'exercice
     * Accessible à tous les rôles authentifiés
     */
    @GetMapping("/filter/annee/{anneeExerciceId}")
    @Operation(summary = "Filtrer les CP par année d'exercice", 
               description = "Filtre les Classes Progressives par année d'exercice")
    public ResponseEntity<List<ClasseProgressiveResponse>> filterByAnneeExercice(
            @PathVariable Long anneeExerciceId) {
        List<ClasseProgressiveResponse> response = cpService.filterByAnneeExercice(anneeExerciceId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Filtrer les CP par plage de dates ET année d'exercice
     * Accessible à tous les rôles authentifiés
     */
    @GetMapping("/filter/dates-annee")
    @Operation(summary = "Filtrer les CP par dates et année", 
               description = "Filtre les Classes Progressives par dates et année d'exercice")
    public ResponseEntity<List<ClasseProgressiveResponse>> filterByDateRangeAndAnneeExercice(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam Long anneeExerciceId) {
        List<ClasseProgressiveResponse> response = cpService.filterByDateRangeAndAnneeExercice(
                dateDebut, dateFin, anneeExerciceId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Clôturer une CP
     * Accessible uniquement au Directeur et Co-Directeur
     */
    @PutMapping("/{id}/cloturer")
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur')")
    @Operation(summary = "Clôturer une CP", 
               description = "Met l'état de la CP à clôturé (etat=1). Une fois clôturée, les présences et changements de statuts sont interdits. Réservé au Directeur et Co-Directeur.")
    public ResponseEntity<ClasseProgressiveResponse> cloturerCP(@PathVariable Long id) {
        ClasseProgressiveResponse response = cpService.cloturerCP(id);
        return ResponseEntity.ok(response);
    }
}
