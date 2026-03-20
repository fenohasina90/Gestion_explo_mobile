package com.explorateur.backend.controller;

import com.explorateur.backend.dto.JournalFilterRequest;
import com.explorateur.backend.dto.JournalResponse;
import com.explorateur.backend.service.JournalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/journal-query")
@RequiredArgsConstructor
@Tag(name = "Journal Query", description = "Endpoints alternatifs pour consultation du journal")
@SecurityRequirement(name = "bearerAuth")
public class JournalQueryController {

    private final JournalService journalService;

    @GetMapping("/entries")
    @Operation(summary = "Filtrer les entrées du journal", description = "Route alternative GET pour filtrage journal")
    public ResponseEntity<List<JournalResponse>> getEntries(
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
}
