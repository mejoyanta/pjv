package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import com.tax.vat.enums.CategoryType;
import jakarta.persistence.*;

@Entity
@Table(name = "company_categories")
public class CompanyCategory extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(length = 50)
    private CategoryType type;

    @Column(columnDefinition = "TEXT")
    private String description;

    public CompanyCategory() {
    }

    public CompanyCategory(String name, String slug, CategoryType type, String description) {
        this.name = name;
        this.slug = slug;
        this.type = type;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
