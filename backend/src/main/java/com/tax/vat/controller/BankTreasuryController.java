package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.BankTreasury;
import com.tax.vat.repository.BankTreasuryRepository;
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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bank-treasuries")
@Tag(name = "Bank Treasury", description = "Endpoints for Bank Treasury CRUD operations")
public class BankTreasuryController {

    private final BankTreasuryRepository repository;
    private final PdfService pdfService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public BankTreasuryController(BankTreasuryRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Bank Treasuries for DataTable")
    @GetMapping
    public DataTableResponse<BankTreasury> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<BankTreasury> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchBankTreasuries(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findBankTreasuries(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Bank Treasury by ID")
    @GetMapping("/{id}")
    public ApiResponse<BankTreasury> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Bank treasury retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Bank treasury not found"));
    }

    @Operation(summary = "Create Bank Treasury")
    @PostMapping
    public ApiResponse<BankTreasury> create(@RequestBody BankTreasury treasury) {
        if (treasury.getCompanyId() == null) {
            return ApiResponse.error("Company ID is required");
        }
        if (treasury.getAmount() == null) {
            treasury.setAmount(0.0);
        }
        BankTreasury saved = repository.save(treasury);
        return ApiResponse.ok("Bank treasury created successfully", saved);
    }

    @Operation(summary = "Update Bank Treasury")
    @PutMapping("/{id}")
    public ApiResponse<BankTreasury> update(@PathVariable Long id, @RequestBody BankTreasury details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getBankId() != null) existing.setBankId(details.getBankId());
                    if (details.getBranchId() != null) existing.setBranchId(details.getBranchId());
                    if (details.getDate() != null) existing.setDate(details.getDate());
                    if (details.getChallanNo() != null) existing.setChallanNo(details.getChallanNo());
                    if (details.getAmount() != null) existing.setAmount(details.getAmount());
                    if (details.getAccountCode() != null) existing.setAccountCode(details.getAccountCode());
                    if (details.getAddInto91() != null) existing.setAddInto91(details.getAddInto91());
                    if (details.getComments() != null) existing.setComments(details.getComments());
                    BankTreasury updated = repository.save(existing);
                    return ApiResponse.ok("Bank treasury updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Bank treasury not found"));
    }

    @Operation(summary = "Delete Bank Treasury")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    repository.delete(existing);
                    return ApiResponse.<Void>ok("Bank treasury deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Bank treasury not found"));
    }

    @Operation(summary = "Download Bank Treasury List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<BankTreasury> list = (search != null && !search.trim().isEmpty())
                ? repository.searchBankTreasuries(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findBankTreasuries(companyId, PageRequest.of(0, 1000)).getContent();

        String[] headers = {"#", "Date", "Bank Name", "Branch", "Challan No", "Account Code", "Amount"};
        float[] widths = {0.8f, 2.2f, 3.5f, 3.0f, 2.5f, 2.5f, 2.2f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (BankTreasury b : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    b.getDate() != null ? b.getDate().format(DATE_FMT) : "-",
                    b.getBank() != null ? b.getBank().getName() : "-",
                    b.getBranch() != null ? b.getBranch().getName() : "-",
                    b.getChallanNo() != null ? b.getChallanNo() : "-",
                    b.getAccountCode() != null ? b.getAccountCode() : "-",
                    String.format("%.2f", b.getAmount() != null ? b.getAmount() : 0.0)
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Payment Information - Bank Treasury List", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bank_treasury.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
