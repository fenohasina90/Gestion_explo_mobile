package com.explorateur.backendbeta.controller;

import com.explorateur.backendbeta.dto.JournalFilterRequest;
import com.explorateur.backendbeta.dto.JournalResponse;
import com.explorateur.backendbeta.service.JournalReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/journal")
@CrossOrigin(origins = "*", methods = {org.springframework.web.bind.annotation.RequestMethod.GET, org.springframework.web.bind.annotation.RequestMethod.OPTIONS})
@RequiredArgsConstructor
public class JournalReadController {

    private final JournalReadService journalReadService;

    @GetMapping
    public ResponseEntity<List<JournalResponse>> getAllJournal() {
        return ResponseEntity.ok(journalReadService.getAllJournal());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<JournalResponse>> filterJournal(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            @RequestParam(required = false) String searchText) {

        JournalFilterRequest filter = JournalFilterRequest.builder()
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .searchText(searchText)
                .build();

        return ResponseEntity.ok(journalReadService.getJournalWithFilters(filter));
    }
}
