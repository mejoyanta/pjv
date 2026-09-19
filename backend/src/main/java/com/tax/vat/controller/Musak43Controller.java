package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.dto.response.Musak43DataTableItem;
import com.tax.vat.entity.Musak43;
import com.tax.vat.repository.CompanyBranchRepository;
import com.tax.vat.repository.CompanyRepository;
import com.tax.vat.repository.Musak43Repository;
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
@RequestMapping({"/api/v1/mushak-4-3", "/api/v1/musak-4-3"})
@Tag(name = "Mushak 4.3", description = "Endpoints for Mushak 4.3 Form Operations")
public class Musak43Controller {

    private final Musak43Repository musak43Repository;
    private final CompanyRepository companyRepository;
    private final CompanyBranchRepository companyBranchRepository;
    private final ProductRepository productRepository;
    private final PdfService pdfService;

    public Musak43Controller(
            Musak43Repository musak43Repository,
            CompanyRepository companyRepository,
            CompanyBranchRepository companyBranchRepository,
            ProductRepository productRepository,
            PdfService pdfService
    ) {
        this.musak43Repository = musak43Repository;
        this.companyRepository = companyRepository;
        this.companyBranchRepository = companyBranchRepository;
        this.productRepository = productRepository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Mushak 4.3 DataTable")
    @GetMapping
    public DataTableResponse<Musak43DataTableItem> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, Math.min(request.getLength(), 100), Sort.by(Sort.Direction.DESC, "id"));

        Page<Musak43> result = musak43Repository.findFiltered(
                companyId,
                productId,
                fromDate,
                toDate,
                request.getSearchValue() != null ? request.getSearchValue().trim() : null,
                pageable
        );

        int serial = request.getStart() + 1;
        List<Musak43DataTableItem> items = new ArrayList<>();
        for (Musak43 m : result.getContent()) {
            Musak43DataTableItem item = new Musak43DataTableItem();
            item.setId(m.getId());
            item.setSlug(m.getSlug());
            item.setSn(serial++);
            item.setSubmissionId(m.getSubmissionId());
            item.setSubmissionDate(m.getSubmissionDate());

            if (m.getPurchase() != null) {
                item.setPurchaseShowDate(m.getPurchase().getPurchaseShowDate() != null ? m.getPurchase().getPurchaseShowDate() : m.getPurchase().getDate());
                item.setBoeDate(m.getPurchase().getDate());
                item.setBillOfEntry(m.getPurchase().getBillOfEntry());
            } else {
                item.setPurchaseShowDate(m.getDate());
                item.setBoeDate(m.getDate());
                item.setBillOfEntry("N/A");
            }

            item.setCompanyName(m.getCompany() != null ? m.getCompany().getName() : "—");
            item.setBranchName(m.getCompanyBranch() != null ? m.getCompanyBranch().getName() : "N/A");
            item.setProductServiceDetails(m.getProductServiceDetails() != null ? m.getProductServiceDetails() : (m.getProduct() != null ? m.getProduct().getName() : "N/A"));
            item.setHsCode(m.getHsCode() != null ? m.getHsCode() : (m.getProduct() != null ? m.getProduct().getHsCode() : "N/A"));
            item.setUnit(m.getUnit() != null ? m.getUnit().getName() : "N/A");
            item.setPurchasePrice(m.getBasePrice() != null ? m.getBasePrice() : 0.0);
            item.setQtyCost(m.getPurchaseQuantity() != null ? m.getPurchaseQuantity() : 0.0);
            item.setAdditionalCost(m.getTotalAdditionalCost() != null ? m.getTotalAdditionalCost() : 0.0);

            double profitVal = 0.0;
            if (m.getProfit() != null) {
                try {
                    profitVal = Double.parseDouble(m.getProfit());
                } catch (Exception ignored) {}
            }
            item.setProfit(profitVal);

            item.setSalePrice(m.getSellPrice() != null ? m.getSellPrice() : 0.0);
            item.setWholesalePrice(m.getHdWholesaleRate() != null ? m.getHdWholesaleRate() : 0.0);
            item.setRetailerAmount(m.getHdRetailerAmount() != null ? m.getHdRetailerAmount() : 0.0);

            items.add(item);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), items);
    }

    @Operation(summary = "Get single Mushak 4.3 by ID")
    @GetMapping("/{id}")
    public ApiResponse<Musak43> getById(@PathVariable Long id) {
        return musak43Repository.findById(id)
                .map(item -> ApiResponse.ok("Mushak 4.3 found", item))
                .orElseGet(() -> ApiResponse.error("Mushak 4.3 record not found"));
    }

    @Operation(summary = "Get single Mushak 4.3 by Slug")
    @GetMapping("/slug/{slug}")
    public ApiResponse<Musak43> getBySlug(@PathVariable String slug) {
        return musak43Repository.findBySlug(slug)
                .map(item -> ApiResponse.ok("Mushak 4.3 found", item))
                .orElseGet(() -> ApiResponse.error("Mushak 4.3 record not found"));
    }

    @Operation(summary = "Get form data options for Mushak 4.3")
    @GetMapping("/form-data")
    public ApiResponse<Map<String, Object>> getFormData(@RequestParam(required = false) Long companyId) {
        Map<String, Object> data = new HashMap<>();
        data.put("companies", companyRepository.findAllActiveCompanies());
        data.put("branches", companyBranchRepository.findAll());
        data.put("products", productRepository.findAllActiveProducts(companyId));
        return ApiResponse.ok("Form data loaded successfully", data);
    }

    @Operation(summary = "Create Mushak 4.3")
    @PostMapping
    public ApiResponse<Musak43> create(@RequestBody Musak43 input) {
        if (input.getSlug() == null || input.getSlug().trim().isEmpty()) {
            input.setSlug(UUID.randomUUID().toString());
        }
        if (input.getDate() == null) {
            input.setDate(LocalDate.now());
        }
        input.setCreatedAt(LocalDateTime.now());
        input.setUpdatedAt(LocalDateTime.now());
        Musak43 saved = musak43Repository.save(input);
        return ApiResponse.ok("Mushak 4.3 created successfully", saved);
    }

    @Operation(summary = "Update Mushak 4.3")
    @PutMapping("/{id}")
    public ApiResponse<Musak43> update(@PathVariable Long id, @RequestBody Musak43 input) {
        return musak43Repository.findById(id).map(existing -> {
            if (input.getDate() != null) existing.setDate(input.getDate());
            if (input.getSubmissionDate() != null) existing.setSubmissionDate(input.getSubmissionDate());
            if (input.getSubmissionId() != null) existing.setSubmissionId(input.getSubmissionId());
            if (input.getCompanyId() != null) existing.setCompanyId(input.getCompanyId());
            if (input.getCompanyBranchId() != null) existing.setCompanyBranchId(input.getCompanyBranchId());
            if (input.getProductId() != null) existing.setProductId(input.getProductId());
            if (input.getProductServiceDetails() != null) existing.setProductServiceDetails(input.getProductServiceDetails());
            if (input.getHsCode() != null) existing.setHsCode(input.getHsCode());
            if (input.getBasePrice() != null) existing.setBasePrice(input.getBasePrice());
            if (input.getPurchaseQuantity() != null) existing.setPurchaseQuantity(input.getPurchaseQuantity());
            if (input.getTotalAdditionalCost() != null) existing.setTotalAdditionalCost(input.getTotalAdditionalCost());
            if (input.getProfit() != null) existing.setProfit(input.getProfit());
            if (input.getSellPrice() != null) existing.setSellPrice(input.getSellPrice());
            if (input.getHdWholesaleRate() != null) existing.setHdWholesaleRate(input.getHdWholesaleRate());
            if (input.getHdRetailerAmount() != null) existing.setHdRetailerAmount(input.getHdRetailerAmount());
            if (input.getVatAmount() != null) existing.setVatAmount(input.getVatAmount());
            if (input.getTotal() != null) existing.setTotal(input.getTotal());
            if (input.getAmendmentComment() != null) existing.setAmendmentComment(input.getAmendmentComment());
            existing.setUpdatedAt(LocalDateTime.now());
            Musak43 updated = musak43Repository.save(existing);
            return ApiResponse.ok("Mushak 4.3 updated successfully", updated);
        }).orElseGet(() -> ApiResponse.error("Mushak 4.3 record not found"));
    }

    @Operation(summary = "Soft delete Mushak 4.3")
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        return musak43Repository.findById(id).map(item -> {
            item.setDeletedAt(LocalDateTime.now());
            musak43Repository.save(item);
            return ApiResponse.ok("Mushak 4.3 deleted successfully", "Deleted ID: " + id);
        }).orElseGet(() -> ApiResponse.error("Mushak 4.3 record not found"));
    }

    @Operation(summary = "Download Mushak 4.3 PDF")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Musak43 m = musak43Repository.findById(id).orElse(null);
        if (m == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] pdfBytes = pdfService.generateMusak43Pdf(m);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mushak_4_3_" + m.getId() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
