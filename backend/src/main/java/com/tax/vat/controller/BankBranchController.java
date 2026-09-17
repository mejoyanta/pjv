package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.BankBranch;
import com.tax.vat.repository.BankBranchRepository;
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
@RequestMapping("/api/v1/bank-branches")
@Tag(name = "Bank Branch", description = "Endpoints for Bank Branch CRUD operations")
public class BankBranchController {

    private final BankBranchRepository repository;
    private final PdfService pdfService;

    public BankBranchController(BankBranchRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Bank Branches for DataTable")
    @GetMapping
    public DataTableResponse<BankBranch> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long bankId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.ASC, "name"));

        Page<BankBranch> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchBranches(bankId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findBranches(bankId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get branches by bank ID")
    @GetMapping("/by-bank/{bankId}")
    public ApiResponse<List<BankBranch>> getByBank(@PathVariable Long bankId) {
        return ApiResponse.ok("Branches retrieved successfully", repository.findByBankIdOrderByNameAsc(bankId));
    }

    @Operation(summary = "Get single Bank Branch by ID")
    @GetMapping("/{id}")
    public ApiResponse<BankBranch> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Bank branch retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Bank branch not found"));
    }

    @Operation(summary = "Create Bank Branch")
    @PostMapping
    public ApiResponse<BankBranch> create(@RequestBody BankBranch branch) {
        if (branch.getBankId() == null) {
            return ApiResponse.error("Bank ID is required");
        }
        if (branch.getName() == null || branch.getName().trim().isEmpty()) {
            return ApiResponse.error("Branch name is required");
        }
        BankBranch saved = repository.save(branch);
        return ApiResponse.ok("Bank branch created successfully", saved);
    }

    @Operation(summary = "Update Bank Branch")
    @PutMapping("/{id}")
    public ApiResponse<BankBranch> update(@PathVariable Long id, @RequestBody BankBranch details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getBankId() != null) existing.setBankId(details.getBankId());
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getDistrict() != null) existing.setDistrict(details.getDistrict());
                    if (details.getAddress() != null) existing.setAddress(details.getAddress());
                    BankBranch updated = repository.save(existing);
                    return ApiResponse.ok("Bank branch updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Bank branch not found"));
    }

    @Operation(summary = "Delete Bank Branch")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    repository.delete(existing);
                    return ApiResponse.<Void>ok("Bank branch deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Bank branch not found"));
    }

    @Operation(summary = "Download Bank Branch List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long bankId,
            @RequestParam(required = false) String search
    ) {
        List<BankBranch> list = (search != null && !search.trim().isEmpty())
                ? repository.searchBranches(bankId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findBranches(bankId, PageRequest.of(0, 1000)).getContent();

        String[] headers = {"#", "Branch Name", "Bank", "District", "Address"};
        float[] widths = {0.8f, 3.5f, 3.0f, 2.5f, 4.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (BankBranch b : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    b.getName(),
                    b.getBank() != null ? b.getBank().getName() : "-",
                    b.getDistrict() != null ? b.getDistrict() : "-",
                    b.getAddress() != null ? b.getAddress() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Payment Information - Bank Branch List", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bank_branches.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
