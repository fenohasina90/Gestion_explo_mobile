package com.explorateur.backendbeta.service;

import com.explorateur.backendbeta.dto.AnneeExerciceResponse;
import com.explorateur.backendbeta.dto.EtatCaisseResponse;
import com.explorateur.backendbeta.dto.MouvementBudgetaireFilterRequest;
import com.explorateur.backendbeta.dto.MouvementBudgetaireResponse;
import com.explorateur.backendbeta.dto.PageResponse;
import com.explorateur.backendbeta.dto.TypeResponse;
import com.explorateur.backendbeta.entity.AnneeExercice;
import com.explorateur.backendbeta.entity.MouvementBudgetaire;
import com.explorateur.backendbeta.entity.TypeMouvement;
import com.explorateur.backendbeta.repository.AnneeExerciceRepository;
import com.explorateur.backendbeta.repository.MouvementBudgetaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MouvementBudgetaireReadService {

    private final MouvementBudgetaireRepository mouvementBudgetaireRepository;
    private final AnneeExerciceRepository anneeExerciceRepository;

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
            annee = anneeExerciceRepository.findFirstByOrderByAnneeDesc().orElse(null);
        }

        return EtatCaisseResponse.builder()
                .totalRecettes(totalRecettes)
                .totalDepenses(totalDepenses)
                .solde(solde)
                .anneeExercice(mapAnnee(annee))
                .build();
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

    private TypeResponse mapType(TypeMouvement type) {
        if (type == null) {
            return null;
        }
        return TypeResponse.builder()
                .id(type.getId())
                .type(type.getType())
                .build();
    }
}
