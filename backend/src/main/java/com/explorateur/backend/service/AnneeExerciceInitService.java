package com.explorateur.backend.service;

import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.repository.AnneeExerciceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Service pour initialiser et gérer les années d'exercice
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnneeExerciceInitService {

    private final AnneeExerciceRepository anneeExerciceRepository;

    /**
     * S'exécute automatiquement au démarrage de l'application
     * Crée l'année d'exercice en cours si elle n'existe pas
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initAnneeExerciceCourante() {
        // Obtenir l'année en cours (1er janvier de l'année actuelle)
        LocalDate anneeCourante = LocalDate.now().withMonth(1).withDayOfMonth(1);
        
        // Vérifier si l'année d'exercice existe déjà
        anneeExerciceRepository.findByAnnee(anneeCourante).ifPresentOrElse(
            anneeExercice -> log.info("✅ Année d'exercice {} déjà existante (ID: {})", 
                anneeCourante.getYear(), anneeExercice.getId()),
            () -> {
                // Créer la nouvelle année d'exercice
                AnneeExercice nouvelleAnnee = AnneeExercice.builder()
                    .annee(anneeCourante)
                    .build();
                
                AnneeExercice saved = anneeExerciceRepository.save(nouvelleAnnee);
                log.info("🆕 Nouvelle année d'exercice {} créée (ID: {})", 
                    anneeCourante.getYear(), saved.getId());
            }
        );
    }

    /**
     * Récupère ou crée l'année d'exercice en cours
     */
    @Transactional
    public AnneeExercice getOrCreateAnneeExerciceCourante() {
        LocalDate anneeCourante = LocalDate.now().withMonth(1).withDayOfMonth(1);
        
        return anneeExerciceRepository.findByAnnee(anneeCourante)
            .orElseGet(() -> {
                AnneeExercice nouvelleAnnee = AnneeExercice.builder()
                    .annee(anneeCourante)
                    .build();
                return anneeExerciceRepository.save(nouvelleAnnee);
            });
    }

    /**
     * Récupère l'année d'exercice la plus récente
     */
    public AnneeExercice getAnneeExerciceRecente() {
        return anneeExerciceRepository.findFirstByOrderByAnneeDesc()
            .orElseGet(this::getOrCreateAnneeExerciceCourante);
    }
}
