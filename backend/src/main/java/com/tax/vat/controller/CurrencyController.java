package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.Currency;
import com.tax.vat.repository.CurrencyRepository;
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
@RequestMapping("/api/v1/currencies")
@Tag(name = "Currency", description = "Endpoints for Currency CRUD operations")
public class CurrencyController {

    private final CurrencyRepository repository;
    private final PdfService pdfService;

    public CurrencyController(CurrencyRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Currencies for DataTable")
    @GetMapping
    public DataTableResponse<Currency> loadDataTable(@ParameterObject DataTableRequest request) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<Currency> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActiveCurrencies(request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActiveCurrencies(pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Currency by ID")
    @GetMapping("/{id}")
    public ApiResponse<Currency> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Currency retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Currency not found"));
    }

    @Operation(summary = "Create Currency")
    @PostMapping
    public ApiResponse<Currency> create(@RequestBody Currency currency) {
        if (currency.getName() == null || currency.getName().trim().isEmpty()) {
            return ApiResponse.error("Currency name is required");
        }
        if (currency.getCode() == null || currency.getCode().trim().isEmpty()) {
            return ApiResponse.error("Currency code is required");
        }
        currency.setSlug(UUID.randomUUID().toString());
        currency.setDeletedAt(null);
        Currency saved = repository.save(currency);
        return ApiResponse.ok("Currency created successfully", saved);
    }

    @Operation(summary = "Update Currency")
    @PutMapping("/{id}")
    public ApiResponse<Currency> update(@PathVariable Long id, @RequestBody Currency details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getCode() != null) existing.setCode(details.getCode());
                    Currency updated = repository.save(existing);
                    return ApiResponse.ok("Currency updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Currency not found"));
    }

    @Operation(summary = "Delete Currency (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Currency deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Currency not found"));
    }

    @Operation(summary = "Download Currency List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam(required = false) String search) {
        List<Currency> list = (search != null && !search.trim().isEmpty())
                ? repository.searchActiveCurrencies(search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAllActiveCurrencies();

        String[] headers = {"#", "Currency Name", "Code", "Created At"};
        float[] widths = {1.0f, 4.0f, 2.5f, 2.5f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (Currency c : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    c.getName(),
                    c.getCode(),
                    c.getCreatedAt() != null ? c.getCreatedAt().toLocalDate().toString() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Currencies List Report", headers, widths, rows);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"currencies_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
