package com.explorateur.backend.controller;

import com.explorateur.backend.dto.CreateUtilisateurRequest;
import com.explorateur.backend.dto.RoleResponse;
import com.explorateur.backend.dto.UpdateUtilisateurRequest;
import com.explorateur.backend.dto.UserInfoResponse;
import com.explorateur.backend.dto.UtilisateurResponse;
import com.explorateur.backend.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateur")
@RequiredArgsConstructor
@Tag(name = "Utilisateur", description = "API de gestion des utilisateurs")
@SecurityRequirement(name = "bearerAuth")
public class UtilisateurController {
    
    private final UtilisateurService utilisateurService;
    
    @GetMapping("/me")
    @Operation(summary = "Obtenir les informations de l'utilisateur connecté",
               description = "Retourne les informations de l'utilisateur actuellement authentifié")
    public ResponseEntity<UserInfoResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserInfoResponse userInfo = utilisateurService.getUserInfo(username);
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRECTEUR')")
    @Operation(summary = "Créer un nouvel utilisateur",
               description = "Seul un Directeur peut créer un utilisateur. Si un nouveau Directeur est créé, l'ancien devient inactif")
    public ResponseEntity<UtilisateurResponse> createUtilisateur(@Valid @RequestBody CreateUtilisateurRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UtilisateurResponse response = utilisateurService.createUtilisateur(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un utilisateur",
               description = "Chaque utilisateur peut modifier ses propres informations personnelles (username, password). " +
                             "Le Directeur peut modifier le rôle, le statut et l'année d'exercice des autres utilisateurs, " +
                             "mais ne peut pas modifier leur username ou mot de passe.")
    public ResponseEntity<UtilisateurResponse> updateUtilisateur(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUtilisateurRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UtilisateurResponse response = utilisateurService.updateUtilisateur(id, request, username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTEUR')")
    @Operation(summary = "Supprimer un utilisateur",
               description = "Seul un Directeur peut supprimer un utilisateur")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        utilisateurService.deleteUtilisateur(id, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les utilisateurs",
               description = "Retourne la liste de tous les utilisateurs")
    public ResponseEntity<List<UtilisateurResponse>> getAllUtilisateurs() {
        List<UtilisateurResponse> utilisateurs = utilisateurService.getAllUtilisateurs();
        return ResponseEntity.ok(utilisateurs);
    }

    @GetMapping("/roles")
    @Operation(summary = "Récupérer tous les rôles disponibles",
               description = "Retourne la liste de tous les rôles du système")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = utilisateurService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur par ID",
               description = "Retourne les informations d'un utilisateur spécifique")
    public ResponseEntity<UtilisateurResponse> getUtilisateurById(@PathVariable Long id) {
        UtilisateurResponse utilisateur = utilisateurService.getUtilisateurById(id);
        return ResponseEntity.ok(utilisateur);
    }

    @GetMapping("/annee/{anneeExerciceId}")
    @Operation(summary = "Récupérer les utilisateurs par année d'exercice",
               description = "Retourne tous les utilisateurs d'une année d'exercice spécifique")
    public ResponseEntity<List<UtilisateurResponse>> getUtilisateursByAnneeExercice(
            @PathVariable Long anneeExerciceId) {
        List<UtilisateurResponse> utilisateurs = utilisateurService.getUtilisateursByAnneeExercice(anneeExerciceId);
        return ResponseEntity.ok(utilisateurs);
    }

    @GetMapping("/actifs")
    @Operation(summary = "Récupérer les utilisateurs actifs",
               description = "Retourne tous les utilisateurs actifs")
    public ResponseEntity<List<UtilisateurResponse>> getUtilisateursActifs() {
        List<UtilisateurResponse> utilisateurs = utilisateurService.getUtilisateursActifs();
        return ResponseEntity.ok(utilisateurs);
    }
}
