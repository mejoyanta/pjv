package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.CpcItemNo;
import com.tax.vat.repository.CpcItemNoRepository;
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
@RequestMapping("/api/v1/cpc-item-nos")
@Tag(name = "CPC & Item No", description = "Endpoints for CPC & Item No CRUD operations")
public class CpcItemNoController {

    private final CpcItemNoRepository repository;
    private final PdfService pdfService;

    public CpcItemNoController(CpcItemNoRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load CPC & Item Nos for DataTable")
    @GetMapping
    public DataTableResponse<CpcItemNo> loadDataTable(@ParameterObject DataTableRequest request) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<CpcItemNo> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActiveCpcItems(request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActiveCpcItems(pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single CPC & Item No by ID")
    @GetMapping("/{id}")
    public ApiResponse<CpcItemNo> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("CPC Item retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("CPC Item not found"));
    }

    @Operation(summary = "Create CPC & Item No")
    @PostMapping
    public ApiResponse<CpcItemNo> create(@RequestBody CpcItemNo item) {
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            return ApiResponse.error("Name is required");
        }
        item.setSlug(UUID.randomUUID().toString());
        item.setDeletedAt(null);
        CpcItemNo saved = repository.save(item);
        return ApiResponse.ok("CPC Item created successfully", saved);
    }

    @Operation(summary = "Update CPC & Item No")
    @PutMapping("/{id}")
    public ApiResponse<CpcItemNo> update(@PathVariable Long id, @RequestBody CpcItemNo details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getDescription() != null) existing.setDescription(details.getDescription());
                    CpcItemNo updated = repository.save(existing);
                    return ApiResponse.ok("CPC Item updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("CPC Item not found"));
    }

    @Operation(summary = "Delete CPC & Item No (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("CPC Item deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("CPC Item not found"));
    }

    @Operation(summary = "Download CPC & Item No List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam(required = false) String search) {
        List<CpcItemNo> list = (search != null && !search.trim().isEmpty())
                ? repository.searchActiveCpcItems(search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAllActiveCpcItems();

        String[] headers = {"#", "Name", "Description", "Created At"};
        float[] widths = {1.0f, 3.5f, 4.5f, 2.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (CpcItemNo c : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    c.getName(),
                    c.getDescription() != null ? c.getDescription() : "-",
                    c.getCreatedAt() != null ? c.getCreatedAt().toLocalDate().toString() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("CPC & Item No Report", headers, widths, rows);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cpc_item_nos_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
