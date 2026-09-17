package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.AuditFromVat;
import com.tax.vat.repository.AuditFromVatRepository;
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
@RequestMapping("/api/v1/audit-reports")
@Tag(name = "Audit Report", description = "Endpoints for Audit Report CRUD operations")
public class AuditFromVatController {

    private final AuditFromVatRepository repository;

    public AuditFromVatController(AuditFromVatRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Load Audit Reports for DataTable")
    @GetMapping
    public DataTableResponse<AuditFromVat> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<AuditFromVat> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActiveAudits(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActiveAudits(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Audit Report by ID")
    @GetMapping("/{id}")
    public ApiResponse<AuditFromVat> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Audit Report retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Audit Report not found"));
    }

    @Operation(summary = "Create Audit Report")
    @PostMapping
    public ApiResponse<AuditFromVat> create(@RequestBody AuditFromVat audit) {
        if (audit.getSlug() == null || audit.getSlug().trim().isEmpty()) {
            audit.setSlug("AUD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        AuditFromVat saved = repository.save(audit);
        return ApiResponse.ok("Audit Report created successfully", saved);
    }

    @Operation(summary = "Update Audit Report")
    @PutMapping("/{id}")
    public ApiResponse<AuditFromVat> update(@PathVariable Long id, @RequestBody AuditFromVat updated) {
        return repository.findById(id).map(aud -> {
            aud.setCompanyId(updated.getCompanyId());
            aud.setCompanyBranchId(updated.getCompanyBranchId());
            aud.setDate(updated.getDate());
            aud.setCompanyName(updated.getCompanyName());
            aud.setCompanyBin(updated.getCompanyBin());
            aud.setCompanyTin(updated.getCompanyTin());
            aud.setCompanyAddress(updated.getCompanyAddress());
            aud.setOwnerName(updated.getOwnerName());
            aud.setOwnerMobile(updated.getOwnerMobile());
            aud.setVat(updated.getVat());
            aud.setCompanyEmail(updated.getCompanyEmail());
            return ApiResponse.ok("Audit Report updated successfully", repository.save(aud));
        }).orElseGet(() -> ApiResponse.error("Audit Report not found"));
    }

    @Operation(summary = "Delete Audit Report")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id).map(aud -> {
            aud.setDeletedAt(LocalDateTime.now());
            repository.save(aud);
            return ApiResponse.<Void>ok("Audit Report deleted successfully", null);
        }).orElseGet(() -> ApiResponse.error("Audit Report not found"));
    }
}
