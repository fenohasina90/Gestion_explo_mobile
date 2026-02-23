package com.explorateur.backend.controller;

import com.explorateur.backend.entity.RolesStaff;
import com.explorateur.backend.repository.RolesStaffRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Rôles", description = "API de gestion des rôles")
@SecurityRequirement(name = "bearerAuth")
public class RolesController {
    
    private final RolesStaffRepository rolesStaffRepository;
    
    @GetMapping
    @Operation(summary = "Récupérer tous les rôles",
               description = "Retourne la liste de tous les rôles disponibles")
    public ResponseEntity<List<RolesStaff>> getAllRoles() {
        List<RolesStaff> roles = rolesStaffRepository.findAll();
        return ResponseEntity.ok(roles);
    }
}
