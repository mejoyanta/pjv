package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.BankInfoDetail;
import com.tax.vat.repository.BankInfoDetailRepository;
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
@RequestMapping({"/api/v1/bank-info-details", "/api/v1/bank-info-detail"})
@Tag(name = "Bank Account Info", description = "Endpoints for Bank Account Info CRUD operations")
public class BankInfoDetailController {

    private final BankInfoDetailRepository repository;
    private final PdfService pdfService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public BankInfoDetailController(BankInfoDetailRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Bank Account Info for DataTable")
    @GetMapping
    public DataTableResponse<BankInfoDetail> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<BankInfoDetail> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchBankInfoDetails(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findBankInfoDetails(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Bank Account Info by ID")
    @GetMapping("/{id}")
    public ApiResponse<BankInfoDetail> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Bank info detail retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Bank info detail not found"));
    }

    @Operation(summary = "Create Bank Account Info")
    @PostMapping
    public ApiResponse<BankInfoDetail> create(@RequestBody BankInfoDetail info) {
        if (info.getCompanyId() == null) {
            return ApiResponse.error("Company ID is required");
        }
        if (info.getAccNumber() == null || info.getAccNumber().trim().isEmpty()) {
            return ApiResponse.error("Account number is required");
        }
        BankInfoDetail saved = repository.save(info);
        return ApiResponse.ok("Bank account info created successfully", saved);
    }

    @Operation(summary = "Update Bank Account Info")
    @PutMapping("/{id}")
    public ApiResponse<BankInfoDetail> update(@PathVariable Long id, @RequestBody BankInfoDetail details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getBankId() != null) existing.setBankId(details.getBankId());
                    if (details.getBankBranchId() != null) existing.setBankBranchId(details.getBankBranchId());
                    if (details.getAccName() != null) existing.setAccName(details.getAccName());
                    if (details.getAccNumber() != null) existing.setAccNumber(details.getAccNumber());
                    if (details.getRoutingNo() != null) existing.setRoutingNo(details.getRoutingNo());
                    if (details.getSwiftCode() != null) existing.setSwiftCode(details.getSwiftCode());
                    if (details.getAddress() != null) existing.setAddress(details.getAddress());
                    if (details.getDate() != null) existing.setDate(details.getDate());
                    BankInfoDetail updated = repository.save(existing);
                    return ApiResponse.ok("Bank account info updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Bank info detail not found"));
    }

    @Operation(summary = "Delete Bank Account Info (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Bank account info deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Bank info detail not found"));
    }

    @Operation(summary = "Download Bank Account Info List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<BankInfoDetail> list = (search != null && !search.trim().isEmpty())
                ? repository.searchBankInfoDetails(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findBankInfoDetails(companyId, PageRequest.of(0, 1000)).getContent();

        String[] headers = {"#", "Date", "Account Name", "Account Number", "Bank", "Branch", "Routing No", "Swift Code"};
        float[] widths = {0.8f, 2.0f, 3.0f, 3.0f, 3.0f, 2.5f, 2.0f, 2.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (BankInfoDetail b : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    b.getDate() != null ? b.getDate().format(DATE_FMT) : "-",
                    b.getAccName() != null ? b.getAccName() : "-",
                    b.getAccNumber() != null ? b.getAccNumber() : "-",
                    b.getBank() != null ? b.getBank().getName() : "-",
                    b.getBankBranch() != null ? b.getBankBranch().getName() : "-",
                    b.getRoutingNo() != null ? b.getRoutingNo() : "-",
                    b.getSwiftCode() != null ? b.getSwiftCode() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Payment Information - Bank Account Info", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bank_account_info.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
