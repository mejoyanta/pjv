package com.tax.vat.controller;

import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.entity.CompanyCategory;
import com.tax.vat.enums.CategoryType;
import com.tax.vat.helper.SlugGenerator;
import com.tax.vat.repository.CompanyCategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Company Categories", description = "Endpoints for Company Categories")
public class CompanyCategoryController {

    private final CompanyCategoryRepository categoryRepository;

    public CompanyCategoryController(CompanyCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Operation(summary = "Get all company categories")
    @GetMapping
    @Cacheable("categories")
    public ApiResponse<List<CompanyCategory>> getAll() {
        return ApiResponse.ok("Categories retrieved", categoryRepository.findAll());
    }

    @Operation(summary = "Create a new company category (used in modal)")
    @PostMapping
    @CacheEvict(value = "categories", allEntries = true)
    public ApiResponse<CompanyCategory> create(@RequestParam String name,
                                               @RequestParam(required = false, defaultValue = "OTHERS") CategoryType type,
                                               @RequestParam(required = false) String description) {
        String slug = SlugGenerator.generateSlug(name);
        CompanyCategory category = new CompanyCategory(name, slug, type, description);
        CompanyCategory saved = categoryRepository.save(category);
        return ApiResponse.ok("Category created successfully", saved);
    }
}
