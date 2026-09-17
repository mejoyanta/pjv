package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.AdditionalPrice;
import com.tax.vat.repository.AdditionalPriceRepository;
import com.tax.vat.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/additional-prices")
@Tag(name = "Additional Price Area", description = "Endpoints for Additional Price Area CRUD operations")
public class AdditionalPriceController {

    private final AdditionalPriceRepository repository;
    private final PdfService pdfService;

    public AdditionalPriceController(AdditionalPriceRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Additional Prices for DataTable")
    @GetMapping
    public DataTableResponse<AdditionalPrice> loadDataTable(@ParameterObject DataTableRequest request) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<AdditionalPrice> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActivePrices(request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActivePrices(pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Additional Price by ID")
    @GetMapping("/{id}")
    public ApiResponse<AdditionalPrice> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Additional Price retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Additional Price not found"));
    }

    @Operation(summary = "Create Additional Price")
    @PostMapping
    public ApiResponse<AdditionalPrice> create(@RequestBody AdditionalPrice price) {
        if (price.getName() == null || price.getName().trim().isEmpty()) {
            return ApiResponse.error("Name is required");
        }
        price.setSlug(UUID.randomUUID().toString());
        price.setDeletedAt(null);
        AdditionalPrice saved = repository.save(price);
        return ApiResponse.ok("Additional Price created successfully", saved);
    }

    @Operation(summary = "Update Additional Price")
    @PutMapping("/{id}")
    public ApiResponse<AdditionalPrice> update(@PathVariable Long id, @RequestBody AdditionalPrice details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getDescription() != null) existing.setDescription(details.getDescription());
                    AdditionalPrice updated = repository.save(existing);
                    return ApiResponse.ok("Additional Price updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Additional Price not found"));
    }

    @Operation(summary = "Delete Additional Price (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Additional Price deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Additional Price not found"));
    }

    @Operation(summary = "Download Additional Price List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam(required = false) String search) {
        List<AdditionalPrice> list = (search != null && !search.trim().isEmpty())
                ? repository.searchActivePrices(search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAllActivePrices();

        String[] headers = {"#", "Name", "Description", "Created At"};
        float[] widths = {1.0f, 3.5f, 4.5f, 2.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (AdditionalPrice p : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    p.getName(),
                    p.getDescription() != null ? p.getDescription() : "-",
                    p.getCreatedAt() != null ? p.getCreatedAt().toLocalDate().toString() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Additional Price Areas Report", headers, widths, rows);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"additional_prices_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
