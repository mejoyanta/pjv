package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "materials")
public class Material extends BaseEntity {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private String slug;

    @Column(name = "hs_code")
    private String hsCode;

    @Column(nullable = false)
    private String name;

    private Double vat;
    private Double sd;
    private Double at;
    private Double cd;
    private Double rd;
    private Double ait;
    private Double tti;
    private Double exd;

    @Column(name = "vat_type")
    private String vatType;

    private String description;

    @Column(name = "purchase_type")
    private String purchaseType;

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

    @Column(name = "category_id")
    private Long categoryId;

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

    public String getVatType() {
        return vatType;
    }

    public void setVatType(String vatType) {
        this.vatType = vatType;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
