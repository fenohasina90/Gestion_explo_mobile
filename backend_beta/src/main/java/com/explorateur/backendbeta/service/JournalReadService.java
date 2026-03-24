package com.explorateur.backendbeta.service;

import com.explorateur.backendbeta.dto.JournalFilterRequest;
import com.explorateur.backendbeta.dto.JournalResponse;
import com.explorateur.backendbeta.entity.Journal;
import com.explorateur.backendbeta.repository.JournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalReadService {

    private final JournalRepository journalRepository;

    @Transactional(readOnly = true)
    public List<JournalResponse> getAllJournal() {
        return journalRepository.findAllByOrderByTimestampDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JournalResponse> getJournalWithFilters(JournalFilterRequest filters) {
        LocalDateTime dateDebut = (filters != null && filters.getDateDebut() != null)
                ? filters.getDateDebut()
                : LocalDateTime.of(1900, 1, 1, 0, 0, 0);

        LocalDateTime dateFin = (filters != null && filters.getDateFin() != null)
                ? filters.getDateFin()
                : LocalDateTime.of(2999, 12, 31, 23, 59, 59);

        String searchText = (filters != null && filters.getSearchText() != null)
                ? filters.getSearchText().trim()
                : "";

        return journalRepository.findByFilters(dateDebut, dateFin, searchText).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private JournalResponse mapToResponse(Journal journal) {
        return JournalResponse.builder()
                .id(journal.getId())
                .action(journal.getAction())
                .username(journal.getUtilisateur() != null ? journal.getUtilisateur().getUsername() : "Systeme")
                .utilisateurId(journal.getUtilisateur() != null ? journal.getUtilisateur().getId() : null)
                .timestamp(journal.getTimestamp())
                .build();
    }
}
