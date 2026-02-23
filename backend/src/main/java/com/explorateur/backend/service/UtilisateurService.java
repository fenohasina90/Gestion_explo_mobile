package com.explorateur.backend.service;

import com.explorateur.backend.dto.CreateUtilisateurRequest;
import com.explorateur.backend.dto.UpdateUtilisateurRequest;
import com.explorateur.backend.dto.UserInfoResponse;
import com.explorateur.backend.dto.UtilisateurResponse;
import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.entity.RolesStaff;
import com.explorateur.backend.entity.Utilisateur;
import com.explorateur.backend.repository.AnneeExerciceRepository;
import com.explorateur.backend.repository.RolesStaffRepository;
import com.explorateur.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilisateurService {
    
    private final UtilisateurRepository utilisateurRepository;
    private final RolesStaffRepository rolesStaffRepository;
    private final AnneeExerciceRepository anneeExerciceRepository;
    private final PasswordEncoder passwordEncoder;
    
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

    /**
     * Crée un nouvel utilisateur
     * Règles métier :
     * - Seul un Directeur peut créer un utilisateur
     * - Pour créer un utilisateur non-Directeur : doit être dans la même année d'exercice que le Directeur actuel
     * - Pour créer un Directeur : peut être dans n'importe quelle année d'exercice, mais une seule année ne peut avoir qu'un seul Directeur actif
     * - Si un nouveau Directeur est créé, tous les autres Directeurs deviennent inactifs
     */
    @Transactional
    public UtilisateurResponse createUtilisateur(CreateUtilisateurRequest request, String currentUsername) {
        // Vérifier que l'utilisateur actuel est un Directeur
        Utilisateur currentUser = utilisateurRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        if (!"Directeur".equals(currentUser.getRole().getRoleName())) {
            throw new RuntimeException("Seul un Directeur peut créer un utilisateur");
        }

        // Vérifier si le username existe déjà
        if (utilisateurRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Ce nom d'utilisateur existe déjà");
        }

        // Récupérer le rôle et l'année d'exercice
        RolesStaff role = rolesStaffRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rôle introuvable"));
        
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(request.getAnneeExerciceId())
                .orElseThrow(() -> new RuntimeException("Année d'exercice introuvable"));

        // Règle : Le Directeur ne peut créer un utilisateur non-Directeur QUE dans son année d'exercice
        if (!"Directeur".equals(role.getRoleName())) {
            if (!currentUser.getAnneeExercice().getId().equals(anneeExercice.getId())) {
                throw new RuntimeException("Vous ne pouvez créer un utilisateur que dans votre année d'exercice actuelle");
            }
        }

        // Règle : Vérifier qu'il n'existe pas déjà un Directeur actif pour cette année d'exercice
        if ("Directeur".equals(role.getRoleName())) {
            List<Utilisateur> directeursExistants = utilisateurRepository.findByRoleRoleNameAndActive("Directeur", true);
            for (Utilisateur directeur : directeursExistants) {
                if (directeur.getAnneeExercice().getId().equals(anneeExercice.getId())) {
                    throw new RuntimeException("Un Directeur existe déjà pour cette année d'exercice");
                }
            }
            
            // Désactiver tous les autres Directeurs (de toutes les années)
            for (Utilisateur ancienDirecteur : directeursExistants) {
                ancienDirecteur.setActive(false);
                ancienDirecteur.setUpdatedAt(LocalDateTime.now());
                utilisateurRepository.save(ancienDirecteur);
            }
        }

        // Créer le nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(request.getUsername());
        utilisateur.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        utilisateur.setRole(role);
        utilisateur.setAnneeExercice(anneeExercice);
        utilisateur.setActive(true);
        utilisateur.setCreatedAt(LocalDateTime.now());
        utilisateur.setUpdatedAt(LocalDateTime.now());

        Utilisateur saved = utilisateurRepository.save(utilisateur);
        
        return mapToResponse(saved);
    }

    /**
     * Met à jour un utilisateur
     * Règles :
     * - Chaque utilisateur peut modifier ses propres informations personnelles (username, password)
     *   MAIS NE PEUT PAS modifier son role, status actif/inactif ni son année d'exercice
     * - Le Directeur peut modifier le role, status et année d'exercice des autres utilisateurs
     *   MAIS NE PEUT PAS modifier leur username ni leur mot de passe
     */
    @Transactional
    public UtilisateurResponse updateUtilisateur(Long id, UpdateUtilisateurRequest request, String currentUsername) {
        Utilisateur currentUser = utilisateurRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        boolean isDirecteur = "Directeur".equals(currentUser.getRole().getRoleName());
        boolean isSelfUpdate = utilisateur.getId().equals(currentUser.getId());

        // Si ce n'est pas le Directeur et pas une auto-modification, interdire
        if (!isDirecteur && !isSelfUpdate) {
            throw new RuntimeException("Vous ne pouvez modifier que vos propres informations");
        }

        // CAS 1 : Auto-modification (l'utilisateur modifie ses propres informations)
        if (isSelfUpdate) {
            // Peut modifier : username, password
            if (request.getUsername() != null && !request.getUsername().equals(utilisateur.getUsername())) {
                if (utilisateurRepository.findByUsername(request.getUsername()).isPresent()) {
                    throw new RuntimeException("Ce nom d'utilisateur existe déjà");
                }
                utilisateur.setUsername(request.getUsername());
            }

            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                utilisateur.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            }

            // Ne peut PAS modifier : role, active, anneeExerciceId
            if (request.getRoleId() != null || request.getActive() != null || request.getAnneeExerciceId() != null) {
                throw new RuntimeException("Vous ne pouvez pas modifier votre rôle, votre statut ou votre année d'exercice");
            }
        }
        // CAS 2 : Le Directeur modifie un autre utilisateur
        else if (isDirecteur && !isSelfUpdate) {
            // Ne peut PAS modifier : username, password
            if (request.getUsername() != null || request.getPassword() != null) {
                throw new RuntimeException("Vous ne pouvez pas modifier le nom d'utilisateur ou le mot de passe d'un autre utilisateur");
            }

            // Peut modifier : role, active, anneeExerciceId
            if (request.getRoleId() != null) {
                RolesStaff role = rolesStaffRepository.findById(request.getRoleId())
                        .orElseThrow(() -> new RuntimeException("Rôle introuvable"));
                utilisateur.setRole(role);
                
                // Si on change le rôle en Directeur, désactiver les autres Directeurs
                if ("Directeur".equals(role.getRoleName())) {
                    List<Utilisateur> ancienDirecteurs = utilisateurRepository.findByRoleRoleNameAndActive("Directeur", true);
                    for (Utilisateur ancienDirecteur : ancienDirecteurs) {
                        if (!ancienDirecteur.getId().equals(utilisateur.getId())) {
                            ancienDirecteur.setActive(false);
                            ancienDirecteur.setUpdatedAt(LocalDateTime.now());
                            utilisateurRepository.save(ancienDirecteur);
                        }
                    }
                }
            }

            if (request.getActive() != null) {
                utilisateur.setActive(request.getActive());
            }

            if (request.getAnneeExerciceId() != null) {
                AnneeExercice anneeExercice = anneeExerciceRepository.findById(request.getAnneeExerciceId())
                        .orElseThrow(() -> new RuntimeException("Année d'exercice introuvable"));
                utilisateur.setAnneeExercice(anneeExercice);
            }
        }

        utilisateur.setUpdatedAt(LocalDateTime.now());
        Utilisateur updated = utilisateurRepository.save(utilisateur);

        return mapToResponse(updated);
    }

    /**
     * Supprime un utilisateur (seul le Directeur peut supprimer)
     */
    @Transactional
    public void deleteUtilisateur(Long id, String currentUsername) {
        Utilisateur currentUser = utilisateurRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!"Directeur".equals(currentUser.getRole().getRoleName())) {
            throw new RuntimeException("Seul un Directeur peut supprimer un utilisateur");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Empêcher la suppression de soi-même
        if (utilisateur.getUsername().equals(currentUsername)) {
            throw new RuntimeException("Vous ne pouvez pas supprimer votre propre compte");
        }

        utilisateurRepository.delete(utilisateur);
    }

    /**
     * Récupère tous les utilisateurs
     */
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> getAllUtilisateurs() {
        return utilisateurRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un utilisateur par ID
     */
    @Transactional(readOnly = true)
    public UtilisateurResponse getUtilisateurById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return mapToResponse(utilisateur);
    }

    /**
     * Récupère les utilisateurs par année d'exercice
     */
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> getUtilisateursByAnneeExercice(Long anneeExerciceId) {
        return utilisateurRepository.findByAnneeExerciceId(anneeExerciceId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les utilisateurs actifs
     */
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> getUtilisateursActifs() {
        return utilisateurRepository.findByActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mapper Utilisateur vers UtilisateurResponse
     */
    private UtilisateurResponse mapToResponse(Utilisateur utilisateur) {
        UtilisateurResponse response = new UtilisateurResponse();
        response.setId(utilisateur.getId());
        response.setUsername(utilisateur.getUsername());
        response.setRole(utilisateur.getRole().getRoleName());
        response.setActive(utilisateur.getActive());
        response.setAnneeExercice(utilisateur.getAnneeExercice().getAnnee().toString());
        response.setCreatedAt(utilisateur.getCreatedAt());
        response.setUpdatedAt(utilisateur.getUpdatedAt());
        return response;
    }
}
