package com.explorateur.backend.service;

import com.explorateur.backend.dto.InscriptionResponse;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour générer des exports PDF
 */
@Service
@Slf4j
public class PdfExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm");
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(10, 40, 110); // Couleur verte du logo
    private static final DeviceRgb ALTERNATE_ROW_COLOR = new DeviceRgb(245, 245, 245); // Gris très clair
    private static final DeviceRgb BORDER_COLOR = new DeviceRgb(200, 200, 200); // Gris pour les bordures

    /**
     * Génère un PDF des enfants inscrits
     *
     * @param inscriptions Liste des inscriptions à exporter
     * @param annee Année d'exercice
     * @return Contenu du PDF en bytes
     */
    public byte[] generateEnfantsPdf(List<InscriptionResponse> inscriptions, String annee) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc, PageSize.A4)) {
            
            // Ajouter les métadonnées du PDF
            PdfDocumentInfo info = pdfDoc.getDocumentInfo();
            info.setTitle("Liste Explorateur " + annee);
            info.setAuthor("Explorateur Mahazo");
            info.setSubject("Liste des enfants inscrits");
            info.setCreator("Système de Gestion Explorateur");
            
            document.setMargins(30, 40, 30, 40);
            
            // Ajouter l'en-tête avec logo
            addHeader(document, annee);
            
            // Ajouter une ligne de séparation décorative
            addSeparatorLine(document);
            
            // Ajouter un espace
            document.add(new Paragraph("\n"));
            
            // Ajouter le tableau des enfants
            addEnfantsTable(document, inscriptions);
            
            // Ajouter le pied de page avec le nombre total et la date de génération
            addFooter(document, inscriptions.size());
            
            log.info("PDF généré avec succès pour {} enfants", inscriptions.size());
        }
        
        return baos.toByteArray();
    }

    /**
     * Ajoute l'en-tête du document avec logo et titre
     */
    private void addHeader(Document document, String annee) throws IOException {
        // Créer une table pour l'en-tête (logo à gauche, titre au centre-droit)
        Table headerTable = new Table(new float[]{1.5f, 4f});
        headerTable.setWidth(UnitValue.createPercentValue(100));
        headerTable.setBorder(Border.NO_BORDER);
        headerTable.setMarginBottom(10);
        
        // Ajouter le logo
        try {
            ClassPathResource logoResource = new ClassPathResource("static/logo.png");
            Image logo = new Image(ImageDataFactory.create(logoResource.getURL()));
            logo.setWidth(50);
            logo.setHeight(50);
            logo.setHorizontalAlignment(HorizontalAlignment.LEFT);
            
            Cell logoCell = new Cell();
            logoCell.add(logo);
            logoCell.setBorder(Border.NO_BORDER);
            logoCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            logoCell.setPaddingRight(15);
            headerTable.addCell(logoCell);
        } catch (Exception e) {
            log.warn("Impossible de charger le logo: {}", e.getMessage());
            // Ajouter une cellule vide si le logo n'est pas disponible
            Cell emptyCell = new Cell();
            emptyCell.setBorder(Border.NO_BORDER);
            headerTable.addCell(emptyCell);
        }
        
        // Bloc titre avec organisation et année
        Cell titleCell = new Cell();
        titleCell.setBorder(Border.NO_BORDER);
        titleCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        
        // Titre principal
        Paragraph title = new Paragraph("EXPLORATEUR MAHAZO " + annee)
                .setFontSize(20)
                .setBold()
                .setFontColor(new DeviceRgb(33, 37, 41)) // Couleur sombre
                .setMarginBottom(5);
        
        // Sous-titre
        // Paragraph subtitle = new Paragraph("Liste des Enfants Inscrits")
        //         .setFontSize(14)
        //         .setFontColor(new DeviceRgb(108, 117, 125)) // Gris moyen
        //         .setItalic();
        
        titleCell.add(title);
        // titleCell.add(subtitle);
        headerTable.addCell(titleCell);
        
        document.add(headerTable);
    }
    
    /**
     * Ajoute une ligne de séparation décorative
     */
    private void addSeparatorLine(Document document) {
        // Créer une ligne avec un paragraphe vide et une bordure inférieure
        Paragraph separator = new Paragraph();
        separator.setBorderBottom(new SolidBorder(HEADER_COLOR, 2));
        separator.setMarginTop(1);
        separator.setMarginBottom(3);
        document.add(separator);
    }

    /**
     * Ajoute le tableau des enfants
     */
    private void addEnfantsTable(Document document, List<InscriptionResponse> inscriptions) {
        // Trier les inscriptions par ordre de classe (âge croissant)
        List<InscriptionResponse> sortedInscriptions = inscriptions.stream()
                .sorted(Comparator.comparingInt(this::getClasseOrder)
                        .thenComparing(InscriptionResponse::getEnfantNom)
                        .thenComparing(InscriptionResponse::getEnfantPrenom))
                .collect(Collectors.toList());
        
        // Créer le tableau avec 4 colonnes : N°, Nom et Prénom, Classe, Date de naissance
        Table table = new Table(new float[]{0.8f, 3.5f, 2f, 2f});
        table.setWidth(UnitValue.createPercentValue(100));
        // table.setMarginTop(5);
        
        // En-têtes de colonnes
        addTableHeader(table, "N°");
        addTableHeader(table, "Nom et Prénom");
        addTableHeader(table, "Classe");
        addTableHeader(table, "Date de naissance");
        
        // Ajouter les données avec alternance de couleurs et séparation par classe
        int numero = 1;
        String currentClasse = null;
        
        for (InscriptionResponse inscription : sortedInscriptions) {
            // Vérifier si on change de classe pour ajouter une ligne de séparation visuelle
            if (currentClasse != null && !currentClasse.equals(inscription.getClasseNom())) {
                // Ajouter une ligne légèrement plus foncée pour marquer la séparation
                addClasseSeparator(table, currentClasse, inscription.getClasseNom());
            }
            currentClasse = inscription.getClasseNom();
            
            boolean isEvenRow = (numero % 2 == 0);
            
            // Numéro
            addTableCell(table, String.valueOf(numero), true, isEvenRow);
            
            // Nom et prénom
            String nomComplet = inscription.getEnfantNom() + " " + inscription.getEnfantPrenom();
            addTableCell(table, nomComplet, false, isEvenRow);
            
            // Classe
            addTableCell(table, inscription.getClasseNom(), false, isEvenRow);
            
            // Date de naissance
            String dateNaissance = inscription.getEnfantDateNaissance() != null 
                    ? inscription.getEnfantDateNaissance().format(DATE_FORMATTER) 
                    : "-";
            addTableCell(table, dateNaissance, true, isEvenRow);
            
            numero++;
        }
        
        document.add(table);
    }
    
    /**
     * Retourne l'ordre de tri pour une classe donnée
     * Ami (10 ans) < Compagnon (11 ans) < Eclaireur (12 ans) < Pionnier (13 ans) < Voyageur (14 ans) < Guide (15 ans)
     */
    private int getClasseOrder(InscriptionResponse inscription) {
        String classeNom = inscription.getClasseNom().toLowerCase();
        
        if (classeNom.contains("ami")) return 1;
        if (classeNom.contains("compagnon")) return 2;
        if (classeNom.contains("eclaireur") || classeNom.contains("éclaireur")) return 3;
        if (classeNom.contains("pionnier")) return 4;
        if (classeNom.contains("voyageur")) return 5;
        if (classeNom.contains("guide")) return 6;
        
        // Classe inconnue, mettre à la fin
        return 999;
    }
    
    /**
     * Ajoute une ligne de séparation entre deux classes différentes
     */
    private void addClasseSeparator(Table table, String previousClasse, String newClasse) {
        DeviceRgb separatorColor = new DeviceRgb(220, 220, 220);
        
        // Ligne vide avec fond gris pour séparer visuellement les classes
        for (int i = 0; i < 4; i++) {
            Cell cell = new Cell();
            cell.add(new Paragraph(" ").setFontSize(3));
            cell.setBackgroundColor(separatorColor);
            cell.setBorder(Border.NO_BORDER);
            cell.setPadding(2);
            table.addCell(cell);
        }
    }

    /**
     * Ajoute une cellule d'en-tête au tableau
     */
    private void addTableHeader(Table table, String text) {
        Cell cell = new Cell();
        Paragraph p = new Paragraph(text)
                .setBold()
                .setFontSize(11)
                .setFontColor(ColorConstants.WHITE);
        cell.add(p);
        cell.setBackgroundColor(HEADER_COLOR);
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cell.setPadding(8);
        cell.setBorder(new SolidBorder(BORDER_COLOR, 1));
        table.addHeaderCell(cell);
    }

    /**
     * Ajoute une cellule de données au tableau
     */
    private void addTableCell(Table table, String text, boolean centered, boolean isEvenRow) {
        Cell cell = new Cell();
        Paragraph p = new Paragraph(text).setFontSize(10);
        
        if (centered) {
            p.setTextAlignment(TextAlignment.CENTER);
            cell.setTextAlignment(TextAlignment.CENTER);
        }
        
        cell.add(p);
        cell.setPadding(6);
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cell.setBorder(new SolidBorder(BORDER_COLOR, 0.5f));
        
        // Alternance de couleurs pour les lignes
        if (isEvenRow) {
            cell.setBackgroundColor(ALTERNATE_ROW_COLOR);
        }
        
        table.addCell(cell);
    }

    /**
     * Ajoute le pied de page avec le nombre total et la date de génération
     */
    private void addFooter(Document document, int total) {
        document.add(new Paragraph("\n"));
        
        // Créer une table pour le footer (total à gauche, date à droite)
        Table footerTable = new Table(new float[]{1, 1});
        footerTable.setWidth(UnitValue.createPercentValue(100));
        footerTable.setBorder(Border.NO_BORDER);
        footerTable.setMarginTop(5);
        
        // Cellule gauche - Total
        Cell totalCell = new Cell();
        totalCell.setBorder(Border.NO_BORDER);
        // Paragraph totalPara = new Paragraph("Total : " + total + " enfant(s) inscrit(s)")
        //         .setFontSize(12)
        //         .setBold()
        //         .setFontColor(HEADER_COLOR);
        // totalCell.add(totalPara);
        totalCell.setTextAlignment(TextAlignment.LEFT);
        footerTable.addCell(totalCell);
        
        // Cellule droite - Date de génération
        Cell dateCell = new Cell();
        dateCell.setBorder(Border.NO_BORDER);
        String dateGeneration = LocalDateTime.now().format(DATETIME_FORMATTER);
        Paragraph datePara = new Paragraph("Document généré le " + dateGeneration)
                .setFontSize(9)
                .setItalic()
                .setFontColor(new DeviceRgb(108, 117, 125));
        dateCell.add(datePara);
        dateCell.setTextAlignment(TextAlignment.RIGHT);
        dateCell.setVerticalAlignment(VerticalAlignment.BOTTOM);
        footerTable.addCell(dateCell);
        
        document.add(footerTable);
    }
}
