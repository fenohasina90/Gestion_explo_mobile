package com.explorateur.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/v3/api-docs",
                "/api-docs/**",
                "/api-docs",
                "/swagger-resources/**",
                "/webjars/**",
                "/configuration/**"
        );
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // Endpoints publics - API
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                // Consultation budget/journal (lecture) : accessibles sans blocage
                .requestMatchers(HttpMethod.GET, "/api/budget-mouvements").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/budget-mouvements/list").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/budget-mouvements/all").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/budget-mouvements/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/journal/budget-mouvements").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/journal/filter").permitAll()
                // Actuator endpoints (pour health check)
                .requestMatchers("/actuator/**").permitAll()
                // Endpoints budget/journal: tout utilisateur authentifié peut consulter
                .requestMatchers("/api/budget-mouvements/**").authenticated()
                .requestMatchers("/api/mouvements-budgetaires/**").authenticated()
                .requestMatchers("/api/journal/**").authenticated()
                .requestMatchers("/api/budget-query/**").authenticated()
                .requestMatchers("/api/journal-query/**").authenticated()
                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Autoriser toutes les origines pour l'API mobile publique
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false); // Doit être false avec allowedOriginPattern("*")
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
