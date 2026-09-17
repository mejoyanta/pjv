package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.LegalManagementCase;
import com.tax.vat.repository.LegalManagementRepository;
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
@RequestMapping("/api/v1/legal-management")
@Tag(name = "Legal Management", description = "Endpoints for Legal Management Case CRUD operations")
public class LegalManagementController {

    private final LegalManagementRepository repository;

    public LegalManagementController(LegalManagementRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Load Legal Management Cases for DataTable")
    @GetMapping
    public DataTableResponse<LegalManagementCase> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<LegalManagementCase> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActiveCases(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActiveCases(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Legal Management Case by ID")
    @GetMapping("/{id}")
    public ApiResponse<LegalManagementCase> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Case retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Case not found"));
    }

    @Operation(summary = "Create Legal Management Case")
    @PostMapping
    public ApiResponse<LegalManagementCase> create(@RequestBody LegalManagementCase legalCase) {
        if (legalCase.getSlug() == null || legalCase.getSlug().trim().isEmpty()) {
            legalCase.setSlug("LEG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (legalCase.getCompanyPersonName() == null || legalCase.getCompanyPersonName().trim().isEmpty()) {
            legalCase.setCompanyPersonName(legalCase.getClientName() != null && !legalCase.getClientName().trim().isEmpty() 
                    ? legalCase.getClientName() : "N/A");
        }
        if (legalCase.getFileNo() == null || legalCase.getFileNo().trim().isEmpty()) {
            legalCase.setFileNo(legalCase.getCaseNo() != null && !legalCase.getCaseNo().trim().isEmpty()
                    ? legalCase.getCaseNo() : "FN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        if (legalCase.getFileDate() == null) {
            legalCase.setFileDate(legalCase.getNextHearingDate() != null ? legalCase.getNextHearingDate() : java.time.LocalDate.now());
        }
        LegalManagementCase saved = repository.save(legalCase);
        return ApiResponse.ok("Case created successfully", saved);
    }

    @Operation(summary = "Update Legal Management Case")
    @PutMapping("/{id}")
    public ApiResponse<LegalManagementCase> update(@PathVariable Long id, @RequestBody LegalManagementCase updated) {
        return repository.findById(id).map(c -> {
            c.setCompanyId(updated.getCompanyId());
            c.setCompanyBranchId(updated.getCompanyBranchId());
            c.setCompanyPersonName(updated.getCompanyPersonName());
            c.setBarIdNo(updated.getBarIdNo());
            c.setFileNo(updated.getFileNo());
            c.setFileDate(updated.getFileDate());
            c.setDescription(updated.getDescription());
            c.setRepresentativeName(updated.getRepresentativeName());
            c.setRespondentName(updated.getRespondentName());
            c.setFilingLawyer(updated.getFilingLawyer());
            c.setClientName(updated.getClientName());
            c.setCaseNo(updated.getCaseNo());
            c.setCourtName(updated.getCourtName());
            c.setNextHearingDate(updated.getNextHearingDate());
            c.setCommentRemarks(updated.getCommentRemarks());
            return ApiResponse.ok("Case updated successfully", repository.save(c));
        }).orElseGet(() -> ApiResponse.error("Case not found"));
    }

    @Operation(summary = "Delete Legal Management Case")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id).map(c -> {
            c.setDeletedAt(LocalDateTime.now());
            repository.save(c);
            return ApiResponse.<Void>ok("Case deleted successfully", null);
        }).orElseGet(() -> ApiResponse.error("Case not found"));
    }
}
