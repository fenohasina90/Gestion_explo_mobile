package com.explorateur.backend.controller;

import com.explorateur.backend.dto.HistoriqueProgrammeDto;
import com.explorateur.backend.dto.ProgressionAnnuelleDto;
import com.explorateur.backend.dto.StatistiquesAnnuellesDto;
import com.explorateur.backend.service.HistoriqueProgrammeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour l'historique des programmes.
 * Endpoints de consultation uniquement (l'enregistrement est automatique lors du changement de statut).
 */
@Slf4j
@RestController
@RequestMapping("/api/historique-programmes")
@RequiredArgsConstructor
@Tag(name = "Historique Programmes", description = "APIs pour consulter l'historique et la progression des programmes")
public class HistoriqueProgrammeController {
    
    private final HistoriqueProgrammeService historiqueProgrammeService;
    
    /**
     * Récupère l'historique complet d'un programme (toutes années confondues)
     */
    @GetMapping("/programme/{programmeId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Historique complet d'un programme", 
               description = "Récupère tous les changements de statut d'un programme sur toutes les années d'exercice")
    public ResponseEntity<List<HistoriqueProgrammeDto>> getHistoriqueProgramme(
            @Parameter(description = "ID du programme", required = true)
            @PathVariable Long programmeId) {
        
        log.info("GET /api/historique-programmes/programme/{}", programmeId);
        List<HistoriqueProgrammeDto> historique = historiqueProgrammeService.getHistoriqueProgramme(programmeId);
        return ResponseEntity.ok(historique);
    }
    
    /**
     * Récupère l'historique d'un programme pour une année d'exercice spécifique
     */
    @GetMapping("/programme/{programmeId}/annee/{anneeExerciceId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Historique d'un programme par année", 
               description = "Récupère les changements de statut d'un programme pour une année d'exercice donnée")
    public ResponseEntity<List<HistoriqueProgrammeDto>> getHistoriqueProgrammeParAnnee(
            @Parameter(description = "ID du programme", required = true)
            @PathVariable Long programmeId,
            @Parameter(description = "ID de l'année d'exercice", required = true)
            @PathVariable Long anneeExerciceId) {
        
        log.info("GET /api/historique-programmes/programme/{}/annee/{}", programmeId, anneeExerciceId);
        List<HistoriqueProgrammeDto> historique = historiqueProgrammeService
                .getHistoriqueProgrammeParAnnee(programmeId, anneeExerciceId);
        return ResponseEntity.ok(historique);
    }
    
    /**
     * Récupère l'historique d'une classe progressive
     */
    @GetMapping("/cp/{cpId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Historique d'une CP", 
               description = "Récupère tous les changements de statut des programmes dans une CP donnée")
    public ResponseEntity<List<HistoriqueProgrammeDto>> getHistoriqueCP(
            @Parameter(description = "ID de la classe progressive", required = true)
            @PathVariable Long cpId) {
        
        log.info("GET /api/historique-programmes/cp/{}", cpId);
        List<HistoriqueProgrammeDto> historique = historiqueProgrammeService.getHistoriqueCP(cpId);
        return ResponseEntity.ok(historique);
    }
    
    /**
     * Récupère les progressions annuelles d'un programme
     */
    @GetMapping("/progression/programme/{programmeId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Progressions annuelles d'un programme", 
               description = "Récupère l'évolution d'un programme année par année")
    public ResponseEntity<List<ProgressionAnnuelleDto>> getProgressionsProgramme(
            @Parameter(description = "ID du programme", required = true)
            @PathVariable Long programmeId) {
        
        log.info("GET /api/historique-programmes/progression/programme/{}", programmeId);
        List<ProgressionAnnuelleDto> progressions = historiqueProgrammeService
                .getProgressionsProgramme(programmeId);
        return ResponseEntity.ok(progressions);
    }
    
    /**
     * Récupère les statistiques annuelles des programmes
     */
    @GetMapping("/statistiques/annee/{anneeExerciceId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Statistiques annuelles", 
               description = "Calcule les statistiques des programmes pour une année d'exercice")
    public ResponseEntity<StatistiquesAnnuellesDto> getStatistiquesAnnuelles(
            @Parameter(description = "ID de l'année d'exercice", required = true)
            @PathVariable Long anneeExerciceId) {
        
        log.info("GET /api/historique-programmes/statistiques/annee/{}", anneeExerciceId);
        StatistiquesAnnuellesDto statistiques = historiqueProgrammeService
                .getStatistiquesAnnuelles(anneeExerciceId);
        return ResponseEntity.ok(statistiques);
    }
}
