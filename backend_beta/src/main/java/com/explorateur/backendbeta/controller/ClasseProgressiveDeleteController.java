package com.explorateur.backendbeta.controller;

import com.explorateur.backendbeta.service.ClasseProgressiveDeleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/classe-progressive")
@CrossOrigin(origins = "*", methods = {org.springframework.web.bind.annotation.RequestMethod.DELETE, org.springframework.web.bind.annotation.RequestMethod.OPTIONS})
@RequiredArgsConstructor
public class ClasseProgressiveDeleteController {

    private final ClasseProgressiveDeleteService classeProgressiveDeleteService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClasseProgressive(@PathVariable Long id) {
        classeProgressiveDeleteService.deleteClasseProgressive(id);
        return ResponseEntity.noContent().build();
    }
}
