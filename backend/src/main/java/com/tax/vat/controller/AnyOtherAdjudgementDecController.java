package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.AnyOtherAdjudgementDec;
import com.tax.vat.repository.AnyOtherAdjudgementDecRepository;
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
@RequestMapping({"/api/v1/adjustment-decrease", "/api/v1/adjustment-decreases"})
@Tag(name = "Any Other Decreasing Adjustments", description = "Endpoints for Any Other Decreasing Adjustments CRUD operations")
public class AnyOtherAdjudgementDecController {

    private final AnyOtherAdjudgementDecRepository repository;
    private final PdfService pdfService;

    public AnyOtherAdjudgementDecController(AnyOtherAdjudgementDecRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Decreasing Adjustments for DataTable")
    @GetMapping
    public DataTableResponse<AnyOtherAdjudgementDec> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<AnyOtherAdjudgementDec> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchAdjustments(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findAdjustments(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Decreasing Adjustment by ID")
    @GetMapping("/{id}")
    public ApiResponse<AnyOtherAdjudgementDec> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Decreasing adjustment retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Decreasing adjustment not found"));
    }

    @Operation(summary = "Create Decreasing Adjustment")
    @PostMapping
    public ApiResponse<AnyOtherAdjudgementDec> create(@RequestBody AnyOtherAdjudgementDec adj) {
        if (adj.getCompanyId() == null) {
            return ApiResponse.error("Company ID is required");
        }
        AnyOtherAdjudgementDec saved = repository.save(adj);
        return ApiResponse.ok("Decreasing adjustment created successfully", saved);
    }

    @Operation(summary = "Update Decreasing Adjustment")
    @PutMapping("/{id}")
    public ApiResponse<AnyOtherAdjudgementDec> update(@PathVariable Long id, @RequestBody AnyOtherAdjudgementDec details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getChallanNo() != null) existing.setChallanNo(details.getChallanNo());
                    if (details.getDate() != null) existing.setDate(details.getDate());
                    if (details.getShowDate() != null) existing.setShowDate(details.getShowDate());
                    if (details.getAmount() != null) existing.setAmount(details.getAmount());
                    if (details.getVat() != null) existing.setVat(details.getVat());
                    if (details.getNote() != null) existing.setNote(details.getNote());
                    AnyOtherAdjudgementDec updated = repository.save(existing);
                    return ApiResponse.ok("Decreasing adjustment updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Decreasing adjustment not found"));
    }

    @Operation(summary = "Delete Decreasing Adjustment (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Decreasing adjustment deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Decreasing adjustment not found"));
    }

    @Operation(summary = "Download Decreasing Adjustments List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<AnyOtherAdjudgementDec> list = (search != null && !search.trim().isEmpty())
                ? repository.searchAdjustments(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAdjustments(companyId, PageRequest.of(0, 1000)).getContent();

        String[] headers = {"#", "Challan No", "Date", "Amount", "VAT", "Notes"};
        float[] widths = {0.8f, 3.0f, 2.5f, 2.5f, 2.0f, 4.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (AnyOtherAdjudgementDec a : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    a.getChallanNo() != null ? a.getChallanNo() : "-",
                    a.getShowDate() != null ? a.getShowDate() : (a.getDate() != null ? a.getDate() : "-"),
                    a.getAmount() != null ? a.getAmount() : "-",
                    a.getVat() != null ? a.getVat() : "-",
                    a.getNote() != null ? a.getNote() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Payment Information - Decreasing Adjustments", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=decreasing_adjustments.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
