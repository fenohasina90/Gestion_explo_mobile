package com.explorateur.backend.controller;

import com.explorateur.backend.dto.MouvementBudgetaireFilterRequest;
import com.explorateur.backend.dto.MouvementBudgetaireResponse;
import com.explorateur.backend.dto.PageResponse;
import com.explorateur.backend.service.MouvementBudgetaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budget-read")
@RequiredArgsConstructor
@Tag(name = "Budget Read", description = "Routes dédiées à la consultation des mouvements budgétaires")
@SecurityRequirement(name = "bearerAuth")
public class BudgetReadController {

    private final MouvementBudgetaireService mouvementBudgetaireService;

    @GetMapping("/mouvements")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulter les mouvements", description = "Consultation paginée avec filtres")
    public ResponseEntity<PageResponse<MouvementBudgetaireResponse>> getMouvements(
            @Parameter(description = "Recherche par description") @RequestParam(required = false) String recherche,
            @Parameter(description = "Date de début") @RequestParam(required = false) LocalDate dateDebut,
            @Parameter(description = "Date de fin") @RequestParam(required = false) LocalDate dateFin,
            @Parameter(description = "ID du type") @RequestParam(required = false) Long typeId,
            @Parameter(description = "ID de l'année d'exercice") @RequestParam(required = false) Long anneeExerciceId,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "Direction de tri") @RequestParam(defaultValue = "desc") String direction) {

        MouvementBudgetaireFilterRequest filters = MouvementBudgetaireFilterRequest.builder()
                .recherche(recherche)
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .typeId(typeId)
                .anneeExerciceId(anneeExerciceId)
                .build();

        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(mouvementBudgetaireService.getMouvementsWithFiltersPaginated(filters, pageable));
    }

    @GetMapping("/mouvements/all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lister tous les mouvements", description = "Consultation non paginée avec filtres")
    public ResponseEntity<List<MouvementBudgetaireResponse>> getAllMouvements(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Long anneeExerciceId) {

        MouvementBudgetaireFilterRequest filters = MouvementBudgetaireFilterRequest.builder()
                .recherche(recherche)
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .typeId(typeId)
                .anneeExerciceId(anneeExerciceId)
                .build();

        return ResponseEntity.ok(mouvementBudgetaireService.getMouvementsWithFilters(filters));
    }
}
