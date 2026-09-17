package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.Bank;
import com.tax.vat.repository.BankRepository;
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
@RequestMapping("/api/v1/banks")
@Tag(name = "Bank", description = "Endpoints for Bank CRUD operations")
public class BankController {

    private final BankRepository repository;
    private final PdfService pdfService;

    public BankController(BankRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Banks for DataTable")
    @GetMapping
    public DataTableResponse<Bank> loadDataTable(@ParameterObject DataTableRequest request) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.ASC, "name"));

        Page<Bank> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchBanks(request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findAll(pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get all Banks for dropdown")
    @GetMapping("/all")
    public ApiResponse<List<Bank>> getAll() {
        return ApiResponse.ok("Banks retrieved successfully", repository.findAllOrdered());
    }

    @Operation(summary = "Get single Bank by ID")
    @GetMapping("/{id}")
    public ApiResponse<Bank> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Bank retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Bank not found"));
    }

    @Operation(summary = "Create Bank")
    @PostMapping
    public ApiResponse<Bank> create(@RequestBody Bank bank) {
        if (bank.getName() == null || bank.getName().trim().isEmpty()) {
            return ApiResponse.error("Bank name is required");
        }
        Bank saved = repository.save(bank);
        return ApiResponse.ok("Bank created successfully", saved);
    }

    @Operation(summary = "Update Bank")
    @PutMapping("/{id}")
    public ApiResponse<Bank> update(@PathVariable Long id, @RequestBody Bank details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getRoutingNo() != null) existing.setRoutingNo(details.getRoutingNo());
                    if (details.getSwiftCode() != null) existing.setSwiftCode(details.getSwiftCode());
                    Bank updated = repository.save(existing);
                    return ApiResponse.ok("Bank updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Bank not found"));
    }

    @Operation(summary = "Delete Bank")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    repository.delete(existing);
                    return ApiResponse.<Void>ok("Bank deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Bank not found"));
    }

    @Operation(summary = "Download Bank List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam(required = false) String search) {
        List<Bank> list = (search != null && !search.trim().isEmpty())
                ? repository.searchBanks(search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAllOrdered();

        String[] headers = {"#", "Bank Name", "Routing No", "Swift Code"};
        float[] widths = {0.8f, 4.0f, 3.0f, 3.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (Bank b : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    b.getName(),
                    b.getRoutingNo() != null ? b.getRoutingNo() : "-",
                    b.getSwiftCode() != null ? b.getSwiftCode() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Payment Information - Bank List", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=banks.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
