package com.tax.vat.controller;

import com.tax.vat.dto.projection.UserTableProjection;
import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.request.UserCreateRequest;
import com.tax.vat.dto.request.UserUpdateRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.dto.response.UserResponse;
import com.tax.vat.entity.Department;
import com.tax.vat.entity.Designation;
import com.tax.vat.repository.DepartmentRepository;
import com.tax.vat.repository.DesignationRepository;
import com.tax.vat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Endpoints for User CRUD, Stats, DataTables, and Company-Scoping")
public class UserController {

    private final UserService userService;
    private final DesignationRepository designationRepository;
    private final DepartmentRepository departmentRepository;

    public UserController(UserService userService,
                          DesignationRepository designationRepository,
                          DepartmentRepository departmentRepository) {
        this.userService = userService;
        this.designationRepository = designationRepository;
        this.departmentRepository = departmentRepository;
    }

    @Operation(summary = "Load active users for DataTable")
    @GetMapping
    public DataTableResponse<UserTableProjection> loadDataTable(@ParameterObject DataTableRequest request,
                                                                @RequestParam(required = false) Long companyId) {
        return userService.loadDataTable(request, companyId);
    }

    @Operation(summary = "Load archived users for DataTable")
    @GetMapping("/archive")
    public DataTableResponse<UserTableProjection> loadArchiveDataTable(@ParameterObject DataTableRequest request,
                                                                       @RequestParam(required = false) Long companyId) {
        return userService.loadArchiveDataTable(request, companyId);
    }

    @Operation(summary = "Get user count stats (Total, Online, Active)")
    @GetMapping("/stats")
    public ApiResponse<Map<String, Long>> getStats(@RequestParam(required = false) Long companyId) {
        return ApiResponse.ok("Stats loaded", userService.getUserStats(companyId));
    }

    @Operation(summary = "Get single user by ID")
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok("User details loaded", userService.getUserById(id));
    }

    @Operation(summary = "Create a new user")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserResponse> createUser(@ModelAttribute @Valid UserCreateRequest request,
                                                @RequestParam(required = false) Long actorCompanyId) {
        UserResponse created = userService.createUser(request, actorCompanyId);
        return ApiResponse.ok("User created successfully!", created);
    }

    @Operation(summary = "Update user details by ID")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserResponse> updateUser(@PathVariable Long id,
                                                @ModelAttribute @Valid UserUpdateRequest request,
                                                @RequestParam(required = false) Long actorCompanyId) {
        UserResponse updated = userService.updateUser(id, request, actorCompanyId);
        return ApiResponse.ok("User updated successfully!", updated);
    }

    @Operation(summary = "Soft delete user by ID")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> softDelete(@PathVariable Long id) {
        userService.softDelete(id);
        return ApiResponse.ok("User moved to archive!");
    }

    @Operation(summary = "Restore user from archive")
    @PostMapping("/{id}/restore")
    public ApiResponse<Void> restore(@PathVariable Long id) {
        userService.restore(id);
        return ApiResponse.ok("User restored successfully!");
    }

    @Operation(summary = "Permanently force delete user")
    @DeleteMapping("/{id}/force")
    public ApiResponse<Void> forceDelete(@PathVariable Long id) {
        userService.forceDelete(id);
        return ApiResponse.ok("User permanently deleted!");
    }

    @Operation(summary = "Get all designations for dropdown")
    @GetMapping("/designations")
    @Cacheable("designations")
    public ApiResponse<List<Designation>> getDesignations(@RequestParam(required = false) Long companyId) {
        List<Designation> list = companyId != null
                ? designationRepository.findByCompanyIdOrCompanyIdIsNull(companyId)
                : designationRepository.findAll();
        return ApiResponse.ok("Designations retrieved", list);
    }

    @Operation(summary = "Get all departments for dropdown")
    @GetMapping("/departments")
    @Cacheable("departments")
    public ApiResponse<List<Department>> getDepartments(@RequestParam(required = false) Long companyId) {
        List<Department> list = companyId != null
                ? departmentRepository.findByCompanyIdOrCompanyIdIsNull(companyId)
                : departmentRepository.findAll();
        return ApiResponse.ok("Departments retrieved", list);
    }
}
