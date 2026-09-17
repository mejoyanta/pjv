package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.Dvc;
import com.tax.vat.repository.DvcRepository;
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
@RequestMapping("/api/v1/dvcs")
@Tag(name = "DVC Files", description = "Endpoints for DVC File CRUD operations")
public class DvcController {

    private final DvcRepository repository;

    public DvcController(DvcRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Load DVC Files for DataTable")
    @GetMapping
    public DataTableResponse<Dvc> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<Dvc> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActiveDvcs(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActiveDvcs(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single DVC by ID")
    @GetMapping("/{id}")
    public ApiResponse<Dvc> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("DVC retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("DVC not found"));
    }

    @Operation(summary = "Create DVC")
    @PostMapping
    public ApiResponse<Dvc> create(@RequestBody Dvc dvc) {
        if (dvc.getSlug() == null || dvc.getSlug().trim().isEmpty()) {
            dvc.setSlug("DVC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        Dvc saved = repository.save(dvc);
        return ApiResponse.ok("DVC created successfully", saved);
    }

    @Operation(summary = "Update DVC")
    @PutMapping("/{id}")
    public ApiResponse<Dvc> update(@PathVariable Long id, @RequestBody Dvc updated) {
        return repository.findById(id).map(dvc -> {
            dvc.setCompanyId(updated.getCompanyId());
            dvc.setYear(updated.getYear());
            dvc.setActivationDate(updated.getActivationDate());
            dvc.setDvcNo(updated.getDvcNo());
            dvc.setTotalIncome(updated.getTotalIncome());
            dvc.setTotalExpense(updated.getTotalExpense());
            dvc.setTotalProfit(updated.getTotalProfit());
            if (updated.getFileUrl() != null && !updated.getFileUrl().trim().isEmpty()) {
                dvc.setFileUrl(updated.getFileUrl());
            }
            return ApiResponse.ok("DVC updated successfully", repository.save(dvc));
        }).orElseGet(() -> ApiResponse.error("DVC not found"));
    }

    @Operation(summary = "Delete DVC")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id).map(dvc -> {
            dvc.setDeletedAt(LocalDateTime.now());
            repository.save(dvc);
            return ApiResponse.<Void>ok("DVC deleted successfully", null);
        }).orElseGet(() -> ApiResponse.error("DVC not found"));
    }
}
