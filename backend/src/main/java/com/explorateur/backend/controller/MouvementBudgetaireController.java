package com.explorateur.backend.controller;

import com.explorateur.backend.dto.*;
import com.explorateur.backend.service.MouvementBudgetaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budget-mouvements")
@RequiredArgsConstructor
@Tag(name = "Mouvements Budgétaires", description = "API de gestion des mouvements budgétaires")
@SecurityRequirement(name = "bearerAuth")
public class MouvementBudgetaireController {

    private final MouvementBudgetaireService mouvementBudgetaireService;

    @PostMapping
    @PreAuthorize("hasRole('Directeur')")
    @Operation(summary = "Créer un mouvement", description = "Création d'un mouvement budgétaire (Directeur uniquement)")
    public ResponseEntity<MouvementBudgetaireResponse> createMouvement(@Valid @RequestBody CreateMouvementBudgetaireRequest request) {
        return ResponseEntity.ok(mouvementBudgetaireService.createMouvement(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_Directeur', 'ROLE_Co_Directeur', 'ROLE_Co-Directeur')")
    @Operation(summary = "Modifier un mouvement", description = "Modification d'un mouvement (Directeur et Co-Directeur)")
    public ResponseEntity<MouvementBudgetaireResponse> updateMouvement(@PathVariable Long id,
                                                                       @Valid @RequestBody UpdateMouvementBudgetaireRequest request) {
        return ResponseEntity.ok(mouvementBudgetaireService.updateMouvement(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Directeur')")
    @Operation(summary = "Supprimer un mouvement", description = "Suppression d'un mouvement budgétaire (Directeur uniquement)")
    public ResponseEntity<Void> deleteMouvement(@PathVariable Long id) {
        mouvementBudgetaireService.deleteMouvement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un mouvement", description = "Retourne un mouvement budgétaire par son identifiant")
    public ResponseEntity<MouvementBudgetaireResponse> getMouvementById(@PathVariable Long id) {
        return ResponseEntity.ok(mouvementBudgetaireService.getMouvementById(id));
    }

    @GetMapping
    @Operation(summary = "Consulter les mouvements", description = "Consultation paginée avec filtres: recherche, date début/fin, type")
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

    @GetMapping("/all")
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

    @PostMapping("/search")
    @Operation(summary = "Rechercher des mouvements", description = "Recherche paginée via body JSON")
    public ResponseEntity<PageResponse<MouvementBudgetaireResponse>> searchMouvements(
            @RequestBody(required = false) MouvementBudgetaireFilterRequest filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        MouvementBudgetaireFilterRequest safeFilters = filters != null ? filters : new MouvementBudgetaireFilterRequest();
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(mouvementBudgetaireService.getMouvementsWithFiltersPaginated(safeFilters, pageable));
    }

    @GetMapping("/etat-caisse")
    @Operation(summary = "État de caisse", description = "Calcule total recettes, total dépenses et solde")
    public ResponseEntity<EtatCaisseResponse> getEtatCaisse(@RequestParam(required = false) Long anneeExerciceId) {
        return ResponseEntity.ok(mouvementBudgetaireService.getEtatCaisse(anneeExerciceId));
    }
}
