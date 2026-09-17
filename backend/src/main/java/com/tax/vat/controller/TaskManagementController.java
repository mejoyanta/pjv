package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.TaskManagement;
import com.tax.vat.repository.TaskManagementRepository;
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
@RequestMapping("/api/v1/task-management")
@Tag(name = "Task Management", description = "Endpoints for Task Management CRUD operations")
public class TaskManagementController {

    private final TaskManagementRepository repository;

    public TaskManagementController(TaskManagementRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Load Tasks for DataTable")
    @GetMapping
    public DataTableResponse<TaskManagement> loadDataTable(
            @ParameterObject DataTableRequest request,
            @RequestParam(required = false) Long companyId
    ) {
        int page = request.getStart() / Math.max(1, request.getLength());
        Pageable pageable = PageRequest.of(page, request.getLength(), Sort.by(Sort.Direction.DESC, "id"));

        Page<TaskManagement> result;
        if (request.getSearchValue() != null && !request.getSearchValue().trim().isEmpty()) {
            result = repository.searchActiveTasks(companyId, request.getSearchValue().trim(), pageable);
        } else {
            result = repository.findActiveTasks(companyId, pageable);
        }

        return new DataTableResponse<>(request.getDraw(), result.getTotalElements(), result.getTotalElements(), result.getContent());
    }

    @Operation(summary = "Get single Task by ID")
    @GetMapping("/{id}")
    public ApiResponse<TaskManagement> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> ApiResponse.ok("Task retrieved successfully", item))
                .orElseGet(() -> ApiResponse.error("Task not found"));
    }

    @Operation(summary = "Create Task")
    @PostMapping
    public ApiResponse<TaskManagement> create(@RequestBody TaskManagement task) {
        if (task.getSlug() == null || task.getSlug().trim().isEmpty()) {
            task.setSlug("TASK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        TaskManagement saved = repository.save(task);
        return ApiResponse.ok("Task created successfully", saved);
    }

    @Operation(summary = "Update Task")
    @PutMapping("/{id}")
    public ApiResponse<TaskManagement> update(@PathVariable Long id, @RequestBody TaskManagement updated) {
        return repository.findById(id).map(task -> {
            task.setCompanyId(updated.getCompanyId());
            task.setDate(updated.getDate());
            task.setWorkFor(updated.getWorkFor());
            task.setUserName(updated.getUserName());
            task.setEmployerDesignation(updated.getEmployerDesignation());
            task.setAssignFor(updated.getAssignFor());
            task.setAssignBy(updated.getAssignBy());
            return ApiResponse.ok("Task updated successfully", repository.save(task));
        }).orElseGet(() -> ApiResponse.error("Task not found"));
    }

    @Operation(summary = "Delete Task")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return repository.findById(id).map(task -> {
            task.setDeletedAt(LocalDateTime.now());
            repository.save(task);
            return ApiResponse.<Void>ok("Task deleted successfully", null);
        }).orElseGet(() -> ApiResponse.error("Task not found"));
    }
}
