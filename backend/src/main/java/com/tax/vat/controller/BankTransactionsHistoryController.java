package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.BankTransactionsHistory;
import com.tax.vat.repository.BankTransactionsHistoryRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bank-transactions")
@Tag(name = "Bank Transactions", description = "Endpoints for Bank Transactions CRUD operations")
public class BankTransactionsHistoryController {

    private final BankTransactionsHistoryRepository repository;
    private final PdfService pdfService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public BankTransactionsHistoryController(BankTransactionsHistoryRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Bank Transactions for DataTable")
    @GetMapping
    public DataTableResponse<BankTransactionsHistory> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<BankTransactionsHistory> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchBankTransactions(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findBankTransactions(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Bank Transaction by ID")
    @GetMapping("/{id}")
    public ApiResponse<BankTransactionsHistory> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Bank transaction retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Bank transaction not found"));
    }

    @Operation(summary = "Create Bank Transaction")
    @PostMapping
    public ApiResponse<BankTransactionsHistory> create(@RequestBody BankTransactionsHistory tx) {
        if (tx.getCompanyId() == null) {
            return ApiResponse.error("Company ID is required");
        }
        // Calculate closing balance
        double opening = tx.getOpeningBalance() != null ? tx.getOpeningBalance() : 0.0;
        double deposit = tx.getDeposit() != null ? tx.getDeposit() : 0.0;
        double withdraw = tx.getWithdraw() != null ? tx.getWithdraw() : 0.0;
        double debit = tx.getDebit() != null ? tx.getDebit() : 0.0;
        double credit = tx.getCredit() != null ? tx.getCredit() : 0.0;
        tx.setClosingBalance(opening + deposit + credit - withdraw - debit);

        BankTransactionsHistory saved = repository.save(tx);
        return ApiResponse.ok("Bank transaction created successfully", saved);
    }

    @Operation(summary = "Update Bank Transaction")
    @PutMapping("/{id}")
    public ApiResponse<BankTransactionsHistory> update(@PathVariable Long id, @RequestBody BankTransactionsHistory details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getBankId() != null) existing.setBankId(details.getBankId());
                    if (details.getBankInfoDetailId() != null) existing.setBankInfoDetailId(details.getBankInfoDetailId());
                    if (details.getDate() != null) existing.setDate(details.getDate());
                    if (details.getOpeningBalance() != null) existing.setOpeningBalance(details.getOpeningBalance());
                    if (details.getDeposit() != null) existing.setDeposit(details.getDeposit());
                    if (details.getWithdraw() != null) existing.setWithdraw(details.getWithdraw());
                    if (details.getDebit() != null) existing.setDebit(details.getDebit());
                    if (details.getCredit() != null) existing.setCredit(details.getCredit());
                    if (details.getPurposeOf() != null) existing.setPurposeOf(details.getPurposeOf());
                    if (details.getStatus() != null) existing.setStatus(details.getStatus());

                    double opening = existing.getOpeningBalance() != null ? existing.getOpeningBalance() : 0.0;
                    double dep = existing.getDeposit() != null ? existing.getDeposit() : 0.0;
                    double wth = existing.getWithdraw() != null ? existing.getWithdraw() : 0.0;
                    double deb = existing.getDebit() != null ? existing.getDebit() : 0.0;
                    double crd = existing.getCredit() != null ? existing.getCredit() : 0.0;
                    existing.setClosingBalance(opening + dep + crd - wth - deb);

                    BankTransactionsHistory updated = repository.save(existing);
                    return ApiResponse.ok("Bank transaction updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Bank transaction not found"));
    }

    @Operation(summary = "Delete Bank Transaction (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Bank transaction deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Bank transaction not found"));
    }

    @Operation(summary = "Download Bank Transaction List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<BankTransactionsHistory> list = (search != null && !search.trim().isEmpty())
                ? repository.searchBankTransactions(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findBankTransactions(companyId, PageRequest.of(0, 1000)).getContent();

        String[] headers = {"#", "Date", "Bank", "Account", "Opening", "Deposit", "Withdraw", "Closing", "Purpose"};
        float[] widths = {0.8f, 2.0f, 2.5f, 2.5f, 2.0f, 2.0f, 2.0f, 2.0f, 3.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (BankTransactionsHistory b : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    b.getDate() != null ? b.getDate().format(DATE_FMT) : "-",
                    b.getBank() != null ? b.getBank().getName() : "-",
                    b.getBankInfoDetail() != null ? b.getBankInfoDetail().getAccNumber() : "-",
                    String.format("%.2f", b.getOpeningBalance() != null ? b.getOpeningBalance() : 0.0),
                    String.format("%.2f", b.getDeposit() != null ? b.getDeposit() : 0.0),
                    String.format("%.2f", b.getWithdraw() != null ? b.getWithdraw() : 0.0),
                    String.format("%.2f", b.getClosingBalance() != null ? b.getClosingBalance() : 0.0),
                    b.getPurposeOf() != null ? b.getPurposeOf() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Payment Information - Bank Transactions", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bank_transactions.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
