package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.MobileBankingTransaction;
import com.tax.vat.repository.MobileBankingTransactionRepository;
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
@RequestMapping("/api/v1/mobile-banking-transactions")
@Tag(name = "Mobile Banking Transaction", description = "Endpoints for Mobile Banking Transaction CRUD operations")
public class MobileBankingTransactionController {

    private final MobileBankingTransactionRepository repository;
    private final PdfService pdfService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public MobileBankingTransactionController(MobileBankingTransactionRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Mobile Banking Transactions for DataTable")
    @GetMapping
    public DataTableResponse<MobileBankingTransaction> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<MobileBankingTransaction> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchTransactions(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findTransactions(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Mobile Banking Transaction by ID")
    @GetMapping("/{id}")
    public ApiResponse<MobileBankingTransaction> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Mobile banking transaction retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Mobile banking transaction not found"));
    }

    @Operation(summary = "Create Mobile Banking Transaction")
    @PostMapping
    public ApiResponse<MobileBankingTransaction> create(@RequestBody MobileBankingTransaction tx) {
        if (tx.getCompanyId() == null) {
            return ApiResponse.error("Company ID is required");
        }
        double opening = tx.getOpeningBalance() != null ? tx.getOpeningBalance() : 0.0;
        double deposit = tx.getDeposit() != null ? tx.getDeposit() : 0.0;
        double withdraw = tx.getWithdraw() != null ? tx.getWithdraw() : 0.0;
        double send = tx.getSendMoney() != null ? tx.getSendMoney() : 0.0;
        double receive = tx.getReceiveMoney() != null ? tx.getReceiveMoney() : 0.0;
        double payment = tx.getPayment() != null ? tx.getPayment() : 0.0;
        tx.setClosingBalance(opening + deposit + receive - withdraw - send - payment);

        MobileBankingTransaction saved = repository.save(tx);
        return ApiResponse.ok("Mobile banking transaction created successfully", saved);
    }

    @Operation(summary = "Update Mobile Banking Transaction")
    @PutMapping("/{id}")
    public ApiResponse<MobileBankingTransaction> update(@PathVariable Long id, @RequestBody MobileBankingTransaction details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getMobileBankingAccountId() != null) existing.setMobileBankingAccountId(details.getMobileBankingAccountId());
                    if (details.getDate() != null) existing.setDate(details.getDate());
                    if (details.getOpeningBalance() != null) existing.setOpeningBalance(details.getOpeningBalance());
                    if (details.getDeposit() != null) existing.setDeposit(details.getDeposit());
                    if (details.getWithdraw() != null) existing.setWithdraw(details.getWithdraw());
                    if (details.getSendMoney() != null) existing.setSendMoney(details.getSendMoney());
                    if (details.getReceiveMoney() != null) existing.setReceiveMoney(details.getReceiveMoney());
                    if (details.getPayment() != null) existing.setPayment(details.getPayment());
                    if (details.getTransactionId() != null) existing.setTransactionId(details.getTransactionId());
                    if (details.getPurposeOf() != null) existing.setPurposeOf(details.getPurposeOf());
                    if (details.getStatus() != null) existing.setStatus(details.getStatus());

                    double opening = existing.getOpeningBalance() != null ? existing.getOpeningBalance() : 0.0;
                    double dep = existing.getDeposit() != null ? existing.getDeposit() : 0.0;
                    double wth = existing.getWithdraw() != null ? existing.getWithdraw() : 0.0;
                    double snd = existing.getSendMoney() != null ? existing.getSendMoney() : 0.0;
                    double rcv = existing.getReceiveMoney() != null ? existing.getReceiveMoney() : 0.0;
                    double pay = existing.getPayment() != null ? existing.getPayment() : 0.0;
                    existing.setClosingBalance(opening + dep + rcv - wth - snd - pay);

                    MobileBankingTransaction updated = repository.save(existing);
                    return ApiResponse.ok("Mobile banking transaction updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Mobile banking transaction not found"));
    }

    @Operation(summary = "Delete Mobile Banking Transaction (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Mobile banking transaction deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Mobile banking transaction not found"));
    }

    @Operation(summary = "Download Mobile Banking Transaction List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<MobileBankingTransaction> list = (search != null && !search.trim().isEmpty())
                ? repository.searchTransactions(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findTransactions(companyId, PageRequest.of(0, 1000)).getContent();

        String[] headers = {"#", "Date", "Account", "Opening", "Deposit", "Withdraw", "Send Money", "Closing", "Tx ID"};
        float[] widths = {0.8f, 2.0f, 2.5f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.5f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (MobileBankingTransaction m : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    m.getDate() != null ? m.getDate().format(DATE_FMT) : "-",
                    m.getMobileBankingAccount() != null ? m.getMobileBankingAccount().getAccountNumber() : "-",
                    String.format("%.2f", m.getOpeningBalance() != null ? m.getOpeningBalance() : 0.0),
                    String.format("%.2f", m.getDeposit() != null ? m.getDeposit() : 0.0),
                    String.format("%.2f", m.getWithdraw() != null ? m.getWithdraw() : 0.0),
                    String.format("%.2f", m.getSendMoney() != null ? m.getSendMoney() : 0.0),
                    String.format("%.2f", m.getClosingBalance() != null ? m.getClosingBalance() : 0.0),
                    m.getTransactionId() != null ? m.getTransactionId() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Payment Information - Mobile Banking Transactions", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mobile_banking_transactions.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
