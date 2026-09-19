package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.dto.response.Musak91OnlineDataTableItem;
import com.tax.vat.entity.Musak91Online;
import com.tax.vat.repository.CompanyBranchRepository;
import com.tax.vat.repository.CompanyRepository;
import com.tax.vat.repository.Musak91OnlineRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping({"/api/v1/mushak-9-1-online", "/api/v1/musak-9-1-online"})
@Tag(name = "Mushak 9.1 Online", description = "Endpoints for Mushak 9.1 Online Documents")
public class Musak91OnlineController {

    private final Musak91OnlineRepository musak91OnlineRepository;
    private final CompanyRepository companyRepository;
    private final CompanyBranchRepository companyBranchRepository;

    public Musak91OnlineController(
            Musak91OnlineRepository musak91OnlineRepository,
            CompanyRepository companyRepository,
            CompanyBranchRepository companyBranchRepository
    ) {
        this.musak91OnlineRepository = musak91OnlineRepository;
        this.companyRepository = companyRepository;
        this.companyBranchRepository = companyBranchRepository;
    }

    @Operation(summary = "Load Mushak 9.1 Online DataTable")
    @GetMapping
    public DataTableResponse<Musak91OnlineDataTableItem> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String year
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, Math.min(request.getLength(), 100), Sort.by(Sort.Direction.DESC, "id"));

        Page<Musak91Online> result = musak91OnlineRepository.findFiltered(
                companyId,
                month,
                year,
                request.getSearchValue() != null ? request.getSearchValue().trim() : null,
                pageable
        );

        int serial = request.getStart() + 1;
        List<Musak91OnlineDataTableItem> items = new ArrayList<>();
        for (Musak91Online m : result.getContent()) {
            Musak91OnlineDataTableItem item = new Musak91OnlineDataTableItem();
            item.setId(m.getId());
            item.setSlug(m.getSlug());
            item.setSn(serial++);
            item.setPeriod((m.getMonth() != null ? m.getMonth() : "") + (m.getYear() != null ? ", " + m.getYear() : ""));
            item.setUploadedDate(m.getDate());
            item.setCreatedAt(m.getCreatedAt());
            item.setCompany(m.getCompany() != null ? m.getCompany().getName() : "—");
            item.setBin(m.getCompanyBin() != null ? m.getCompanyBin() : (m.getCompany() != null ? m.getCompany().getBin() : "—"));
            item.setFile(m.getFile());
            items.add(item);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), items);
    }

    @Operation(summary = "Get single Mushak 9.1 Online by ID")
    @GetMapping("/{id}")
    public ApiResponse<Musak91Online> getById(@PathVariable Long id) {
        return musak91OnlineRepository.findById(id)
                .map(item -> ApiResponse.ok("Mushak 9.1 Online record found", item))
                .orElseGet(() -> ApiResponse.error("Record not found"));
    }

    @Operation(summary = "Get single Mushak 9.1 Online by Slug")
    @GetMapping("/slug/{slug}")
    public ApiResponse<Musak91Online> getBySlug(@PathVariable String slug) {
        return musak91OnlineRepository.findBySlug(slug)
                .map(item -> ApiResponse.ok("Mushak 9.1 Online record found", item))
                .orElseGet(() -> ApiResponse.error("Record not found"));
    }

    @Operation(summary = "Get form data options for Mushak 9.1 Online")
    @GetMapping("/form-data")
    public ApiResponse<Map<String, Object>> getFormData() {
        Map<String, Object> data = new HashMap<>();
        data.put("companies", companyRepository.findAllActiveCompanies());
        data.put("branches", companyBranchRepository.findAll());
        return ApiResponse.ok("Form data loaded successfully", data);
    }

    @Operation(summary = "Create Mushak 9.1 Online Document")
    @PostMapping
    public ApiResponse<Musak91Online> create(@RequestBody Musak91Online input) {
        if (input.getSlug() == null || input.getSlug().trim().isEmpty()) {
            input.setSlug(UUID.randomUUID().toString());
        }
        if (input.getDate() == null) {
            input.setDate(LocalDate.now());
        }
        input.setCreatedAt(LocalDateTime.now());
        input.setUpdatedAt(LocalDateTime.now());
        Musak91Online saved = musak91OnlineRepository.save(input);
        return ApiResponse.ok("Mushak 9.1 Online document uploaded successfully", saved);
    }

    @Operation(summary = "Update Mushak 9.1 Online Document")
    @PutMapping("/{id}")
    public ApiResponse<Musak91Online> update(@PathVariable Long id, @RequestBody Musak91Online input) {
        return musak91OnlineRepository.findById(id).map(existing -> {
            existing.setDate(input.getDate());
            existing.setMonth(input.getMonth());
            existing.setYear(input.getYear());
            existing.setCompanyId(input.getCompanyId());
            existing.setCompanyBranchId(input.getCompanyBranchId());
            existing.setCompanyBin(input.getCompanyBin());
            existing.setFile(input.getFile());
            existing.setUpdatedAt(LocalDateTime.now());
            Musak91Online updated = musak91OnlineRepository.save(existing);
            return ApiResponse.ok("Mushak 9.1 Online document updated successfully", updated);
        }).orElseGet(() -> ApiResponse.error("Record not found"));
    }

    @Operation(summary = "Soft delete Mushak 9.1 Online Document")
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        return musak91OnlineRepository.findById(id).map(item -> {
            item.setDeletedAt(LocalDateTime.now());
            musak91OnlineRepository.save(item);
            return ApiResponse.ok("Mushak 9.1 Online document deleted successfully", "Deleted ID: " + id);
        }).orElseGet(() -> ApiResponse.error("Record not found"));
    }
}
