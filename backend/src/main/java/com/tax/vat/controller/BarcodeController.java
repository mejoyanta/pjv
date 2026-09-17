package com.tax.vat.controller;

import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.entity.Product;
import com.tax.vat.repository.ProductRepository;
import com.tax.vat.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/barcodes")
@Tag(name = "Barcode", description = "Endpoints for Barcode Generation and PDF printing")
public class BarcodeController {

    private final ProductRepository productRepository;
    private final PdfService pdfService;

    public BarcodeController(ProductRepository productRepository, PdfService pdfService) {
        this.productRepository = productRepository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Get selectable products for barcode generation")
    @GetMapping("/items")
    public ApiResponse<List<Product>> getSelectableItems(@RequestParam(required = false) Long companyId) {
        List<Product> products = productRepository.findAllActiveProducts(companyId);
        return ApiResponse.ok("Products loaded", products);
    }

    @Operation(summary = "Generate Barcode Labels PDF Sheet")
    @PostMapping("/generate-pdf")
    public ResponseEntity<byte[]> generateBarcodePdf(@RequestBody List<Map<String, Object>> requestItems) {
        List<PdfService.BarcodeItem> barcodeItems = new ArrayList<>();

        for (Map<String, Object> req : requestItems) {
            String code = req.get("code") != null ? req.get("code").toString() : "";
            String name = req.get("name") != null ? req.get("name").toString() : "";
            String price = req.get("price") != null ? req.get("price").toString() : "";
            int quantity = 1;
            if (req.get("quantity") != null) {
                try {
                    quantity = Integer.parseInt(req.get("quantity").toString());
                } catch (Exception ignored) {}
            }
            for (int i = 0; i < quantity; i++) {
                barcodeItems.add(new PdfService.BarcodeItem(code, name, price));
            }
        }

        if (barcodeItems.isEmpty()) {
            barcodeItems.add(new PdfService.BarcodeItem("SAMPLE-12345", "Sample Product", "100.00"));
        }

        byte[] pdfBytes = pdfService.generateBarcodeSheetPdf(barcodeItems);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"barcode_labels_sheet.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
