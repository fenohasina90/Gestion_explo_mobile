package com.explorateur.backend.service;

import com.explorateur.backend.dto.StatistiqueEnfantResponse;
import com.explorateur.backend.dto.StatistiqueFilterRequest;
import com.explorateur.backend.dto.StatistiqueStaffResponse;
import com.explorateur.backend.entity.Inscription;
import com.explorateur.backend.entity.Staff;
import com.explorateur.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour calculer les statistiques des enfants et des staffs
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatistiqueService {
    
    private final InscriptionRepository inscriptionRepository;
    private final StaffRepository staffRepository;
    private final ParticipantActiviteExploRepository participantActiviteExploRepository;
    private final ParticipantActiviteStaffRepository participantActiviteStaffRepository;
    private final CpPresenceExploRepository cpPresenceExploRepository;
    private final CpPresenceStaffRepository cpPresenceStaffRepository;
    private final ActiviteRepository activiteRepository;
    private final ClasseProgressiveRepository classeProgressiveRepository;
    private final ProgrammeProgressionAnnuelleRepository programmeProgressionAnnuelleRepository;
    private final AnneeExerciceRepository anneeExerciceRepository;
    
    /**
     * Calcule les statistiques pour tous les enfants selon les filtres
     */
    public List<StatistiqueEnfantResponse> getStatistiquesEnfants(StatistiqueFilterRequest filter) {
        // Récupérer les inscriptions selon les filtres
        List<Inscription> inscriptions = getInscriptionsFiltered(filter);
        
        List<StatistiqueEnfantResponse> statistiques = new ArrayList<>();
        
        for (Inscription inscription : inscriptions) {
            StatistiqueEnfantResponse stat = calculateStatistiqueEnfant(inscription);
            statistiques.add(stat);
        }
        
        // Calculer les classements
        calculerClassementEnfants(statistiques);
        
        return statistiques;
    }
    
    /**
     * Calcule les statistiques pour un enfant spécifique
     */
    public StatistiqueEnfantResponse getStatistiqueEnfant(Long inscriptionId) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable avec l'ID: " + inscriptionId));
        
        return calculateStatistiqueEnfant(inscription);
    }
    
    /**
     * Calcule les statistiques pour tous les staffs selon les filtres
     */
    public List<StatistiqueStaffResponse> getStatistiquesStaffs(StatistiqueFilterRequest filter) {
        // Récupérer les staffs selon les filtres
        List<Staff> staffs = getStaffsFiltered(filter);
        
        List<StatistiqueStaffResponse> statistiques = new ArrayList<>();
        
        for (Staff staff : staffs) {
            StatistiqueStaffResponse stat = calculateStatistiqueStaff(staff, filter.getAnneeExerciceId());
            statistiques.add(stat);
        }
        
        // Calculer les classements
        calculerClassementStaffs(statistiques);
        
        return statistiques;
    }
    
    /**
     * Calcule les statistiques pour un staff spécifique
     */
    public StatistiqueStaffResponse getStatistiqueStaff(Long staffId, Long anneeExerciceId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff introuvable avec l'ID: " + staffId));
        
        return calculateStatistiqueStaff(staff, anneeExerciceId);
    }
    
    // ============= MÉTHODES PRIVÉES =============
    
    private List<Inscription> getInscriptionsFiltered(StatistiqueFilterRequest filter) {
        if (filter.getAnneeExerciceId() != null && filter.getClasseId() != null) {
            return inscriptionRepository.findByAnneeExerciceIdAndClasseId(
                    filter.getAnneeExerciceId(), 
                    filter.getClasseId()
            );
        } else if (filter.getAnneeExerciceId() != null) {
            List<Inscription> inscriptions = inscriptionRepository.findByAnneeExerciceId(filter.getAnneeExerciceId());
            if (filter.getGenre() != null && !filter.getGenre().isEmpty()) {
                inscriptions = inscriptions.stream()
                        .filter(i -> filter.getGenre().equals(i.getEnfant().getGenre()))
                        .collect(Collectors.toList());
            }
            return inscriptions;
        } else if (filter.getClasseId() != null) {
            return inscriptionRepository.findByClasseId(filter.getClasseId());
        } else if (filter.getGenre() != null && !filter.getGenre().isEmpty()) {
            return inscriptionRepository.findByGenre(filter.getGenre());
        } else {
            return inscriptionRepository.findAll();
        }
    }
    
    private List<Staff> getStaffsFiltered(StatistiqueFilterRequest filter) {
        if (filter.getAnneeExerciceId() != null) {
            return staffRepository.findByAnneeExerciceId(filter.getAnneeExerciceId());
        } else {
            return staffRepository.findAll();
        }
    }
    
    private StatistiqueEnfantResponse calculateStatistiqueEnfant(Inscription inscription) {
        Long anneeExerciceId = inscription.getAnneeExercice().getId();
        Long classeId = inscription.getClasse().getId();
        
        // Programmes complétés
        Long nombreProgrammesCompletes = programmeProgressionAnnuelleRepository
                .countProgrammesTerminesParClasseEtAnnee(classeId, anneeExerciceId);
        Long totalProgrammesClasse = programmeProgressionAnnuelleRepository
                .countProgrammesTotalParClasseEtAnnee(classeId, anneeExerciceId);
        Double pourcentageProgrammes = calculerPourcentage(nombreProgrammesCompletes, totalProgrammesClasse);
        
        // Participation aux activités
        Long nombreParticipationsActivites = participantActiviteExploRepository
                .countByInscriptionIdAndAnneeExerciceId(inscription.getId(), anneeExerciceId);
        Long totalActivites = activiteRepository.countByAnneeExerciceId(anneeExerciceId);
        Double pourcentageActivites = calculerPourcentage(nombreParticipationsActivites, totalActivites);
        
        // Présence aux classes progressives
        Long nombrePresencesCP = cpPresenceExploRepository
                .countByInscriptionIdAndAnneeExerciceId(inscription.getId(), anneeExerciceId);
        Long totalCP = classeProgressiveRepository.countByAnneeExerciceId(anneeExerciceId);
        Double pourcentageCP = calculerPourcentage(nombrePresencesCP, totalCP);
        
        return StatistiqueEnfantResponse.builder()
                .enfantId(inscription.getEnfant().getId())
                .nom(inscription.getEnfant().getNom())
                .prenom(inscription.getEnfant().getPrenom())
                .classe(inscription.getClasse().getNom())
                .anneeExerciceId(anneeExerciceId)
                .anneeExercice(inscription.getAnneeExercice().getAnnee().toString())
                .nombreProgrammesCompletes(nombreProgrammesCompletes)
                .totalProgrammesClasse(totalProgrammesClasse)
                .pourcentageProgrammes(pourcentageProgrammes)
                .nombreParticipationsActivites(nombreParticipationsActivites)
                .totalActivites(totalActivites)
                .pourcentageActivites(pourcentageActivites)
                .nombrePresencesCP(nombrePresencesCP)
                .totalCP(totalCP)
                .pourcentageCP(pourcentageCP)
                .build();
    }
    
    private StatistiqueStaffResponse calculateStatistiqueStaff(Staff staff, Long anneeExerciceId) {
        // Si anneeExerciceId n'est pas fourni, utiliser l'année d'exercice du staff
        if (anneeExerciceId == null) {
            anneeExerciceId = staff.getAnneeExercice().getId();
        }
        
        // Participation aux activités
        Long nombreParticipationsActivites = participantActiviteStaffRepository
                .countByStaffIdAndAnneeExerciceId(staff.getId(), anneeExerciceId);
        Long totalActivites = activiteRepository.countByAnneeExerciceId(anneeExerciceId);
        Double pourcentageActivites = calculerPourcentage(nombreParticipationsActivites, totalActivites);
        
        // Présence aux classes progressives
        Long nombrePresencesCP = cpPresenceStaffRepository
                .countByStaffIdAndAnneeExerciceId(staff.getId(), anneeExerciceId);
        Long totalCP = classeProgressiveRepository.countByAnneeExerciceId(anneeExerciceId);
        Double pourcentageCP = calculerPourcentage(nombrePresencesCP, totalCP);
        
        String anneeExercice = anneeExerciceRepository.findById(anneeExerciceId)
                .map(ae -> ae.getAnnee().toString())
                .orElse("");
        
        return StatistiqueStaffResponse.builder()
                .staffId(staff.getId())
                .nom(staff.getInstructeur().getNom())
                .prenom(staff.getInstructeur().getPrenom())
                .role(staff.getRole().getRoleName())
                .anneeExerciceId(anneeExerciceId)
                .anneeExercice(anneeExercice)
                .nombreParticipationsActivites(nombreParticipationsActivites)
                .totalActivites(totalActivites)
                .pourcentageActivites(pourcentageActivites)
                .nombrePresencesCP(nombrePresencesCP)
                .totalCP(totalCP)
                .pourcentageCP(pourcentageCP)
                .build();
    }
    
    private void calculerClassementEnfants(List<StatistiqueEnfantResponse> statistiques) {
        // Classement par participation aux activités
        List<StatistiqueEnfantResponse> sortedByActivites = new ArrayList<>(statistiques);
        sortedByActivites.sort(Comparator.comparing(StatistiqueEnfantResponse::getPourcentageActivites).reversed());
        
        for (int i = 0; i < sortedByActivites.size(); i++) {
            sortedByActivites.get(i).setRangActivites(i + 1);
        }
        
        // Classement par présence aux CP
        List<StatistiqueEnfantResponse> sortedByCP = new ArrayList<>(statistiques);
        sortedByCP.sort(Comparator.comparing(StatistiqueEnfantResponse::getPourcentageCP).reversed());
        
        for (int i = 0; i < sortedByCP.size(); i++) {
            sortedByCP.get(i).setRangCP(i + 1);
        }
    }
    
    private void calculerClassementStaffs(List<StatistiqueStaffResponse> statistiques) {
        // Classement par participation aux activités
        List<StatistiqueStaffResponse> sortedByActivites = new ArrayList<>(statistiques);
        sortedByActivites.sort(Comparator.comparing(StatistiqueStaffResponse::getPourcentageActivites).reversed());
        
        for (int i = 0; i < sortedByActivites.size(); i++) {
            sortedByActivites.get(i).setRangActivites(i + 1);
        }
        
        // Classement par présence aux CP
        List<StatistiqueStaffResponse> sortedByCP = new ArrayList<>(statistiques);
        sortedByCP.sort(Comparator.comparing(StatistiqueStaffResponse::getPourcentageCP).reversed());
        
        for (int i = 0; i < sortedByCP.size(); i++) {
            sortedByCP.get(i).setRangCP(i + 1);
        }
    }
    
    private Double calculerPourcentage(Long partiel, Long total) {
        if (total == null || total == 0) {
            return 0.0;
        }
        return (partiel != null ? partiel.doubleValue() : 0.0) / total.doubleValue() * 100.0;
    }
}
