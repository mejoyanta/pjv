package com.tax.vat.controller;

import com.tax.vat.dto.projection.CompanyTableProjection;
import com.tax.vat.dto.request.CompanyCreateRequest;
import com.tax.vat.dto.request.CompanyUpdateRequest;
import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.CompanyResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.dto.response.DocumentResponse;
import com.tax.vat.service.CompanyDocumentService;
import com.tax.vat.service.CompanyService;
import com.tax.vat.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
@Tag(name = "Company Management", description = "Endpoints for Company CRUD, DataTables, Documents, and PDF Reports")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyDocumentService documentService;
    private final PdfService pdfService;

    public CompanyController(CompanyService companyService,
                             CompanyDocumentService documentService,
                             PdfService pdfService) {
        this.companyService = companyService;
        this.documentService = documentService;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load active companies for DataTable", description = "Server-side pagination, sorting, and fast search for active companies")
    @GetMapping
    public DataTableResponse<CompanyTableProjection> loadDataTable(@ParameterObject DataTableRequest request) {
        return companyService.loadDataTable(request);
    }

    @Operation(summary = "Load archived companies for DataTable", description = "Server-side pagination for soft-deleted companies")
    @GetMapping("/archive")
    public DataTableResponse<CompanyTableProjection> loadArchiveDataTable(@ParameterObject DataTableRequest request) {
        return companyService.loadArchiveDataTable(request);
    }

    @Operation(summary = "Get single company details by slug")
    @GetMapping("/{slug}")
    public ApiResponse<CompanyResponse> getBySlug(@PathVariable String slug) {
        return ApiResponse.ok("Company retrieved successfully", companyService.getCompanyBySlug(slug));
    }

    @Operation(summary = "Get single company details by ID")
    @GetMapping("/info")
    public ApiResponse<CompanyResponse> getById(@RequestParam Long id) {
        return ApiResponse.ok("Company info retrieved", companyService.getCompanyById(id));
    }

    @Operation(summary = "Create a new company with file attachments")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CompanyResponse> createCompany(@ModelAttribute @Valid CompanyCreateRequest request) {
        CompanyResponse created = companyService.createCompany(request);
        return ApiResponse.ok("Company created successfully!", created);
    }

    @Operation(summary = "Update company details by slug")
    @PutMapping(value = "/{slug}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CompanyResponse> updateCompany(@PathVariable String slug,
                                                      @ModelAttribute @Valid CompanyUpdateRequest request,
                                                      HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        CompanyResponse updated = companyService.updateCompany(slug, request, clientIp);
        return ApiResponse.ok("Company updated successfully!", updated);
    }

    @Operation(summary = "Soft delete company by ID")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> softDelete(@PathVariable Long id) {
        companyService.softDelete(id);
        return ApiResponse.ok("Company moved to archive successfully!");
    }

    @Operation(summary = "Restore soft-deleted company")
    @PostMapping("/{id}/restore")
    public ApiResponse<Void> restore(@PathVariable Long id) {
        companyService.restore(id);
        return ApiResponse.ok("Company restored successfully!");
    }

    @Operation(summary = "Permanently force delete company")
    @DeleteMapping("/{id}/force")
    public ApiResponse<Void> forceDelete(@PathVariable Long id) {
        companyService.forceDelete(id);
        return ApiResponse.ok("Company permanently deleted!");
    }

    @Operation(summary = "Download Company Information PDF report")
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam(required = false) String search) {
        byte[] pdfBytes = pdfService.generateCompanyListPdf(search);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=company-list.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    // Document Management
    @Operation(summary = "Get company document attachments list")
    @GetMapping("/{slug}/documents")
    public ApiResponse<List<DocumentResponse>> getDocuments(@PathVariable String slug) {
        return ApiResponse.ok("Documents retrieved", documentService.getDocuments(slug));
    }

    @Operation(summary = "Upload document attachment for company")
    @PostMapping(value = "/{slug}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<DocumentResponse>> uploadDocument(@PathVariable String slug,
                                                              @RequestParam(required = false) String title,
                                                              @RequestParam(required = false) String expiryDate,
                                                              @RequestParam("file") MultipartFile file) {
        List<DocumentResponse> docs = documentService.uploadDocument(slug, title, expiryDate, file);
        return ApiResponse.ok("Document uploaded successfully!", docs);
    }

    @Operation(summary = "Delete document attachment from company")
    @DeleteMapping("/{slug}/documents/{documentId}")
    public ApiResponse<List<DocumentResponse>> deleteDocument(@PathVariable String slug,
                                                              @PathVariable String documentId) {
        List<DocumentResponse> docs = documentService.deleteDocument(slug, documentId);
        return ApiResponse.ok("Document deleted successfully!", docs);
    }
}
