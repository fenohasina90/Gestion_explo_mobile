package com.explorateur.backendbeta.controller;

import com.explorateur.backendbeta.dto.EtatCaisseResponse;
import com.explorateur.backendbeta.dto.MouvementBudgetaireFilterRequest;
import com.explorateur.backendbeta.dto.MouvementBudgetaireResponse;
import com.explorateur.backendbeta.dto.PageResponse;
import com.explorateur.backendbeta.service.MouvementBudgetaireReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budget-mouvements")
@CrossOrigin(origins = "*", methods = {org.springframework.web.bind.annotation.RequestMethod.GET, org.springframework.web.bind.annotation.RequestMethod.POST, org.springframework.web.bind.annotation.RequestMethod.OPTIONS})
@RequiredArgsConstructor
public class MouvementBudgetaireReadController {

    private final MouvementBudgetaireReadService mouvementBudgetaireReadService;

    @GetMapping("/{id}")
    public ResponseEntity<MouvementBudgetaireResponse> getMouvementById(@PathVariable Long id) {
        return ResponseEntity.ok(mouvementBudgetaireReadService.getMouvementById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<MouvementBudgetaireResponse>> getMouvements(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Long anneeExerciceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        MouvementBudgetaireFilterRequest filters = MouvementBudgetaireFilterRequest.builder()
                .recherche(recherche)
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .typeId(typeId)
                .anneeExerciceId(anneeExerciceId)
                .build();

        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(mouvementBudgetaireReadService.getMouvementsWithFiltersPaginated(filters, pageable));
    }

    @GetMapping("/all")
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

        return ResponseEntity.ok(mouvementBudgetaireReadService.getMouvementsWithFilters(filters));
    }

    @PostMapping("/search")
    public ResponseEntity<PageResponse<MouvementBudgetaireResponse>> searchMouvements(
            @RequestBody(required = false) MouvementBudgetaireFilterRequest filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        MouvementBudgetaireFilterRequest safeFilters = filters != null ? filters : new MouvementBudgetaireFilterRequest();
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(mouvementBudgetaireReadService.getMouvementsWithFiltersPaginated(safeFilters, pageable));
    }

    @GetMapping("/etat-caisse")
    public ResponseEntity<EtatCaisseResponse> getEtatCaisse(@RequestParam(required = false) Long anneeExerciceId) {
        return ResponseEntity.ok(mouvementBudgetaireReadService.getEtatCaisse(anneeExerciceId));
    }
}
