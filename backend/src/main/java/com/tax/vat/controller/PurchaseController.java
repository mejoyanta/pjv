package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.dto.response.PurchaseDataTableItem;
import com.tax.vat.entity.Purchase;
import com.tax.vat.repository.*;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/v1/purchases", "/api/v1/purchase"})
@Tag(name = "Product Purchase", description = "Endpoints for Stock Management Product Purchase CRUD operations")
public class PurchaseController {

    private final PurchaseRepository purchaseRepository;
    private final CompanyRepository companyRepository;
    private final CompanyBranchRepository companyBranchRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final PortRepository portRepository;
    private final UnitOfSupplyRepository unitOfSupplyRepository;
    private final PdfService pdfService;

    public PurchaseController(
            PurchaseRepository purchaseRepository,
            CompanyRepository companyRepository,
            CompanyBranchRepository companyBranchRepository,
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            PortRepository portRepository,
            UnitOfSupplyRepository unitOfSupplyRepository,
            PdfService pdfService
    ) {
        this.purchaseRepository = purchaseRepository;
        this.companyRepository = companyRepository;
        this.companyBranchRepository = companyBranchRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.portRepository = portRepository;
        this.unitOfSupplyRepository = unitOfSupplyRepository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Purchases for DataTable")
    @GetMapping
    public DataTableResponse<PurchaseDataTableItem> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String purchaseType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, Math.min(request.getLength(), 100), Sort.by(Sort.Direction.DESC, "id"));

        Page<Purchase> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = purchaseRepository.searchPurchases(companyId, supplierId, purchaseType, fromDate, toDate, request.getSearchValue().trim(), pageable);
        } else {
            result = purchaseRepository.findPurchases(companyId, supplierId, purchaseType, fromDate, toDate, pageable);
        }

        long serial = (long) request.getStart() + 1;
        List<PurchaseDataTableItem> items = new ArrayList<>();
        for (Purchase p : result.getContent()) {
            PurchaseDataTableItem item = new PurchaseDataTableItem();
            item.setId(p.getId());
            item.setSlug(p.getSlug());
            item.setSn(serial++);
            item.setPurchaseType(p.getPurchaseType());
            item.setDate(p.getPurchaseShowDate() != null ? p.getPurchaseShowDate() : p.getDate());
            item.setBoeDate(p.getDate());
            item.setCompanyId(p.getCompanyId());
            item.setCompanyName(p.getCompany() != null ? p.getCompany().getName() : "—");
            item.setBranchName(p.getCompanyBranch() != null ? p.getCompanyBranch().getName() : "N/A");
            item.setBin(p.getCompany() != null ? p.getCompany().getBin() : "N/A");
            item.setTin(p.getCompany() != null ? p.getCompany().getTin() : "N/A");
            item.setHsCode(p.getProduct() != null ? p.getProduct().getHsCode() : "N/A");
            item.setProductDescription(p.getProduct() != null ? p.getProduct().getName() : (p.getDescription() != null ? p.getDescription() : "N/A"));
            item.setBillOfEntry(p.getBillOfEntry());
            item.setSupplierName(p.getSupplier() != null ? p.getSupplier().getName() : (p.getSeller() != null ? p.getSeller() : "N/A"));
            item.setSupplierAddress(p.getSupplier() != null ? p.getSupplier().getAddress() : (p.getSellerAddress() != null ? p.getSellerAddress() : "N/A"));
            item.setSupplyBinNid(p.getSupplier() != null ? p.getSupplier().getBinTin() : (p.getSellerPhone() != null ? p.getSellerPhone() : "N/A"));
            item.setAssessable(p.getAssessable() != null ? p.getAssessable() : 0.0);
            item.setVatAmount(p.getVat() != null ? p.getVat() : 0.0);
            item.setSdAmount(p.getSd() != null ? p.getSd() : 0.0);
            item.setAtAmount(p.getAt() != null ? p.getAt() : 0.0);
            item.setTotalPurchaseAmount(p.getTotalAmount() != null ? p.getTotalAmount() : 0.0);
            item.setWholesaleRate(p.getSellPrice() != null ? p.getSellPrice() : 0.0);
            item.setRetailerRate(0.0);
            item.setQuantity(p.getQuantity() != null ? p.getQuantity() : 1.0);
            item.setBasePrice(p.getBasePrice() != null ? p.getBasePrice() : 0.0);
            item.setIsDraft(p.getIsDraft() != null ? p.getIsDraft() : false);
            item.setCreatedAt(p.getCreatedAt());

            items.add(item);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), items);
    }

    @Operation(summary = "Get single Purchase by ID")
    @GetMapping("/{id}")
    public ApiResponse<Purchase> getById(@PathVariable Long id) {
        return purchaseRepository.findById(id)
                .map(item -> ApiResponse.ok("Purchase found", item))
                .orElseGet(() -> ApiResponse.error("Purchase record not found"));
    }

    @Operation(summary = "Get form data options for Purchase creation")
    @GetMapping("/form-data")
    public ApiResponse<Map<String, Object>> getFormData(@RequestParam(required = false) Long companyId) {
        Map<String, Object> data = new HashMap<>();
        data.put("companies", companyRepository.findAllActiveCompanies());
        data.put("branches", companyBranchRepository.findAll());
        data.put("products", productRepository.findAllActiveProducts(companyId));
        data.put("suppliers", supplierRepository.findAllByCompany(companyId));
        data.put("ports", portRepository.findAllActivePorts());
        data.put("units", unitOfSupplyRepository.findAllActiveUnits());
        return ApiResponse.ok("Form data loaded successfully", data);
    }

    @Operation(summary = "Create new Purchase")
    @PostMapping
    public ApiResponse<Purchase> create(@RequestBody Purchase purchase) {
        if (purchase.getBillOfEntry() == null || purchase.getBillOfEntry().trim().isEmpty()) {
            return ApiResponse.error("Bill of Entry / Invoice No is required");
        }
        if (purchase.getProductId() == null) {
            return ApiResponse.error("Product is required");
        }
        if (purchase.getCompanyId() == null) {
            return ApiResponse.error("Company is required");
        }
        if (purchase.getDate() == null) {
            purchase.setDate(LocalDate.now());
        }
        if (purchase.getPurchaseShowDate() == null) {
            purchase.setPurchaseShowDate(purchase.getDate());
        }
        if (purchase.getSlug() == null || purchase.getSlug().trim().isEmpty()) {
            purchase.setSlug(LocalDate.now() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase() + "-" + (System.currentTimeMillis() / 1000));
        }
        if (purchase.getPurchaseType() == null) {
            purchase.setPurchaseType("foreign");
        }
        if (purchase.getQuantity() == null) {
            purchase.setQuantity(1.0);
        }
        if (purchase.getBasePrice() == null) {
            purchase.setBasePrice(0.0);
        }
        if (purchase.getTotalAmount() == null || purchase.getTotalAmount() == 0.0) {
            purchase.setTotalAmount(purchase.getBasePrice() + (purchase.getBasePrice() * (purchase.getVat() != null ? purchase.getVat() : 15.0) / 100.0));
        }

        Purchase saved = purchaseRepository.save(purchase);
        return ApiResponse.ok("Purchase created successfully", saved);
    }

    @Operation(summary = "Update Purchase")
    @PutMapping("/{id}")
    public ApiResponse<Purchase> update(@PathVariable Long id, @RequestBody Purchase purchase) {
        return purchaseRepository.findById(id)
                .map(existing -> {
                    if (purchase.getBillOfEntry() != null) existing.setBillOfEntry(purchase.getBillOfEntry());
                    if (purchase.getDate() != null) existing.setDate(purchase.getDate());
                    if (purchase.getPurchaseShowDate() != null) existing.setPurchaseShowDate(purchase.getPurchaseShowDate());
                    if (purchase.getPurchaseType() != null) existing.setPurchaseType(purchase.getPurchaseType());
                    if (purchase.getProductId() != null) existing.setProductId(purchase.getProductId());
                    if (purchase.getCompanyId() != null) existing.setCompanyId(purchase.getCompanyId());
                    if (purchase.getCompanyBranchId() != null) existing.setCompanyBranchId(purchase.getCompanyBranchId());
                    if (purchase.getSupplierId() != null) existing.setSupplierId(purchase.getSupplierId());
                    if (purchase.getPortId() != null) existing.setPortId(purchase.getPortId());
                    if (purchase.getUnitOfSupplyId() != null) existing.setUnitOfSupplyId(purchase.getUnitOfSupplyId());
                    if (purchase.getQuantity() != null) existing.setQuantity(purchase.getQuantity());
                    if (purchase.getAssessable() != null) existing.setAssessable(purchase.getAssessable());
                    if (purchase.getBasePrice() != null) existing.setBasePrice(purchase.getBasePrice());
                    if (purchase.getSellPrice() != null) existing.setSellPrice(purchase.getSellPrice());
                    if (purchase.getVat() != null) existing.setVat(purchase.getVat());
                    if (purchase.getSd() != null) existing.setSd(purchase.getSd());
                    if (purchase.getAt() != null) existing.setAt(purchase.getAt());
                    if (purchase.getTotalAmount() != null) existing.setTotalAmount(purchase.getTotalAmount());
                    if (purchase.getSeller() != null) existing.setSeller(purchase.getSeller());
                    if (purchase.getSellerAddress() != null) existing.setSellerAddress(purchase.getSellerAddress());
                    if (purchase.getSellerPhone() != null) existing.setSellerPhone(purchase.getSellerPhone());
                    if (purchase.getLcNo() != null) existing.setLcNo(purchase.getLcNo());
                    if (purchase.getLcDate() != null) existing.setLcDate(purchase.getLcDate());
                    if (purchase.getChassisOrDescription() != null) existing.setChassisOrDescription(purchase.getChassisOrDescription());
                    if (purchase.getDescription() != null) existing.setDescription(purchase.getDescription());
                    if (purchase.getIsDraft() != null) existing.setIsDraft(purchase.getIsDraft());

                    Purchase updated = purchaseRepository.save(existing);
                    return ApiResponse.ok("Purchase updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Purchase not found"));
    }

    @Operation(summary = "Delete Purchase")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return purchaseRepository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    purchaseRepository.save(existing);
                    return ApiResponse.<Void>ok("Purchase deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Purchase not found"));
    }

    @Operation(summary = "Batch Delete Purchases")
    @PostMapping("/multiple-delete")
    public ApiResponse<Integer> multipleDelete(@RequestBody Map<String, List<Long>> payload) {
        List<Long> ids = payload.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ApiResponse.error("No items selected for deletion");
        }
        int deletedCount = 0;
        for (Long id : ids) {
            Optional<Purchase> p = purchaseRepository.findById(id);
            if (p.isPresent()) {
                p.get().setDeletedAt(LocalDateTime.now());
                purchaseRepository.save(p.get());
                deletedCount++;
            }
        }
        return ApiResponse.ok("Successfully deleted " + deletedCount + " purchase records", deletedCount);
    }

    @Operation(summary = "Download Purchases PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC, "id"));
        Page<Purchase> page = (search != null && !search.trim().isEmpty())
                ? purchaseRepository.searchPurchases(companyId, null, null, null, null, search.trim(), pageable)
                : purchaseRepository.findPurchases(companyId, null, null, null, null, pageable);

        String[] headers = {"#", "BOE / Invoice", "Date", "Company", "Product / Description", "Qty", "Base Price", "VAT", "Total (BDT)"};
        float[] widths = {0.8f, 2.5f, 2.0f, 3.5f, 4.0f, 1.2f, 2.2f, 1.5f, 2.5f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (Purchase p : page.getContent()) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    p.getBillOfEntry(),
                    p.getPurchaseShowDate() != null ? p.getPurchaseShowDate().toString() : (p.getDate() != null ? p.getDate().toString() : "—"),
                    p.getCompany() != null ? p.getCompany().getName() : "—",
                    p.getProduct() != null ? p.getProduct().getName() : (p.getDescription() != null ? p.getDescription() : "—"),
                    String.format("%.2f", p.getQuantity() != null ? p.getQuantity() : 0.0),
                    String.format("%,.2f", p.getBasePrice() != null ? p.getBasePrice() : 0.0),
                    String.format("%.1f%%", p.getVat() != null ? p.getVat() : 0.0),
                    String.format("%,.2f", p.getTotalAmount() != null ? p.getTotalAmount() : 0.0)
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Stock Management - Product Purchase Report", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=product_purchases.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
