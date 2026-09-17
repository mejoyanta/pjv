package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.Port;
import com.tax.vat.repository.PortRepository;
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
@RequestMapping("/api/v1/ports")
@Tag(name = "Port", description = "Endpoints for Port CRUD operations")
public class PortController {

    private final PortRepository repository;
    private final PdfService pdfService;

    public PortController(PortRepository repository, PdfService pdfService) {
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @Operation(summary = "Load Ports for DataTable")
    @GetMapping
    public DataTableResponse<Port> loadDataTable(@ParameterObject DataTableRequest request) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<Port> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActivePorts(request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActivePorts(pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Port by ID")
    @GetMapping("/{id}")
    public ApiResponse<Port> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Port retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Port not found"));
    }

    @Operation(summary = "Create Port")
    @PostMapping
    public ApiResponse<Port> create(@RequestBody Port port) {
        if (port.getName() == null || port.getName().trim().isEmpty()) {
            return ApiResponse.error("Port name is required");
        }
        if (port.getCode() == null || port.getCode().trim().isEmpty()) {
            return ApiResponse.error("Port code is required");
        }
        port.setSlug(UUID.randomUUID().toString());
        port.setDeletedAt(null);
        Port saved = repository.save(port);
        return ApiResponse.ok("Port created successfully", saved);
    }

    @Operation(summary = "Update Port")
    @PutMapping("/{id}")
    public ApiResponse<Port> update(@PathVariable Long id, @RequestBody Port details) {
        return repository.findById(id)
                .map(existing -> {
                    if (details.getName() != null) existing.setName(details.getName());
                    if (details.getCode() != null) existing.setCode(details.getCode());
                    if (details.getDescription() != null) existing.setDescription(details.getDescription());
                    Port updated = repository.save(existing);
                    return ApiResponse.ok("Port updated successfully", updated);
                })
                .orElseGet(() -> ApiResponse.error("Port not found"));
    }

    @Operation(summary = "Delete Port (Soft delete)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(LocalDateTime.now());
                    repository.save(existing);
                    return ApiResponse.<Void>ok("Port deleted successfully", null);
                })
                .orElseGet(() -> ApiResponse.error("Port not found"));
    }

    @Operation(summary = "Download Port List PDF")
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam(required = false) String search) {
        List<Port> list = (search != null && !search.trim().isEmpty())
                ? repository.searchActivePorts(search.trim(), PageRequest.of(0, 1000)).getContent()
                : repository.findAllActivePorts();

        String[] headers = {"#", "Port Code", "Port Name", "Description", "Created At"};
        float[] widths = {0.8f, 2.0f, 3.5f, 4.0f, 2.0f};
        List<String[]> rows = new ArrayList<>();
        int serial = 1;
        for (Port p : list) {
            rows.add(new String[]{
                    String.valueOf(serial++),
                    p.getCode(),
                    p.getName(),
                    p.getDescription() != null ? p.getDescription() : "-",
                    p.getCreatedAt() != null ? p.getCreatedAt().toLocalDate().toString() : "-"
            });
        }

        byte[] pdfBytes = pdfService.generateTablePdf("Ports List Report", headers, widths, rows);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ports_report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
