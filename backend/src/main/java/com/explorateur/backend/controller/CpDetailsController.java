package com.explorateur.backend.controller;

import com.explorateur.backend.dto.AddProgrammeToCpRequest;
import com.explorateur.backend.dto.CpDetailsResponse;
import com.explorateur.backend.dto.UpdateCpDetailsInstructeurRequest;
import com.explorateur.backend.service.CpDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour la gestion des affectations de programmes aux CP
 */
@RestController
@RequestMapping("/api/cp-details")
@RequiredArgsConstructor
@Tag(name = "CP Details", description = "Endpoints pour l'affectation de programmes aux CP")
public class CpDetailsController {
    
    private final CpDetailsService cpDetailsService;
    
    /**
     * Ajouter un programme à une CP avec un ou plusieurs instructeurs
     * Accessible uniquement au Directeur et Co-Directeur
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur')")
    @Operation(summary = "Ajouter un programme à une CP", 
               description = "Ajoute un programme à une CP avec un ou plusieurs instructeurs et initialisation automatique du statut 'En attente'. Réservé au Directeur et Co-Directeur.")
    public ResponseEntity<CpDetailsResponse> addProgrammeToCP(
            @Valid @RequestBody AddProgrammeToCpRequest request) {
        CpDetailsResponse response = cpDetailsService.addProgrammeToCP(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Modifier les instructeurs d'un programme dans une CP
     * Accessible uniquement au Directeur et Co-Directeur
     */
    @PutMapping("/{id}/instructeur")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur')")
    @Operation(summary = "Modifier les instructeurs", 
               description = "Modifie les instructeurs assignés à un programme dans une CP (remplace tous les instructeurs existants). Réservé au Directeur et Co-Directeur.")
    public ResponseEntity<CpDetailsResponse> updateInstructeur(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCpDetailsInstructeurRequest request) {
        CpDetailsResponse response = cpDetailsService.updateInstructeur(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Supprimer un programme d'une CP
     * Accessible uniquement au Directeur et Co-Directeur
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur')")
    @Operation(summary = "Supprimer un programme d'une CP", 
               description = "Retire un programme d'une CP. Impossible si le statut est 'Terminé'. Réservé au Directeur et Co-Directeur.")
    public ResponseEntity<Void> removeProgrammeFromCP(@PathVariable Long id) {
        cpDetailsService.removeProgrammeFromCP(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Récupérer tous les programmes d'une CP
     * Accessible à tous les rôles authentifiés
     */
    @GetMapping("/cp/{classeProgressiveId}")
    @Operation(summary = "Voir les programmes d'une CP", 
               description = "Récupère la liste de tous les programmes affectés à une CP")
    public ResponseEntity<List<CpDetailsResponse>> getProgrammesByCP(
            @PathVariable Long classeProgressiveId) {
        List<CpDetailsResponse> response = cpDetailsService.getProgrammesByCP(classeProgressiveId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Récupérer un détail par ID
     * Accessible à tous les rôles authentifiés
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un détail CP par ID", 
               description = "Récupère les détails d'une affectation de programme à une CP")
    public ResponseEntity<CpDetailsResponse> getCpDetailsById(@PathVariable Long id) {
        CpDetailsResponse response = cpDetailsService.getCpDetailsById(id);
        return ResponseEntity.ok(response);
    }
}
