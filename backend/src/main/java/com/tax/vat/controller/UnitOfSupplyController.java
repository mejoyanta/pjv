package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.UnitOfSupply;
import com.tax.vat.repository.UnitOfSupplyRepository;
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
@RequestMapping("/api/v1/units")
@Tag(name = "Unit of Supply", description = "Endpoints for Unit of Supply CRUD operations")
public class UnitOfSupplyController {

    private final UnitOfSupplyRepository repository;
    private final PdfService pdfService;

    public UnitOfSupplyController(UnitOfSupplyRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Units for DataTable")
    @GetMapping
    public DataTableResponse<UnitOfSupply> loadDataTable(@ParameterObject DataTableRequest request) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<UnitOfSupply> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActiveUnits(request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActiveUnits(pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Unit by ID")
    @GetMapping("/{id}")
    public ApiResponse<UnitOfSupply> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Unit retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Unit not found"));
    }

    @Operation(summary = "Create Unit")
    @PostMapping
    public ApiResponse<UnitOfSupply> create(@RequestBody UnitOfSupply unit) {
        if (unit.getName() == null || unit.getName().trim().isEmpty()) {
            return ApiResponse.error("Unit name is required");
        }
        unit.setSlug(UUID.randomUUID().toString());
        unit.setDeletedAt(null);
        UnitOfSupply saved = repository.save(unit);
        return ApiResponse.ok("Unit created successfully", saved);
    }

    @Operation(summary = "Update Unit")
    @PutMapping("/{id}")
    public ApiResponse<UnitOfSupply> update(@PathVariable Long id, @RequestBody UnitOfSupply details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getDescription() != null) existing.setDescription(details.getDescription());
                    UnitOfSupply updated = repository.save(existing);
                    return ApiResponse.ok("Unit updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Unit not found"));
    }

    @Operation(summary = "Delete Unit (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Unit deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Unit not found"));
    }

    @Operation(summary = "Download Unit List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam(required = false) String search) {
        List<UnitOfSupply> list = (search != null && !search.trim().isEmpty())
                ? repository.searchActiveUnits(search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAllActiveUnits();

        String[] headers = {"#", "Unit Name", "Description", "Created At"};
        float[] widths = {1.0f, 3.5f, 4.0f, 2.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (UnitOfSupply u : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    u.getName(),
                    u.getDescription() != null ? u.getDescription() : "-",
                    u.getCreatedAt() != null ? u.getCreatedAt().toLocalDate().toString() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Unit of Supply Report", headers, widths, rows);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"unit_of_supplies_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
