package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.dto.response.Musak610DataTableItem;
import com.tax.vat.entity.Musak63;
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
import java.util.*;

@RestController
@RequestMapping({"/api/v1/mushak-6-10", "/api/v1/musak-6-10"})
@Tag(name = "Mushak 6.10", description = "Endpoints for Mushak 6.10 Large Value Transactions (>= 200,000 BDT)")
public class Musak610Controller {

    private final Musak63Repository musak63Repository;
    private final CompanyRepository companyRepository;
    private final PdfService pdfService;

    public Musak610Controller(
            Musak63Repository musak63Repository,
            CompanyRepository companyRepository,
            PdfService pdfService
    ) {
        this.musak63Repository = musak63Repository;
        this.companyRepository = companyRepository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Mushak 6.10 DataTable")
    @GetMapping
    public DataTableResponse<Musak610DataTableItem> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, Math.min(request.getLength(), 100), Sort.by(Sort.Direction.DESC, "id"));

        Page<Musak63> result = musak63Repository.findFiltered610(
                companyId,
                fromDate,
                toDate,
                request.getSearchValue() != null ? request.getSearchValue().trim() : null,
                pageable
        );

        int serial = request.getStart() + 1;
        List<Musak610DataTableItem> items = new ArrayList<>();
        for (Musak63 m : result.getContent()) {
            Musak610DataTableItem item = new Musak610DataTableItem();
            item.setId(m.getId());
            item.setSlug(m.getSlug());
            item.setSn(serial++);
            item.setCompany(m.getCompany() != null ? m.getCompany().getName() : "—");
            item.setDate(m.getDate());
            item.setViNo(m.getViNo());

            // Seller Info (from Company)
            item.setSellerName(m.getCompany() != null ? m.getCompany().getName() : "—");
            item.setSellerAddress(m.getCompany() != null ? m.getCompany().getAddress() : "—");
            item.setSellerBinNid(m.getCompany() != null ? m.getCompany().getBin() : "—");
            item.setSellerChallanNo(m.getViNo() != null ? String.valueOf(m.getViNo()) : "—");
            item.setIssueDate(m.getDate() != null ? m.getDate().toString() : "—");

            // Buyer Info & Sale Info
            if (m.getSale() != null) {
                item.setBuyerName(m.getSale().getBuyerName());
                item.setBuyerAddress(m.getSale().getBuyerAddress());
                item.setBuyerBinNid(m.getSale().getBuyerBinTinNid());
                item.setBuyerIdentification(m.getSale().getBuyerBinTinNid());
                item.setBuyer(m.getSale().getBuyerName());
                item.setSaleType(m.getSale().getSalesType());
                item.setSale(m.getSale().getTotalSaleAmount());
                item.setPrice(m.getSale().getTotalSaleAmount());
            } else {
                item.setBuyerName("N/A");
                item.setBuyerAddress("N/A");
                item.setBuyerBinNid("N/A");
                item.setBuyerIdentification("N/A");
                item.setBuyer("N/A");
                item.setSaleType("N/A");
                item.setSale(0.0);
                item.setPrice(0.0);
            }

            items.add(item);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), items);
    }

    @Operation(summary = "Get single Mushak 6.10 by ID")
    @GetMapping("/{id}")
    public ApiResponse<Musak63> getById(@PathVariable Long id) {
        return musak63Repository.findById(id)
                .map(item -> ApiResponse.ok("Mushak 6.10 found", item))
                .orElseGet(() -> ApiResponse.error("Mushak 6.10 record not found"));
    }

    @Operation(summary = "Get single Mushak 6.10 by Slug")
    @GetMapping("/slug/{slug}")
    public ApiResponse<Musak63> getBySlug(@PathVariable String slug) {
        return musak63Repository.findBySlug(slug)
                .map(item -> ApiResponse.ok("Mushak 6.10 found", item))
                .orElseGet(() -> ApiResponse.error("Mushak 6.10 record not found"));
    }

    @Operation(summary = "Get form data options for Mushak 6.10")
    @GetMapping("/form-data")
    public ApiResponse<Map<String, Object>> getFormData() {
        Map<String, Object> data = new HashMap<>();
        data.put("companies", companyRepository.findAllActiveCompanies());
        return ApiResponse.ok("Form data loaded successfully", data);
    }

    @Operation(summary = "Download Mushak 6.10 PDF")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Musak63 m = musak63Repository.findById(id).orElse(null);
        if (m == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] pdfBytes = pdfService.generateMusak610Pdf(m);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mushak_6_10_" + m.getId() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
