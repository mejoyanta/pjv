package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.Designation;
import com.tax.vat.repository.DesignationRepository;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/designations")
@Tag(name = "Designation", description = "Endpoints for Designation CRUD operations")
public class DesignationController {

    private final DesignationRepository repository;
    private final PdfService pdfService;

    public DesignationController(DesignationRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Designations for DataTable")
    @GetMapping
    public DataTableResponse<Designation> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<Designation> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchDesignations(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findDesignations(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Designation by ID")
    @GetMapping("/{id}")
    public ApiResponse<Designation> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Designation retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Designation not found"));
    }

    @Operation(summary = "Create Designation")
    @PostMapping
    public ApiResponse<Designation> create(@RequestBody Designation designation) {
        if (designation.getName() == null || designation.getName().trim().isEmpty()) {
            return ApiResponse.error("Designation name is required");
        }
        Designation saved = repository.save(designation);
        return ApiResponse.ok("Designation created successfully", saved);
    }

    @Operation(summary = "Update Designation")
    @PutMapping("/{id}")
    public ApiResponse<Designation> update(@PathVariable Long id, @RequestBody Designation details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getCompanyId() != null) existing.setCompanyId(details.getCompanyId());
                    Designation updated = repository.save(existing);
                    return ApiResponse.ok("Designation updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Designation not found"));
    }

    @Operation(summary = "Delete Designation")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    repository.delete(existing);
                    return ApiResponse.<Void>ok("Designation deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Designation not found"));
    }

    @Operation(summary = "Download Designation List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<Designation> list = (search != null && !search.trim().isEmpty())
                ? repository.searchDesignations(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAllByCompany(companyId);

        String[] headers = {"#", "Designation Name", "Company", "Created At"};
        float[] widths = {1.0f, 4.0f, 3.5f, 2.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (Designation d : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    d.getName(),
                    d.getCompany() != null ? d.getCompany().getName() : "-",
                    d.getCreatedAt() != null ? d.getCreatedAt().toLocalDate().toString() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Designations Report", headers, widths, rows);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"designations_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
