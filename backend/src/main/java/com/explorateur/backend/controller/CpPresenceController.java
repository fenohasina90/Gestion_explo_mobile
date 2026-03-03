package com.explorateur.backend.controller;

import com.explorateur.backend.dto.*;
import com.explorateur.backend.service.CpPresenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour la gestion de la présence aux classes progressives
 */
@RestController
@RequestMapping("/api/cp-presence")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Présence CP", description = "API pour la gestion de la présence aux classes progressives")
@SecurityRequirement(name = "bearerAuth")
public class CpPresenceController {
    
    private final CpPresenceService cpPresenceService;
    
    /**
     * Récupérer les personnes disponibles pour faire la présence à une CP
     */
    @GetMapping("/disponibles/{classeProgressiveId}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur')")
    @Operation(summary = "Obtenir la liste des personnes disponibles",
               description = "Retourne tous les enfants et staff inscrits pour l'année de la classe progressive")
    public ResponseEntity<PersonnesDisponiblesResponse> getPersonnesDisponibles(
            @PathVariable Long classeProgressiveId,
            Authentication authentication) {
        
        log.info("Requête de personnes disponibles pour la CP ID: {} par {}", 
            classeProgressiveId, authentication.getName());
        
        PersonnesDisponiblesResponse response = cpPresenceService.getPersonnesDisponibles(classeProgressiveId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Enregistrer la présence à une classe progressive
     */
    @PostMapping("/presence")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur')")
    @Operation(summary = "Enregistrer la présence",
               description = "Enregistre les participants présents à la classe progressive. " +
                           "Règles: un enfant/staff ne peut être enregistré qu'une seule fois par CP, " +
                           "et la présence ne peut être saisie que pour une CP existante.")
    public ResponseEntity<String> enregistrerPresence(
            @RequestBody EnregistrerPresenceCpRequest request,
            Authentication authentication) {
        
        log.info("Enregistrement de présence pour la CP ID: {} par {}", 
            request.getClasseProgressiveId(), authentication.getName());
        
        cpPresenceService.enregistrerPresence(request, authentication.getName());
        return ResponseEntity.ok("Présence enregistrée avec succès");
    }
    
    /**
     * Consulter les participants d'une classe progressive avec filtres
     */
    @GetMapping("/{classeProgressiveId}")
    @Operation(summary = "Consulter les participants",
               description = "Retourne la liste des participants à une CP avec filtres optionnels (enfant, staff, classe)")
    public ResponseEntity<ParticipantsResponse> getParticipants(
            @PathVariable Long classeProgressiveId,
            @RequestParam(required = false, defaultValue = "true") Boolean filtreEnfant,
            @RequestParam(required = false, defaultValue = "true") Boolean filtreStaff,
            @RequestParam(required = false) Long classeId,
            Authentication authentication) {
        
        log.info("Consultation des participants de la CP ID: {} par {} (filtreEnfant: {}, filtreStaff: {}, classeId: {})", 
            classeProgressiveId, authentication.getName(), filtreEnfant, filtreStaff, classeId);
        
        ParticipantsResponse response = cpPresenceService.getParticipants(
            classeProgressiveId, filtreEnfant, filtreStaff, classeId);
        
        return ResponseEntity.ok(response);
    }
}
