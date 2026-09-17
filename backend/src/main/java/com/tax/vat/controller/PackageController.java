package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.PackageEntity;
import com.tax.vat.repository.PackageRepository;
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
@RequestMapping("/api/v1/packages")
@Tag(name = "Package", description = "Endpoints for Package CRUD operations")
public class PackageController {

    private final PackageRepository repository;
    private final PdfService pdfService;

    public PackageController(PackageRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Packages for DataTable")
    @GetMapping
    public DataTableResponse<PackageEntity> loadDataTable(@ParameterObject DataTableRequest request) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.ASC, "id"));

        Page<PackageEntity> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchPackages(request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findAll(pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get all Packages for dropdown")
    @GetMapping("/all")
    public ApiResponse<List<PackageEntity>> getAll() {
        return ApiResponse.ok("Packages retrieved successfully", repository.findAllOrdered());
    }

    @Operation(summary = "Get single Package by ID")
    @GetMapping("/{id}")
    public ApiResponse<PackageEntity> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Package retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Package not found"));
    }

    @Operation(summary = "Create Package")
    @PostMapping
    public ApiResponse<PackageEntity> create(@RequestBody PackageEntity pkg) {
        if (pkg.getName() == null || pkg.getName().trim().isEmpty()) {
            return ApiResponse.error("Package name is required");
        }
        PackageEntity saved = repository.save(pkg);
        return ApiResponse.ok("Package created successfully", saved);
    }

    @Operation(summary = "Update Package")
    @PutMapping("/{id}")
    public ApiResponse<PackageEntity> update(@PathVariable Long id, @RequestBody PackageEntity details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getPrice() != null) existing.setPrice(details.getPrice());
                    if (details.getValidity() != null) existing.setValidity(details.getValidity());
                    if (details.getValidityType() != null) existing.setValidityType(details.getValidityType());
                    if (details.getPublicationStatus() != null) existing.setPublicationStatus(details.getPublicationStatus());
                    if (details.getPackageType() != null) existing.setPackageType(details.getPackageType());
                    PackageEntity updated = repository.save(existing);
                    return ApiResponse.ok("Package updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Package not found"));
    }

    @Operation(summary = "Delete Package")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    repository.delete(existing);
                    return ApiResponse.<Void>ok("Package deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Package not found"));
    }

    @Operation(summary = "Download Package List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam(required = false) String search) {
        List<PackageEntity> list = (search != null && !search.trim().isEmpty())
                ? repository.searchPackages(search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAllOrdered();

        String[] headers = {"#", "Package Name", "Price (BDT)", "Validity", "Type", "Status"};
        float[] widths = {0.8f, 3.5f, 2.0f, 2.0f, 2.5f, 1.8f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (PackageEntity p : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    p.getName(),
                    String.format("%.2f", p.getPrice() != null ? p.getPrice() : 0.0),
                    (p.getValidity() != null ? String.valueOf(p.getValidity().intValue()) : "0") + " " + (p.getValidityType() != null ? p.getValidityType() : "days"),
                    p.getPackageType() != null ? p.getPackageType() : "-",
                    (p.getPublicationStatus() != null && p.getPublicationStatus()) ? "Active" : "Inactive"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Payment Information - Packages List", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=packages.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
