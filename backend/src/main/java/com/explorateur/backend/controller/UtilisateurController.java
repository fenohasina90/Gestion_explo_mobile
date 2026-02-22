package com.explorateur.backend.controller;

import com.explorateur.backend.dto.UserInfoResponse;
import com.explorateur.backend.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/utilisateur")
@RequiredArgsConstructor
@Tag(name = "Utilisateur", description = "API de gestion des utilisateurs")
public class UtilisateurController {
    
    private final UtilisateurService utilisateurService;
    
    @GetMapping("/me")
    @Operation(summary = "Obtenir les informations de l'utilisateur connecté",
               description = "Retourne les informations de l'utilisateur actuellement authentifié",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserInfoResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserInfoResponse userInfo = utilisateurService.getUserInfo(username);
        return ResponseEntity.ok(userInfo);
    }
}
