package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.Department;
import com.tax.vat.repository.DepartmentRepository;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@Tag(name = "Department", description = "Endpoints for Department CRUD operations")
public class DepartmentController {

    private final DepartmentRepository repository;
    private final PdfService pdfService;

    public DepartmentController(DepartmentRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Departments for DataTable")
    @GetMapping
    public DataTableResponse<Department> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<Department> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchDepartments(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findDepartments(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Department by ID")
    @GetMapping("/{id}")
    public ApiResponse<Department> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Department retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Department not found"));
    }

    @Operation(summary = "Create Department")
    @PostMapping
    public ApiResponse<Department> create(@RequestBody Department department) {
        if (department.getName() == null || department.getName().trim().isEmpty()) {
            return ApiResponse.error("Department name is required");
        }
        Department saved = repository.save(department);
        return ApiResponse.ok("Department created successfully", saved);
    }

    @Operation(summary = "Update Department")
    @PutMapping("/{id}")
    public ApiResponse<Department> update(@PathVariable Long id, @RequestBody Department details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getCompanyId() != null) existing.setCompanyId(details.getCompanyId());
                    Department updated = repository.save(existing);
                    return ApiResponse.ok("Department updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Department not found"));
    }

    @Operation(summary = "Delete Department")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    repository.delete(existing);
                    return ApiResponse.<Void>ok("Department deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Department not found"));
    }

    @Operation(summary = "Download Department List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search
    ) {
        List<Department> list = (search != null && !search.trim().isEmpty())
                ? repository.searchDepartments(companyId, search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAllByCompany(companyId);

        String[] headers = {"#", "Department Name", "Company", "Created At"};
        float[] widths = {1.0f, 4.0f, 3.5f, 2.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (Department d : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    d.getName(),
                    d.getCompany() != null ? d.getCompany().getName() : "-",
                    d.getCreatedAt() != null ? d.getCreatedAt().toLocalDate().toString() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Departments Report", headers, widths, rows);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"departments_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
