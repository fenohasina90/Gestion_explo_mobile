package com.explorateur.backendbeta.service;

import com.explorateur.backendbeta.repository.ClasseProgressiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClasseProgressiveDeleteService {

    private final ClasseProgressiveRepository classeProgressiveRepository;

    @Transactional
    public void deleteClasseProgressive(Long id) {
        if (!classeProgressiveRepository.existsById(id)) {
            throw new RuntimeException("CP non trouvee avec l'ID: " + id);
        }

        // Supprimer les dependances avant la suppression de la CP pour eviter les violations FK.
        classeProgressiveRepository.deleteCpDetailsInstructeursByCpId(id);
        classeProgressiveRepository.deleteHistoriqueByCpId(id);
        classeProgressiveRepository.deleteCpPresenceExploByCpId(id);
        classeProgressiveRepository.deleteCpPresenceStaffByCpId(id);
        classeProgressiveRepository.deleteCpDetailsByCpId(id);

        int deleted = classeProgressiveRepository.hardDeleteCpById(id);
        if (deleted == 0) {
            throw new RuntimeException("Suppression CP echouee pour l'ID: " + id);
        }
    }
}
