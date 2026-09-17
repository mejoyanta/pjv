package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.MobileBankingAccount;
import com.tax.vat.repository.MobileBankingAccountRepository;
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

@RestController
@RequestMapping("/api/v1/mobile-banking-accounts")
@Tag(name = "Mobile Banking Account", description = "Endpoints for Mobile Banking Account CRUD operations")
public class MobileBankingAccountController {

    private final MobileBankingAccountRepository repository;
    private final PdfService pdfService;

    public MobileBankingAccountController(MobileBankingAccountRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Mobile Banking Accounts for DataTable")
    @GetMapping
    public DataTableResponse<MobileBankingAccount> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<MobileBankingAccount> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchAccounts(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findAccounts(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Mobile Banking Account by ID")
    @GetMapping("/{id}")
    public ApiResponse<MobileBankingAccount> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Mobile banking account retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Mobile banking account not found"));
    }

    @Operation(summary = "Create Mobile Banking Account")
    @PostMapping
    public ApiResponse<MobileBankingAccount> create(@RequestBody MobileBankingAccount account) {
        if (account.getCompanyId() == null) {
            return ApiResponse.error("Company ID is required");
        }
        if (account.getAccountNumber() == null || account.getAccountNumber().trim().isEmpty()) {
            return ApiResponse.error("Account number is required");
        }
        MobileBankingAccount saved = repository.save(account);
        return ApiResponse.ok("Mobile banking account created successfully", saved);
    }

    @Operation(summary = "Update Mobile Banking Account")
    @PutMapping("/{id}")
    public ApiResponse<MobileBankingAccount> update(@PathVariable Long id, @RequestBody MobileBankingAccount details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getAccountNumber() != null) existing.setAccountNumber(details.getAccountNumber());
                    if (details.getAccountName() != null) existing.setAccountName(details.getAccountName());
                    if (details.getAccountType() != null) existing.setAccountType(details.getAccountType());
                    if (details.getIsActive() != null) existing.setIsActive(details.getIsActive());
                    MobileBankingAccount updated = repository.save(existing);
                    return ApiResponse.ok("Mobile banking account updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Mobile banking account not found"));
    }

    @Operation(summary = "Delete Mobile Banking Account (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Mobile banking account deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Mobile banking account not found"));
    }

    @Operation(summary = "Download Mobile Banking Account List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<MobileBankingAccount> list = (search != null && !search.trim().isEmpty())
                ? repository.searchAccounts(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAccounts(companyId, PageRequest.of(0, 1000)).getContent();

        String[] headers = {"#", "Account Name", "Account Number", "Type", "Status"};
        float[] widths = {0.8f, 3.5f, 3.5f, 2.5f, 2.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (MobileBankingAccount m : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    m.getAccountName() != null ? m.getAccountName() : "-",
                    m.getAccountNumber() != null ? m.getAccountNumber() : "-",
                    m.getAccountType() != null ? m.getAccountType() : "-",
                    (m.getIsActive() != null && m.getIsActive()) ? "Active" : "Inactive"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Payment Information - Mobile Banking Accounts", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mobile_banking_accounts.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
