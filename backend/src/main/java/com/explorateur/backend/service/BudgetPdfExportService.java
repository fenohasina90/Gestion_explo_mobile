package com.explorateur.backend.service;

import com.explorateur.backend.dto.ExportBudgetPdfRequest;
import com.explorateur.backend.entity.*;
import com.explorateur.backend.repository.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour l'export PDF du budget
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetPdfExportService {

    private final BudgetGlobalRepository budgetGlobalRepository;
    private final ActiviteRepository activiteRepository;
    private final DetailActiviteRepository detailActiviteRepository;
    private final AnneeExerciceRepository anneeExerciceRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    // Couleurs sombres
    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(139, 0, 0); // Rouge sombre
    private static final DeviceRgb SECONDARY_COLOR = new DeviceRgb(25, 25, 112); // Bleu nuit
    private static final DeviceRgb HEADER_BG_COLOR = new DeviceRgb(47, 79, 79); // Gris ardoise sombre
    private static final DeviceRgb TABLE_HEADER_COLOR = new DeviceRgb(33, 33, 33); // Presque noir
    private static final DeviceRgb BORDER_COLOR = new DeviceRgb(80, 80, 80); // Gris sombre
    private static final DeviceRgb LIGHT_ROW_COLOR = new DeviceRgb(245, 245, 245); // Blanc cassé
    private static final DeviceRgb TEXT_DARK = new DeviceRgb(33, 33, 33); // Texte sombre

    /**
     * Générer le PDF du budget selon les colonnes sélectionnées
     */
    public byte[] generateBudgetPdf(ExportBudgetPdfRequest request) throws Exception {
        log.info("Génération du PDF pour l'année d'exercice ID: {}", request.getAnneeExerciceId());
        
        // Récupérer l'année d'exercice
        AnneeExercice anneeExercice = anneeExerciceRepository.findById(request.getAnneeExerciceId())
            .orElseThrow(() -> new RuntimeException("Année d'exercice introuvable"));
        
        // Récupérer le budget global
        BudgetGlobal budget = budgetGlobalRepository.findByAnneeExerciceId(request.getAnneeExerciceId())
            .orElseThrow(() -> new RuntimeException("Budget introuvable"));
        
        // Récupérer les activités
        List<Activite> activites = activiteRepository.findByBudgetGlobalId(budget.getId());
        
        // Créer le PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.setMargins(40, 40, 60, 40);
        
        // Polices
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        
        // ==================== EN-TÊTE AVEC LOGO ====================
        addHeader(document, boldFont, regularFont, anneeExercice);
        
        // ==================== LIGNE DE SÉPARATION ====================
        addSeparator(document);
        
        // ==================== STATUT DU BUDGET ====================
        addBudgetStatus(document, regularFont, budget);
        
        // ==================== TABLEAU DES ACTIVITÉS ====================
        addActivitiesTable(document, boldFont, regularFont, activites, request, budget);
        
        // ==================== PIED DE PAGE ====================
        addFooter(document, regularFont);
        
        // Fermer le document
        document.close();
        
        return baos.toByteArray();
    }
    
    /**
     * Ajouter l'en-tête avec le logo
     */
    private void addHeader(Document document, PdfFont boldFont, PdfFont regularFont, AnneeExercice anneeExercice) throws Exception {
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 4}));
        headerTable.setWidth(UnitValue.createPercentValue(100));
        headerTable.setMarginBottom(5);
        
        // Logo à gauche
        try {
            ClassPathResource logoResource = new ClassPathResource("images/logo.png");
            Image logo = new Image(ImageDataFactory.create(logoResource.getContentAsByteArray()));
            logo.setWidth(50);
            logo.setHeight(50);
            
            Cell logoCell = new Cell()
                .add(logo)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setTextAlignment(TextAlignment.CENTER);
            headerTable.addCell(logoCell);
        } catch (Exception e) {
            log.warn("Logo non trouvé, utilisation du symbole à la place");
            Cell logoCell = new Cell()
                .add(new Paragraph("⚜")
                    .setFont(boldFont)
                    .setFontSize(40)
                    .setFontColor(SECONDARY_COLOR)
                    .setTextAlignment(TextAlignment.CENTER))
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
            headerTable.addCell(logoCell);
        }
        
        // Titre à droite
        Cell titleCell = new Cell()
            .add(new Paragraph("BUDGET PROGRAMME EXPLORATEUR " + anneeExercice.getAnnee().getYear())
                .setFont(boldFont)
                .setFontSize(18)
                .setFontColor(SECONDARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold())
            .setBorder(Border.NO_BORDER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE);
        headerTable.addCell(titleCell);
        
        document.add(headerTable);
    }
    
    /**
     * Ajouter une ligne de séparation
     */
    private void addSeparator(Document document) {
        Table separator = new Table(1);
        separator.setWidth(UnitValue.createPercentValue(100));
        separator.addCell(new Cell()
            .add(new Paragraph(""))
            .setHeight(1)
            .setBackgroundColor(SECONDARY_COLOR)
            .setBorder(Border.NO_BORDER));
        separator.setMarginBottom(20);
        document.add(separator);
    }
    
    /**
     * Ajouter le statut du budget
     */
    private void addBudgetStatus(Document document, PdfFont regularFont, BudgetGlobal budget) {
        String statutText = "";
        if (budget.getStatus() != null && "Créé".equals(budget.getStatus().getNom())) {
            statutText = "En attente de validation par le comité de l'Église";
        } else if (budget.getStatus() != null) {
            statutText = "Validé par le comité de l'Église";
        }
        
        if (!statutText.isEmpty()) {
            Paragraph statusPara = new Paragraph(statutText)
                .setFont(regularFont)
                .setFontSize(11)
                .setFontColor(SECONDARY_COLOR)
                .setItalic()
                .setMarginBottom(20)
                .setTextAlignment(TextAlignment.CENTER);
            document.add(statusPara);
        }
    }
    
    /**
     * Ajouter le tableau des activités
     */
    private void addActivitiesTable(Document document, PdfFont boldFont, PdfFont regularFont,
                                    List<Activite> activites, ExportBudgetPdfRequest request,
                                    BudgetGlobal budget) {
        // Déterminer les colonnes
        List<String> headers = new ArrayList<>();
        if (Boolean.TRUE.equals(request.getIncludeDate())) headers.add("Date");
        if (Boolean.TRUE.equals(request.getIncludeNomActivite())) headers.add("Activité");
        if (Boolean.TRUE.equals(request.getIncludeDescriptionActivite())) headers.add("Description");
        if (Boolean.TRUE.equals(request.getIncludeDetailsActivite())) headers.add("Détails");
        if (Boolean.TRUE.equals(request.getIncludeStatutActivite())) headers.add("Statut");
        if (Boolean.TRUE.equals(request.getIncludeCoutActivite())) headers.add("Montant (Ar)");
        
        if (headers.isEmpty()) {
            headers.add("Activité");
            headers.add("Montant (Ar)");
        }
        
        // Créer le tableau
        float[] columnWidths = new float[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).equals("Détails")) {
                columnWidths[i] = 2.5f;
            } else if (headers.get(i).equals("Activité")) {
                columnWidths[i] = 1.8f;
            } else if (headers.get(i).equals("Description")) {
                columnWidths[i] = 1.5f;
            } else {
                columnWidths[i] = 1f;
            }
        }
        
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));
        
        // En-têtes sombres
        for (String header : headers) {
            Cell cell = new Cell()
                .add(new Paragraph(header)
                    .setFont(boldFont)
                    .setFontSize(10))
                .setBackgroundColor(SECONDARY_COLOR)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(10)
                .setBorder(new SolidBorder(BORDER_COLOR, 1));
            table.addHeaderCell(cell);
        }
        
        // Données des activités - une activité par ligne avec détails imbriqués
        for (Activite activite : activites) {
            List<DetailActivite> details = detailActiviteRepository.findByActiviteId(activite.getId());
            addActivityRow(table, activite, details, request, headers, regularFont, boldFont);
        }
        
        document.add(table);
        
        // Montant total en bas à droite
        addTotalAmount(document, boldFont, budget);
    }
    
    /**
     * Ajouter une ligne d'activité avec ses détails
     */
    private void addActivityRow(Table table, Activite activite, List<DetailActivite> details,
                                ExportBudgetPdfRequest request, List<String> headers,
                                PdfFont regularFont, PdfFont boldFont) {
        for (String header : headers) {
            Cell cell = new Cell()
                .setFont(regularFont)
                .setFontSize(9)
                .setPadding(8)
                .setBorder(new SolidBorder(BORDER_COLOR, 0.5f))
                .setVerticalAlignment(VerticalAlignment.TOP);
            
            switch (header) {
                case "Date":
                    if (activite.getDateDebut() != null && activite.getDateFin() != null) {
                        // Les deux dates sont présentes : "12/02/2026 - 15/02/2026"
                        cell.add(new Paragraph(
                            activite.getDateDebut().format(DATE_FORMATTER) + " - " + 
                            activite.getDateFin().format(DATE_FORMATTER))
                            .setFontSize(8));
                    } else if (activite.getDateDebut() != null) {
                        // Seule la date début : "12/02/2026"
                        cell.add(new Paragraph(activite.getDateDebut().format(DATE_FORMATTER))
                            .setFontSize(8));
                    } else {
                        // Aucune date : "-"
                        cell.add(new Paragraph("-").setFontSize(8));
                    }
                    break;
                    
                case "Activité":
                    cell.add(new Paragraph(activite.getNom())
                        .setFont(boldFont)
                        .setFontSize(10));
                    break;
                    
                case "Description":
                    if (activite.getDescription() != null && !activite.getDescription().isEmpty()) {
                        cell.add(new Paragraph(activite.getDescription())
                            .setFontSize(8));
                    } else {
                        cell.add(new Paragraph("-").setItalic());
                    }
                    break;
                    
                case "Détails":
                    if (Boolean.TRUE.equals(request.getIncludeDetailsActivite()) && !details.isEmpty()) {
                        for (int i = 0; i < details.size(); i++) {
                            DetailActivite detail = details.get(i);
                            Paragraph detailPara = new Paragraph()
                                .add("• " + detail.getDetails() + " : ")
                                .add(new Paragraph(formatMontantMillier(detail.getMontant()) + " Ar")
                                    .setFont(boldFont))
                                .setFontSize(8)
                                .setMarginBottom(i < details.size() - 1 ? 4 : 0);
                            cell.add(detailPara);
                        }
                    } else {
                        cell.add(new Paragraph("-").setItalic());
                    }
                    break;
                    
                case "Statut":
                    String statut = activite.getStatus() != null ? activite.getStatus().getStatus() : "N/A";
                    cell.add(new Paragraph(statut)
                        .setFont(boldFont)
                        .setFontSize(8)
                        .setTextAlignment(TextAlignment.CENTER));
                    break;
                    
                case "Montant (Ar)":
                    cell.add(new Paragraph(formatMontantMillier(activite.getMontant()))
                        .setFont(boldFont)
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.RIGHT));
                    break;
                    
                default:
                    cell.add(new Paragraph("-"));
            }
            
            table.addCell(cell);
        }
    }
    
    /**
     * Ajouter le montant total avec le statut
     */
    private void addTotalAmount(Document document, PdfFont boldFont, BudgetGlobal budget) {
        // Créer un tableau avec deux colonnes : statut à gauche, montant à droite
        Table totalTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        totalTable.setWidth(UnitValue.createPercentValue(100));
        totalTable.setMarginTop(5);
        
        
        // Cellule droite : Montant total
        Cell montantCell = new Cell()
            .add(new Paragraph("MONTANT TOTAL : " + formatMontantMillier(budget.getMontant()) + " Ar")
                .setFont(boldFont)
                .setFontSize(14)
                // .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.RIGHT))
            .setBorder(Border.NO_BORDER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .setPaddingRight(10);
        
        // totalTable.addCell(statutCell);
        totalTable.addCell(montantCell);
        
        document.add(totalTable);
    }
    
    /**
     * Ajouter le pied de page avec date et heure
     */
    private void addFooter(Document document, PdfFont regularFont) {
        // Ligne de séparation fine
        Table separator = new Table(1);
        separator.setWidth(UnitValue.createPercentValue(100));
        separator.addCell(new Cell()
            .add(new Paragraph(""))
            .setHeight(1)
            .setBackgroundColor(SECONDARY_COLOR)
            .setBorder(Border.NO_BORDER));
        separator.setMarginTop(30);
        separator.setMarginBottom(10);
        document.add(separator);
        
        // Date et heure de génération
        Paragraph footer = new Paragraph("Document généré le " + LocalDateTime.now().format(DATETIME_FORMATTER))
            .setFont(regularFont)
            .setFontSize(8)
            .setFontColor(ColorConstants.DARK_GRAY)
            .setTextAlignment(TextAlignment.CENTER);
        
        document.add(footer);
    }
    
    /**
     * Formater un montant avec séparateur de milliers
     */
    private String formatMontantMillier(Double montant) {
        if (montant == null) return "0";
        return String.format("%,.0f", montant).replace(",", " ");
    }
}
