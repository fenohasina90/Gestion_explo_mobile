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
        
        // Vérifier que l'instructeur n'est pas déjà staff pour cette année d'exercice
        staffRepository.findByInstructeurAndAnneeExercice(request.getInstructeurId(), request.getAnneeExerciceId())
            .ifPresent(s -> {
                throw new RuntimeException("Cet instructeur est déjà staff pour cette année d'exercice");
            });
        
        Staff staff = Staff.builder()
            .instructeur(instructeur)
            .role(role)
            .anneeExercice(anneeExercice)
            .build();
        
        Staff saved = staffRepository.save(staff);
        
        // Log l'action
        journalService.logAction("Création du staff " + instructeur.getNom() + " " + instructeur.getPrenom() + 
                                 " en tant que " + role.getRoleName() + 
                                 " pour l'année " + anneeExercice.getAnnee().getYear());
        
        return mapToResponse(saved);
    }
    
    /**
     * Met à jour un staff
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
        
        String instructeurName = staff.getInstructeur().getNom() + " " + staff.getInstructeur().getPrenom();
        String oldRole = staff.getRole().getRoleName();
        
        if (request.getRoleId() != null) {
            RolesStaff role = rolesStaffRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rôle introuvable"));
            staff.setRole(role);
            
            if (!oldRole.equals(role.getRoleName())) {
                journalService.logAction("Changement du rôle de " + instructeurName + " de " + oldRole + " à " + role.getRoleName());
            }
        }
        
        Staff updated = staffRepository.save(staff);
        return mapToResponse(updated);
    }
    
    /**
     * Supprime un staff
     */
    @Transactional
    public void deleteStaff(Long id, String currentUsername) {
        // Vérifier l'utilisateur actuel
        Utilisateur currentUser = utilisateurRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Staff staff = staffRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Staff introuvable"));
        
        // Vérifier que le staff appartient à l'année d'exercice du Directeur
        if (!currentUser.getAnneeExercice().getId().equals(staff.getAnneeExercice().getId())) {
            throw new RuntimeException("Vous ne pouvez supprimer que les staffs de votre année d'exercice");
        }
        
        String instructeurName = staff.getInstructeur().getNom() + " " + staff.getInstructeur().getPrenom();
        staffRepository.delete(staff);
        
        // Log la suppression
        journalService.logAction("Suppression du staff " + instructeurName);
    }
    
    /**
     * Récupère tous les staffs
     */
    @Transactional(readOnly = true)
    public List<StaffResponse> getAllStaffs() {
        return staffRepository.findAll().stream()
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
    public List<StaffResponse> getStaffsWithFilters(Long anneeExerciceId, Long roleId) {
        return staffRepository.findByFilters(anneeExerciceId, roleId).stream()
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
            .createdAt(staff.getCreatedAt())
            .updatedAt(staff.getUpdatedAt())
            .build();
    }
}
