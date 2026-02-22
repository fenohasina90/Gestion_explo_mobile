package com.explorateur.backend.controller;

import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.service.AnneeExerciceInitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour la gestion des années d'exercice
 */
@RestController
@RequestMapping("/api/annee-exercice")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class AnneeExerciceController {

    private final AnneeExerciceInitService anneeExerciceInitService;

    /**
     * Obtenir l'année d'exercice en cours
     */
    @GetMapping("/courante")
    public ResponseEntity<AnneeExercice> getAnneeExerciceCourante() {
        AnneeExercice annee = anneeExerciceInitService.getOrCreateAnneeExerciceCourante();
        return ResponseEntity.ok(annee);
    }

    /**
     * Obtenir l'année d'exercice la plus récente
     */
    @GetMapping("/recente")
    public ResponseEntity<AnneeExercice> getAnneeExerciceRecente() {
        AnneeExercice annee = anneeExerciceInitService.getAnneeExerciceRecente();
        return ResponseEntity.ok(annee);
    }
}
