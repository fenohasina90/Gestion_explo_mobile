package com.explorateur.backend.service;

import com.explorateur.backend.dto.ClasseResponse;
import com.explorateur.backend.entity.Classe;
import com.explorateur.backend.repository.ClasseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClasseService {
    
    private final ClasseRepository classeRepository;
    
    /**
     * Récupérer toutes les classes
     */
    @Transactional(readOnly = true)
    public List<ClasseResponse> getAllClasses() {
        return classeRepository.findAllByOrderByNomAsc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer une classe par ID
     */
    @Transactional(readOnly = true)
    public ClasseResponse getClasseById(Long id) {
        Classe classe = classeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classe introuvable"));
        return mapToResponse(classe);
    }
    
    // Méthode de mapping
    
    private ClasseResponse mapToResponse(Classe classe) {
        return ClasseResponse.builder()
                .id(classe.getId())
                .nom(classe.getNom())
                .logo(classe.getLogo())
                .age(classe.getAge())
                .build();
    }
}
