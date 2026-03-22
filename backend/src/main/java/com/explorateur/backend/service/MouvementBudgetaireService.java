package com.explorateur.backend.service;

import com.explorateur.backend.dto.*;
import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.entity.MouvementBudgetaire;
import com.explorateur.backend.entity.Type;
import com.explorateur.backend.entity.Utilisateur;
import com.explorateur.backend.repository.AnneeExerciceRepository;
import com.explorateur.backend.repository.MouvementBudgetaireRepository;
import com.explorateur.backend.repository.TypeRepository;
import com.explorateur.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MouvementBudgetaireService {

    private final MouvementBudgetaireRepository mouvementBudgetaireRepository;
    private final TypeRepository typeRepository;
    private final AnneeExerciceRepository anneeExerciceRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JournalService journalService;

    @Transactional
    public MouvementBudgetaireResponse createMouvement(CreateMouvementBudgetaireRequest request) {
        Type type = typeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new RuntimeException("Type de mouvement introuvable"));

        AnneeExercice anneeExercice = resolveAnneeExerciceCourante();

        MouvementBudgetaire mouvement = MouvementBudgetaire.builder()
                .type(type)
                .anneeExercice(anneeExercice)
                .montant(request.getMontant())
                .description(request.getDescription())
                .build();

        MouvementBudgetaire saved = mouvementBudgetaireRepository.save(mouvement);
        journalService.logAction(String.format(
                "Création mouvement budgétaire #%d - Type: %s - Montant: %s",
                saved.getId(),
                saved.getType().getType(),
                saved.getMontant()
        ));

        return mapToResponse(saved);
    }

    @Transactional
    public MouvementBudgetaireResponse updateMouvement(Long id, UpdateMouvementBudgetaireRequest request) {
        MouvementBudgetaire mouvement = mouvementBudgetaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement budgétaire introuvable"));

        Type type = typeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new RuntimeException("Type de mouvement introuvable"));

        mouvement.setType(type);
        mouvement.setMontant(request.getMontant());
        mouvement.setDescription(request.getDescription());

        MouvementBudgetaire saved = mouvementBudgetaireRepository.save(mouvement);
        journalService.logAction(String.format(
                "Modification mouvement budgétaire #%d - Type: %s - Montant: %s",
                saved.getId(),
                saved.getType().getType(),
                saved.getMontant()
        ));

        return mapToResponse(saved);
    }

    @Transactional
    public void deleteMouvement(Long id) {
        MouvementBudgetaire mouvement = mouvementBudgetaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement budgétaire introuvable"));

        String type = mouvement.getType() != null ? mouvement.getType().getType() : "N/A";
        BigDecimal montant = mouvement.getMontant();

        mouvementBudgetaireRepository.delete(mouvement);
        journalService.logAction(String.format(
                "Suppression mouvement budgétaire #%d - Type: %s - Montant: %s",
                id,
                type,
                montant
        ));
    }

    @Transactional(readOnly = true)
    public MouvementBudgetaireResponse getMouvementById(Long id) {
        MouvementBudgetaire mouvement = mouvementBudgetaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement budgétaire introuvable"));
        return mapToResponse(mouvement);
    }

    @Transactional(readOnly = true)
    public List<MouvementBudgetaireResponse> getMouvementsWithFilters(MouvementBudgetaireFilterRequest filters) {
        List<MouvementBudgetaire> mouvements = mouvementBudgetaireRepository.findByFilters(
                filters != null ? filters.getRecherche() : null,
                filters != null ? filters.getDateDebut() : null,
                filters != null ? filters.getDateFin() : null,
                filters != null ? filters.getTypeId() : null,
                filters != null ? filters.getAnneeExerciceId() : null,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return mouvements.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<MouvementBudgetaireResponse> getMouvementsWithFiltersPaginated(MouvementBudgetaireFilterRequest filters,
                                                                                         Pageable pageable) {
        Page<MouvementBudgetaire> page = mouvementBudgetaireRepository.findByFilters(
                filters != null ? filters.getRecherche() : null,
                filters != null ? filters.getDateDebut() : null,
                filters != null ? filters.getDateFin() : null,
                filters != null ? filters.getTypeId() : null,
                filters != null ? filters.getAnneeExerciceId() : null,
                pageable
        );

        List<MouvementBudgetaireResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.<MouvementBudgetaireResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    @Transactional(readOnly = true)
    public EtatCaisseResponse getEtatCaisse(Long anneeExerciceId) {
        BigDecimal totalRecettes = mouvementBudgetaireRepository.sumMontantByType("RECETTE", anneeExerciceId);
        BigDecimal totalDepenses = mouvementBudgetaireRepository.sumMontantByType("DEPENSE", anneeExerciceId);
        BigDecimal solde = totalRecettes.subtract(totalDepenses);

        AnneeExercice annee = null;
        if (anneeExerciceId != null) {
            annee = anneeExerciceRepository.findById(anneeExerciceId).orElse(null);
        } else {
            annee = resolveAnneeExerciceCouranteOrNull();
        }

        return EtatCaisseResponse.builder()
                .totalRecettes(totalRecettes)
                .totalDepenses(totalDepenses)
                .solde(solde)
                .anneeExercice(mapAnnee(annee))
                .build();
    }

    private AnneeExercice resolveAnneeExerciceCourante() {
        AnneeExercice annee = resolveAnneeExerciceCouranteOrNull();
        if (annee == null) {
            throw new RuntimeException("Aucune année d'exercice disponible");
        }
        return annee;
    }

    private AnneeExercice resolveAnneeExerciceCouranteOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Utilisateur utilisateur = utilisateurRepository.findByUsername(username).orElse(null);
            if (utilisateur != null && utilisateur.getAnneeExercice() != null) {
                return utilisateur.getAnneeExercice();
            }
        }

        return anneeExerciceRepository.findFirstByOrderByAnneeDesc().orElse(null);
    }

    private MouvementBudgetaireResponse mapToResponse(MouvementBudgetaire mouvement) {
        return MouvementBudgetaireResponse.builder()
                .id(mouvement.getId())
                .anneeExercice(mapAnnee(mouvement.getAnneeExercice()))
                .type(mapType(mouvement.getType()))
                .montant(mouvement.getMontant())
                .description(mouvement.getDescription())
                .createdAt(mouvement.getCreatedAt())
                .build();
    }

    private AnneeExerciceResponse mapAnnee(AnneeExercice annee) {
        if (annee == null) {
            return null;
        }
        return AnneeExerciceResponse.builder()
                .id(annee.getId())
                .annee(annee.getAnnee())
                .dateFin(annee.getDateFin())
                .createdAt(annee.getCreatedAt())
                .build();
    }

    private TypeResponse mapType(Type type) {
        if (type == null) {
            return null;
        }
        return TypeResponse.builder()
                .id(type.getId())
                .type(type.getType())
                .build();
    }
}
