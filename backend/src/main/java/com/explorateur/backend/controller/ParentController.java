package com.explorateur.backend.controller;

import com.explorateur.backend.dto.CreateParentRequest;
import com.explorateur.backend.dto.ParentResponse;
import com.explorateur.backend.dto.ParentSuggestion;
import com.explorateur.backend.service.ParentService;
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

@RestController
@RequestMapping("/api/parents")
@RequiredArgsConstructor
@Tag(name = "Parents", description = "API de gestion des parents")
@SecurityRequirement(name = "bearerAuth")
public class ParentController {
    
    private final ParentService parentService;
    
    @GetMapping("/search")
    @Operation(
            summary = "Rechercher des parents",
            description = "Recherche de parents par nom/prénom pour l'auto-complétion"
    )
    public ResponseEntity<List<ParentSuggestion>> searchParents(
            @Parameter(description = "Terme de recherche (nom ou prénom)")
            @RequestParam String query
    ) {
        return ResponseEntity.ok(parentService.searchParents(query));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('Directeur') or hasRole('Co_Directeur')")
    @Operation(
            summary = "Créer un nouveau parent",
            description = "Crée un nouveau parent. Accessible uniquement au Directeur et Co-Directeur."
    )
    public ResponseEntity<ParentResponse> createParent(@Valid @RequestBody CreateParentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parentService.createParent(request));
    }
    
    @GetMapping
    @Operation(summary = "Récupérer tous les parents")
    public ResponseEntity<List<ParentResponse>> getAllParents() {
        return ResponseEntity.ok(parentService.getAllParents());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un parent par ID")
    public ResponseEntity<ParentResponse> getParentById(
            @Parameter(description = "ID du parent")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(parentService.getParentById(id));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Directeur')")
    @Operation(
            summary = "Supprimer un parent",
            description = "Supprime un parent. Accessible uniquement au Directeur."
    )
    public ResponseEntity<Void> deleteParent(
            @Parameter(description = "ID du parent")
            @PathVariable Long id
    ) {
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
    }
}
