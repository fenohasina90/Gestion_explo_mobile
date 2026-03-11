package com.explorateur.backend.controller;

import com.explorateur.backend.dto.StatistiqueEnfantResponse;
import com.explorateur.backend.dto.StatistiqueFilterRequest;
import com.explorateur.backend.dto.StatistiqueStaffResponse;
import com.explorateur.backend.service.StatistiqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour les statistiques des enfants et des staffs
 */
@RestController
@RequestMapping("/api/statistiques")
@RequiredArgsConstructor
@Tag(name = "Statistiques", description = "Gestion des statistiques des enfants et des staffs")
public class StatistiqueController {
    
    private final StatistiqueService statistiqueService;
    
    /**
     * Récupère les statistiques de tous les enfants avec filtres
     */
    @GetMapping("/enfants")
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Récupérer les statistiques des enfants", 
               description = "Retourne les statistiques de tous les enfants selon les filtres (année d'exercice, classe, genre)")
    public ResponseEntity<List<StatistiqueEnfantResponse>> getStatistiquesEnfants(
            @RequestParam(required = false) Long anneeExerciceId,
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) String genre
    ) {
        StatistiqueFilterRequest filter = StatistiqueFilterRequest.builder()
                .anneeExerciceId(anneeExerciceId)
                .classeId(classeId)
                .genre(genre)
                .build();
        
        List<StatistiqueEnfantResponse> statistiques = statistiqueService.getStatistiquesEnfants(filter);
        return ResponseEntity.ok(statistiques);
    }
    
    /**
     * Récupère les statistiques d'un enfant spécifique
     */
    @GetMapping("/enfants/{inscriptionId}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Récupérer les statistiques d'un enfant", 
               description = "Retourne les statistiques détaillées d'un enfant spécifique")
    public ResponseEntity<StatistiqueEnfantResponse> getStatistiqueEnfant(
            @PathVariable Long inscriptionId
    ) {
        StatistiqueEnfantResponse statistique = statistiqueService.getStatistiqueEnfant(inscriptionId);
        return ResponseEntity.ok(statistique);
    }
    
    /**
     * Récupère les statistiques de tous les staffs avec filtres
     */
    @GetMapping("/staffs")
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Récupérer les statistiques des staffs", 
               description = "Retourne les statistiques de tous les staffs selon les filtres (année d'exercice)")
    public ResponseEntity<List<StatistiqueStaffResponse>> getStatistiquesStaffs(
            @RequestParam(required = false) Long anneeExerciceId
    ) {
        StatistiqueFilterRequest filter = StatistiqueFilterRequest.builder()
                .anneeExerciceId(anneeExerciceId)
                .build();
        
        List<StatistiqueStaffResponse> statistiques = statistiqueService.getStatistiquesStaffs(filter);
        return ResponseEntity.ok(statistiques);
    }
    
    /**
     * Récupère les statistiques d'un staff spécifique
     */
    @GetMapping("/staffs/{staffId}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co-Directeur', 'Secrétaire', 'Instructeur')")
    @Operation(summary = "Récupérer les statistiques d'un staff", 
               description = "Retourne les statistiques détaillées d'un staff spécifique")
    public ResponseEntity<StatistiqueStaffResponse> getStatistiqueStaff(
            @PathVariable Long staffId,
            @RequestParam(required = false) Long anneeExerciceId
    ) {
        StatistiqueStaffResponse statistique = statistiqueService.getStatistiqueStaff(staffId, anneeExerciceId);
        return ResponseEntity.ok(statistique);
    }
}
