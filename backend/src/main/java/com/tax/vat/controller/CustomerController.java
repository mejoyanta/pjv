package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.Customer;
import com.tax.vat.repository.CustomerRepository;
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
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer", description = "Endpoints for Customer CRUD operations")
public class CustomerController {

    private final CustomerRepository repository;
    private final PdfService pdfService;

    public CustomerController(CustomerRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Customers for DataTable")
    @GetMapping
    public DataTableResponse<Customer> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<Customer> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchCustomers(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findCustomers(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Customer by ID")
    @GetMapping("/{id}")
    public ApiResponse<Customer> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Customer retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Customer not found"));
    }

    @Operation(summary = "Create Customer")
    @PostMapping
    public ApiResponse<Customer> create(@RequestBody Customer customer) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            return ApiResponse.error("Customer name is required");
        }
        if (customer.getCompanyId() == null) {
            return ApiResponse.error("Company ID is required");
        }
        Customer saved = repository.save(customer);
        return ApiResponse.ok("Customer created successfully", saved);
    }

    @Operation(summary = "Update Customer")
    @PutMapping("/{id}")
    public ApiResponse<Customer> update(@PathVariable Long id, @RequestBody Customer details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getBinTin() != null) existing.setBinTin(details.getBinTin());
                    if (details.getPhone() != null) existing.setPhone(details.getPhone());
                    if (details.getAddress() != null) existing.setAddress(details.getAddress());
                    if (details.getCountry() != null) existing.setCountry(details.getCountry());
                    if (details.getCompanyId() != null) existing.setCompanyId(details.getCompanyId());
                    Customer updated = repository.save(existing);
                    return ApiResponse.ok("Customer updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Customer not found"));
    }

    @Operation(summary = "Delete Customer")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    repository.delete(existing);
                    return ApiResponse.<Void>ok("Customer deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Customer not found"));
    }

    @Operation(summary = "Download Customer List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<Customer> list = (search != null && !search.trim().isEmpty())
                ? repository.searchCustomers(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findCustomers(companyId, PageRequest.of(0, 1000)).getContent();

        String[] headers = {"#", "Customer Name", "BIN / TIN", "Phone", "Address", "Company"};
        float[] widths = {0.8f, 3.5f, 2.5f, 2.2f, 3.5f, 3.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (Customer c : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    c.getName(),
                    c.getBinTin() != null ? c.getBinTin() : "-",
                    c.getPhone() != null ? c.getPhone() : "-",
                    c.getAddress() != null ? c.getAddress() : "-",
                    c.getCompany() != null ? c.getCompany().getName() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Customers List Report", headers, widths, rows);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"customers_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
