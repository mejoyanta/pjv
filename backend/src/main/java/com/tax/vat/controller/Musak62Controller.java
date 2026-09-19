package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.dto.response.Musak62DataTableItem;
import com.tax.vat.entity.Musak62;
import com.tax.vat.entity.Sale;
import com.tax.vat.repository.CompanyBranchRepository;
import com.tax.vat.repository.CompanyRepository;
import com.tax.vat.repository.Musak62Repository;
import com.tax.vat.repository.ProductRepository;
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
@RequestMapping({"/api/v1/mushak-6-2", "/api/v1/musak-6-2"})
@Tag(name = "Mushak 6.2", description = "Endpoints for Mushak 6.2 Form Operations")
public class Musak62Controller {

    private final Musak62Repository musak62Repository;
    private final CompanyRepository companyRepository;
    private final CompanyBranchRepository companyBranchRepository;
    private final ProductRepository productRepository;
    private final PdfService pdfService;

    public Musak62Controller(
            Musak62Repository musak62Repository,
            CompanyRepository companyRepository,
            CompanyBranchRepository companyBranchRepository,
            ProductRepository productRepository,
            PdfService pdfService
    ) {
        this.musak62Repository = musak62Repository;
        this.companyRepository = companyRepository;
        this.companyBranchRepository = companyBranchRepository;
        this.productRepository = productRepository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Mushak 6.2 DataTable")
    @GetMapping
    public DataTableResponse<Musak62DataTableItem> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, Math.min(request.getLength(), 100), Sort.by(Sort.Direction.DESC, "id"));

        Page<Musak62> result = musak62Repository.findFiltered(
                companyId,
                productId,
                fromDate,
                toDate,
                request.getSearchValue() != null ? request.getSearchValue().trim() : null,
                pageable
        );

        int serial = request.getStart() + 1;
        List<Musak62DataTableItem> items = new ArrayList<>();
        for (Musak62 m : result.getContent()) {
            Musak62DataTableItem item = new Musak62DataTableItem();
            item.setId(m.getId());
            item.setSlug(m.getSlug());
            item.setSn(serial++);
            item.setDate(m.getSaleDate() != null ? m.getSaleDate() : m.getPurchaseDate());

            Sale saleObj = m.getSale() != null ? m.getSale() : (m.getMusak63() != null ? m.getMusak63().getSale() : null);
            if (saleObj != null) {
                item.setBuyer(saleObj.getBuyerName());
                item.setVatAmount(saleObj.getVatAmount());
                item.setSale(saleObj.getTotalSaleAmount());
            } else {
                item.setBuyer("N/A");
                item.setVatAmount(0.0);
                item.setSale(0.0);
            }

            if (m.getMusak63() != null) {
                item.setViNo(m.getMusak63().getViNo());
            } else {
                item.setViNo(null);
            }

            item.setCompanyName(m.getCompany() != null ? m.getCompany().getName() : "—");
            item.setProductName(m.getProduct() != null ? m.getProduct().getName() : "—");
            item.setOpeningStock(m.getOpeningStock());
            item.setClosingStock(m.getClosingStock());

            items.add(item);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), items);
    }

    @Operation(summary = "Get single Mushak 6.2 by ID")
    @GetMapping("/{id}")
    public ApiResponse<Musak62> getById(@PathVariable Long id) {
        return musak62Repository.findById(id)
                .map(item -> ApiResponse.ok("Mushak 6.2 found", item))
                .orElseGet(() -> ApiResponse.error("Mushak 6.2 record not found"));
    }

    @Operation(summary = "Get single Mushak 6.2 by Slug")
    @GetMapping("/slug/{slug}")
    public ApiResponse<Musak62> getBySlug(@PathVariable String slug) {
        return musak62Repository.findBySlug(slug)
                .map(item -> ApiResponse.ok("Mushak 6.2 found", item))
                .orElseGet(() -> ApiResponse.error("Mushak 6.2 record not found"));
    }

    @Operation(summary = "Get form data options for Mushak 6.2")
    @GetMapping("/form-data")
    public ApiResponse<Map<String, Object>> getFormData(@RequestParam(required = false) Long companyId) {
        Map<String, Object> data = new HashMap<>();
        data.put("companies", companyRepository.findAllActiveCompanies());
        data.put("branches", companyBranchRepository.findAll());
        data.put("products", productRepository.findAllActiveProducts(companyId));
        return ApiResponse.ok("Form data loaded successfully", data);
    }

    @Operation(summary = "Create Mushak 6.2")
    @PostMapping
    public ApiResponse<Musak62> create(@RequestBody Musak62 input) {
        if (input.getSlug() == null || input.getSlug().trim().isEmpty()) {
            input.setSlug(UUID.randomUUID().toString());
        }
        input.setCreatedAt(LocalDateTime.now());
        input.setUpdatedAt(LocalDateTime.now());
        Musak62 saved = musak62Repository.save(input);
        return ApiResponse.ok("Mushak 6.2 created successfully", saved);
    }

    @Operation(summary = "Update Mushak 6.2")
    @PutMapping("/{id}")
    public ApiResponse<Musak62> update(@PathVariable Long id, @RequestBody Musak62 input) {
        return musak62Repository.findById(id).map(existing -> {
            existing.setPurchaseDate(input.getPurchaseDate());
            existing.setSaleDate(input.getSaleDate());
            existing.setCompanyId(input.getCompanyId());
            existing.setCompanyBranchId(input.getCompanyBranchId());
            existing.setProductId(input.getProductId());
            existing.setPurchaseId(input.getPurchaseId());
            existing.setSaleId(input.getSaleId());
            existing.setMusak63Id(input.getMusak63Id());
            existing.setOpeningStock(input.getOpeningStock());
            existing.setClosingStock(input.getClosingStock());
            existing.setUpdatedAt(LocalDateTime.now());
            Musak62 updated = musak62Repository.save(existing);
            return ApiResponse.ok("Mushak 6.2 updated successfully", updated);
        }).orElseGet(() -> ApiResponse.error("Mushak 6.2 record not found"));
    }

    @Operation(summary = "Soft delete Mushak 6.2")
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        return musak62Repository.findById(id).map(item -> {
            item.setDeletedAt(LocalDateTime.now());
            musak62Repository.save(item);
            return ApiResponse.ok("Mushak 6.2 deleted successfully", "Deleted ID: " + id);
        }).orElseGet(() -> ApiResponse.error("Mushak 6.2 record not found"));
    }

    @Operation(summary = "Download Mushak 6.2 PDF")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Musak62 m = musak62Repository.findById(id).orElse(null);
        if (m == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] pdfBytes = pdfService.generateMusak62Pdf(m);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mushak_6_2_" + m.getId() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
