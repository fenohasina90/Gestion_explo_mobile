package com.explorateur.backend.controller;

import com.explorateur.backend.dto.CreateStaffRequest;
import com.explorateur.backend.dto.StaffResponse;
import com.explorateur.backend.dto.UpdateStaffRequest;
import com.explorateur.backend.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour la gestion des staffs
 */
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Tag(name = "Staff", description = "API de gestion des staffs")
@SecurityRequirement(name = "bearerAuth")
public class StaffController {
    
    private final StaffService staffService;
    
    @PostMapping
    @PreAuthorize("hasRole('Directeur')")
    @Operation(summary = "Créer un nouveau staff",
               description = "Crée un nouveau staff à partir d'un instructeur existant (Directeur uniquement)")
    public ResponseEntity<StaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request, Authentication authentication) {
        StaffResponse response = staffService.createStaff(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Directeur') or hasRole('Co-Directeur')")
    @Operation(summary = "Modifier un staff",
               description = "Met à jour les informations d'un staff (Directeur et Co-directeur uniquement)")
    public ResponseEntity<StaffResponse> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStaffRequest request,
            Authentication authentication) {
        StaffResponse response = staffService.updateStaff(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Directeur')")
    @Operation(summary = "Supprimer un staff",
               description = "Supprime un staff (Directeur uniquement)")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id, Authentication authentication) {
        staffService.deleteStaff(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    @Operation(summary = "Récupérer tous les staffs",
               description = "Retourne la liste de tous les staffs")
    public ResponseEntity<List<StaffResponse>> getAllStaffs() {
        List<StaffResponse> staffs = staffService.getAllStaffs();
        return ResponseEntity.ok(staffs);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un staff par ID",
               description = "Retourne les détails d'un staff spécifique")
    public ResponseEntity<StaffResponse> getStaffById(@PathVariable Long id) {
        StaffResponse staff = staffService.getStaffById(id);
        return ResponseEntity.ok(staff);
    }
    
    @GetMapping("/filter")
    @Operation(summary = "Filtrer les staffs",
               description = "Récupère les staffs avec filtres : année d'exercice, rôle, chef guide")
    public ResponseEntity<List<StaffResponse>> getStaffsWithFilters(
            @RequestParam(required = false) Long anneeExerciceId,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Boolean estChefGuide) {
        List<StaffResponse> staffs = staffService.getStaffsWithFilters(anneeExerciceId, roleId, estChefGuide);
        return ResponseEntity.ok(staffs);
    }
    
    @GetMapping("/annee/{anneeExerciceId}")
    @Operation(summary = "Récupérer les staffs par année d'exercice",
               description = "Retourne tous les staffs d'une année d'exercice spécifique")
    public ResponseEntity<List<StaffResponse>> getStaffsByAnneeExercice(@PathVariable Long anneeExerciceId) {
        List<StaffResponse> staffs = staffService.getStaffsByAnneeExercice(anneeExerciceId);
        return ResponseEntity.ok(staffs);
    }
}
