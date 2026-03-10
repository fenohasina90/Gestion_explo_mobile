package com.explorateur.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Configuration
public class DatabaseInitializer {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Bean
    public CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            // Extraire le chemin du fichier de la datasource URL
            String dbPath = datasourceUrl.replace("jdbc:sqlite:", "");
            File dbFile = new File(dbPath);

            // Vérifier si la base de données existe déjà
            if (!dbFile.exists() || dbFile.length() == 0) {
                System.out.println("🔧 Base de données non trouvée. Initialisation en cours...");
                
                // Créer le répertoire /data s'il n'existe pas
                File dataDir = dbFile.getParentFile();
                if (dataDir != null && !dataDir.exists()) {
                    dataDir.mkdirs();
                    System.out.println("✅ Répertoire créé : " + dataDir.getAbsolutePath());
                }

                try {
                    // Lire le script SQL
                    ClassPathResource resource = new ClassPathResource("sql/BD_sqlite.sql");
                    String sqlScript = new BufferedReader(
                        new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

                    // Séparer et exécuter les commandes SQL
                    String[] sqlStatements = sqlScript.split(";");
                    
                    for (String statement : sqlStatements) {
                        String trimmed = statement.trim();
                        if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                            try {
                                jdbcTemplate.execute(trimmed);
                            } catch (Exception e) {
                                System.err.println("⚠️  Erreur lors de l'exécution : " + trimmed);
                                System.err.println("   Message : " + e.getMessage());
                            }
                        }
                    }

                    System.out.println("✅ Base de données initialisée avec succès !");
                    System.out.println("📍 Emplacement : " + dbFile.getAbsolutePath());
                    
                    // Vérifier les tables créées
                    Integer tableCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM sqlite_master WHERE type='table'", 
                        Integer.class
                    );
                    System.out.println("📊 Nombre de tables créées : " + tableCount);

                } catch (Exception e) {
                    System.err.println("❌ Erreur lors de l'initialisation de la base de données : " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("✅ Base de données existante trouvée : " + dbFile.getAbsolutePath());
                
                // Afficher quelques stats
                try {
                    Integer tableCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM sqlite_master WHERE type='table'", 
                        Integer.class
                    );
                    System.out.println("📊 Tables dans la base : " + tableCount);
                } catch (Exception e) {
                    System.err.println("⚠️  Impossible de lire les informations de la base");
                }
            }
        };
    }
}
