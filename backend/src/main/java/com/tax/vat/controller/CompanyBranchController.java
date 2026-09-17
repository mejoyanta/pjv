package com.tax.vat.controller;

import com.tax.vat.dto.request.CompanyBranchCreateRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.entity.CompanyBranch;
import com.tax.vat.service.CompanyBranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@Tag(name = "Company Branches", description = "Endpoints for managing branches of a company")
public class CompanyBranchController {

    private final CompanyBranchService branchService;

    public CompanyBranchController(CompanyBranchService branchService) {
        this.branchService = branchService;
    }

    @Operation(summary = "Get all branches of a company")
    @GetMapping
    public ApiResponse<List<CompanyBranch>> getBranches(@RequestParam Long companyId) {
        return ApiResponse.ok("Branches retrieved", branchService.getBranchesByCompany(companyId));
    }

    @Operation(summary = "Create a new branch for a company")
    @PostMapping
    public ApiResponse<CompanyBranch> createBranch(@RequestBody @Valid CompanyBranchCreateRequest request) {
        CompanyBranch created = branchService.createBranch(request);
        return ApiResponse.ok("Branch created successfully!", created);
    }
}
