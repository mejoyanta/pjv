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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    private final CompanyRepository companyRepository;

    public PdfService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public static class BarcodeItem {
        public String code;
        public String name;
        public String price;

        public BarcodeItem() {}
        public BarcodeItem(String code, String name, String price) {
            this.code = code;
            this.name = name;
            this.price = price;
        }
    }

    public byte[] generateTablePdf(String title, String[] headers, float[] relativeWidths, List<String[]> rows) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            boolean isLandscape = headers.length > 5;
            Document document = new Document(isLandscape ? PageSize.A4.rotate() : PageSize.A4, 20, 20, 30, 30);
            PdfWriter.getInstance(document, out);
            document.open();

            // Header Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, new Color(41, 128, 185));
            Paragraph titlePara = new Paragraph(title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(4);
            document.add(titlePara);

            // Subtitle with timestamp
            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
            Paragraph subPara = new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), subFont);
            subPara.setAlignment(Element.ALIGN_CENTER);
            subPara.setSpacingAfter(12);
            document.add(subPara);

            // Table
            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            if (relativeWidths != null && relativeWidths.length == headers.length) {
                table.setWidths(relativeWidths);
            }

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Color headerBg = new Color(52, 73, 94);

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(headerBg);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
            }

            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
            int rowIdx = 0;
            for (String[] row : rows) {
                Color rowBg = (rowIdx % 2 == 0) ? new Color(248, 249, 250) : Color.WHITE;
                for (int c = 0; c < row.length; c++) {
                    String val = row[c] != null ? row[c] : "-";
                    int align = (c == 0) ? Element.ALIGN_CENTER : Element.ALIGN_LEFT;
                    addCell(table, val, dataFont, rowBg, align);
                }
                rowIdx++;
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating table PDF: " + e.getMessage(), e);
        }
    }

    public byte[] generateBarcodeSheetPdf(List<BarcodeItem> items) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 20, 20, 25, 25);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, new Color(41, 128, 185));
            Paragraph p = new Paragraph("Product Barcode Labels Sheet", titleFont);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingAfter(4);
            document.add(p);

            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
            Paragraph sub = new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), subFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(15);
            document.add(sub);

            PdfPTable table = new PdfPTable(3); // 3 labels per row
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3.3f, 3.3f, 3.3f});

            PdfContentByte cb = writer.getDirectContent();
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);
            Font priceFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Color.DARK_GRAY);

            for (BarcodeItem item : items) {
                PdfPCell cell = new PdfPCell();
                cell.setPadding(8);
                cell.setBorderColor(new Color(220, 224, 230));
                cell.setBorderWidth(1);

                String name = (item.name != null && item.name.length() > 25) ? item.name.substring(0, 25) + "..." : (item.name != null ? item.name : "Product");
                Paragraph namePara = new Paragraph(name, labelFont);
                namePara.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(namePara);

                String codeStr = (item.code != null && !item.code.isEmpty()) ? item.code : "PROD-001";
                try {
                    Barcode128 code128 = new Barcode128();
                    code128.setCode(codeStr);
                    code128.setCodeType(Barcode128.CODE128);
                    code128.setBarHeight(24f);
                    Image barcodeImage = code128.createImageWithBarcode(cb, null, null);
                    barcodeImage.setAlignment(Element.ALIGN_CENTER);
                    barcodeImage.scalePercent(95);
                    cell.addElement(barcodeImage);
                } catch (Exception ex) {
                    Paragraph err = new Paragraph("[" + codeStr + "]", labelFont);
                    err.setAlignment(Element.ALIGN_CENTER);
                    cell.addElement(err);
                }

                if (item.price != null && !item.price.isEmpty()) {
                    Paragraph pricePara = new Paragraph("Price: " + item.price, priceFont);
                    pricePara.setAlignment(Element.ALIGN_CENTER);
                    cell.addElement(pricePara);
                }

                table.addCell(cell);
            }

            table.completeRow();
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating barcode sheet PDF: " + e.getMessage(), e);
        }
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

    public byte[] generateMusak43Pdf(com.tax.vat.entity.Musak43 item) {
        String title = "GOVERNMENT OF THE PEOPLE'S REPUBLIC OF BANGLADESH\nNATIONAL BOARD OF REVENUE\nMushak - 4.3 (Input-Output Coefficient)";
        String[] headers = {"Field", "Value"};
        float[] widths = {35f, 65f};
        List<String[]> rows = new java.util.ArrayList<>();
        rows.add(new String[]{"Company Name", item.getCompany() != null ? item.getCompany().getName() : "—"});
        rows.add(new String[]{"BIN", item.getCompany() != null ? item.getCompany().getBin() : "—"});
        rows.add(new String[]{"Submission Date", item.getSubmissionDate() != null ? item.getSubmissionDate().toString() : "—"});
        rows.add(new String[]{"Submission ID", item.getSubmissionId() != null ? item.getSubmissionId() : "—"});
        rows.add(new String[]{"Product / Goods Name", item.getProductServiceDetails() != null ? item.getProductServiceDetails() : (item.getProduct() != null ? item.getProduct().getName() : "—")});
        rows.add(new String[]{"HS Code", item.getHsCode() != null ? item.getHsCode() : (item.getProduct() != null ? item.getProduct().getHsCode() : "—")});
        rows.add(new String[]{"Purchase Quantity", String.format("%.2f", item.getPurchaseQuantity() != null ? item.getPurchaseQuantity() : 0.0)});
        rows.add(new String[]{"Base Price", String.format("%.2f", item.getBasePrice() != null ? item.getBasePrice() : 0.0)});
        rows.add(new String[]{"Additional Cost", String.format("%.2f", item.getTotalAdditionalCost() != null ? item.getTotalAdditionalCost() : 0.0)});
        rows.add(new String[]{"Profit", item.getProfit() != null ? item.getProfit() : "0.00"});
        rows.add(new String[]{"Sell Price", String.format("%.2f", item.getSellPrice() != null ? item.getSellPrice() : 0.0)});
        rows.add(new String[]{"Wholesale Rate", String.format("%.2f", item.getHdWholesaleRate() != null ? item.getHdWholesaleRate() : 0.0)});
        rows.add(new String[]{"Retailer Amount", String.format("%.2f", item.getHdRetailerAmount() != null ? item.getHdRetailerAmount() : 0.0)});
        rows.add(new String[]{"VAT Amount", String.format("%.2f", item.getVatAmount() != null ? item.getVatAmount() : 0.0)});
        rows.add(new String[]{"Total", item.getTotal() != null ? item.getTotal() : "0.00"});
        rows.add(new String[]{"Remarks", item.getAmendmentComment() != null ? item.getAmendmentComment() : "—"});
        return generateTablePdf(title, headers, widths, rows);
    }

    public byte[] generateMusak62Pdf(com.tax.vat.entity.Musak62 item) {
        String title = "GOVERNMENT OF THE PEOPLE'S REPUBLIC OF BANGLADESH\nNATIONAL BOARD OF REVENUE\nMushak - 6.2 (Purchase Register)";
        String[] headers = {"Field", "Value"};
        float[] widths = {35f, 65f};
        List<String[]> rows = new java.util.ArrayList<>();
        rows.add(new String[]{"Company Name", item.getCompany() != null ? item.getCompany().getName() : "—"});
        rows.add(new String[]{"BIN", item.getCompany() != null ? item.getCompany().getBin() : "—"});
        rows.add(new String[]{"Date", item.getSaleDate() != null ? item.getSaleDate().toString() : (item.getPurchaseDate() != null ? item.getPurchaseDate().toString() : "—")});
        rows.add(new String[]{"VI / Challan No", item.getMusak63() != null && item.getMusak63().getViNo() != null ? String.valueOf(item.getMusak63().getViNo()) : "—"});
        rows.add(new String[]{"Product Name", item.getProduct() != null ? item.getProduct().getName() : "—"});
        rows.add(new String[]{"Opening Stock", item.getOpeningStock() != null ? item.getOpeningStock() : "0.00"});
        rows.add(new String[]{"Closing Stock", String.format("%.2f", item.getClosingStock() != null ? item.getClosingStock() : 0.0)});
        rows.add(new String[]{"VAT Amount", String.format("%.2f", item.getMusak63() != null && item.getMusak63().getSale() != null && item.getMusak63().getSale().getVatAmount() != null ? item.getMusak63().getSale().getVatAmount() : 0.0)});
        rows.add(new String[]{"Total Price", String.format("%.2f", item.getMusak63() != null && item.getMusak63().getSale() != null && item.getMusak63().getSale().getTotalSaleAmount() != null ? item.getMusak63().getSale().getTotalSaleAmount() : 0.0)});
        return generateTablePdf(title, headers, widths, rows);
    }

    public byte[] generateMusak621Pdf(com.tax.vat.entity.Musak621 item) {
        String title = "GOVERNMENT OF THE PEOPLE'S REPUBLIC OF BANGLADESH\nNATIONAL BOARD OF REVENUE\nMushak - 6.2.1 (Purchase & Sales Register)";
        String[] headers = {"Field", "Value"};
        float[] widths = {35f, 65f};
        List<String[]> rows = new java.util.ArrayList<>();
        rows.add(new String[]{"Company Name", item.getCompany() != null ? item.getCompany().getName() : "—"});
        rows.add(new String[]{"BIN", item.getCompany() != null ? item.getCompany().getBin() : "—"});
        rows.add(new String[]{"Purchase Date", item.getPurchaseDate() != null ? item.getPurchaseDate().toString() : "—"});
        rows.add(new String[]{"Sale Date", item.getSaleDate() != null ? item.getSaleDate().toString() : "—"});
        rows.add(new String[]{"Bill of Entry", item.getPurchase() != null ? item.getPurchase().getBillOfEntry() : "—"});
        rows.add(new String[]{"VI No", item.getMusak63() != null && item.getMusak63().getViNo() != null ? String.valueOf(item.getMusak63().getViNo()) : "—"});
        rows.add(new String[]{"Buyer Name", item.getSale() != null && item.getSale().getBuyerName() != null ? item.getSale().getBuyerName() : "—"});
        rows.add(new String[]{"Product Name", item.getProduct() != null ? item.getProduct().getName() : "—"});
        rows.add(new String[]{"Opening Stock", String.format("%.2f", item.getOpeningStock() != null ? item.getOpeningStock() : 0.0)});
        rows.add(new String[]{"Closing Stock", String.format("%.2f", item.getClosingStock() != null ? item.getClosingStock() : 0.0)});
        rows.add(new String[]{"VAT Amount", String.format("%.2f", item.getSale() != null && item.getSale().getVatAmount() != null ? item.getSale().getVatAmount() : 0.0)});
        rows.add(new String[]{"Total Amount", String.format("%.2f", item.getSale() != null && item.getSale().getTotalSaleAmount() != null ? item.getSale().getTotalSaleAmount() : 0.0)});
        return generateTablePdf(title, headers, widths, rows);
    }

    public byte[] generateMusak63Pdf(com.tax.vat.entity.Musak63 item) {
        String title = "GOVERNMENT OF THE PEOPLE'S REPUBLIC OF BANGLADESH\nNATIONAL BOARD OF REVENUE\nMushak - 6.3 (Tax Invoice)";
        String[] headers = {"Field", "Value"};
        float[] widths = {35f, 65f};
        List<String[]> rows = new java.util.ArrayList<>();
        rows.add(new String[]{"Seller / Company Name", item.getCompany() != null ? item.getCompany().getName() : "—"});
        rows.add(new String[]{"Seller BIN", item.getCompany() != null ? item.getCompany().getBin() : "—"});
        rows.add(new String[]{"Invoice (VI) No", item.getViNo() != null ? String.valueOf(item.getViNo()) : "—"});
        rows.add(new String[]{"Issue Date", item.getDate() != null ? item.getDate().toString() : "—"});
        rows.add(new String[]{"Buyer Name", item.getSale() != null && item.getSale().getBuyerName() != null ? item.getSale().getBuyerName() : "—"});
        rows.add(new String[]{"Buyer Address", item.getSale() != null && item.getSale().getBuyerAddress() != null ? item.getSale().getBuyerAddress() : "—"});
        rows.add(new String[]{"Buyer BIN / NID", item.getSale() != null && item.getSale().getBuyerBinTinNid() != null ? item.getSale().getBuyerBinTinNid() : "—"});
        rows.add(new String[]{"Subtotal", String.format("%.2f", item.getSale() != null && item.getSale().getTotalSaleAmount() != null ? item.getSale().getTotalSaleAmount() : 0.0)});
        rows.add(new String[]{"VAT Amount", String.format("%.2f", item.getSale() != null && item.getSale().getVatAmount() != null ? item.getSale().getVatAmount() : 0.0)});
        rows.add(new String[]{"SD Amount", String.format("%.2f", item.getSale() != null && item.getSale().getSdAmount() != null ? item.getSale().getSdAmount() : 0.0)});
        rows.add(new String[]{"Total Amount", String.format("%.2f", item.getSale() != null && item.getSale().getTotalSaleAmount() != null ? item.getSale().getTotalSaleAmount() : 0.0)});
        return generateTablePdf(title, headers, widths, rows);
    }

    public byte[] generateMusak610Pdf(com.tax.vat.entity.Musak63 item) {
        String title = "GOVERNMENT OF THE PEOPLE'S REPUBLIC OF BANGLADESH\nNATIONAL BOARD OF REVENUE\nMushak - 6.10 (Transaction Value Exceeding 2 Lac BDT)";
        String[] headers = {"Field", "Value"};
        float[] widths = {35f, 65f};
        List<String[]> rows = new java.util.ArrayList<>();
        rows.add(new String[]{"Company Name", item.getCompany() != null ? item.getCompany().getName() : "—"});
        rows.add(new String[]{"Company BIN", item.getCompany() != null ? item.getCompany().getBin() : "—"});
        rows.add(new String[]{"Challan / VI No", item.getViNo() != null ? String.valueOf(item.getViNo()) : "—"});
        rows.add(new String[]{"Date of Supply", item.getDate() != null ? item.getDate().toString() : "—"});
        rows.add(new String[]{"Buyer Name", item.getSale() != null && item.getSale().getBuyerName() != null ? item.getSale().getBuyerName() : "—"});
        rows.add(new String[]{"Buyer Address", item.getSale() != null && item.getSale().getBuyerAddress() != null ? item.getSale().getBuyerAddress() : "—"});
        rows.add(new String[]{"Buyer BIN / NID", item.getSale() != null && item.getSale().getBuyerBinTinNid() != null ? item.getSale().getBuyerBinTinNid() : "—"});
        rows.add(new String[]{"Total Transaction Value", String.format("%.2f", item.getSale() != null && item.getSale().getTotalSaleAmount() != null ? item.getSale().getTotalSaleAmount() : 0.0)});
        return generateTablePdf(title, headers, widths, rows);
    }

    public byte[] generateMusak91Pdf(com.tax.vat.entity.Musak91 item) {
        String title = "GOVERNMENT OF THE PEOPLE'S REPUBLIC OF BANGLADESH\nNATIONAL BOARD OF REVENUE\nMushak - 9.1 (Value Added Tax Return)";
        String[] headers = {"Part / Note", "Description", "Value"};
        float[] widths = {20f, 50f, 30f};
        List<String[]> rows = new java.util.ArrayList<>();
        rows.add(new String[]{"Part 1", "Taxpayer BIN", item.getCompany() != null ? item.getCompany().getBin() : "—"});
        rows.add(new String[]{"Part 1", "Taxpayer Name", item.getCompany() != null ? item.getCompany().getName() : "—"});
        rows.add(new String[]{"Part 2", "Tax Period", item.getDate() != null ? item.getDate().toString() : "—"});
        rows.add(new String[]{"Part 2", "Return Type", item.getReturnType() != null ? item.getReturnType() : "Main Return (Sec 64)"});
        rows.add(new String[]{"Part 2", "Submission Date", item.getSubmisionDate() != null ? item.getSubmisionDate().toString() : "—"});
        rows.add(new String[]{"Part 5", "Note 24 (VDS Withheld by Receiver)", item.getInput24() != null ? item.getInput24() : "0.00"});
        rows.add(new String[]{"Part 5", "Note 25 (Non-banking channel payment)", item.getInput25() != null ? item.getInput25() : "0.00"});
        rows.add(new String[]{"Part 5", "Note 27 (Other Increasing Adjustments)", item.getInput27() != null ? item.getInput27() : "0.00"});
        rows.add(new String[]{"Part 6", "Note 29 (VDS from Supplies Delivered)", item.getInput29() != null ? item.getInput29() : "0.00"});
        rows.add(new String[]{"Part 6", "Note 30 (AT Paid at Import)", item.getInput30() != null ? item.getInput30() : "0.00"});
        rows.add(new String[]{"Part 6", "Note 32 (Other Decreasing Adjustments)", item.getInput32() != null ? item.getInput32() : "0.00"});
        rows.add(new String[]{"Part 7", "Note 52 (Last Period Closing VAT)", item.getInput52() != null ? item.getInput52() : "0.00"});
        rows.add(new String[]{"Part 9", "Note 58 (Treasury Deposit VAT)", item.getInput58() != null ? item.getInput58() : "0.00"});
        rows.add(new String[]{"Part 10", "Note 65 (Closing Balance VAT)", item.getInput65() != null ? item.getInput65() : "0.00"});
        rows.add(new String[]{"Part 12", "Declarant Name", item.getDeclarationName() != null ? item.getDeclarationName() : "—"});
        rows.add(new String[]{"Part 12", "Declarant Designation", item.getDeclarationDesignation() != null ? item.getDeclarationDesignation() : "—"});
        return generateTablePdf(title, headers, widths, rows);
    }

    private void addCell(PdfPTable table, String text, Font font, Color bg, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(5);
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }
}
