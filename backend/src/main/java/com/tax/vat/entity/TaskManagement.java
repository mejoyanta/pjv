package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_management", indexes = {
        @Index(name = "idx_task_mgmt_slug", columnList = "slug"),
        @Index(name = "idx_task_mgmt_company_id", columnList = "company_id"),
        @Index(name = "idx_task_mgmt_deleted_at", columnList = "deleted_at")
})
public class TaskManagement extends BaseEntity {

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

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "work_for")
    private String workFor;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "assign_for")
    private Long assignFor;

    @Column(name = "assign_by")
    private Long assignBy;

    @Column(name = "employer_designation")
    private String employerDesignation;

    @Column(name = "image", columnDefinition = "text")
    private String image;

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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getWorkFor() {
        return workFor;
    }

    public void setWorkFor(String workFor) {
        this.workFor = workFor;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getAssignFor() {
        return assignFor;
    }

    public void setAssignFor(Long assignFor) {
        this.assignFor = assignFor;
    }

    public Long getAssignBy() {
        return assignBy;
    }

    public void setAssignBy(Long assignBy) {
        this.assignBy = assignBy;
    }

    public String getEmployerDesignation() {
        return employerDesignation;
    }

    public void setEmployerDesignation(String employerDesignation) {
        this.employerDesignation = employerDesignation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
