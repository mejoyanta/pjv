package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_reports", indexes = {
        @Index(name = "idx_comp_rep_slug", columnList = "slug"),
        @Index(name = "idx_comp_rep_company_id", columnList = "company_id"),
        @Index(name = "idx_comp_rep_deleted_at", columnList = "deleted_at")
})
public class CompanyReport extends BaseEntity {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private String slug;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Company company;

    @Column(name = "company_branch_id")
    private Long companyBranchId;

    @Column(name = "comany_bin")
    private String companyBin;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private String year;

    @Column(name = "sub_date")
    private LocalDate subDate;

    @Column(name = "created_by")
    private Long createdBy;

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

    public String getCompanyBin() {
        return companyBin;
    }

    public void setCompanyBin(String companyBin) {
        this.companyBin = companyBin;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public LocalDate getSubDate() {
        return subDate;
    }

    public void setSubDate(LocalDate subDate) {
        this.subDate = subDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}
