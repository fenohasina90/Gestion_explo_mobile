package com.explorateur.backend.controller;

import com.explorateur.backend.dto.TypeResponse;
import com.explorateur.backend.service.TypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour la gestion des types de mouvements budgétaires
 */
@RestController
@RequestMapping("/api/types-mouvement")
@RequiredArgsConstructor
@Tag(name = "Types de Mouvement", description = "API de gestion des types de mouvements budgétaires")
@SecurityRequirement(name = "bearerAuth")
public class TypeController {
    
    private final TypeService typeService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Lister tous les types de mouvements",
               description = "Récupère la liste de tous les types de mouvements budgétaires (RECETTE, DEPENSE)")
    public ResponseEntity<List<TypeResponse>> getAllTypes() {
        List<TypeResponse> types = typeService.getAllTypes();
        return ResponseEntity.ok(types);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Obtenir un type par ID",
               description = "Récupère les détails d'un type de mouvement budgétaire")
    public ResponseEntity<TypeResponse> getTypeById(@PathVariable Long id) {
        TypeResponse response = typeService.getTypeById(id);
        return ResponseEntity.ok(response);
    }
}
