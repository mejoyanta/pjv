package com.tax.vat.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.tax.vat.dto.projection.CompanyTableProjection;
import com.tax.vat.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    private final CompanyRepository companyRepository;

    public PdfService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public byte[] generateCompanyListPdf(String search) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 20, 20, 30, 30);
            PdfWriter.getInstance(document, out);
            document.open();

            // Header Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(41, 128, 185));
            Paragraph title = new Paragraph("Company Information Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            // Table with 7 columns
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.0f, 3.5f, 2.5f, 2.5f, 3.5f, 2.5f, 2.5f});
            table.setSpacingBefore(10);

            // Table Header
            String[] headers = {"#", "Company Name", "Company ID", "BIN", "Email", "Category", "Expire Date"};
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Color headerBg = new Color(52, 73, 94);

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(headerBg);
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Stream rows in chunks of 200 (memory safe for large dataset)
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
            int page = 0;
            int pageSize = 200;
            int serial = 1;
            boolean hasMore = true;

            String searchTerm = (search != null) ? search.trim() : "";
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            while (hasMore) {
                Page<CompanyTableProjection> pagedData = searchTerm.isEmpty()
                        ? companyRepository.findActiveCompanies(PageRequest.of(page, pageSize))
                        : companyRepository.searchActiveCompanies(searchTerm, PageRequest.of(page, pageSize));

                for (CompanyTableProjection c : pagedData.getContent()) {
                    Color rowBg = (serial % 2 == 0) ? new Color(245, 247, 250) : Color.WHITE;

                    addCell(table, String.valueOf(serial++), dataFont, rowBg, Element.ALIGN_CENTER);
                    addCell(table, c.getName() != null ? c.getName() : "-", dataFont, rowBg, Element.ALIGN_LEFT);
                    addCell(table, c.getUsername() != null ? c.getUsername() : "-", dataFont, rowBg, Element.ALIGN_CENTER);
                    addCell(table, c.getBin() != null ? c.getBin() : "-", dataFont, rowBg, Element.ALIGN_CENTER);
                    addCell(table, c.getEmail() != null ? c.getEmail() : "-", dataFont, rowBg, Element.ALIGN_LEFT);
                    addCell(table, c.getCategoryName() != null ? c.getCategoryName() : "-", dataFont, rowBg, Element.ALIGN_CENTER);

                    Object expDate = c.getSubscriptionExpireDate();
                    String expDateStr = "-";
                    if (expDate instanceof java.time.LocalDate ld) {
                        expDateStr = ld.format(dtf);
                    } else if (expDate != null) {
                        expDateStr = String.valueOf(expDate);
                    }
                    addCell(table, expDateStr, dataFont, rowBg, Element.ALIGN_CENTER);
                }

                hasMore = pagedData.hasNext();
                page++;
                if (serial > 5000) {
                    // Safety limit for single PDF export
                    break;
                }
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    private void addCell(PdfPTable table, String text, Font font, Color bg, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(5);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }
}
