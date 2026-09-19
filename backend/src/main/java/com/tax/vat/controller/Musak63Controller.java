package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.dto.response.Musak63DataTableItem;
import com.tax.vat.entity.Musak63;
import com.tax.vat.repository.CompanyBranchRepository;
import com.tax.vat.repository.CompanyRepository;
import com.tax.vat.repository.Musak63Repository;
import com.tax.vat.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping({"/api/v1/mushak-6-3", "/api/v1/musak-6-3"})
@Tag(name = "Mushak 6.3", description = "Endpoints for Mushak 6.3 Form Operations")
public class Musak63Controller {

    private final Musak63Repository musak63Repository;
    private final CompanyRepository companyRepository;
    private final CompanyBranchRepository companyBranchRepository;
    private final PdfService pdfService;

    public Musak63Controller(
            Musak63Repository musak63Repository,
            CompanyRepository companyRepository,
            CompanyBranchRepository companyBranchRepository,
            PdfService pdfService
    ) {
        this.musak63Repository = musak63Repository;
        this.companyRepository = companyRepository;
        this.companyBranchRepository = companyBranchRepository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Mushak 6.3 DataTable")
    @GetMapping
    public DataTableResponse<Musak63DataTableItem> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, Math.min(request.getLength(), 100), Sort.by(Sort.Direction.DESC, "id"));

        Page<Musak63> result = musak63Repository.findFiltered(
                companyId,
                fromDate,
                toDate,
                request.getSearchValue() != null ? request.getSearchValue().trim() : null,
                pageable
        );

        int serial = request.getStart() + 1;
        List<Musak63DataTableItem> items = new ArrayList<>();
        for (Musak63 m : result.getContent()) {
            Musak63DataTableItem item = new Musak63DataTableItem();
            item.setId(m.getId());
            item.setSlug(m.getSlug());
            item.setSn(serial++);
            item.setDate(m.getDate());
            item.setViNo(m.getViNo());
            item.setCompanyName(m.getCompany() != null ? m.getCompany().getName() : "—");
            item.setBranchName(m.getCompanyBranch() != null ? m.getCompanyBranch().getName() : "N/A");

            if (m.getSale() != null) {
                item.setBuyer(m.getSale().getBuyerName());
                item.setBuyerBin(m.getSale().getBuyerBinTinNid());
                item.setBuyerAddress(m.getSale().getBuyerAddress());
                item.setSubTotal(m.getSale().getTotalSaleAmount());
                item.setVatAmount(m.getSale().getVatAmount());
                item.setTotalAmount(m.getSale().getTotalSaleAmount());
                item.setSaleTypeIdentify(m.getSale().getSalesType());
                item.setPaymentStatus(m.getSale().getPaymentType());
            } else {
                item.setSubTotal(0.0);
                item.setVatAmount(0.0);
                item.setTotalAmount(0.0);
            }

            item.setIsThisCreditNote(m.getAmendment() != null ? m.getAmendment() : false);
            items.add(item);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), items);
    }

    @Operation(summary = "Get single Mushak 6.3 by ID")
    @GetMapping("/{id}")
    public ApiResponse<Musak63> getById(@PathVariable Long id) {
        return musak63Repository.findById(id)
                .map(item -> ApiResponse.ok("Mushak 6.3 found", item))
                .orElseGet(() -> ApiResponse.error("Mushak 6.3 record not found"));
    }

    @Operation(summary = "Get single Mushak 6.3 by Slug")
    @GetMapping("/slug/{slug}")
    public ApiResponse<Musak63> getBySlug(@PathVariable String slug) {
        return musak63Repository.findBySlug(slug)
                .map(item -> ApiResponse.ok("Mushak 6.3 found", item))
                .orElseGet(() -> ApiResponse.error("Mushak 6.3 record not found"));
    }

    @Operation(summary = "Get form data options for Mushak 6.3")
    @GetMapping("/form-data")
    public ApiResponse<Map<String, Object>> getFormData(@RequestParam(required = false) Long companyId) {
        Map<String, Object> data = new HashMap<>();
        data.put("companies", companyRepository.findAllActiveCompanies());
        data.put("branches", companyBranchRepository.findAll());
        return ApiResponse.ok("Form data loaded successfully", data);
    }

    @Operation(summary = "Create Mushak 6.3")
    @PostMapping
    public ApiResponse<Musak63> create(@RequestBody Musak63 input) {
        if (input.getSlug() == null || input.getSlug().trim().isEmpty()) {
            input.setSlug(UUID.randomUUID().toString());
        }
        if (input.getDate() == null) {
            input.setDate(LocalDate.now());
        }
        input.setCreatedAt(LocalDateTime.now());
        input.setUpdatedAt(LocalDateTime.now());
        Musak63 saved = musak63Repository.save(input);
        return ApiResponse.ok("Mushak 6.3 created successfully", saved);
    }

    @Operation(summary = "Update Mushak 6.3")
    @PutMapping("/{id}")
    public ApiResponse<Musak63> update(@PathVariable Long id, @RequestBody Musak63 input) {
        return musak63Repository.findById(id).map(existing -> {
            if (input.getDate() != null) existing.setDate(input.getDate());
            if (input.getViNo() != null) existing.setViNo(input.getViNo());
            if (input.getCompanyId() != null) existing.setCompanyId(input.getCompanyId());
            if (input.getCompanyBranchId() != null) existing.setCompanyBranchId(input.getCompanyBranchId());
            if (input.getPaymentStatus() != null) existing.setPaymentStatus(input.getPaymentStatus());
            if (input.getComments() != null) existing.setComments(input.getComments());
            if (input.getBuyerType() != null) existing.setBuyerType(input.getBuyerType());
            if (input.getRejectionReason() != null) existing.setRejectionReason(input.getRejectionReason());
            existing.setUpdatedAt(LocalDateTime.now());
            Musak63 updated = musak63Repository.save(existing);
            return ApiResponse.ok("Mushak 6.3 updated successfully", updated);
        }).orElseGet(() -> ApiResponse.error("Mushak 6.3 record not found"));
    }

    @Operation(summary = "Update Mushak 6.3 CR / Payment Status")
    @PostMapping("/{id}/status")
    public ApiResponse<Musak63> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String reason
    ) {
        return musak63Repository.findById(id).map(existing -> {
            existing.setPaymentStatus(status);
            if (reason != null) existing.setRejectionReason(reason);
            existing.setUpdatedAt(LocalDateTime.now());
            Musak63 updated = musak63Repository.save(existing);
            return ApiResponse.ok("Mushak 6.3 status updated successfully", updated);
        }).orElseGet(() -> ApiResponse.error("Mushak 6.3 record not found"));
    }

    @Operation(summary = "Soft delete Mushak 6.3")
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        return musak63Repository.findById(id).map(item -> {
            item.setDeletedAt(LocalDateTime.now());
            musak63Repository.save(item);
            return ApiResponse.ok("Mushak 6.3 deleted successfully", "Deleted ID: " + id);
        }).orElseGet(() -> ApiResponse.error("Mushak 6.3 record not found"));
    }

    @Operation(summary = "Download Mushak 6.3 PDF")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Musak63 m = musak63Repository.findById(id).orElse(null);
        if (m == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] pdfBytes = pdfService.generateMusak63Pdf(m);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mushak_6_3_" + m.getId() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
