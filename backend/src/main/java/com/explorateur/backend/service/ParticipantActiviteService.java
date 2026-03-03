package com.explorateur.backend.service;

import com.explorateur.backend.dto.*;
import com.explorateur.backend.entity.*;
import com.explorateur.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantActiviteService {
    
    private final ParticipantActiviteExploRepository participantExploRepository;
    private final ParticipantActiviteStaffRepository participantStaffRepository;
    private final ActiviteRepository activiteRepository;
    private final InscriptionRepository inscriptionRepository;
    private final StaffRepository staffRepository;
    private final ActiviteStatusRepository activiteStatusRepository;
    private final JournalService journalService;
    
    /**
     * Récupérer les personnes disponibles pour faire la présence
     */
    @Transactional(readOnly = true)
    public PersonnesDisponiblesResponse getPersonnesDisponibles(Long activiteId) {
        Activite activite = activiteRepository.findById(activiteId)
                .orElseThrow(() -> new RuntimeException("Activité non trouvée"));
        
        Long anneeExerciceId = activite.getBudgetGlobal().getAnneeExercice().getId();
        
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
     * Enregistrer la présence à une activité
     */
    @Transactional
    public void enregistrerPresence(EnregistrerPresenceRequest request, String username) {
        Activite activite = activiteRepository.findById(request.getActiviteId())
                .orElseThrow(() -> new RuntimeException("Activité non trouvée"));
        
        // Vérifier que le budget est approuvé
        if (!"Approuvé comité".equals(activite.getBudgetGlobal().getStatus().getNom())) {
            throw new RuntimeException("Le budget doit être approuvé pour enregistrer la présence");
        }
        
        // Supprimer les anciennes présences
        participantExploRepository.deleteByActiviteId(request.getActiviteId());
        participantStaffRepository.deleteByActiviteId(request.getActiviteId());
        
        // Enregistrer les présences des enfants
        for (Long inscriptionId : request.getEnfantsPresents()) {
            Inscription inscription = inscriptionRepository.findById(inscriptionId)
                    .orElseThrow(() -> new RuntimeException("Inscription non trouvée: " + inscriptionId));
            
            ParticipantActiviteExplo participant = ParticipantActiviteExplo.builder()
                    .activite(activite)
                    .inscription(inscription)
                    .build();
            participantExploRepository.save(participant);
        }
        
        // Enregistrer les présences du staff
        for (Long staffId : request.getStaffPresents()) {
            Staff staff = staffRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff non trouvé: " + staffId));
            
            ParticipantActiviteStaff participant = ParticipantActiviteStaff.builder()
                    .activite(activite)
                    .staff(staff)
                    .build();
            participantStaffRepository.save(participant);
        }
        
        // Changer le statut de l'activité à "Terminé" (ID 2)
        ActiviteStatus statusTermine = activiteStatusRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Statut 'Terminé' non trouvé"));
        activite.setStatus(statusTermine);
        activiteRepository.save(activite);
        
        log.info("Présence enregistrée pour l'activité {} par {} - {} enfants, {} staff",
                activite.getId(), username, request.getEnfantsPresents().size(), request.getStaffPresents().size());
        
        // Journalisation
        int nbEnfants = request.getEnfantsPresents().size();
        int nbStaff = request.getStaffPresents().size();
        journalService.logAction("Enregistrement de presence pour l'activite " + activite.getNom() + 
                " (" + nbEnfants + " enfant" + (nbEnfants > 1 ? "s" : "") + 
                " et " + nbStaff + " staff" + (nbStaff > 1 ? "s" : "") + ")");
    }
    
    /**
     * Consulter les participants d'une activité avec filtres
     */
    @Transactional(readOnly = true)
    public ParticipantsResponse getParticipants(Long activiteId, Boolean filtreEnfant, Boolean filtreStaff, Long classeId) {
        List<ParticipantEnfantDto> enfants = new ArrayList<>();
        List<ParticipantStaffDto> staff = new ArrayList<>();
        
        // Récupérer les enfants présents
        if (filtreEnfant != null && filtreEnfant) {
            List<ParticipantActiviteExplo> participantsExplo;
            
            if (classeId != null) {
                participantsExplo = participantExploRepository.findByActiviteIdAndClasseId(activiteId, classeId);
            } else {
                participantsExplo = participantExploRepository.findByActiviteId(activiteId);
            }
            
            enfants = participantsExplo.stream()
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
            List<ParticipantActiviteStaff> participantsStaff = participantStaffRepository.findByActiviteId(activiteId);
            
            staff = participantsStaff.stream()
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
