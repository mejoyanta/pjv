package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.NocCertificate;
import com.tax.vat.repository.NocCertificateRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/company-noc")
@Tag(name = "Company NOC", description = "Endpoints for Company NOC CRUD operations")
public class NocCertificateController {

    private final NocCertificateRepository repository;

    public NocCertificateController(NocCertificateRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Load NOC Certificates for DataTable")
    @GetMapping
    public DataTableResponse<NocCertificate> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<NocCertificate> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActiveNocs(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActiveNocs(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single NOC Certificate by ID")
    @GetMapping("/{id}")
    public ApiResponse<NocCertificate> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("NOC retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("NOC not found"));
    }

    @Operation(summary = "Create NOC Certificate")
    @PostMapping
    public ApiResponse<NocCertificate> create(@RequestBody NocCertificate noc) {
        if (noc.getSlug() == null || noc.getSlug().trim().isEmpty()) {
            noc.setSlug("NOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        NocCertificate saved = repository.save(noc);
        return ApiResponse.ok("NOC Certificate created successfully", saved);
    }

    @Operation(summary = "Update NOC Certificate")
    @PutMapping("/{id}")
    public ApiResponse<NocCertificate> update(@PathVariable Long id, @RequestBody NocCertificate updated) {
        return repository.findById(id).map(noc -> {
            noc.setCompanyId(updated.getCompanyId());
            noc.setDate(updated.getDate());
            noc.setNocNo(updated.getNocNo());
            return ApiResponse.ok("NOC Certificate updated successfully", repository.save(noc));
        }).orElseGet(() -> ApiResponse.error("NOC not found"));
    }

    @Operation(summary = "Delete NOC Certificate")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id).map(noc -> {
            noc.setDeletedAt(LocalDateTime.now());
            repository.save(noc);
            return ApiResponse.<Void>ok("NOC Certificate deleted successfully", null);
        }).orElseGet(() -> ApiResponse.error("NOC not found"));
    }
}
