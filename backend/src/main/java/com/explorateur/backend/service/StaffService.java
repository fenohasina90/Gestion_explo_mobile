package com.explorateur.backend.service;

import com.explorateur.backend.dto.CreateStaffRequest;
import com.explorateur.backend.dto.StaffResponse;
import com.explorateur.backend.dto.UpdateStaffRequest;
import com.explorateur.backend.entity.AnneeExercice;
import com.explorateur.backend.entity.Instructeur;
import com.explorateur.backend.entity.RolesStaff;
import com.explorateur.backend.entity.Staff;
import com.explorateur.backend.entity.Utilisateur;
import com.explorateur.backend.repository.AnneeExerciceRepository;
import com.explorateur.backend.repository.InstructeurRepository;
import com.explorateur.backend.repository.RolesStaffRepository;
import com.explorateur.backend.repository.StaffRepository;
import com.explorateur.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des staffs
 */
@Service
@RequiredArgsConstructor
public class StaffService {
    
    private final StaffRepository staffRepository;
    private final InstructeurRepository instructeurRepository;
    private final RolesStaffRepository rolesStaffRepository;
    private final AnneeExerciceRepository anneeExerciceRepository;
    private final JournalService journalService;
    private final UtilisateurRepository utilisateurRepository;
    
    /**
     * Crée un nouveau staff à partir d'un instructeur
     */
    @Transactional
    public StaffResponse createStaff(CreateStaffRequest request, String currentUsername) {
        // Vérifier l'utilisateur actuel et son année d'exercice
        Utilisateur currentUser = utilisateurRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        if (!"Directeur".equals(currentUser.getRole().getRoleName())) {
            throw new RuntimeException("Seul un Directeur peut créer un staff");
        }
        
        // Vérifier que l'instructeur existe
        Instructeur instructeur = instructeurRepository.findById(request.getInstructeurId())
            .orElseThrow(() -> new RuntimeException("Instructeur introuvable"));
        
        // Vérifier que le rôle existe
        RolesStaff role = rolesStaffRepository.findById(request.getRoleId())
            .orElseThrow(() -> new RuntimeException("Rôle introuvable"));
        
        // Vérifier que l'année d'exercice existe
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(request.getAnneeExerciceId())
            .orElseThrow(() -> new RuntimeException("Année d'exercice introuvable"));
        
        // Le Directeur ne peut créer un staff que pour son année d'exercice
        if (!currentUser.getAnneeExercice().getId().equals(anneeExercice.getId())) {
            throw new RuntimeException("Vous ne pouvez créer un staff que pour votre année d'exercice");
        }
        
        // Vérifier si l'instructeur a déjà été staff pour cette année (incluant les supprimés)
        Optional<Staff> existingStaff = staffRepository.findByInstructeurAndAnneeExerciceIncludingDeleted(
            request.getInstructeurId(), 
            request.getAnneeExerciceId()
        );
        
        Staff staff;
        String logMessage;
        
        if (existingStaff.isPresent() && existingStaff.get().getEtat() == 11) {
            // Réactiver le staff supprimé
            staff = existingStaff.get();
            staff.setEtat(1);
            staff.setRole(role);
            logMessage = "Réactivation du staff " + instructeur.getNom() + " " + instructeur.getPrenom() + 
                        " en tant que " + role.getRoleName() + 
                        " pour l'année " + anneeExercice.getAnnee().getYear();
        } else if (existingStaff.isPresent()) {
            // Staff actif existe déjà
            throw new RuntimeException("Cet instructeur est déjà staff pour cette année d'exercice");
        } else {
            // Créer un nouveau staff
            staff = Staff.builder()
                .instructeur(instructeur)
                .role(role)
                .anneeExercice(anneeExercice)
                .etat(1) // 1 = actif
                .build();
            logMessage = "Création du staff " + instructeur.getNom() + " " + instructeur.getPrenom() + 
                        " en tant que " + role.getRoleName() + 
                        " pour l'année " + anneeExercice.getAnnee().getYear();
        }
        
        Staff saved = staffRepository.save(staff);
        
        // Log l'action
        journalService.logAction(logMessage);
        
        return mapToResponse(saved);
    }
    
    /**
     * Met à jour un staff (et les infos de l'instructeur associé)
     */
    @Transactional
    public StaffResponse updateStaff(Long id, UpdateStaffRequest request, String currentUsername) {
        // Vérifier l'utilisateur actuel
        Utilisateur currentUser = utilisateurRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Staff staff = staffRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Staff introuvable"));
        
        // Vérifier que le staff appartient à l'année d'exercice du Directeur/Co-directeur
        if (!currentUser.getAnneeExercice().getId().equals(staff.getAnneeExercice().getId())) {
            throw new RuntimeException("Vous ne pouvez modifier que les staffs de votre année d'exercice");
        }
        
        Instructeur instructeur = staff.getInstructeur();
        String instructeurName = instructeur.getNom() + " " + instructeur.getPrenom();
        String oldRole = staff.getRole().getRoleName();
        StringBuilder logMessage = new StringBuilder();
        
        // Mise à jour du rôle
        if (request.getRoleId() != null) {
            RolesStaff role = rolesStaffRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rôle introuvable"));
            
            if (!oldRole.equals(role.getRoleName())) {
                staff.setRole(role);
                logMessage.append("Changement du rôle de ").append(instructeurName)
                          .append(" de ").append(oldRole).append(" à ").append(role.getRoleName()).append(". ");
            }
        }
        
        // Mise à jour des informations de l'instructeur avec journalisation détaillée
        StringBuilder instructeurChanges = new StringBuilder();
        
        if (request.getNom() != null && !request.getNom().equals(instructeur.getNom())) {
            instructeurChanges.append("Nom: ").append(instructeur.getNom()).append(" → ").append(request.getNom()).append("; ");
            instructeur.setNom(request.getNom());
        }
        if (request.getPrenom() != null && !request.getPrenom().equals(instructeur.getPrenom())) {
            instructeurChanges.append("Prénom: ").append(instructeur.getPrenom()).append(" → ").append(request.getPrenom()).append("; ");
            instructeur.setPrenom(request.getPrenom());
        }
        if (request.getGenre() != null && !request.getGenre().equals(instructeur.getGenre())) {
            instructeurChanges.append("Genre: ").append(instructeur.getGenre()).append(" → ").append(request.getGenre()).append("; ");
            instructeur.setGenre(request.getGenre());
        }
        if (request.getTotem() != null && !request.getTotem().equals(instructeur.getTotem())) {
            String oldTotem = instructeur.getTotem() != null ? instructeur.getTotem() : "(vide)";
            instructeurChanges.append("Totem: ").append(oldTotem).append(" → ").append(request.getTotem()).append("; ");
            instructeur.setTotem(request.getTotem());
        }
        if (request.getTelephone() != null && !request.getTelephone().equals(instructeur.getTelephone())) {
            String oldTel = instructeur.getTelephone() != null ? instructeur.getTelephone() : "(vide)";
            instructeurChanges.append("Téléphone: ").append(oldTel).append(" → ").append(request.getTelephone()).append("; ");
            instructeur.setTelephone(request.getTelephone());
        }
        if (request.getEstChefGuide() != null && !request.getEstChefGuide().equals(instructeur.getEstChefGuide())) {
            instructeurChanges.append("Chef Guide: ").append(instructeur.getEstChefGuide()).append(" → ").append(request.getEstChefGuide()).append("; ");
            instructeur.setEstChefGuide(request.getEstChefGuide());
        }
        
        if (instructeurChanges.length() > 0) {
            instructeurRepository.save(instructeur);
            logMessage.append("Modification de l'instructeur ").append(instructeurName).append(": ").append(instructeurChanges);
        }
        
        // Enregistrer dans le journal si des modifications ont été effectuées
        if (logMessage.length() > 0) {
            journalService.logAction(logMessage.toString());
        }
        
        Staff updated = staffRepository.save(staff);
        return mapToResponse(updated);
    }
    
    /**
     * Supprime un staff (suppression logique)
     */
    @Transactional
    public void deleteStaff(Long id, String currentUsername) {
        // Vérifier l'utilisateur actuel
        Utilisateur currentUser = utilisateurRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Staff staff = staffRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Staff introuvable"));
        
        // Vérifier que le staff n'est pas déjà supprimé
        if (staff.getEtat() == 11) {
            throw new RuntimeException("Ce staff est déjà supprimé");
        }
        
        // Vérifier que le staff appartient à l'année d'exercice du Directeur
        if (!currentUser.getAnneeExercice().getId().equals(staff.getAnneeExercice().getId())) {
            throw new RuntimeException("Vous ne pouvez supprimer que les staffs de votre année d'exercice");
        }
        
        String instructeurName = staff.getInstructeur().getNom() + " " + staff.getInstructeur().getPrenom();
        
        // Suppression logique : mettre etat à 11
        staff.setEtat(11);
        staffRepository.save(staff);
        
        // Log la suppression
        journalService.logAction("Suppression du staff " + instructeurName);
    }
    
    /**
     * Récupère tous les staffs (excluant les supprimés)
     */
    @Transactional(readOnly = true)
    public List<StaffResponse> getAllStaffs() {
        return staffRepository.findAllActive().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère un staff par ID
     */
    @Transactional(readOnly = true)
    public StaffResponse getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Staff introuvable"));
        return mapToResponse(staff);
    }
    
    /**
     * Récupère les staffs avec filtres
     */
    @Transactional(readOnly = true)
    public List<StaffResponse> getStaffsWithFilters(Long anneeExerciceId, Long roleId, Boolean estChefGuide) {
        return staffRepository.findByFilters(anneeExerciceId, roleId, estChefGuide).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère les staffs par année d'exercice
     */
    @Transactional(readOnly = true)
    public List<StaffResponse> getStaffsByAnneeExercice(Long anneeExerciceId) {
        return staffRepository.findByAnneeExerciceId(anneeExerciceId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère les staffs actifs
     */
    /**
     * Mapper Staff vers StaffResponse
     */
    private StaffResponse mapToResponse(Staff staff) {
        return StaffResponse.builder()
            .id(staff.getId())
            .instructeurId(staff.getInstructeur().getId())
            .instructeurNom(staff.getInstructeur().getNom())
            .instructeurPrenom(staff.getInstructeur().getPrenom())
            .instructeurGenre(staff.getInstructeur().getGenre())
            .instructeurTotem(staff.getInstructeur().getTotem())
            .instructeurTelephone(staff.getInstructeur().getTelephone())
            .instructeurEstChefGuide(staff.getInstructeur().getEstChefGuide())
            .role(staff.getRole().getRoleName())
            .roleId(staff.getRole().getId())
            .anneeExerciceId(staff.getAnneeExercice().getId())
            .anneeExercice(staff.getAnneeExercice().getAnnee().toString())
            .etat(staff.getEtat())
            .createdAt(staff.getCreatedAt())
            .updatedAt(staff.getUpdatedAt())
            .build();
    }
}
