package com.explorateur.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration Swagger/OpenAPI pour la documentation de l'API
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI explorateurOpenAPI() {
        // Serveur local
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Serveur de développement local");

        // Informations de contact
        Contact contact = new Contact();
        contact.setName("Club des Explorateurs");
        contact.setEmail("contact@explorateurs.com");

        // Licence
        License license = new License()
                .name("Propriétaire")
                .url("https://explorateurs.com/license");

        // Informations générales de l'API
        Info info = new Info()
                .title("API Club des Explorateurs")
                .version("1.0.0")
                .description("API REST pour la gestion du Club des Explorateurs (type scout de l'Église Adventiste). " +
                        "Cette API permet de gérer les utilisateurs, les enfants, les inscriptions, les activités, " +
                        "les budgets, les programmes et les classes progressives.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
