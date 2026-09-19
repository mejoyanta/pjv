package com.tax.vat.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Product extends BaseEntity {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private String slug;

    @Column(name = "hs_code")
    private String hsCode;

    @Column(nullable = false)
    private String name;

    private String brand;
    private String color;

    @Column(name = "model_year")
    private String modelYear;

    private String type;

    @Column(name = "product_type")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.tax.vat.config.FlexibleBooleanDeserializer.class)
    private Boolean productType = false;

    @Column(name = "vat_type")
    private String vatType = "exclude";

    private Double vat = 0.0;
    private Double sd = 0.0;
    private Double at = 0.0;
    private Double cd = 0.0;
    private Double rd = 0.0;
    private Double ait = 0.0;
    private Double tti = 0.0;
    private Double exd = 0.0;

    private String description;

    @Column(name = "purchase_type")
    private String purchaseType = "both";

    @Column(name = "is_service", nullable = false)
    private Boolean isService = false;

    @Column(name = "company_id")
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Company company;

    @Column(name = "company_branch_id")
    private Long companyBranchId;

    @Column(name = "supplyment_unit_id")
    private Long supplymentUnitId;

    @Column(name = "gold_type")
    private String goldType;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "commercial_description")
    private String commercialDescription;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getHsCode() {
        return hsCode;
    }

    public void setHsCode(String hsCode) {
        this.hsCode = hsCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getModelYear() {
        return modelYear;
    }

    public void setModelYear(String modelYear) {
        this.modelYear = modelYear;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getProductType() {
        return productType;
    }

    public void setProductType(Boolean productType) {
        this.productType = productType;
    }

    public String getVatType() {
        return vatType;
    }

    public void setVatType(String vatType) {
        this.vatType = vatType;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    public Double getSd() {
        return sd;
    }

    public void setSd(Double sd) {
        this.sd = sd;
    }

    public Double getAt() {
        return at;
    }

    public void setAt(Double at) {
        this.at = at;
    }

    public Double getCd() {
        return cd;
    }

    public void setCd(Double cd) {
        this.cd = cd;
    }

    public Double getRd() {
        return rd;
    }

    public void setRd(Double rd) {
        this.rd = rd;
    }

    public Double getAit() {
        return ait;
    }

    public void setAit(Double ait) {
        this.ait = ait;
    }

    public Double getTti() {
        return tti;
    }

    public void setTti(Double tti) {
        this.tti = tti;
    }

    public Double getExd() {
        return exd;
    }

    public void setExd(Double exd) {
        this.exd = exd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public Boolean getIsService() {
        return isService;
    }

    public void setIsService(Boolean isService) {
        this.isService = isService;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Long getCompanyBranchId() {
        return companyBranchId;
    }

    public void setCompanyBranchId(Long companyBranchId) {
        this.companyBranchId = companyBranchId;
    }

    public Long getSupplymentUnitId() {
        return supplymentUnitId;
    }

    public void setSupplymentUnitId(Long supplymentUnitId) {
        this.supplymentUnitId = supplymentUnitId;
    }

    public String getGoldType() {
        return goldType;
    }

    public void setGoldType(String goldType) {
        this.goldType = goldType;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCommercialDescription() {
        return commercialDescription;
    }

    public void setCommercialDescription(String commercialDescription) {
        this.commercialDescription = commercialDescription;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
}
