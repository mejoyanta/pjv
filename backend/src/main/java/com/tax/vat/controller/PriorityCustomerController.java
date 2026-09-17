package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.PriorityCustomer;
import com.tax.vat.repository.PriorityCustomerRepository;
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
@RequestMapping("/api/v1/priority-customers")
@Tag(name = "Priority Customer", description = "Endpoints for Priority Customer CRUD operations")
public class PriorityCustomerController {

    private final PriorityCustomerRepository repository;
    private final PdfService pdfService;

    public PriorityCustomerController(PriorityCustomerRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Priority Customers for DataTable")
    @GetMapping
    public DataTableResponse<PriorityCustomer> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<PriorityCustomer> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActivePriorityCustomers(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActivePriorityCustomers(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Priority Customer by ID")
    @GetMapping("/{id}")
    public ApiResponse<PriorityCustomer> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Priority Customer retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Priority Customer not found"));
    }

    @Operation(summary = "Create Priority Customer")
    @PostMapping
    public ApiResponse<PriorityCustomer> create(@RequestBody PriorityCustomer customer) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            return ApiResponse.error("Customer name is required");
        }
        if (customer.getCompanyId() == null) {
            return ApiResponse.error("Company ID is required");
        }
        customer.setSlug(UUID.randomUUID().toString());
        customer.setDeletedAt(null);
        PriorityCustomer saved = repository.save(customer);
        return ApiResponse.ok("Priority Customer created successfully", saved);
    }

    @Operation(summary = "Update Priority Customer")
    @PutMapping("/{id}")
    public ApiResponse<PriorityCustomer> update(@PathVariable Long id, @RequestBody PriorityCustomer details) {
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
                    PriorityCustomer updated = repository.save(existing);
                    return ApiResponse.ok("Priority Customer updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Priority Customer not found"));
    }

    @Operation(summary = "Delete Priority Customer (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Priority Customer deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Priority Customer not found"));
    }

    @Operation(summary = "Download Priority Customer List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<PriorityCustomer> list = (search != null && !search.trim().isEmpty())
                ? repository.searchActivePriorityCustomers(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAllActivePriorityCustomers(companyId);

        String[] headers = {"#", "Customer Name", "BIN / TIN", "Mobile", "Email", "Bank Name", "Company"};
        float[] widths = {0.8f, 3.5f, 2.2f, 2.0f, 2.5f, 2.5f, 2.8f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (PriorityCustomer c : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    c.getName(),
                    c.getBinTin() != null ? c.getBinTin() : "-",
                    c.getMobileNo() != null ? c.getMobileNo() : "-",
                    c.getEmail() != null ? c.getEmail() : "-",
                    c.getBankName() != null ? c.getBankName() : "-",
                    c.getCompany() != null ? c.getCompany().getName() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Priority Customers List Report", headers, widths, rows);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"priority_customers_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
