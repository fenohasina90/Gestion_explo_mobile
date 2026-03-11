package com.explorateur.backend.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Convertisseur JPA pour stocker LocalDateTime au format TEXT dans SQLite
 * Format: yyyy-MM-dd HH:mm:ss
 * 
 * ⚠️ DÉSACTIVÉ pour PostgreSQL (autoApply = false)
 * PostgreSQL gère nativement les types TIMESTAMP, pas besoin de conversion String
 */
@Converter(autoApply = false)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String convertToDatabaseColumn(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(FORMATTER);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        // Gérer les formats possibles (timestamp en millisecondes ou datetime string)
        try {
            // Essayer de parser comme un nombre (timestamp en millisecondes)
            long timestamp = Long.parseLong(dateString);
            return new Timestamp(timestamp).toLocalDateTime();
        } catch (NumberFormatException e) {
            // Si ce n'est pas un nombre, parser comme une date formatée
            return LocalDateTime.parse(dateString, FORMATTER);
        }
    }
}
