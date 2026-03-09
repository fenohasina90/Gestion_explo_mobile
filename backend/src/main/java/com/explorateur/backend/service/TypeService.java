package com.explorateur.backend.service;

import com.explorateur.backend.dto.TypeResponse;
import com.explorateur.backend.entity.Type;
import com.explorateur.backend.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des types de mouvements budgétaires
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TypeService {
    
    private final TypeRepository typeRepository;
    
    /**
     * Obtenir tous les types de mouvements
     */
    @Transactional(readOnly = true)
    public List<TypeResponse> getAllTypes() {
        log.info("Récupération de tous les types de mouvements");
        return typeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtenir un type par son ID
     */
    @Transactional(readOnly = true)
    public TypeResponse getTypeById(Long id) {
        log.info("Récupération du type ID: {}", id);
        Type type = typeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type introuvable"));
        return mapToResponse(type);
    }
    
    /**
     * Mapper Type vers TypeResponse
     */
    private TypeResponse mapToResponse(Type type) {
        return TypeResponse.builder()
                .id(type.getId())
                .type(type.getType())
                .build();
    }
}
