package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.Product;
import com.tax.vat.repository.ProductRepository;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product", description = "Endpoints for Product CRUD operations")
public class ProductController {

    private final ProductRepository repository;
    private final PdfService pdfService;
    private final com.tax.vat.repository.CompanyRepository companyRepository;
    private final com.tax.vat.repository.CompanyBranchRepository companyBranchRepository;
    private final com.tax.vat.repository.ProductCategoryRepository categoryRepository;
    private final com.tax.vat.repository.UnitOfSupplyRepository unitRepository;

    public ProductController(
            ProductRepository repository,
            PdfService pdfService,
            com.tax.vat.repository.CompanyRepository companyRepository,
            com.tax.vat.repository.CompanyBranchRepository companyBranchRepository,
            com.tax.vat.repository.ProductCategoryRepository categoryRepository,
            com.tax.vat.repository.UnitOfSupplyRepository unitRepository
    ) {
        this.repository = repository;
        this.pdfService = pdfService;
        this.companyRepository = companyRepository;
        this.companyBranchRepository = companyBranchRepository;
        this.categoryRepository = categoryRepository;
        this.unitRepository = unitRepository;
    }

    @Operation(summary = "Get form data options for Product creation and edit")
    @GetMapping("/form-data")
    public ApiResponse<java.util.Map<String, Object>> getFormData(@RequestParam(required = false) Long companyId) {
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("companies", companyRepository.findAllActiveCompanies());
        data.put("branches", companyBranchRepository.findAll());
        data.put("categories", categoryRepository.findActiveCategories(companyId));
        data.put("units", unitRepository.findAllActiveUnits());
        return ApiResponse.ok("Form data loaded successfully", data);
    }

    @Operation(summary = "Load Products for DataTable")
    @GetMapping
    public DataTableResponse<Product> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<Product> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActiveProducts(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActiveProducts(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Product by ID")
    @GetMapping("/{id}")
    public ApiResponse<Product> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Product retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Product not found"));
    }

    @Operation(summary = "Create Product")
    @PostMapping
    public ApiResponse<Product> create(@RequestBody Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return ApiResponse.error("Product name is required");
        }
        product.setSlug(UUID.randomUUID().toString());
        product.setDeletedAt(null);
        if (product.getHsCode() == null || product.getHsCode().trim().isEmpty()) {
            product.setHsCode(String.valueOf(System.currentTimeMillis() / 1000));
        }
        if (product.getIsService() == null) {
            product.setIsService("service".equalsIgnoreCase(product.getType()));
        }
        if (product.getPurchaseType() == null || product.getPurchaseType().trim().isEmpty()) {
            product.setPurchaseType("both");
        }
        if (product.getVatType() == null || product.getVatType().trim().isEmpty()) {
            product.setVatType("exclude");
        }
        if (product.getVat() == null) product.setVat(0.0);
        if (product.getSd() == null) product.setSd(0.0);
        if (product.getAt() == null) product.setAt(0.0);
        if (product.getCd() == null) product.setCd(0.0);
        if (product.getRd() == null) product.setRd(0.0);
        if (product.getAit() == null) product.setAit(0.0);
        if (product.getTti() == null) product.setTti(0.0);
        if (product.getExd() == null) product.setExd(0.0);

        Product saved = repository.save(product);
        return ApiResponse.ok("Product created successfully", saved);
    }

    @Operation(summary = "Update Product")
    @PutMapping("/{id}")
    public ApiResponse<Product> update(@PathVariable Long id, @RequestBody Product details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getHsCode() != null) existing.setHsCode(details.getHsCode());
                    if (details.getBrand() != null) existing.setBrand(details.getBrand());
                    if (details.getColor() != null) existing.setColor(details.getColor());
                    if (details.getModelYear() != null) existing.setModelYear(details.getModelYear());
                    if (details.getProductType() != null) existing.setProductType(details.getProductType());
                    if (details.getType() != null) {
                        existing.setType(details.getType());
                        if ("service".equalsIgnoreCase(details.getType())) {
                            existing.setIsService(true);
                        }
                    }
                    if (details.getIsService() != null) existing.setIsService(details.getIsService());
                    if (details.getPurchaseType() != null) existing.setPurchaseType(details.getPurchaseType());
                    if (details.getVatType() != null) existing.setVatType(details.getVatType());
                    if (details.getVat() != null) existing.setVat(details.getVat());
                    if (details.getSd() != null) existing.setSd(details.getSd());
                    if (details.getAt() != null) existing.setAt(details.getAt());
                    if (details.getCd() != null) existing.setCd(details.getCd());
                    if (details.getRd() != null) existing.setRd(details.getRd());
                    if (details.getAit() != null) existing.setAit(details.getAit());
                    if (details.getTti() != null) existing.setTti(details.getTti());
                    if (details.getExd() != null) existing.setExd(details.getExd());
                    if (details.getDescription() != null) existing.setDescription(details.getDescription());
                    if (details.getCompanyId() != null) existing.setCompanyId(details.getCompanyId());
                    if (details.getCompanyBranchId() != null) existing.setCompanyBranchId(details.getCompanyBranchId());
                    if (details.getCategoryId() != null) existing.setCategoryId(details.getCategoryId());
                    if (details.getSupplymentUnitId() != null) existing.setSupplymentUnitId(details.getSupplymentUnitId());
                    Product updated = repository.save(existing);
                    return ApiResponse.ok("Product updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Product not found"));
    }

    @Operation(summary = "Delete Product (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Product deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Product not found"));
    }

    @Operation(summary = "Download Product List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<Product> list = (search != null && !search.trim().isEmpty())
                ? repository.searchActiveProducts(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAllActiveProducts(companyId);

        String[] headers = {"#", "HS Code", "Product Name", "Brand", "Color", "VAT %", "VAT Type", "Company"};
        float[] widths = {0.8f, 2.0f, 4.0f, 1.5f, 1.2f, 1.2f, 1.5f, 3.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (Product p : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    p.getHsCode() != null ? p.getHsCode() : "-",
                    p.getName(),
                    p.getBrand() != null ? p.getBrand() : "-",
                    p.getColor() != null ? p.getColor() : "-",
                    p.getVat() != null ? String.valueOf(p.getVat()) : "0",
                    p.getVatType() != null ? p.getVatType() : "-",
                    p.getCompany() != null ? p.getCompany().getName() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Products List Report", headers, widths, rows);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"products_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
