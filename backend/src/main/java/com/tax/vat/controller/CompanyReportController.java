package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.CompanyReport;
import com.tax.vat.repository.CompanyReportRepository;
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
@RequestMapping("/api/v1/company-reports")
@Tag(name = "Company Report Summary", description = "Endpoints for Company Report CRUD operations")
public class CompanyReportController {

    private final CompanyReportRepository repository;

    public CompanyReportController(CompanyReportRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Load Company Reports for DataTable")
    @GetMapping
    public DataTableResponse<CompanyReport> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<CompanyReport> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActiveReports(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActiveReports(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Company Report by ID")
    @GetMapping("/{id}")
    public ApiResponse<CompanyReport> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Report retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Report not found"));
    }

    @Operation(summary = "Create Company Report")
    @PostMapping
    public ApiResponse<CompanyReport> create(@RequestBody CompanyReport report) {
        if (report.getSlug() == null || report.getSlug().trim().isEmpty()) {
            report.setSlug("REP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        CompanyReport saved = repository.save(report);
        return ApiResponse.ok("Company Report created successfully", saved);
    }

    @Operation(summary = "Update Company Report")
    @PutMapping("/{id}")
    public ApiResponse<CompanyReport> update(@PathVariable Long id, @RequestBody CompanyReport updated) {
        return repository.findById(id).map(rep -> {
            rep.setCompanyId(updated.getCompanyId());
            rep.setCompanyBranchId(updated.getCompanyBranchId());
            rep.setCompanyBin(updated.getCompanyBin());
            rep.setDate(updated.getDate());
            rep.setMonth(updated.getMonth());
            rep.setYear(updated.getYear());
            rep.setSubDate(updated.getSubDate());
            return ApiResponse.ok("Company Report updated successfully", repository.save(rep));
        }).orElseGet(() -> ApiResponse.error("Report not found"));
    }

    @Operation(summary = "Delete Company Report")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id).map(rep -> {
            rep.setDeletedAt(LocalDateTime.now());
            repository.save(rep);
            return ApiResponse.<Void>ok("Company Report deleted successfully", null);
        }).orElseGet(() -> ApiResponse.error("Report not found"));
    }
}
