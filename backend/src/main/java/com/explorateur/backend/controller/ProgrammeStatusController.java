package com.explorateur.backend.controller;

import com.explorateur.backend.dto.ChangeProgrammeStatusRequest;
import com.explorateur.backend.dto.HistoriqueProgrammesResponse;
import com.explorateur.backend.dto.ProgrammeStatusResponse;
import com.explorateur.backend.service.ProgrammeStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour la gestion des statuts de programme
 */
@RestController
@RequestMapping("/api/programme-status")
@RequiredArgsConstructor
@Tag(name = "Statuts de Programme", description = "API de gestion des statuts et historique des programmes")
@SecurityRequirement(name = "bearerAuth")
public class ProgrammeStatusController {
    
    private final ProgrammeStatusService programmeStatusService;
    
    @GetMapping("/statuts")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Lister tous les statuts disponibles",
               description = "Récupère la liste de tous les statuts possibles (En attente, En cours, Terminé)")
    public ResponseEntity<List<ProgrammeStatusResponse>> getAllStatuts() {
        List<ProgrammeStatusResponse> statuts = programmeStatusService.getAllStatuts();
        return ResponseEntity.ok(statuts);
    }
    
    @PostMapping("/change")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur')")
    @Operation(summary = "Changer le statut d'un programme",
               description = "Change le statut d'un programme dans une CP (Directeur et Co-Directeur uniquement)")
    public ResponseEntity<HistoriqueProgrammesResponse> changeProgrammeStatus(
            @Valid @RequestBody ChangeProgrammeStatusRequest request) {
        HistoriqueProgrammesResponse response = programmeStatusService.changeProgrammeStatus(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/initialize")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur')")
    @Operation(summary = "Initialiser le statut d'un programme",
               description = "Initialise le statut d'un programme dans une CP à 'En attente' (Directeur et Co-Directeur uniquement)")
    public ResponseEntity<HistoriqueProgrammesResponse> initializeProgrammeStatus(
            @Parameter(description = "ID du programme") @RequestParam Long programmeId,
            @Parameter(description = "ID de la classe progressive") @RequestParam Long classeProgressiveId) {
        HistoriqueProgrammesResponse response = programmeStatusService.initializeProgrammeStatus(programmeId, classeProgressiveId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/historique/programme/{programmeId}/cp/{classeProgressiveId}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Obtenir l'historique d'un programme dans une CP",
               description = "Récupère l'historique complet des changements de statut d'un programme dans une CP")
    public ResponseEntity<List<HistoriqueProgrammesResponse>> getHistoriqueByProgrammeAndCP(
            @PathVariable Long programmeId,
            @PathVariable Long classeProgressiveId) {
        List<HistoriqueProgrammesResponse> historique = programmeStatusService
                .getHistoriqueByProgrammeAndCP(programmeId, classeProgressiveId);
        return ResponseEntity.ok(historique);
    }
    
    @GetMapping("/historique/programme/{programmeId}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Obtenir l'historique complet d'un programme",
               description = "Récupère l'historique complet d'un programme dans toutes les CP")
    public ResponseEntity<List<HistoriqueProgrammesResponse>> getHistoriqueByProgramme(
            @PathVariable Long programmeId) {
        List<HistoriqueProgrammesResponse> historique = programmeStatusService.getHistoriqueByProgramme(programmeId);
        return ResponseEntity.ok(historique);
    }
    
    @GetMapping("/current/programme/{programmeId}/cp/{classeProgressiveId}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Obtenir le statut actuel d'un programme",
               description = "Récupère le statut actuel d'un programme dans une CP spécifique")
    public ResponseEntity<HistoriqueProgrammesResponse> getCurrentStatus(
            @PathVariable Long programmeId,
            @PathVariable Long classeProgressiveId) {
        HistoriqueProgrammesResponse current = programmeStatusService.getCurrentStatus(programmeId, classeProgressiveId);
        return ResponseEntity.ok(current);
    }
}
