package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.PrioritySupplier;
import com.tax.vat.repository.PrioritySupplierRepository;
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
@RequestMapping("/api/v1/priority-suppliers")
@Tag(name = "Priority Supplier", description = "Endpoints for Priority Supplier CRUD operations")
public class PrioritySupplierController {

    private final PrioritySupplierRepository repository;
    private final PdfService pdfService;

    public PrioritySupplierController(PrioritySupplierRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Priority Suppliers for DataTable")
    @GetMapping
    public DataTableResponse<PrioritySupplier> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<PrioritySupplier> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActivePrioritySuppliers(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActivePrioritySuppliers(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Priority Supplier by ID")
    @GetMapping("/{id}")
    public ApiResponse<PrioritySupplier> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Priority Supplier retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Priority Supplier not found"));
    }

    @Operation(summary = "Create Priority Supplier")
    @PostMapping
    public ApiResponse<PrioritySupplier> create(@RequestBody PrioritySupplier supplier) {
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            return ApiResponse.error("Supplier name is required");
        }
        if (supplier.getCompanyId() == null) {
            return ApiResponse.error("Company ID is required");
        }
        supplier.setSlug(UUID.randomUUID().toString());
        supplier.setDeletedAt(null);
        PrioritySupplier saved = repository.save(supplier);
        return ApiResponse.ok("Priority Supplier created successfully", saved);
    }

    @Operation(summary = "Update Priority Supplier")
    @PutMapping("/{id}")
    public ApiResponse<PrioritySupplier> update(@PathVariable Long id, @RequestBody PrioritySupplier details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getAddress() != null) existing.setAddress(details.getAddress());
                    if (details.getBinTin() != null) existing.setBinTin(details.getBinTin());
                    if (details.getMobileNo() != null) existing.setMobileNo(details.getMobileNo());
                    if (details.getEmail() != null) existing.setEmail(details.getEmail());
                    if (details.getBankName() != null) existing.setBankName(details.getBankName());
                    if (details.getAcNo() != null) existing.setAcNo(details.getAcNo());
                    if (details.getCompanyId() != null) existing.setCompanyId(details.getCompanyId());
                    PrioritySupplier updated = repository.save(existing);
                    return ApiResponse.ok("Priority Supplier updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Priority Supplier not found"));
    }

    @Operation(summary = "Delete Priority Supplier (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Priority Supplier deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Priority Supplier not found"));
    }

    @Operation(summary = "Download Priority Supplier List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<PrioritySupplier> list = (search != null && !search.trim().isEmpty())
                ? repository.searchActivePrioritySuppliers(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAllActivePrioritySuppliers(companyId);

        String[] headers = {"#", "Supplier Name", "BIN / TIN", "Mobile", "Email", "Bank Name", "Company"};
        float[] widths = {0.8f, 3.5f, 2.2f, 2.0f, 2.5f, 2.5f, 2.8f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (PrioritySupplier s : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    s.getName(),
                    s.getBinTin() != null ? s.getBinTin() : "-",
                    s.getMobileNo() != null ? s.getMobileNo() : "-",
                    s.getEmail() != null ? s.getEmail() : "-",
                    s.getBankName() != null ? s.getBankName() : "-",
                    s.getCompany() != null ? s.getCompany().getName() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Priority Suppliers List Report", headers, widths, rows);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"priority_suppliers_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
