package com.explorateur.backend.controller;

import com.explorateur.backend.dto.JournalFilterRequest;
import com.explorateur.backend.dto.JournalResponse;
import com.explorateur.backend.service.JournalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller REST pour la gestion du journal d'audit
 */
@RestController
@RequestMapping("/api/journal")
@RequiredArgsConstructor
@Tag(name = "Journal d'audit", description = "Endpoints pour consulter le journal d'audit du système")
@SecurityRequirement(name = "bearerAuth")
public class JournalController {

    private final JournalService journalService;

    @GetMapping
    @Operation(summary = "Récupérer toutes les entrées du journal",
               description = "Retourne toutes les entrées du journal d'audit triées par date décroissante")
    public ResponseEntity<List<JournalResponse>> getAllJournal() {
        List<JournalResponse> journals = journalService.getAllJournal();
        return ResponseEntity.ok(journals);
    }

    @PostMapping({"/filter", "/filtrer"})
    @Operation(summary = "Filtrer les entrées du journal",
               description = "Retourne les entrées du journal selon les critères de filtrage (date début, date fin, recherche textuelle)")
    public ResponseEntity<List<JournalResponse>> filterJournal(@RequestBody JournalFilterRequest filter) {
        List<JournalResponse> journals = journalService.getJournalWithFilters(filter);
        return ResponseEntity.ok(journals);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filtrer les entrées du journal (GET)",
               description = "Retourne les entrées du journal selon les critères de filtrage via query params")
    public ResponseEntity<List<JournalResponse>> filterJournalGet(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            @RequestParam(required = false) Long utilisateurId,
            @RequestParam(required = false) String searchText) {

        JournalFilterRequest filter = JournalFilterRequest.builder()
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .utilisateurId(utilisateurId)
                .searchText(searchText)
                .build();

        List<JournalResponse> journals = journalService.getJournalWithFilters(filter);
        return ResponseEntity.ok(journals);
    }

    @GetMapping("/period")
    @Operation(summary = "Récupérer les entrées par période",
               description = "Retourne les entrées du journal entre deux dates")
    public ResponseEntity<List<JournalResponse>> getJournalByPeriod(
            @Parameter(description = "Date de début (format: yyyy-MM-dd'T'HH:mm:ss)", example = "2026-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            
            @Parameter(description = "Date de fin (format: yyyy-MM-dd'T'HH:mm:ss)", example = "2026-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        
        List<JournalResponse> journals = journalService.getJournalByPeriod(dateDebut, dateFin);
        return ResponseEntity.ok(journals);
    }

}
