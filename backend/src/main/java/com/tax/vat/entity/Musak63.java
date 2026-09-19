package com.tax.vat.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "musak_6_3s")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Musak63 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", length = 255)
    private String slug;

    @Column(name = "company_id")
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    @Column(name = "company_branch_id")
    private Long companyBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_branch_id", insertable = false, updatable = false)
    private CompanyBranch companyBranch;

    @Column(name = "sale_id")
    private Long saleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", insertable = false, updatable = false)
    private Sale sale;

    @Column(name = "vi_no")
    private Long viNo;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "duplicate")
    private Boolean duplicate;

    @Column(name = "comments", length = 255)
    private String comments;

    @Column(name = "amendment")
    private Boolean amendment;

    @Column(name = "parent_6_3_id")
    private Long parent63Id;

    @Column(name = "amendment_comment", length = 255)
    private String amendmentComment;

    @Column(name = "payment_status", length = 255)
    private String paymentStatus;

    @Column(name = "is_show")
    private Boolean isShow;

    @Column(name = "sale_type_identify", length = 255)
    private String saleTypeIdentify;

    @Column(name = "is_duplicate_bn")
    private Boolean isDuplicateBn;

    @Column(name = "buyer_type", length = 255)
    private String buyerType;

    @Column(name = "show_in_tob")
    private Boolean showInTob;

    @Column(name = "rejection_reason", columnDefinition = "text")
    private String rejectionReason;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Musak63() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (duplicate == null) duplicate = false;
        if (amendment == null) amendment = false;
        if (isShow == null) isShow = false;
        if (isDuplicateBn == null) isDuplicateBn = false;
        if (showInTob == null) showInTob = true;
        if (paymentStatus == null || paymentStatus.trim().isEmpty()) paymentStatus = "pending";
        if (buyerType == null || buyerType.trim().isEmpty()) buyerType = "registered";
        if (slug == null || slug.trim().isEmpty()) {
            slug = java.util.UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Long getCompanyBranchId() { return companyBranchId; }
    public void setCompanyBranchId(Long companyBranchId) { this.companyBranchId = companyBranchId; }

    public CompanyBranch getCompanyBranch() { return companyBranch; }
    public void setCompanyBranch(CompanyBranch companyBranch) { this.companyBranch = companyBranch; }

    public Long getSaleId() { return saleId; }
    public void setSaleId(Long saleId) { this.saleId = saleId; }

    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }

    public Long getViNo() { return viNo; }
    public void setViNo(Long viNo) { this.viNo = viNo; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Boolean getDuplicate() { return duplicate; }
    public void setDuplicate(Boolean duplicate) { this.duplicate = duplicate; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Boolean getAmendment() { return amendment; }
    public void setAmendment(Boolean amendment) { this.amendment = amendment; }

    public Long getParent63Id() { return parent63Id; }
    public void setParent63Id(Long parent63Id) { this.parent63Id = parent63Id; }

    public String getAmendmentComment() { return amendmentComment; }
    public void setAmendmentComment(String amendmentComment) { this.amendmentComment = amendmentComment; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Boolean getIsShow() { return isShow; }
    public void setIsShow(Boolean isShow) { this.isShow = isShow; }

    public String getSaleTypeIdentify() { return saleTypeIdentify; }
    public void setSaleTypeIdentify(String saleTypeIdentify) { this.saleTypeIdentify = saleTypeIdentify; }

    public Boolean getIsDuplicateBn() { return isDuplicateBn; }
    public void setIsDuplicateBn(Boolean isDuplicateBn) { this.isDuplicateBn = isDuplicateBn; }

    public String getBuyerType() { return buyerType; }
    public void setBuyerType(String buyerType) { this.buyerType = buyerType; }

    public Boolean getShowInTob() { return showInTob; }
    public void setShowInTob(Boolean showInTob) { this.showInTob = showInTob; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
