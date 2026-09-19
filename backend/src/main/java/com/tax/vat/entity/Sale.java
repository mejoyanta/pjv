package com.tax.vat.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sale {

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

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "sales_type", length = 255)
    private String salesType;

    @Column(name = "buyer_name", length = 255)
    private String buyerName;

    @Column(name = "buyer_phone", length = 255)
    private String buyerPhone;

    @Column(name = "buyer_address", length = 255)
    private String buyerAddress;

    @Column(name = "buyer_bin_tin_nid", length = 255)
    private String buyerBinTinNid;

    @Column(name = "vat_amount")
    private Double vatAmount;

    @Column(name = "total_sale_amount")
    private Double totalSaleAmount;

    @Column(name = "sd_amount")
    private Double sdAmount;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "payment_type", length = 255)
    private String paymentType;

    @Column(name = "buyer_type", length = 255)
    private String buyerType;

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

    public Sale() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
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

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getSalesType() { return salesType; }
    public void setSalesType(String salesType) { this.salesType = salesType; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getBuyerPhone() { return buyerPhone; }
    public void setBuyerPhone(String buyerPhone) { this.buyerPhone = buyerPhone; }

    public String getBuyerAddress() { return buyerAddress; }
    public void setBuyerAddress(String buyerAddress) { this.buyerAddress = buyerAddress; }

    public String getBuyerBinTinNid() { return buyerBinTinNid; }
    public void setBuyerBinTinNid(String buyerBinTinNid) { this.buyerBinTinNid = buyerBinTinNid; }

    public Double getVatAmount() { return vatAmount; }
    public void setVatAmount(Double vatAmount) { this.vatAmount = vatAmount; }

    public Double getTotalSaleAmount() { return totalSaleAmount; }
    public void setTotalSaleAmount(Double totalSaleAmount) { this.totalSaleAmount = totalSaleAmount; }

    public Double getSdAmount() { return sdAmount; }
    public void setSdAmount(Double sdAmount) { this.sdAmount = sdAmount; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getBuyerType() { return buyerType; }
    public void setBuyerType(String buyerType) { this.buyerType = buyerType; }

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
