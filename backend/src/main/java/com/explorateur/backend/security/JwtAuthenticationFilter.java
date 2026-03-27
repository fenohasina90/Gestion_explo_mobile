package com.explorateur.backend.security;

import com.explorateur.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") || 
               path.startsWith("/api/test/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars/") ||
               path.startsWith("/configuration/");
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        
        // Si pas de header Authorization ou ne commence pas par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extraire le token JWT
        jwt = authHeader.substring(7);
        
        try {
            username = jwtUtil.extractUsername(jwt);
            
            // Si l'utilisateur n'est pas déjà authentifié
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Valider le token
                if (jwtUtil.validateToken(jwt, username)) {
                    // Extraire le rôle du token
                    String role = jwtUtil.extractClaim(jwt, claims -> claims.get("role", String.class));

                    List<SimpleGrantedAuthority> authorities = buildAuthorities(role);
                    
                    // Créer l'authentification avec les alias nécessaires (underscore et tiret)
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                        authorities
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la validation du JWT: " + e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> buildAuthorities(String role) {
        Set<String> roleVariants = new LinkedHashSet<>();

        String safeRole = role == null ? "" : role.trim();
        if (!safeRole.isEmpty()) {
            roleVariants.add(safeRole);
            roleVariants.add(safeRole.replace('_', '-'));
            roleVariants.add(safeRole.replace('-', '_'));
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String variant : roleVariants) {
            if (!variant.isEmpty()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + variant));
            }
        }

        return authorities;
    }
}
