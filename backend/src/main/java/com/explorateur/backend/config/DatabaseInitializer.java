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

    // ⚠️ DatabaseInitializer désactivé pour PostgreSQL/Supabase
    // La base de données est initialisée manuellement via BD_postgres.sql dans Supabase
    // Décommentez seulement si vous revenez à SQLite
    
    // @Bean
    public CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            // Extraire le chemin du fichier de la datasource URL
            String dbPath = datasourceUrl.replace("jdbc:sqlite:", "");
            File dbFile = new File(dbPath);

            // Créer le répertoire parent s'il n'existe pas
            File dataDir = dbFile.getParentFile();
            if (dataDir != null && !dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                if (created) {
                    System.out.println("✅ Répertoire créé : " + dataDir.getAbsolutePath());
                } else {
                    System.err.println("❌ Impossible de créer le répertoire : " + dataDir.getAbsolutePath());
                    System.err.println("   Vérifiez les permissions ou utilisez un autre chemin");
                    return;
                }
            }

            // Vérifier si la base de données existe déjà
            if (!dbFile.exists() || dbFile.length() == 0) {
                System.out.println("🔧 Base de données non trouvée. Initialisation en cours...");

                try {
                    // Lire le script SQL
                    ClassPathResource resource = new ClassPathResource("sql/BD_sqlite.sql");
                    String sqlScript = new BufferedReader(
                        new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                        .lines()
                        // Filtrer les commentaires et lignes vides AVANT de joindre
                        .filter(line -> {
                            String trimmed = line.trim();
                            return !trimmed.isEmpty() && !trimmed.startsWith("--");
                        })
                        .collect(Collectors.joining("\n"));

                    // Séparer et exécuter les commandes SQL
                    String[] sqlStatements = sqlScript.split(";");
                    
                    int successCount = 0;
                    int errorCount = 0;
                    
                    for (String statement : sqlStatements) {
                        String trimmed = statement.trim();
                        if (!trimmed.isEmpty()) {
                            try {
                                jdbcTemplate.execute(trimmed);
                                successCount++;
                                
                                // Logger les CREATE TABLE pour debug
                                if (trimmed.toUpperCase().startsWith("CREATE TABLE")) {
                                    String tableName = trimmed.substring(13).split("\\s+|\\(")[0].trim();
                                    System.out.println("  ✓ Table créée: " + tableName);
                                }
                            } catch (Exception e) {
                                errorCount++;
                                String preview = trimmed.length() > 80 ? trimmed.substring(0, 80) + "..." : trimmed;
                                System.err.println("⚠️  Erreur #" + errorCount + ": " + preview);
                                System.err.println("   " + e.getMessage());
                                
                                // Arrêter si trop d'erreurs critiques
                                if (errorCount > 10) {
                                    System.err.println("❌ Trop d'erreurs, arrêt de l'initialisation");
                                    throw new RuntimeException("Initialisation échouée: " + errorCount + " erreurs");
                                }
                            }
                        }
                    }
                    
                    System.out.println("📈 Statistiques: " + successCount + " commandes réussies, " + errorCount + " erreurs");

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
