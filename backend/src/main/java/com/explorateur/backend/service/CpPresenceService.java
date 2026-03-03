package com.explorateur.backend.service;

import com.explorateur.backend.dto.*;
import com.explorateur.backend.entity.*;
import com.explorateur.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CpPresenceService {
    
    private final CpPresenceExploRepository cpPresenceExploRepository;
    private final CpPresenceStaffRepository cpPresenceStaffRepository;
    private final ClasseProgressiveRepository classeProgressiveRepository;
    private final InscriptionRepository inscriptionRepository;
    private final StaffRepository staffRepository;
    private final JournalService journalService;
    
    /**
     * Récupérer les personnes disponibles pour faire la présence à une CP
     */
    @Transactional(readOnly = true)
    public PersonnesDisponiblesResponse getPersonnesDisponibles(Long classeProgressiveId) {
        ClasseProgressive cp = classeProgressiveRepository.findById(classeProgressiveId)
                .orElseThrow(() -> new RuntimeException("Classe progressive non trouvée"));
        
        Long anneeExerciceId = cp.getAnneeExercice().getId();
        
        // Récupérer tous les enfants inscrits pour cette année
        List<Inscription> inscriptions = inscriptionRepository.findByAnneeExerciceId(anneeExerciceId);
        List<ParticipantEnfantDto> enfants = inscriptions.stream()
                .map(inscription -> ParticipantEnfantDto.builder()
                        .inscriptionId(inscription.getId())
                        .enfantId(inscription.getEnfant().getId())
                        .nom(inscription.getEnfant().getNom())
                        .prenom(inscription.getEnfant().getPrenom())
                        .genre(inscription.getEnfant().getGenre())
                        .classeId(inscription.getClasse() != null ? inscription.getClasse().getId() : null)
                        .classeNom(inscription.getClasse() != null ? inscription.getClasse().getNom() : null)
                        .build())
                .collect(Collectors.toList());
        
        // Récupérer tous les staff actifs pour cette année
        List<Staff> staffList = staffRepository.findByAnneeExerciceId(anneeExerciceId)
                .stream()
                .filter(s -> s.getEtat() == 1)
                .toList();
        
        List<ParticipantStaffDto> staff = staffList.stream()
                .map(s -> ParticipantStaffDto.builder()
                        .staffId(s.getId())
                        .instructeurId(s.getInstructeur().getId())
                        .nom(s.getInstructeur().getNom())
                        .prenom(s.getInstructeur().getPrenom())
                        .totem(s.getInstructeur().getTotem())
                        .role(s.getRole().getRoleName())
                        .build())
                .collect(Collectors.toList());
        
        return PersonnesDisponiblesResponse.builder()
                .enfants(enfants)
                .staff(staff)
                .build();
    }
    
    /**
     * Enregistrer la présence à une classe progressive
     * Règles métier:
     * - Un enfant ne peut être enregistré qu'une seule fois par CP (contrainte unique en base)
     * - Un staff ne peut être enregistré qu'une seule fois par CP (contrainte unique en base)
     * - La présence ne peut être saisie que pour une CP existante (vérification)
     * - Modification autorisée uniquement le jour même (optionnel - vérification commentée)
     */
    @Transactional
    public void enregistrerPresence(EnregistrerPresenceCpRequest request, String username) {
        // Vérifier que la CP existe
        ClasseProgressive cp = classeProgressiveRepository.findById(request.getClasseProgressiveId())
                .orElseThrow(() -> new RuntimeException("Classe progressive non trouvée"));
        
        // Optionnel: Vérifier que la modification est autorisée uniquement le jour même
        // LocalDate today = LocalDate.now();
        // if (!cp.getDateCp().equals(today)) {
        //     throw new RuntimeException("La présence ne peut être modifiée que le jour de la CP");
        // }
        
        // Supprimer les anciennes présences pour permettre la modification
        cpPresenceExploRepository.deleteByClasseProgressiveId(request.getClasseProgressiveId());
        cpPresenceStaffRepository.deleteByClasseProgressiveId(request.getClasseProgressiveId());
        
        // Enregistrer les présences des enfants
        for (Long inscriptionId : request.getEnfantsPresents()) {
            Inscription inscription = inscriptionRepository.findById(inscriptionId)
                    .orElseThrow(() -> new RuntimeException("Inscription non trouvée: " + inscriptionId));
            
            // Règle métier: vérifier qu'un enfant ne soit pas enregistré deux fois
            // (Déjà géré par la contrainte unique en base de données)
            if (cpPresenceExploRepository.existsByClasseProgressiveIdAndInscriptionId(
                    request.getClasseProgressiveId(), inscriptionId)) {
                throw new RuntimeException("Cet enfant est déjà enregistré pour cette CP");
            }
            
            CpPresenceExplo presence = CpPresenceExplo.builder()
                    .classeProgressive(cp)
                    .inscription(inscription)
                    .build();
            cpPresenceExploRepository.save(presence);
        }
        
        // Enregistrer les présences du staff
        for (Long staffId : request.getStaffPresents()) {
            Staff staff = staffRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff non trouvé: " + staffId));
            
            // Règle métier: vérifier qu'un staff ne soit pas enregistré deux fois
            // (Déjà géré par la contrainte unique en base de données)
            if (cpPresenceStaffRepository.existsByClasseProgressiveIdAndStaffId(
                    request.getClasseProgressiveId(), staffId)) {
                throw new RuntimeException("Ce staff est déjà enregistré pour cette CP");
            }
            
            CpPresenceStaff presence = CpPresenceStaff.builder()
                    .classeProgressive(cp)
                    .staff(staff)
                    .build();
            cpPresenceStaffRepository.save(presence);
        }
        
        log.info("Présence enregistrée pour la CP {} par {} - {} enfants, {} staff",
                cp.getId(), username, request.getEnfantsPresents().size(), request.getStaffPresents().size());
        
        // Journalisation
        int nbEnfants = request.getEnfantsPresents().size();
        int nbStaff = request.getStaffPresents().size();
        String dateCp = cp.getDateCp().toString();
        journalService.logAction("Enregistrement de presence pour la CP du " + dateCp + 
                " (" + nbEnfants + " enfant" + (nbEnfants > 1 ? "s" : "") + 
                " et " + nbStaff + " staff" + (nbStaff > 1 ? "s" : "") + ")");
    }
    
    /**
     * Consulter les participants d'une CP avec filtres
     */
    @Transactional(readOnly = true)
    public ParticipantsResponse getParticipants(Long classeProgressiveId, Boolean filtreEnfant, Boolean filtreStaff, Long classeId) {
        List<ParticipantEnfantDto> enfants = new ArrayList<>();
        List<ParticipantStaffDto> staff = new ArrayList<>();
        
        // Récupérer les enfants présents
        if (filtreEnfant != null && filtreEnfant) {
            List<CpPresenceExplo> presencesExplo;
            
            if (classeId != null) {
                presencesExplo = cpPresenceExploRepository.findByClasseProgressiveIdAndClasseId(classeProgressiveId, classeId);
            } else {
                presencesExplo = cpPresenceExploRepository.findByClasseProgressiveId(classeProgressiveId);
            }
            
            enfants = presencesExplo.stream()
                    .map(p -> ParticipantEnfantDto.builder()
                            .inscriptionId(p.getInscription().getId())
                            .enfantId(p.getInscription().getEnfant().getId())
                            .nom(p.getInscription().getEnfant().getNom())
                            .prenom(p.getInscription().getEnfant().getPrenom())
                            .genre(p.getInscription().getEnfant().getGenre())
                            .classeId(p.getInscription().getClasse() != null ? p.getInscription().getClasse().getId() : null)
                            .classeNom(p.getInscription().getClasse() != null ? p.getInscription().getClasse().getNom() : null)
                            .build())
                    .collect(Collectors.toList());
        }
        
        // Récupérer le staff présent
        if (filtreStaff != null && filtreStaff) {
            List<CpPresenceStaff> presencesStaff = cpPresenceStaffRepository.findByClasseProgressiveId(classeProgressiveId);
            
            staff = presencesStaff.stream()
                    .map(p -> ParticipantStaffDto.builder()
                            .staffId(p.getStaff().getId())
                            .instructeurId(p.getStaff().getInstructeur().getId())
                            .nom(p.getStaff().getInstructeur().getNom())
                            .prenom(p.getStaff().getInstructeur().getPrenom())
                            .totem(p.getStaff().getInstructeur().getTotem())
                            .role(p.getStaff().getRole().getRoleName())
                            .build())
                    .collect(Collectors.toList());
        }
        
        return ParticipantsResponse.builder()
                .enfants(enfants)
                .staff(staff)
                .build();
    }
}
