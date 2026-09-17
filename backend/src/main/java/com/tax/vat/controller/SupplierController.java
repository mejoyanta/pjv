package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.Supplier;
import com.tax.vat.repository.SupplierRepository;
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
@RequestMapping("/api/v1/suppliers")
@Tag(name = "Supplier", description = "Endpoints for Supplier CRUD operations")
public class SupplierController {

    private final SupplierRepository repository;
    private final PdfService pdfService;

    public SupplierController(SupplierRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Suppliers for DataTable")
    @GetMapping
    public DataTableResponse<Supplier> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<Supplier> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchSuppliers(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findSuppliers(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Supplier by ID")
    @GetMapping("/{id}")
    public ApiResponse<Supplier> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Supplier retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Supplier not found"));
    }

    @Operation(summary = "Create Supplier")
    @PostMapping
    public ApiResponse<Supplier> create(@RequestBody Supplier supplier) {
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            return ApiResponse.error("Supplier name is required");
        }
        if (supplier.getCompanyId() == null) {
            return ApiResponse.error("Company ID is required");
        }
        Supplier saved = repository.save(supplier);
        return ApiResponse.ok("Supplier created successfully", saved);
    }

    @Operation(summary = "Update Supplier")
    @PutMapping("/{id}")
    public ApiResponse<Supplier> update(@PathVariable Long id, @RequestBody Supplier details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getBinTin() != null) existing.setBinTin(details.getBinTin());
                    if (details.getPhone() != null) existing.setPhone(details.getPhone());
                    if (details.getAddress() != null) existing.setAddress(details.getAddress());
                    if (details.getCountry() != null) existing.setCountry(details.getCountry());
                    if (details.getPurchaseType() != null) existing.setPurchaseType(details.getPurchaseType());
                    if (details.getCompanyId() != null) existing.setCompanyId(details.getCompanyId());
                    Supplier updated = repository.save(existing);
                    return ApiResponse.ok("Supplier updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Supplier not found"));
    }

    @Operation(summary = "Delete Supplier")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    repository.delete(existing);
                    return ApiResponse.<Void>ok("Supplier deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Supplier not found"));
    }

    @Operation(summary = "Download Supplier List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<Supplier> list = (search != null && !search.trim().isEmpty())
                ? repository.searchSuppliers(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findSuppliers(companyId, PageRequest.of(0, 1000)).getContent();

        String[] headers = {"#", "Supplier Name", "BIN / TIN", "Phone", "Address", "Company"};
        float[] widths = {0.8f, 3.5f, 2.5f, 2.2f, 3.5f, 3.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (Supplier s : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    s.getName(),
                    s.getBinTin() != null ? s.getBinTin() : "-",
                    s.getPhone() != null ? s.getPhone() : "-",
                    s.getAddress() != null ? s.getAddress() : "-",
                    s.getCompany() != null ? s.getCompany().getName() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Suppliers List Report", headers, widths, rows);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"suppliers_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
