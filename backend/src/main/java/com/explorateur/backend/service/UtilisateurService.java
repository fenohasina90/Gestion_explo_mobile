package com.explorateur.backend.service;

import com.explorateur.backend.dto.UserInfoResponse;
import com.explorateur.backend.entity.Utilisateur;
import com.explorateur.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UtilisateurService {
    
    private final UtilisateurRepository utilisateurRepository;
    
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(String username) {
        Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        return UserInfoResponse.builder()
                .id(utilisateur.getId())
                .username(utilisateur.getUsername())
                .role(utilisateur.getRole().getRoleName())
                .active(utilisateur.getActive())
                .anneeExercice(utilisateur.getAnneeExercice() != null ? 
                        utilisateur.getAnneeExercice().getAnnee().toString() : null)
                .build();
    }
}
