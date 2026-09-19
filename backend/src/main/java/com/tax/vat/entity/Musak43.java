package com.tax.vat.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "musak_4_3s")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Musak43 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", length = 255)
    private String slug;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "pad_date")
    private LocalDate padDate;

    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @Column(name = "purchase_id")
    private Long purchaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", insertable = false, updatable = false)
    private Purchase purchase;

    @Column(name = "supplyment_unit_id")
    private Long supplymentUnitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplyment_unit_id", insertable = false, updatable = false)
    private UnitOfSupply unit;

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

    @Column(name = "manufacture_id")
    private Long manufactureId;

    @Column(name = "hs_code", length = 255)
    private String hsCode;

    @Column(name = "product_service_details", length = 255)
    private String productServiceDetails;

    @Column(name = "chassis_or_description", length = 255)
    private String chassisOrDescription;

    @Column(name = "base_price")
    private Double basePrice;

    @Column(name = "sd")
    private Double sd;

    @Column(name = "assessable", length = 255)
    private String assessable;

    @Column(name = "profit", length = 255)
    private String profit;

    @Column(name = "total", length = 255)
    private String total;

    @Column(name = "purchase_quantity")
    private Double purchaseQuantity;

    @Column(name = "sell_price")
    private Double sellPrice;

    @Column(name = "submission_id", length = 255)
    private String submissionId;

    @Column(name = "submission_date")
    private LocalDate submissionDate;

    @Column(name = "vat_15")
    private Double vat15;

    @Column(name = "vat_type", length = 255)
    private String vatType;

    @Column(name = "vat_amount")
    private Double vatAmount;

    @Column(name = "total_cost")
    private Double totalCost;

    @Column(name = "total_additional_cost")
    private Double totalAdditionalCost;

    @Column(name = "hd_sd_percent")
    private Double hdSdPercent;

    @Column(name = "hd_vat_percent")
    private Double hdVatPercent;

    @Column(name = "hd_wholesale_rate")
    private Double hdWholesaleRate;

    @Column(name = "hd_retailer_amount")
    private Double hdRetailerAmount;

    @Column(name = "hd_mrp_rate")
    private Double hdMrpRate;

    @Column(name = "hd_surcharge_percent")
    private Double hdSurchargePercent;

    @Column(name = "hd_wholesale_percent")
    private Double hdWholesalePercent;

    @Column(name = "hd_retailer_percent")
    private Double hdRetailerPercent;

    @Column(name = "hd_mrp_percent")
    private Double hdMrpPercent;

    @Column(name = "hd_total_percent")
    private Double hdTotalPercent;

    @Column(name = "hd_cost_price")
    private Double hdCostPrice;

    @Column(name = "hd_sd_amount")
    private Double hdSdAmount;

    @Column(name = "hd_vat_amount")
    private Double hdVatAmount;

    @Column(name = "hd_surcharge_amount")
    private Double hdSurchargeAmount;

    @Column(name = "hd_factory_rate")
    private Double hdFactoryRate;

    @Column(name = "hd_total_sale_price")
    private Double hdTotalSalePrice;

    @Column(name = "hd_use_total_percent")
    private Boolean hdUseTotalPercent;

    @Column(name = "hd_personal_use")
    private Boolean hdPersonalUse;

    @Column(name = "without_vat")
    private Boolean withoutVat;

    @Column(name = "hd_barcode", length = 255)
    private String hdBarcode;

    @Column(name = "hd_tag_no", length = 255)
    private String hdTagNo;

    @Column(name = "hd_design_no", length = 255)
    private String hdDesignNo;

    @Column(name = "is_draft")
    private Boolean isDraft;

    @Column(name = "is_show")
    private Boolean isShow;

    @Column(name = "parent_4_3_id")
    private Long parent43Id;

    @Column(name = "amendment_comment", length = 255)
    private String amendmentComment;

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

    public Musak43() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (isDraft == null) isDraft = false;
        if (isShow == null) isShow = false;
        if (basePrice == null) basePrice = 0.0;
        if (totalAdditionalCost == null) totalAdditionalCost = 0.0;
        if (totalCost == null) {
            totalCost = basePrice + totalAdditionalCost;
        }
        if (sellPrice == null) sellPrice = totalCost;
        if (purchaseQuantity == null) purchaseQuantity = 1.0;
        if (slug == null || slug.trim().isEmpty()) {
            slug = java.util.UUID.randomUUID().toString();
        }
        if (hdSdPercent == null) hdSdPercent = 0.0;
        if (hdVatPercent == null) hdVatPercent = 0.0;
        if (hdSurchargePercent == null) hdSurchargePercent = 0.0;
        if (hdWholesalePercent == null) hdWholesalePercent = 0.0;
        if (hdRetailerPercent == null) hdRetailerPercent = 0.0;
        if (hdMrpPercent == null) hdMrpPercent = 0.0;
        if (hdTotalPercent == null) hdTotalPercent = 0.0;
        if (hdCostPrice == null) hdCostPrice = 0.0;
        if (hdSdAmount == null) hdSdAmount = 0.0;
        if (hdVatAmount == null) hdVatAmount = 0.0;
        if (hdSurchargeAmount == null) hdSurchargeAmount = 0.0;
        if (hdFactoryRate == null) hdFactoryRate = 0.0;
        if (hdWholesaleRate == null) hdWholesaleRate = 0.0;
        if (hdRetailerAmount == null) hdRetailerAmount = 0.0;
        if (hdMrpRate == null) hdMrpRate = 0.0;
        if (hdTotalSalePrice == null) hdTotalSalePrice = 0.0;
        if (hdUseTotalPercent == null) hdUseTotalPercent = true;
        if (hdPersonalUse == null) hdPersonalUse = false;
        if (withoutVat == null) withoutVat = false;
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

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDate getPadDate() { return padDate; }
    public void setPadDate(LocalDate padDate) { this.padDate = padDate; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Long getPurchaseId() { return purchaseId; }
    public void setPurchaseId(Long purchaseId) { this.purchaseId = purchaseId; }

    public Purchase getPurchase() { return purchase; }
    public void setPurchase(Purchase purchase) { this.purchase = purchase; }

    public Long getSupplymentUnitId() { return supplymentUnitId; }
    public void setSupplymentUnitId(Long supplymentUnitId) { this.supplymentUnitId = supplymentUnitId; }

    public UnitOfSupply getUnit() { return unit; }
    public void setUnit(UnitOfSupply unit) { this.unit = unit; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Long getCompanyBranchId() { return companyBranchId; }
    public void setCompanyBranchId(Long companyBranchId) { this.companyBranchId = companyBranchId; }

    public CompanyBranch getCompanyBranch() { return companyBranch; }
    public void setCompanyBranch(CompanyBranch companyBranch) { this.companyBranch = companyBranch; }

    public Long getManufactureId() { return manufactureId; }
    public void setManufactureId(Long manufactureId) { this.manufactureId = manufactureId; }

    public String getHsCode() { return hsCode; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }

    public String getProductServiceDetails() { return productServiceDetails; }
    public void setProductServiceDetails(String productServiceDetails) { this.productServiceDetails = productServiceDetails; }

    public String getChassisOrDescription() { return chassisOrDescription; }
    public void setChassisOrDescription(String chassisOrDescription) { this.chassisOrDescription = chassisOrDescription; }

    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

    public Double getSd() { return sd; }
    public void setSd(Double sd) { this.sd = sd; }

    public String getAssessable() { return assessable; }
    public void setAssessable(String assessable) { this.assessable = assessable; }

    public String getProfit() { return profit; }
    public void setProfit(String profit) { this.profit = profit; }

    public String getTotal() { return total; }
    public void setTotal(String total) { this.total = total; }

    public Double getPurchaseQuantity() { return purchaseQuantity; }
    public void setPurchaseQuantity(Double purchaseQuantity) { this.purchaseQuantity = purchaseQuantity; }

    public Double getSellPrice() { return sellPrice; }
    public void setSellPrice(Double sellPrice) { this.sellPrice = sellPrice; }

    public String getSubmissionId() { return submissionId; }
    public void setSubmissionId(String submissionId) { this.submissionId = submissionId; }

    public LocalDate getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDate submissionDate) { this.submissionDate = submissionDate; }

    public Double getVat15() { return vat15; }
    public void setVat15(Double vat15) { this.vat15 = vat15; }

    public String getVatType() { return vatType; }
    public void setVatType(String vatType) { this.vatType = vatType; }

    public Double getVatAmount() { return vatAmount; }
    public void setVatAmount(Double vatAmount) { this.vatAmount = vatAmount; }

    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

    public Double getTotalAdditionalCost() { return totalAdditionalCost; }
    public void setTotalAdditionalCost(Double totalAdditionalCost) { this.totalAdditionalCost = totalAdditionalCost; }

    public Double getHdSdPercent() { return hdSdPercent; }
    public void setHdSdPercent(Double hdSdPercent) { this.hdSdPercent = hdSdPercent; }

    public Double getHdVatPercent() { return hdVatPercent; }
    public void setHdVatPercent(Double hdVatPercent) { this.hdVatPercent = hdVatPercent; }

    public Double getHdWholesaleRate() { return hdWholesaleRate; }
    public void setHdWholesaleRate(Double hdWholesaleRate) { this.hdWholesaleRate = hdWholesaleRate; }

    public Double getHdRetailerAmount() { return hdRetailerAmount; }
    public void setHdRetailerAmount(Double hdRetailerAmount) { this.hdRetailerAmount = hdRetailerAmount; }

    public Double getHdMrpRate() { return hdMrpRate; }
    public void setHdMrpRate(Double hdMrpRate) { this.hdMrpRate = hdMrpRate; }

    public Boolean getIsDraft() { return isDraft; }
    public void setIsDraft(Boolean isDraft) { this.isDraft = isDraft; }

    public Boolean getIsShow() { return isShow; }
    public void setIsShow(Boolean isShow) { this.isShow = isShow; }

    public Long getParent43Id() { return parent43Id; }
    public void setParent43Id(Long parent43Id) { this.parent43Id = parent43Id; }

    public String getAmendmentComment() { return amendmentComment; }
    public void setAmendmentComment(String amendmentComment) { this.amendmentComment = amendmentComment; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Double getHdSurchargePercent() { return hdSurchargePercent; }
    public void setHdSurchargePercent(Double hdSurchargePercent) { this.hdSurchargePercent = hdSurchargePercent; }

    public Double getHdWholesalePercent() { return hdWholesalePercent; }
    public void setHdWholesalePercent(Double hdWholesalePercent) { this.hdWholesalePercent = hdWholesalePercent; }

    public Double getHdRetailerPercent() { return hdRetailerPercent; }
    public void setHdRetailerPercent(Double hdRetailerPercent) { this.hdRetailerPercent = hdRetailerPercent; }

    public Double getHdMrpPercent() { return hdMrpPercent; }
    public void setHdMrpPercent(Double hdMrpPercent) { this.hdMrpPercent = hdMrpPercent; }

    public Double getHdTotalPercent() { return hdTotalPercent; }
    public void setHdTotalPercent(Double hdTotalPercent) { this.hdTotalPercent = hdTotalPercent; }

    public Double getHdCostPrice() { return hdCostPrice; }
    public void setHdCostPrice(Double hdCostPrice) { this.hdCostPrice = hdCostPrice; }

    public Double getHdSdAmount() { return hdSdAmount; }
    public void setHdSdAmount(Double hdSdAmount) { this.hdSdAmount = hdSdAmount; }

    public Double getHdVatAmount() { return hdVatAmount; }
    public void setHdVatAmount(Double hdVatAmount) { this.hdVatAmount = hdVatAmount; }

    public Double getHdSurchargeAmount() { return hdSurchargeAmount; }
    public void setHdSurchargeAmount(Double hdSurchargeAmount) { this.hdSurchargeAmount = hdSurchargeAmount; }

    public Double getHdFactoryRate() { return hdFactoryRate; }
    public void setHdFactoryRate(Double hdFactoryRate) { this.hdFactoryRate = hdFactoryRate; }

    public Double getHdTotalSalePrice() { return hdTotalSalePrice; }
    public void setHdTotalSalePrice(Double hdTotalSalePrice) { this.hdTotalSalePrice = hdTotalSalePrice; }

    public Boolean getHdUseTotalPercent() { return hdUseTotalPercent; }
    public void setHdUseTotalPercent(Boolean hdUseTotalPercent) { this.hdUseTotalPercent = hdUseTotalPercent; }

    public Boolean getHdPersonalUse() { return hdPersonalUse; }
    public void setHdPersonalUse(Boolean hdPersonalUse) { this.hdPersonalUse = hdPersonalUse; }

    public Boolean getWithoutVat() { return withoutVat; }
    public void setWithoutVat(Boolean withoutVat) { this.withoutVat = withoutVat; }

    public String getHdBarcode() { return hdBarcode; }
    public void setHdBarcode(String hdBarcode) { this.hdBarcode = hdBarcode; }

    public String getHdTagNo() { return hdTagNo; }
    public void setHdTagNo(String hdTagNo) { this.hdTagNo = hdTagNo; }

    public String getHdDesignNo() { return hdDesignNo; }
    public void setHdDesignNo(String hdDesignNo) { this.hdDesignNo = hdDesignNo; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
