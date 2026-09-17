package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.AnalyzeReport;
import com.tax.vat.repository.AnalyzeReportRepository;
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
@RequestMapping("/api/v1/analyze-reports")
@Tag(name = "Analyze Report", description = "Endpoints for Analyze Report CRUD operations")
public class AnalyzeReportController {

    private final AnalyzeReportRepository repository;

    public AnalyzeReportController(AnalyzeReportRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Load Analyze Reports for DataTable")
    @GetMapping
    public DataTableResponse<AnalyzeReport> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<AnalyzeReport> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActiveReports(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActiveReports(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Analyze Report by ID")
    @GetMapping("/{id}")
    public ApiResponse<AnalyzeReport> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Analyze Report retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Analyze Report not found"));
    }

    @Operation(summary = "Create Analyze Report")
    @PostMapping
    public ApiResponse<AnalyzeReport> create(@RequestBody AnalyzeReport report) {
        if (report.getSlug() == null || report.getSlug().trim().isEmpty()) {
            report.setSlug("AR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        AnalyzeReport saved = repository.save(report);
        return ApiResponse.ok("Analyze Report created successfully", saved);
    }

    @Operation(summary = "Update Analyze Report")
    @PutMapping("/{id}")
    public ApiResponse<AnalyzeReport> update(@PathVariable Long id, @RequestBody AnalyzeReport updated) {
        return repository.findById(id).map(rep -> {
            rep.setCompanyId(updated.getCompanyId());
            rep.setCompanyBranchId(updated.getCompanyBranchId());
            rep.setDate(updated.getDate());
            rep.setRefNo(updated.getRefNo());
            rep.setSubject(updated.getSubject());
            rep.setReceiveDate(updated.getReceiveDate());
            rep.setAnalysedDate(updated.getAnalysedDate());
            rep.setDeliveryDate(updated.getDeliveryDate());
            return ApiResponse.ok("Analyze Report updated successfully", repository.save(rep));
        }).orElseGet(() -> ApiResponse.error("Analyze Report not found"));
    }

    @Operation(summary = "Delete Analyze Report")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id).map(rep -> {
            rep.setDeletedAt(LocalDateTime.now());
            repository.save(rep);
            return ApiResponse.<Void>ok("Analyze Report deleted successfully", null);
        }).orElseGet(() -> ApiResponse.error("Analyze Report not found"));
    }
}
