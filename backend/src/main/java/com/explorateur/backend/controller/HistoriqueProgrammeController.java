package com.explorateur.backend.controller;

import com.explorateur.backend.dto.*;
import com.explorateur.backend.service.HistoriqueProgrammeService;
import com.explorateur.backend.scheduler.ProgrammeScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour la gestion de l'historique des programmes
 */
@RestController
@RequestMapping("/api/historique-programmes")
@RequiredArgsConstructor
@Tag(name = "Historique Programmes", description = "API de gestion de l'historique et progression des programmes")
@SecurityRequirement(name = "bearerAuth")
public class HistoriqueProgrammeController {
    
    private final HistoriqueProgrammeService historiqueProgrammeService;
    private final ProgrammeScheduler programmeScheduler;
    
    @GetMapping("/programme/{programmeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_Directeur', 'ROLE_Co-Directeur', 'ROLE_Co_Directeur', 'ROLE_Secrétaire', 'ROLE_Instructeur')")
    @Operation(summary = "Obtenir l'historique complet d'un programme",
               description = "Récupère tous les changements de statut d'un programme (toutes années confondues)")
    public ResponseEntity<List<HistoriqueProgrammeDto>> getHistoriqueProgramme(
            @Parameter(description = "ID du programme")
            @PathVariable Long programmeId) {
        List<HistoriqueProgrammeDto> historique = historiqueProgrammeService.getHistoriqueProgramme(programmeId);
        return ResponseEntity.ok(historique);
    }
    
    @GetMapping("/programme/{programmeId}/annee/{anneeExerciceId}")
    @PreAuthorize("hasAnyAuthority('ROLE_Directeur', 'ROLE_Co-Directeur', 'ROLE_Co_Directeur', 'ROLE_Secrétaire', 'ROLE_Instructeur')")
    @Operation(summary = "Obtenir l'historique d'un programme pour une année",
               description = "Récupère tous les changements de statut d'un programme pour une année d'exercice spécifique")
    public ResponseEntity<List<HistoriqueProgrammeDto>> getHistoriqueProgrammeParAnnee(
            @Parameter(description = "ID du programme")
            @PathVariable Long programmeId,
            @Parameter(description = "ID de l'année d'exercice")
            @PathVariable Long anneeExerciceId) {
        List<HistoriqueProgrammeDto> historique = historiqueProgrammeService
                .getHistoriqueProgrammeParAnnee(programmeId, anneeExerciceId);
        return ResponseEntity.ok(historique);
    }
    
    @GetMapping("/cp/{cpId}")
    @PreAuthorize("hasAnyAuthority('ROLE_Directeur', 'ROLE_Co-Directeur', 'ROLE_Co_Directeur', 'ROLE_Secrétaire', 'ROLE_Instructeur')")
    @Operation(summary = "Obtenir l'historique d'une Classe Progressive",
               description = "Récupère tous les changements de statut effectués dans une CP")
    public ResponseEntity<List<HistoriqueProgrammeDto>> getHistoriqueCP(
            @Parameter(description = "ID de la Classe Progressive")
            @PathVariable Long cpId) {
        List<HistoriqueProgrammeDto> historique = historiqueProgrammeService.getHistoriqueCP(cpId);
        return ResponseEntity.ok(historique);
    }
    
    @GetMapping("/progression/programme/{programmeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_Directeur', 'ROLE_Co-Directeur', 'ROLE_Co_Directeur', 'ROLE_Secrétaire', 'ROLE_Instructeur')")
    @Operation(summary = "Obtenir les progressions annuelles d'un programme",
               description = "Récupère le résumé des progressions d'un programme par année")
    public ResponseEntity<List<ProgressionAnnuelleDto>> getProgressionsProgramme(
            @Parameter(description = "ID du programme")
            @PathVariable Long programmeId) {
        List<ProgressionAnnuelleDto> progressions = historiqueProgrammeService.getProgressionsProgramme(programmeId);
        return ResponseEntity.ok(progressions);
    }
    
    @GetMapping("/progression")
    @PreAuthorize("hasAnyAuthority('ROLE_Directeur', 'ROLE_Co-Directeur', 'ROLE_Co_Directeur', 'ROLE_Secrétaire', 'ROLE_Instructeur')")
    @Operation(summary = "Obtenir la progression annuelle de tous les programmes",
               description = "Récupère le résumé des progressions de tous les programmes, optionnellement filtrée par année")
    public ResponseEntity<List<ProgressionAnnuelleDto>> getProgressionAnnuelle(
            @Parameter(description = "ID de l'année d'exercice (optionnel)")
            @RequestParam(required = false) Long anneeExerciceId) {
        List<ProgressionAnnuelleDto> progressions = historiqueProgrammeService.getProgressionAnnuelle(anneeExerciceId);
        return ResponseEntity.ok(progressions);
    }
    
    @GetMapping("/statistiques/annee/{anneeExerciceId}")
    @PreAuthorize("hasAnyAuthority('ROLE_Directeur', 'ROLE_Co-Directeur', 'ROLE_Co_Directeur', 'ROLE_Secrétaire', 'ROLE_Instructeur')")
    @Operation(summary = "Obtenir les statistiques annuelles des programmes",
               description = "Récupère les statistiques agrégées des programmes pour une année (nombre terminés, en cours, etc.)")
    public ResponseEntity<StatistiquesAnnuellesDto> getStatistiquesAnnuelles(
            @Parameter(description = "ID de l'année d'exercice")
            @PathVariable Long anneeExerciceId) {
        StatistiquesAnnuellesDto statistiques = historiqueProgrammeService.getStatistiquesAnnuelles(anneeExerciceId);
        return ResponseEntity.ok(statistiques);
    }
    
    @GetMapping("/statistiques")
    @PreAuthorize("hasAnyAuthority('ROLE_Directeur', 'ROLE_Co-Directeur', 'ROLE_Co_Directeur', 'ROLE_Secrétaire', 'ROLE_Instructeur')")
    @Operation(summary = "Obtenir les statistiques de tous les programmes",
               description = "Récupère les statistiques agrégées de tous les programmes, optionnellement filtrées par année")
    public ResponseEntity<List<StatistiquesAnnuellesDto>> getToutesStatistiques(
            @Parameter(description = "ID de l'année d'exercice (optionnel)")
            @RequestParam(required = false) Long anneeExerciceId) {
        List<StatistiquesAnnuellesDto> statistiques = historiqueProgrammeService.getToutesStatistiques(anneeExerciceId);
        return ResponseEntity.ok(statistiques);
    }
    
    @GetMapping("/avancement/annee/{anneeExerciceId}")
    @PreAuthorize("hasAnyAuthority('ROLE_Directeur', 'ROLE_Co-Directeur', 'ROLE_Co_Directeur', 'ROLE_Secrétaire', 'ROLE_Instructeur')")
    @Operation(summary = "Obtenir l'avancement de tous les programmes pour une année",
               description = "Récupère l'état d'avancement détaillé de tous les programmes pour une année donnée, " +
                           "avec possibilité de filtrer par classe et catégorie")
    public ResponseEntity<List<ProgrammeAvancementDto>> getAvancementProgrammes(
            @Parameter(description = "ID de l'année d'exercice")
            @PathVariable Long anneeExerciceId,
            @Parameter(description = "ID de la classe (optionnel)")
            @RequestParam(required = false) Long classeId,
            @Parameter(description = "ID de la catégorie (optionnel)")
            @RequestParam(required = false) Long categorieId) {
        List<ProgrammeAvancementDto> avancement = historiqueProgrammeService
                .getAvancementProgrammes(anneeExerciceId, classeId, categorieId);
        return ResponseEntity.ok(avancement);
    }
    
    @GetMapping("/avancement")
    @PreAuthorize("hasAnyAuthority('ROLE_Directeur', 'ROLE_Co-Directeur', 'ROLE_Co_Directeur', 'ROLE_Secrétaire', 'ROLE_Instructeur')")
    @Operation(summary = "Obtenir l'avancement de tous les programmes",
               description = "Récupère l'état d'avancement détaillé de tous les programmes, " +
                           "optionnellement filtrés par année, classe et catégorie")
    public ResponseEntity<List<ProgrammeAvancementDto>> getTousAvancementProgrammes(
            @Parameter(description = "ID de l'année d'exercice (optionnel)")
            @RequestParam(required = false) Long anneeExerciceId,
            @Parameter(description = "ID de la classe (optionnel)")
            @RequestParam(required = false) Long classeId,
            @Parameter(description = "ID de la catégorie (optionnel)")
            @RequestParam(required = false) Long categorieId) {
        List<ProgrammeAvancementDto> avancement = historiqueProgrammeService
                .getTousAvancementProgrammes(anneeExerciceId, classeId, categorieId);
        return ResponseEntity.ok(avancement);
    }
    
    @PostMapping("/initialiser-annee/{anneeExerciceId}")
    @PreAuthorize("hasRole('Directeur')")
    @Operation(summary = "Initialiser les statuts de tous les programmes pour une année",
               description = "Initialise tous les programmes à 'EN ATTENTE' pour une année d'exercice donnée. " +
                           "Cette action ne peut être effectuée qu'une seule fois par année. (Directeur uniquement)")
    public ResponseEntity<InitialisationStatutsResponse> initialiserStatutsPourAnnee(
            @Parameter(description = "ID de l'année d'exercice")
            @PathVariable Long anneeExerciceId) {
        try {
            int nombreProgrammesInitialises = historiqueProgrammeService.initialiserStatutsAnnuels(anneeExerciceId);
            InitialisationStatutsResponse response = InitialisationStatutsResponse.builder()
                    .success(true)
                    .message("Initialisation des statuts effectuée avec succès")
                    .nombreProgrammes(nombreProgrammesInitialises)
                    .anneeExerciceId(anneeExerciceId)
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            InitialisationStatutsResponse response = InitialisationStatutsResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .nombreProgrammes(0)
                    .anneeExerciceId(anneeExerciceId)
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * DTO pour la réponse d'initialisation des statuts
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class InitialisationStatutsResponse {
        private boolean success;
        private String message;
        private int nombreProgrammes;
        private Long anneeExerciceId;
    }
}
