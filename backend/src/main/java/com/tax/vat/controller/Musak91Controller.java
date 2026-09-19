package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.dto.response.Musak91DataTableItem;
import com.tax.vat.entity.Musak91;
import com.tax.vat.repository.CompanyBranchRepository;
import com.tax.vat.repository.CompanyRepository;
import com.tax.vat.repository.Musak91Repository;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping({"/api/v1/mushak-9-1", "/api/v1/musak-9-1"})
@Tag(name = "Mushak 9.1", description = "Endpoints for Mushak 9.1 VAT Return Operations")
public class Musak91Controller {

    private final Musak91Repository musak91Repository;
    private final CompanyRepository companyRepository;
    private final CompanyBranchRepository companyBranchRepository;
    private final PdfService pdfService;

    public Musak91Controller(
            Musak91Repository musak91Repository,
            CompanyRepository companyRepository,
            CompanyBranchRepository companyBranchRepository,
            PdfService pdfService
    ) {
        this.musak91Repository = musak91Repository;
        this.companyRepository = companyRepository;
        this.companyBranchRepository = companyBranchRepository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Mushak 9.1 DataTable")
    @GetMapping
    public DataTableResponse<Musak91DataTableItem> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, Math.min(request.getLength(), 100), Sort.by(Sort.Direction.DESC, "date", "id"));

        Page<Musak91> result = musak91Repository.findFiltered(
                companyId,
                fromDate,
                toDate,
                request.getSearchValue() != null ? request.getSearchValue().trim() : null,
                pageable
        );

        DateTimeFormatter periodFormatter = DateTimeFormatter.ofPattern("MMMM, yyyy");
        int serial = request.getStart() + 1;
        List<Musak91DataTableItem> items = new ArrayList<>();
        for (Musak91 m : result.getContent()) {
            Musak91DataTableItem item = new Musak91DataTableItem();
            item.setId(m.getId());
            item.setSlug(m.getSlug());
            item.setSn(serial++);
            item.setPeriod(m.getDate() != null ? m.getDate().format(periodFormatter) : "N/A");
            item.setCompany(m.getCompany() != null ? m.getCompany().getName() : "—");
            item.setBin(m.getCompany() != null ? m.getCompany().getBin() : "—");

            if (m.getDate() != null) {
                LocalDate first = m.getDate().withDayOfMonth(1);
                LocalDate last = m.getDate().withDayOfMonth(m.getDate().lengthOfMonth());
                LocalDate due = first.plusMonths(1).withDayOfMonth(15);
                item.setPeriodFrom(first);
                item.setPeriodTo(last);
                item.setDueDate(due);
            }

            item.setSubmissionDate(m.getSubmisionDate());
            item.setReturnType(m.getReturnType() != null ? m.getReturnType() : "Main Return (Sec 64)");
            items.add(item);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), items);
    }

    @Operation(summary = "Get single Mushak 9.1 by ID")
    @GetMapping("/{id}")
    public ApiResponse<Musak91> getById(@PathVariable Long id) {
        return musak91Repository.findById(id)
                .map(item -> ApiResponse.ok("Mushak 9.1 found", item))
                .orElseGet(() -> ApiResponse.error("Mushak 9.1 record not found"));
    }

    @Operation(summary = "Get single Mushak 9.1 by Slug")
    @GetMapping("/slug/{slug}")
    public ApiResponse<Musak91> getBySlug(@PathVariable String slug) {
        return musak91Repository.findBySlug(slug)
                .map(item -> ApiResponse.ok("Mushak 9.1 found", item))
                .orElseGet(() -> ApiResponse.error("Mushak 9.1 record not found"));
    }

    @Operation(summary = "Get form data options for Mushak 9.1")
    @GetMapping("/form-data")
    public ApiResponse<Map<String, Object>> getFormData(@RequestParam(required = false) Long companyId) {
        Map<String, Object> data = new HashMap<>();
        data.put("companies", companyRepository.findAllActiveCompanies());
        data.put("branches", companyBranchRepository.findAll());
        return ApiResponse.ok("Form data loaded successfully", data);
    }

    @Operation(summary = "Create Mushak 9.1")
    @PostMapping
    public ApiResponse<Musak91> create(@RequestBody Musak91 input) {
        if (input.getSlug() == null || input.getSlug().trim().isEmpty()) {
            input.setSlug(UUID.randomUUID().toString());
        }
        if (input.getDate() == null) {
            input.setDate(LocalDate.now());
        }
        input.setCreatedAt(LocalDateTime.now());
        input.setUpdatedAt(LocalDateTime.now());
        Musak91 saved = musak91Repository.save(input);
        return ApiResponse.ok("Mushak 9.1 created successfully", saved);
    }

    @Operation(summary = "Update Mushak 9.1")
    @PutMapping("/{id}")
    public ApiResponse<Musak91> update(@PathVariable Long id, @RequestBody Musak91 input) {
        return musak91Repository.findById(id).map(existing -> {
            existing.setDate(input.getDate());
            existing.setSubmisionDate(input.getSubmisionDate());
            existing.setReturnType(input.getReturnType());
            existing.setTaxPeriodActivity(input.getTaxPeriodActivity());
            existing.setGetRefund(input.getGetRefund());
            existing.setDeclarationName(input.getDeclarationName());
            existing.setDeclarationEmail(input.getDeclarationEmail());
            existing.setDeclarationPhone(input.getDeclarationPhone());
            existing.setDeclarationDesignation(input.getDeclarationDesignation());
            existing.setDeclarationNidPassport(input.getDeclarationNidPassport());
            existing.setCompanyId(input.getCompanyId());
            existing.setInput24(input.getInput24());
            existing.setInput25(input.getInput25());
            existing.setInput26(input.getInput26());
            existing.setInput27(input.getInput27());
            existing.setInput28(input.getInput28());
            existing.setInput29(input.getInput29());
            existing.setInput30(input.getInput30());
            existing.setInput31(input.getInput31());
            existing.setInput32(input.getInput32());
            existing.setInput52(input.getInput52());
            existing.setInput53(input.getInput53());
            existing.setInput54(input.getInput54());
            existing.setInput55(input.getInput55());
            existing.setInput56(input.getInput56());
            existing.setInput57(input.getInput57());
            existing.setInput58(input.getInput58());
            existing.setInput59(input.getInput59());
            existing.setInput60(input.getInput60());
            existing.setInput61(input.getInput61());
            existing.setInput62(input.getInput62());
            existing.setInput63(input.getInput63());
            existing.setInput64(input.getInput64());
            existing.setInput65(input.getInput65());
            existing.setField66(input.getField66());
            existing.setInput67(input.getInput67());
            existing.setInput68(input.getInput68());
            existing.setUpdatedAt(LocalDateTime.now());
            Musak91 updated = musak91Repository.save(existing);
            return ApiResponse.ok("Mushak 9.1 updated successfully", updated);
        }).orElseGet(() -> ApiResponse.error("Mushak 9.1 record not found"));
    }

    @Operation(summary = "Delete Mushak 9.1")
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        return musak91Repository.findById(id).map(item -> {
            musak91Repository.delete(item);
            return ApiResponse.ok("Mushak 9.1 deleted successfully", "Deleted ID: " + id);
        }).orElseGet(() -> ApiResponse.error("Mushak 9.1 record not found"));
    }

    @Operation(summary = "Download Mushak 9.1 PDF")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Musak91 m = musak91Repository.findById(id).orElse(null);
        if (m == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] pdfBytes = pdfService.generateMusak91Pdf(m);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mushak_9_1_" + m.getId() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
