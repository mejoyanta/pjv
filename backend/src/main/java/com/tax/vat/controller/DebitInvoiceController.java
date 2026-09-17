package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.DebitInvoice;
import com.tax.vat.repository.DebitInvoiceRepository;
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
@RequestMapping("/api/v1/debit-invoices")
@Tag(name = "Debit Invoice", description = "Endpoints for Debit Invoice CRUD operations")
public class DebitInvoiceController {

    private final DebitInvoiceRepository repository;
    private final PdfService pdfService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public DebitInvoiceController(DebitInvoiceRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Debit Invoices for DataTable")
    @GetMapping
    public DataTableResponse<DebitInvoice> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<DebitInvoice> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchDebitInvoices(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findDebitInvoices(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Debit Invoice by ID")
    @GetMapping("/{id}")
    public ApiResponse<DebitInvoice> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Debit invoice retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Debit invoice not found"));
    }

    @Operation(summary = "Create Debit Invoice")
    @PostMapping
    public ApiResponse<DebitInvoice> create(@RequestBody DebitInvoice invoice) {
        if (invoice.getCompanyId() == null) {
            return ApiResponse.error("Company ID is required");
        }
        if (invoice.getDebitInvoiceNo() == null || invoice.getDebitInvoiceNo().trim().isEmpty()) {
            invoice.setDebitInvoiceNo("DI-" + System.currentTimeMillis());
        }
        DebitInvoice saved = repository.save(invoice);
        return ApiResponse.ok("Debit invoice created successfully", saved);
    }

    @Operation(summary = "Update Debit Invoice")
    @PutMapping("/{id}")
    public ApiResponse<DebitInvoice> update(@PathVariable Long id, @RequestBody DebitInvoice details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getSeller() != null) existing.setSeller(details.getSeller());
                    if (details.getSellerAddress() != null) existing.setSellerAddress(details.getSellerAddress());
                    if (details.getBillOfEntry() != null) existing.setBillOfEntry(details.getBillOfEntry());
                    if (details.getBasePrice() != null) existing.setBasePrice(details.getBasePrice());
                    if (details.getVatAmount() != null) existing.setVatAmount(details.getVatAmount());
                    if (details.getTotalAmount() != null) existing.setTotalAmount(details.getTotalAmount());
                    if (details.getPaidAmount() != null) existing.setPaidAmount(details.getPaidAmount());
                    if (details.getDueAmount() != null) existing.setDueAmount(details.getDueAmount());
                    if (details.getDate() != null) existing.setDate(details.getDate());
                    if (details.getPaymentStatus() != null) existing.setPaymentStatus(details.getPaymentStatus());
                    if (details.getDebitInvoiceNo() != null) existing.setDebitInvoiceNo(details.getDebitInvoiceNo());
                    DebitInvoice updated = repository.save(existing);
                    return ApiResponse.ok("Debit invoice updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Debit invoice not found"));
    }

    @Operation(summary = "Delete Debit Invoice (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Debit invoice deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Debit invoice not found"));
    }

    @Operation(summary = "Download Debit Invoice List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<DebitInvoice> list = (search != null && !search.trim().isEmpty())
                ? repository.searchDebitInvoices(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findDebitInvoices(companyId, PageRequest.of(0, 1000)).getContent();

        String[] headers = {"#", "Date", "Debit Invoice No", "Bill of Entry", "Seller", "Price", "VAT", "Total", "Paid", "Due", "Status"};
        float[] widths = {0.8f, 2.0f, 2.5f, 2.0f, 2.5f, 1.8f, 1.5f, 1.8f, 1.8f, 1.8f, 1.5f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (DebitInvoice d : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    d.getDate() != null ? d.getDate().format(DATE_FMT) : "-",
                    d.getDebitInvoiceNo() != null ? d.getDebitInvoiceNo() : "-",
                    d.getBillOfEntry() != null ? d.getBillOfEntry() : "-",
                    d.getSeller() != null ? d.getSeller() : "-",
                    String.format("%.2f", d.getBasePrice() != null ? d.getBasePrice() : 0.0),
                    String.format("%.2f", d.getVatAmount() != null ? d.getVatAmount() : 0.0),
                    String.format("%.2f", d.getTotalAmount() != null ? d.getTotalAmount() : 0.0),
                    String.format("%.2f", d.getPaidAmount() != null ? d.getPaidAmount() : 0.0),
                    String.format("%.2f", d.getDueAmount() != null ? d.getDueAmount() : 0.0),
                    d.getPaymentStatus() != null ? d.getPaymentStatus() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Payment Information - Debit Invoices", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=debit_invoices.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
