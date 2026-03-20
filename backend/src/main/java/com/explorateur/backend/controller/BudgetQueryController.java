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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/budget-query")
@RequiredArgsConstructor
@Tag(name = "Budget Query", description = "Endpoints alternatifs pour consultation des mouvements budgétaires")
@SecurityRequirement(name = "bearerAuth")
public class BudgetQueryController {

    private final MouvementBudgetaireService mouvementBudgetaireService;

    @GetMapping("/mouvements")
    @Operation(summary = "Consulter les mouvements budgétaires", description = "Route alternative de consultation paginée avec filtres")
    public ResponseEntity<PageResponse<MouvementBudgetaireResponse>> getMouvements(
            @Parameter(description = "Recherche par description") @RequestParam(required = false) String recherche,
            @Parameter(description = "Date de début") @RequestParam(required = false) LocalDate dateDebut,
            @Parameter(description = "Date de fin") @RequestParam(required = false) LocalDate dateFin,
            @Parameter(description = "ID du type de mouvement") @RequestParam(required = false) Long typeId,
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

        PageResponse<MouvementBudgetaireResponse> mouvementsPage =
                mouvementBudgetaireService.getMouvementsWithFiltersPaginated(filters, pageable);

        return ResponseEntity.ok(mouvementsPage);
    }
}
