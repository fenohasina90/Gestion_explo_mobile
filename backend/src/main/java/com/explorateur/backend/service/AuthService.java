package com.explorateur.backend.service;

import com.explorateur.backend.dto.LoginRequest;
import com.explorateur.backend.dto.LoginResponse;
import com.explorateur.backend.entity.Utilisateur;
import com.explorateur.backend.repository.UtilisateurRepository;
import com.explorateur.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Transactional(readOnly = true)
    public LoginResponse authenticate(LoginRequest loginRequest) {
        // Chercher l'utilisateur actif par son username
        Utilisateur utilisateur = utilisateurRepository.findByUsernameAndActiveTrue(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé ou inactif"));
        
        // Vérifier le mot de passe
        if (!passwordEncoder.matches(loginRequest.getPassword(), utilisateur.getPasswordHash())) {
            throw new RuntimeException("Mot de passe incorrect");
        }
        
        // Générer le token JWT
        String token = jwtUtil.generateToken(
                utilisateur.getUsername(), 
                utilisateur.getRole().getRoleName(),
                utilisateur.getId()
        );
        
        // Construire la réponse
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(utilisateur.getId())
                .username(utilisateur.getUsername())
                .role(utilisateur.getRole().getRoleName())
                .anneeExercice(utilisateur.getAnneeExercice() != null ? 
                        utilisateur.getAnneeExercice().getAnnee().toString() : null)
                .build();
    }
}
