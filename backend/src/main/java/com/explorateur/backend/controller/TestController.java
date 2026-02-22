package com.explorateur.backend.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Hidden // Cachéde Swagger en production
public class TestController {
    
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping("/hash/{password}")
    public String generateHash(@PathVariable String password) {
        return passwordEncoder.encode(password);
    }
    
    @PostMapping("/verify")
    public boolean verifyPassword(@RequestParam String rawPassword, @RequestParam String hash) {
        return passwordEncoder.matches(rawPassword, hash);
    }
}
