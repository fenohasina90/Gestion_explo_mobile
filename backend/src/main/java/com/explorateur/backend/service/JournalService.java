package com.explorateur.backend.service;

import com.explorateur.backend.dto.JournalFilterRequest;
import com.explorateur.backend.dto.JournalResponse;
import com.explorateur.backend.entity.Journal;
import com.explorateur.backend.entity.Utilisateur;
import com.explorateur.backend.repository.JournalRepository;
import com.explorateur.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion du journal d'audit
 */
@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalRepository journalRepository;
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Enregistre une action dans le journal
     */
    @Transactional
    public void logAction(String action) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                
                String username = authentication.getName();
                Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                        .orElse(null);

                Journal journal = Journal.builder()
                        .action(action)
                        .utilisateur(utilisateur)
                        .timestamp(LocalDateTime.now())
                        .build();

                journalRepository.save(journal);
            }
        } catch (Exception e) {
            // Ne pas propager les erreurs de journalisation
            System.err.println("Erreur lors de la journalisation: " + e.getMessage());
        }
    }

    /**
     * Enregistre une action avec un utilisateur spécifique
     */
    @Transactional
    public void logAction(String action, Long utilisateurId) {
        try {
            Utilisateur utilisateur = null;
            if (utilisateurId != null) {
                utilisateur = utilisateurRepository.findById(utilisateurId).orElse(null);
            }

            Journal journal = Journal.builder()
                    .action(action)
                    .utilisateur(utilisateur)
                    .timestamp(LocalDateTime.now())
                    .build();

            journalRepository.save(journal);
        } catch (Exception e) {
            System.err.println("Erreur lors de la journalisation: " + e.getMessage());
        }
    }

    /**
     * Récupère toutes les entrées du journal
     */
    @Transactional(readOnly = true)
    public List<JournalResponse> getAllJournal() {
        return journalRepository.findAllByOrderByTimestampDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les entrées du journal avec filtres
     */
    @Transactional(readOnly = true)
    public List<JournalResponse> getJournalWithFilters(JournalFilterRequest filter) {
        List<Journal> journals = journalRepository.findByFilters(
                filter.getDateDebut(),
                filter.getDateFin(),
                filter.getUtilisateurId(),
                filter.getSearchText()
        );

        return journals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les entrées du journal par période
     */
    @Transactional(readOnly = true)
    public List<JournalResponse> getJournalByPeriod(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return journalRepository.findByTimestampBetweenOrderByTimestampDesc(dateDebut, dateFin)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les entrées du journal par utilisateur
     */
    @Transactional(readOnly = true)
    public List<JournalResponse> getJournalByUtilisateur(Long utilisateurId) {
        return journalRepository.findByUtilisateurIdOrderByTimestampDesc(utilisateurId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mapper Journal vers JournalResponse
     */
    private JournalResponse mapToResponse(Journal journal) {
        return JournalResponse.builder()
                .id(journal.getId())
                .action(journal.getAction())
                .username(journal.getUtilisateur() != null ? journal.getUtilisateur().getUsername() : "Système")
                .utilisateurId(journal.getUtilisateur() != null ? journal.getUtilisateur().getId() : null)
                .timestamp(journal.getTimestamp())
                .build();
    }
}
