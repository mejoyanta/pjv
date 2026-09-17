package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.RentVat;
import com.tax.vat.repository.RentVatRepository;
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
@RequestMapping({"/api/v1/rent-vat", "/api/v1/rent-vats"})
@Tag(name = "Any Other Increasing Adjustments", description = "Endpoints for Any Other Increasing Adjustments (Rent Vat) CRUD operations")
public class RentVatController {

    private final RentVatRepository repository;
    private final PdfService pdfService;

    public RentVatController(RentVatRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Rent Vats for DataTable")
    @GetMapping
    public DataTableResponse<RentVat> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<RentVat> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchRentVats(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findRentVats(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Rent Vat by ID")
    @GetMapping("/{id}")
    public ApiResponse<RentVat> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Rent vat retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Rent vat not found"));
    }

    @Operation(summary = "Create Rent Vat")
    @PostMapping
    public ApiResponse<RentVat> create(@RequestBody RentVat rentVat) {
        if (rentVat.getCompanyId() == null) {
            return ApiResponse.error("Company ID is required");
        }
        RentVat saved = repository.save(rentVat);
        return ApiResponse.ok("Increasing adjustment created successfully", saved);
    }

    @Operation(summary = "Update Rent Vat")
    @PutMapping("/{id}")
    public ApiResponse<RentVat> update(@PathVariable Long id, @RequestBody RentVat details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getChallanNo() != null) existing.setChallanNo(details.getChallanNo());
                    if (details.getDate() != null) existing.setDate(details.getDate());
                    if (details.getShowDate() != null) existing.setShowDate(details.getShowDate());
                    if (details.getAmount() != null) existing.setAmount(details.getAmount());
                    if (details.getVat() != null) existing.setVat(details.getVat());
                    if (details.getNote() != null) existing.setNote(details.getNote());
                    RentVat updated = repository.save(existing);
                    return ApiResponse.ok("Increasing adjustment updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Rent vat not found"));
    }

    @Operation(summary = "Delete Rent Vat (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Increasing adjustment deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Rent vat not found"));
    }

    @Operation(summary = "Download Increasing Adjustments List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<RentVat> list = (search != null && !search.trim().isEmpty())
                ? repository.searchRentVats(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findRentVats(companyId, PageRequest.of(0, 1000)).getContent();

        String[] headers = {"#", "Challan No", "Date", "Amount", "VAT", "Note"};
        float[] widths = {0.8f, 3.0f, 2.5f, 2.5f, 2.0f, 4.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (RentVat r : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    r.getChallanNo() != null ? r.getChallanNo() : "-",
                    r.getShowDate() != null ? r.getShowDate() : (r.getDate() != null ? r.getDate() : "-"),
                    r.getAmount() != null ? r.getAmount() : "-",
                    r.getVat() != null ? r.getVat() : "-",
                    r.getNote() != null ? r.getNote() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Payment Information - Increasing Adjustments (Rent Vat)", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=increasing_adjustments.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
