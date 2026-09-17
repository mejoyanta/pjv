package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.DocumentRegister;
import com.tax.vat.repository.DocumentRegisterRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/document-registers")
@Tag(name = "Document Register", description = "Endpoints for Document Register CRUD operations")
public class DocumentRegisterController {

    private final DocumentRegisterRepository repository;

    public DocumentRegisterController(DocumentRegisterRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Load Document Registers for DataTable")
    @GetMapping
    public DataTableResponse<DocumentRegister> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<DocumentRegister> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActiveDocuments(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActiveDocuments(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Document Register by ID or slug")
    @GetMapping("/{id}")
    public ApiResponse<DocumentRegister> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Document retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Document not found"));
    }

    @Operation(summary = "Create Document Register")
    @PostMapping
    public ApiResponse<DocumentRegister> create(@RequestBody DocumentRegister document) {
        if (document.getSlug() == null || document.getSlug().trim().isEmpty()) {
            document.setSlug("DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (document.getCompanyBranchId() == null) {
            document.setCompanyBranchId(0L);
        }
        DocumentRegister saved = repository.save(document);
        return ApiResponse.ok("Document created successfully", saved);
    }

    @Operation(summary = "Update Document Register")
    @PutMapping("/{id}")
    public ApiResponse<DocumentRegister> update(@PathVariable Long id, @RequestBody DocumentRegister updated) {
        return repository.findById(id).map(doc -> {
            doc.setName(updated.getName());
            doc.setRef(updated.getRef());
            doc.setDate(updated.getDate());
            doc.setToken(updated.getToken());
            doc.setCompanyId(updated.getCompanyId());
            doc.setComments(updated.getComments());
            return ApiResponse.ok("Document updated successfully", repository.save(doc));
        }).orElseGet(() -> ApiResponse.error("Document not found"));
    }

    @Operation(summary = "Delete Document Register")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id).map(doc -> {
            doc.setDeletedAt(LocalDateTime.now());
            repository.save(doc);
            return ApiResponse.<Void>ok("Document deleted successfully", null);
        }).orElseGet(() -> ApiResponse.error("Document not found"));
    }
}
