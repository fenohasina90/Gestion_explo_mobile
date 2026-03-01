package com.explorateur.backend.controller;

import com.explorateur.backend.dto.*;
import com.explorateur.backend.service.ParticipantActiviteService;
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
 * Contrôleur pour la gestion de la présence aux activités
 */
@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Participants", description = "API pour la gestion de la présence aux activités")
@SecurityRequirement(name = "bearerAuth")
public class ParticipantActiviteController {
    
    private final ParticipantActiviteService participantService;
    
    /**
     * Récupérer les personnes disponibles pour faire la présence
     */
    @GetMapping("/disponibles/{activiteId}")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur')")
    @Operation(summary = "Obtenir la liste des personnes disponibles",
               description = "Retourne tous les enfants et staff inscrits pour l'année de l'activité")
    public ResponseEntity<PersonnesDisponiblesResponse> getPersonnesDisponibles(
            @PathVariable Long activiteId,
            Authentication authentication) {
        
        log.info("Requête de personnes disponibles pour l'activité ID: {} par {}", 
            activiteId, authentication.getName());
        
        PersonnesDisponiblesResponse response = participantService.getPersonnesDisponibles(activiteId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Enregistrer la présence à une activité
     */
    @PostMapping("/presence")
    @PreAuthorize("hasAnyRole('Directeur', 'Co_Directeur')")
    @Operation(summary = "Enregistrer la présence",
               description = "Enregistre les participants présents et change le statut de l'activité à 'Terminé'")
    public ResponseEntity<String> enregistrerPresence(
            @RequestBody EnregistrerPresenceRequest request,
            Authentication authentication) {
        
        log.info("Enregistrement de présence pour l'activité ID: {} par {}", 
            request.getActiviteId(), authentication.getName());
        
        participantService.enregistrerPresence(request, authentication.getName());
        return ResponseEntity.ok("Présence enregistrée avec succès");
    }
    
    /**
     * Consulter les participants d'une activité avec filtres
     */
    @GetMapping("/{activiteId}")
    @Operation(summary = "Consulter les participants",
               description = "Retourne la liste des participants avec filtres optionnels (enfant, staff, classe)")
    public ResponseEntity<ParticipantsResponse> getParticipants(
            @PathVariable Long activiteId,
            @RequestParam(required = false, defaultValue = "true") Boolean filtreEnfant,
            @RequestParam(required = false, defaultValue = "true") Boolean filtreStaff,
            @RequestParam(required = false) Long classeId,
            Authentication authentication) {
        
        log.info("Consultation des participants de l'activité ID: {} par {} (filtreEnfant: {}, filtreStaff: {}, classeId: {})", 
            activiteId, authentication.getName(), filtreEnfant, filtreStaff, classeId);
        
        ParticipantsResponse response = participantService.getParticipants(
            activiteId, filtreEnfant, filtreStaff, classeId);
        
        return ResponseEntity.ok(response);
    }
}
