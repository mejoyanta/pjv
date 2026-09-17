package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.CreditInvoice;
import com.tax.vat.repository.CreditInvoiceRepository;
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
@RequestMapping("/api/v1/credit-invoices")
@Tag(name = "Credit Invoice", description = "Endpoints for Credit Invoice CRUD operations")
public class CreditInvoiceController {

    private final CreditInvoiceRepository repository;
    private final PdfService pdfService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public CreditInvoiceController(CreditInvoiceRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Credit Invoices for DataTable")
    @GetMapping
    public DataTableResponse<CreditInvoice> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<CreditInvoice> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchCreditInvoices(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findCreditInvoices(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Credit Invoice by ID")
    @GetMapping("/{id}")
    public ApiResponse<CreditInvoice> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Credit invoice retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Credit invoice not found"));
    }

    @Operation(summary = "Create Credit Invoice")
    @PostMapping
    public ApiResponse<CreditInvoice> create(@RequestBody CreditInvoice invoice) {
        if (invoice.getCompanyId() == null) {
            return ApiResponse.error("Company ID is required");
        }
        if (invoice.getCreditInvoiceNo() == null || invoice.getCreditInvoiceNo().trim().isEmpty()) {
            invoice.setCreditInvoiceNo("CI-" + System.currentTimeMillis());
        }
        CreditInvoice saved = repository.save(invoice);
        return ApiResponse.ok("Credit invoice created successfully", saved);
    }

    @Operation(summary = "Update Credit Invoice")
    @PutMapping("/{id}")
    public ApiResponse<CreditInvoice> update(@PathVariable Long id, @RequestBody CreditInvoice details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getBuyerName() != null) existing.setBuyerName(details.getBuyerName());
                    if (details.getBuyerAddress() != null) existing.setBuyerAddress(details.getBuyerAddress());
                    if (details.getViNo() != null) existing.setViNo(details.getViNo());
                    if (details.getTotalPrice() != null) existing.setTotalPrice(details.getTotalPrice());
                    if (details.getVatAmount() != null) existing.setVatAmount(details.getVatAmount());
                    if (details.getDate() != null) existing.setDate(details.getDate());
                    if (details.getPaymentStatus() != null) existing.setPaymentStatus(details.getPaymentStatus());
                    if (details.getCreditInvoiceNo() != null) existing.setCreditInvoiceNo(details.getCreditInvoiceNo());
                    CreditInvoice updated = repository.save(existing);
                    return ApiResponse.ok("Credit invoice updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Credit invoice not found"));
    }

    @Operation(summary = "Delete Credit Invoice (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Credit invoice deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Credit invoice not found"));
    }

    @Operation(summary = "Download Credit Invoice List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<CreditInvoice> list = (search != null && !search.trim().isEmpty())
                ? repository.searchCreditInvoices(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findCreditInvoices(companyId, PageRequest.of(0, 1000)).getContent();

        String[] headers = {"#", "Date", "Credit Invoice No", "VI No", "Buyer Name", "VAT Amount", "Total Price", "Status"};
        float[] widths = {0.8f, 2.2f, 3.0f, 2.0f, 3.0f, 2.0f, 2.0f, 1.8f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (CreditInvoice c : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    c.getDate() != null ? c.getDate().format(DATE_FMT) : "-",
                    c.getCreditInvoiceNo() != null ? c.getCreditInvoiceNo() : "-",
                    c.getViNo() != null ? c.getViNo() : "-",
                    c.getBuyerName() != null ? c.getBuyerName() : "-",
                    String.format("%.2f", c.getVatAmount() != null ? c.getVatAmount() : 0.0),
                    String.format("%.2f", c.getTotalPrice() != null ? c.getTotalPrice() : 0.0),
                    c.getPaymentStatus() != null ? c.getPaymentStatus() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Payment Information - Credit Invoices", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=credit_invoices.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
