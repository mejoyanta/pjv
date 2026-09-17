package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.Payment;
import com.tax.vat.repository.PaymentRepository;
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
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment", description = "Endpoints for Payment CRUD operations")
public class PaymentController {

    private final PaymentRepository repository;
    private final PdfService pdfService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public PaymentController(PaymentRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Payments for DataTable")
    @GetMapping
    public DataTableResponse<Payment> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<Payment> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchPayments(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findPayments(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Payment by ID")
    @GetMapping("/{id}")
    public ApiResponse<Payment> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Payment retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Payment not found"));
    }

    @Operation(summary = "Create Payment")
    @PostMapping
    public ApiResponse<Payment> create(@RequestBody Payment payment) {
        if (payment.getAmount() == null && payment.getPaidAmount() == null) {
            return ApiResponse.error("Amount is required");
        }
        if (payment.getPaidAmount() == null) {
            payment.setPaidAmount(payment.getAmount());
        }
        Payment saved = repository.save(payment);
        return ApiResponse.ok("Payment created successfully", saved);
    }

    @Operation(summary = "Update Payment")
    @PutMapping("/{id}")
    public ApiResponse<Payment> update(@PathVariable Long id, @RequestBody Payment details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getTranId() != null) existing.setTranId(details.getTranId());
                    if (details.getAmount() != null) existing.setAmount(details.getAmount());
                    if (details.getPaidAmount() != null) existing.setPaidAmount(details.getPaidAmount());
                    if (details.getDueAmount() != null) existing.setDueAmount(details.getDueAmount());
                    if (details.getStatus() != null) existing.setStatus(details.getStatus());
                    if (details.getPaymentType() != null) existing.setPaymentType(details.getPaymentType());
                    if (details.getPaymentMonth() != null) existing.setPaymentMonth(details.getPaymentMonth());
                    if (details.getSendingNumber() != null) existing.setSendingNumber(details.getSendingNumber());
                    if (details.getPaymentMethod() != null) existing.setPaymentMethod(details.getPaymentMethod());
                    if (details.getPaymentDetails() != null) existing.setPaymentDetails(details.getPaymentDetails());
                    Payment updated = repository.save(existing);
                    return ApiResponse.ok("Payment updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Payment not found"));
    }

    @Operation(summary = "Delete Payment")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    repository.delete(existing);
                    return ApiResponse.<Void>ok("Payment deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Payment not found"));
    }

    @Operation(summary = "Download Payment List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<Payment> list = (search != null && !search.trim().isEmpty())
                ? repository.searchPayments(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findPayments(companyId, PageRequest.of(0, 1000)).getContent();

        String[] headers = {"#", "Tran ID", "Paid Amount", "Due Amount", "Month", "Type", "Status", "Date"};
        float[] widths = {0.8f, 3.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.2f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (Payment p : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    p.getTranId() != null ? p.getTranId() : "-",
                    String.format("%.2f", p.getPaidAmount() != null ? p.getPaidAmount() : 0.0),
                    String.format("%.2f", p.getDueAmount() != null ? p.getDueAmount() : 0.0),
                    p.getPaymentMonth() != null ? p.getPaymentMonth() : "-",
                    p.getPaymentType() != null ? p.getPaymentType() : "-",
                    p.getStatus() != null ? p.getStatus() : "-",
                    p.getTranDate() != null ? p.getTranDate().format(DATE_FMT) : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Payment Information - Payments List", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payments_list.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
