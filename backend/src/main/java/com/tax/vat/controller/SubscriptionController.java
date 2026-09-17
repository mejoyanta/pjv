package com.tax.vat.controller;

import com.tax.vat.dto.projection.CompanyTableProjection;
import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.Company;
import com.tax.vat.repository.CompanyRepository;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscription", description = "Endpoints for Subscription monitoring and renewal")
public class SubscriptionController {

    private final CompanyRepository companyRepository;
    private final PdfService pdfService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public SubscriptionController(CompanyRepository companyRepository, PdfService pdfService) {
        this.companyRepository = companyRepository;
        this.pdfService = pdfService;
    }

    private LocalDate parseExpireDate(Object obj) {
        if (obj == null) return null;
        if (obj instanceof LocalDate ld) return ld;
        if (obj instanceof java.sql.Date sd) return sd.toLocalDate();
        try {
            return LocalDate.parse(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    @Operation(summary = "Load Companies for Subscription DataTable")
    @GetMapping
    public DataTableResponse<Map<String, Object>> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.ASC, "name"));

        Page<CompanyTableProjection> compPage;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            compPage = companyRepository.searchActiveCompanies(request.getSearchValue().trim(), pageable);
        } else {
            compPage = companyRepository.findActiveCompanies(pageable);
        }

        LocalDate today = LocalDate.now();
        List<Map<String, Object>> list = new ArrayList<>();
        for (CompanyTableProjection c : compPage.getContent()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", c.getId());
            item.put("companyId", c.getId());
            item.put("companyName", c.getName());
            item.put("bin", c.getBin());
            item.put("phone", c.getPhone());
            LocalDate expire = parseExpireDate(c.getSubscriptionExpireDate());
            item.put("expireDate", expire != null ? expire.format(DATE_FMT) : "N/A");

            boolean isActive = expire != null && !expire.isBefore(today);
            long remainDays = 0;
            if (expire != null) {
                remainDays = ChronoUnit.DAYS.between(today, expire);
            }
            item.put("remainDays", Math.max(0, remainDays));
            item.put("status", isActive ? "active" : "expired");
            item.put("canRenew", remainDays < 30);
            list.add(item);
        }

        return new DataTableResponse<>(request.getDraw(), compPage.getTotalElements(), compPage.getTotalElements(), list);
    }

    @Operation(summary = "Renew Subscription for Company")
    @PostMapping("/renew/{companyId}")
    public ApiResponse<Company> renewSubscription(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "30") int days
    ) {
        return companyRepository.findById(companyId)
                .map(comp -> {
                    LocalDate currentExpire = comp.getSubscriptionExpireDate();
                    LocalDate baseDate = (currentExpire != null && currentExpire.isAfter(LocalDate.now())) ? currentExpire : LocalDate.now();
                    comp.setSubscriptionExpireDate(baseDate.plusDays(days));
                    Company updated = companyRepository.save(comp);
                    return ApiResponse.ok("Subscription renewed successfully for " + days + " days", updated);
                })
                .orElseGet(() -> ApiResponse.error("Company not found"));
    }

    @Operation(summary = "Download Subscription List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam(required = false) String search) {
        Page<CompanyTableProjection> compPage = (search != null && !search.trim().isEmpty())
                ? companyRepository.searchActiveCompanies(search.trim(), PageRequest.of(0, 1000))
                : companyRepository.findActiveCompanies(PageRequest.of(0, 1000));

        LocalDate today = LocalDate.now();
        String[] headers = {"#", "Company Name", "BIN / TIN", "Expire Date", "Remain Days", "Status"};
        float[] widths = {0.8f, 4.0f, 2.5f, 2.5f, 2.0f, 2.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;

        for (CompanyTableProjection c : compPage.getContent()) {
            LocalDate expire = parseExpireDate(c.getSubscriptionExpireDate());
            boolean isActive = expire != null && !expire.isBefore(today);
            long remainDays = expire != null ? Math.max(0, ChronoUnit.DAYS.between(today, expire)) : 0;

            rows.add(new String[]{
                    String.valueOf(serial++),
                    c.getName(),
                    c.getBin() != null ? c.getBin() : "-",
                    expire != null ? expire.format(DATE_FMT) : "N/A",
                    String.valueOf(remainDays),
                    isActive ? "Active" : "Expired"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Payment Information - Company Subscriptions", headers, widths, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=subscriptions.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
