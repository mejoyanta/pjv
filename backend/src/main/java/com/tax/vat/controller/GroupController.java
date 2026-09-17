package com.tax.vat.controller;

import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.request.GroupAccessRequest;
import com.tax.vat.dto.request.GroupCreateRequest;
import com.tax.vat.dto.request.GroupUpdateRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.dto.response.GroupResponse;
import com.tax.vat.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@Tag(name = "Group Management", description = "Endpoints for Group CRUD, Hierarchy, and Permission Matrix")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @Operation(summary = "Load active groups for DataTable")
    @GetMapping
    public DataTableResponse<GroupResponse> loadDataTable(@ParameterObject DataTableRequest request) {
        return groupService.loadDataTable(request);
    }

    @Operation(summary = "Load archived groups for DataTable")
    @GetMapping("/archive")
    public DataTableResponse<GroupResponse> loadArchiveDataTable(@ParameterObject DataTableRequest request) {
        return groupService.loadArchiveDataTable(request);
    }

    @Operation(summary = "Get flat list of active groups for dropdown selects")
    @GetMapping("/list")
    public ApiResponse<List<GroupResponse>> getActiveGroupsList() {
        return ApiResponse.ok("Groups list retrieved", groupService.getAllActiveGroups());
    }

    @Operation(summary = "Get single group by ID")
    @GetMapping("/{id}")
    public ApiResponse<GroupResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok("Group details loaded", groupService.getGroupById(id));
    }

    @Operation(summary = "Get group details by slug")
    @GetMapping("/view/{slug}")
    public ApiResponse<GroupResponse> getBySlug(@PathVariable String slug) {
        return ApiResponse.ok("Group details loaded", groupService.getGroupBySlug(slug));
    }

    @Operation(summary = "Create a new user group")
    @PostMapping
    public ApiResponse<GroupResponse> createGroup(@RequestBody @Valid GroupCreateRequest request) {
        GroupResponse created = groupService.createGroup(request);
        return ApiResponse.ok("Group created successfully!", created);
    }

    @Operation(summary = "Update user group by ID")
    @PutMapping("/{id}")
    public ApiResponse<GroupResponse> updateGroup(@PathVariable Long id, @RequestBody @Valid GroupUpdateRequest request) {
        GroupResponse updated = groupService.updateGroup(id, request);
        return ApiResponse.ok("Group updated successfully!", updated);
    }

    @Operation(summary = "Soft delete user group by ID")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ApiResponse.ok("Group moved to archive!");
    }

    @Operation(summary = "Restore user group from archive")
    @PostMapping("/{id}/restore")
    public ApiResponse<Void> restoreGroup(@PathVariable Long id) {
        groupService.restoreGroup(id);
        return ApiResponse.ok("Group restored successfully!");
    }

    @Operation(summary = "Permanently force delete user group")
    @DeleteMapping("/{id}/force")
    public ApiResponse<Void> forceDeleteGroup(@PathVariable Long id) {
        groupService.forceDeleteGroup(id);
        return ApiResponse.ok("Group permanently deleted!");
    }

    @Operation(summary = "Get permission access list for a group by slug")
    @GetMapping("/{slug}/access")
    public ApiResponse<List<String>> getGroupAccess(@PathVariable String slug) {
        return ApiResponse.ok("Group permissions retrieved", groupService.getGroupPermissions(slug));
    }

    @Operation(summary = "Save permission access list for a group by slug")
    @PutMapping("/{slug}/access")
    public ApiResponse<Void> saveGroupAccess(@PathVariable String slug, @RequestBody @Valid GroupAccessRequest request) {
        groupService.saveGroupPermissions(slug, request.getPermissions());
        return ApiResponse.ok("Group permissions updated successfully!");
    }
}
