package com.explorateur.backend.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Convertisseur pour gérer les dates SQLite avec JPA
 * SQLite stocke les dates comme du texte, ce convertisseur fait le pont entre LocalDate et String
 * 
 * ⚠️ DÉSACTIVÉ pour PostgreSQL (autoApply = false)
 * PostgreSQL gère nativement les types DATE, pas besoin de conversion String
 */
@Converter(autoApply = false)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public String convertToDatabaseColumn(LocalDate locDate) {
        return (locDate == null ? null : locDate.format(FORMATTER));
    }

    @Override
    public LocalDate convertToEntityAttribute(String sqlDate) {
        return (sqlDate == null || sqlDate.isEmpty() ? null : LocalDate.parse(sqlDate, FORMATTER));
    }
}
