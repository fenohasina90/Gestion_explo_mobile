package com.explorateur.backend.controller;

import com.explorateur.backend.dto.CreateInstructeurRequest;
import com.explorateur.backend.dto.InstructeurResponse;
import com.explorateur.backend.dto.InstructeurSuggestion;
import com.explorateur.backend.service.InstructeurService;
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
 * Controller pour la gestion des instructeurs
 */
@RestController
@RequestMapping("/api/instructeur")
@RequiredArgsConstructor
@Tag(name = "Instructeur", description = "API de gestion des instructeurs")
@SecurityRequirement(name = "bearerAuth")
public class InstructeurController {
    
    private final InstructeurService instructeurService;
    
    @PostMapping
    @PreAuthorize("hasRole('Directeur')")
    @Operation(summary = "Créer un nouvel instructeur",
               description = "Crée un nouvel instructeur avec vérification des doublons (Directeur uniquement)")
    public ResponseEntity<InstructeurResponse> createInstructeur(@Valid @RequestBody CreateInstructeurRequest request) {
        InstructeurResponse response = instructeurService.createInstructeur(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Directeur') or hasRole('Co-Directeur')")
    @Operation(summary = "Modifier un instructeur",
               description = "Met à jour les informations d'un instructeur (Directeur et Co-directeur uniquement)")
    public ResponseEntity<InstructeurResponse> updateInstructeur(
            @PathVariable Long id,
            @Valid @RequestBody CreateInstructeurRequest request) {
        InstructeurResponse response = instructeurService.updateInstructeur(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Directeur')")
    @Operation(summary = "Supprimer un instructeur",
               description = "Supprime un instructeur (Directeur uniquement)")
    public ResponseEntity<Void> deleteInstructeur(@PathVariable Long id) {
        instructeurService.deleteInstructeur(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    @Operation(summary = "Récupérer tous les instructeurs",
               description = "Retourne la liste de tous les instructeurs triés par nom")
    public ResponseEntity<List<InstructeurResponse>> getAllInstructeurs() {
        List<InstructeurResponse> instructeurs = instructeurService.getAllInstructeurs();
        return ResponseEntity.ok(instructeurs);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un instructeur par ID",
               description = "Retourne les détails d'un instructeur spécifique")
    public ResponseEntity<InstructeurResponse> getInstructeurById(@PathVariable Long id) {
        InstructeurResponse instructeur = instructeurService.getInstructeurById(id);
        return ResponseEntity.ok(instructeur);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Rechercher des instructeurs (auto-complétion)",
               description = "Recherche des instructeurs par nom ou prénom pour l'auto-complétion")
    public ResponseEntity<List<InstructeurSuggestion>> searchInstructeurs(
            @RequestParam(required = false) String query) {
        List<InstructeurSuggestion> suggestions = instructeurService.searchInstructeurs(query);
        return ResponseEntity.ok(suggestions);
    }
}
