package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.Material;
import com.tax.vat.repository.MaterialRepository;
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
@RequestMapping("/api/v1/materials")
@Tag(name = "Material", description = "Endpoints for Material CRUD operations")
public class MaterialController {

    private final MaterialRepository repository;
    private final PdfService pdfService;

    public MaterialController(MaterialRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Materials for DataTable")
    @GetMapping
    public DataTableResponse<Material> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<Material> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActiveMaterials(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActiveMaterials(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Material by ID")
    @GetMapping("/{id}")
    public ApiResponse<Material> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Material retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Material not found"));
    }

    @Operation(summary = "Create Material")
    @PostMapping
    public ApiResponse<Material> create(@RequestBody Material material) {
        if (material.getName() == null || material.getName().trim().isEmpty()) {
            return ApiResponse.error("Material name is required");
        }
        material.setSlug(UUID.randomUUID().toString());
        material.setDeletedAt(null);
        Material saved = repository.save(material);
        return ApiResponse.ok("Material created successfully", saved);
    }

    @Operation(summary = "Update Material")
    @PutMapping("/{id}")
    public ApiResponse<Material> update(@PathVariable Long id, @RequestBody Material details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getHsCode() != null) existing.setHsCode(details.getHsCode());
                    if (details.getVat() != null) existing.setVat(details.getVat());
                    if (details.getSd() != null) existing.setSd(details.getSd());
                    if (details.getAt() != null) existing.setAt(details.getAt());
                    if (details.getCd() != null) existing.setCd(details.getCd());
                    if (details.getRd() != null) existing.setRd(details.getRd());
                    if (details.getAit() != null) existing.setAit(details.getAit());
                    if (details.getVatType() != null) existing.setVatType(details.getVatType());
                    if (details.getDescription() != null) existing.setDescription(details.getDescription());
                    if (details.getCompanyId() != null) existing.setCompanyId(details.getCompanyId());
                    Material updated = repository.save(existing);
                    return ApiResponse.ok("Material updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Material not found"));
    }

    @Operation(summary = "Delete Material (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Material deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Material not found"));
    }

    @Operation(summary = "Download Material List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<Material> list = (search != null && !search.trim().isEmpty())
                ? repository.searchActiveMaterials(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAllActiveMaterials(companyId);

        String[] headers = {"#", "HS Code", "Material Name", "VAT %", "SD %", "CD %", "RD %", "Company"};
        float[] widths = {0.8f, 2.0f, 4.0f, 1.2f, 1.2f, 1.2f, 1.2f, 3.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (Material m : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    m.getHsCode() != null ? m.getHsCode() : "-",
                    m.getName(),
                    m.getVat() != null ? String.valueOf(m.getVat()) : "0",
                    m.getSd() != null ? String.valueOf(m.getSd()) : "0",
                    m.getCd() != null ? String.valueOf(m.getCd()) : "0",
                    m.getRd() != null ? String.valueOf(m.getRd()) : "0",
                    m.getCompany() != null ? m.getCompany().getName() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Materials List Report", headers, widths, rows);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"materials_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
