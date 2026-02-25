package com.explorateur.backend.controller;

import com.explorateur.backend.dto.ClasseResponse;
import com.explorateur.backend.service.ClasseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Tag(name = "Classes", description = "API de gestion des classes")
@SecurityRequirement(name = "bearerAuth")
public class ClasseController {
    
    private final ClasseService classeService;
    
    @GetMapping
    @Operation(summary = "Récupérer toutes les classes")
    public ResponseEntity<List<ClasseResponse>> getAllClasses() {
        return ResponseEntity.ok(classeService.getAllClasses());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une classe par ID")
    public ResponseEntity<ClasseResponse> getClasseById(
            @Parameter(description = "ID de la classe")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(classeService.getClasseById(id));
    }
}
