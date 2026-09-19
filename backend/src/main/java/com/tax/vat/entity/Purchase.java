package com.tax.vat.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
@SQLDelete(sql = "UPDATE purchases SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Purchase extends BaseEntity {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private String slug;

    @Column(name = "purchase_type", nullable = false)
    private String purchaseType = "foreign"; // "foreign" | "local"

    @Column(name = "bill_of_entry", nullable = false)
    private String billOfEntry;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "purchase_show_date")
    private LocalDate purchaseShowDate;

    @Column(name = "lc_no")
    private String lcNo;

    @Column(name = "lc_date")
    private LocalDate lcDate;

    @Column(name = "boe_office_no")
    private String boeOfficeNo;

    @Column(name = "cpc_item_no")
    private String cpcItemNo;

    @Column(name = "boe_item")
    private String boeItem;

    @Column(nullable = false)
    private Double quantity = 1.0;

    private Double assessable = 0.0;
    private Double cd = 0.0;
    private Double rd = 0.0;

    @Column(name = "base_price", nullable = false)
    private Double basePrice = 0.0;

    @Column(name = "sell_price", nullable = false)
    private Double sellPrice = 0.0;

    @Column(name = "vat_type", nullable = false)
    private String vatType = "exclude";

    @Column(nullable = false)
    private Double vat = 15.0;

    private Double sd = 0.0;
    private Double at = 0.0;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount = 0.0;

    @Column(name = "engine_no")
    private String engineNo;

    @Column(name = "chassis_or_description")
    private String chassisOrDescription;

    private String seller;

    @Column(name = "seller_address")
    private String sellerAddress;

    @Column(name = "seller_phone")
    private String sellerPhone;

    @Column(name = "country_region")
    private String countryRegion;

    @Column(name = "assessment_date")
    private LocalDate assessmentDate;

    private String description;
    private String image;

    @Column(name = "is_draft", nullable = false)
    private Boolean isDraft = false;

    @Column(name = "add_into_9_1", nullable = false)
    private Boolean addInto91 = false;

    @Column(name = "is_sale", nullable = false)
    private Boolean isSale = false;

    @Column(name = "is_show", nullable = false)
    private Boolean isShow = false;

    @Column(name = "personal_use", nullable = false)
    private Boolean personalUse = false;

    @Column(name = "vds", nullable = false)
    private Boolean vds = false;

    @Column(name = "batch_no")
    private String batchNo;

    @Column(name = "grand_total")
    private Double grandTotal;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    // Foreign Keys
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private Company company;

    @Column(name = "company_branch_id")
    private Long companyBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_branch_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private CompanyBranch companyBranch;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private Product product;

    @Column(name = "supplier_id")
    private Long supplierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private Supplier supplier;

    @Column(name = "port_id")
    private Long portId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "port_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private Port port;

    @Column(name = "unit_of_supply_id")
    private Long unitOfSupplyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_of_supply_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private UnitOfSupply unitOfSupply;

    // Getters and Setters
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getPurchaseType() { return purchaseType; }
    public void setPurchaseType(String purchaseType) { this.purchaseType = purchaseType; }

    public String getBillOfEntry() { return billOfEntry; }
    public void setBillOfEntry(String billOfEntry) { this.billOfEntry = billOfEntry; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDate getPurchaseShowDate() { return purchaseShowDate; }
    public void setPurchaseShowDate(LocalDate purchaseShowDate) { this.purchaseShowDate = purchaseShowDate; }

    public String getLcNo() { return lcNo; }
    public void setLcNo(String lcNo) { this.lcNo = lcNo; }

    public LocalDate getLcDate() { return lcDate; }
    public void setLcDate(LocalDate lcDate) { this.lcDate = lcDate; }

    public String getBoeOfficeNo() { return boeOfficeNo; }
    public void setBoeOfficeNo(String boeOfficeNo) { this.boeOfficeNo = boeOfficeNo; }

    public String getCpcItemNo() { return cpcItemNo; }
    public void setCpcItemNo(String cpcItemNo) { this.cpcItemNo = cpcItemNo; }

    public String getBoeItem() { return boeItem; }
    public void setBoeItem(String boeItem) { this.boeItem = boeItem; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Double getAssessable() { return assessable; }
    public void setAssessable(Double assessable) { this.assessable = assessable; }

    public Double getCd() { return cd; }
    public void setCd(Double cd) { this.cd = cd; }

    public Double getRd() { return rd; }
    public void setRd(Double rd) { this.rd = rd; }

    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

    public Double getSellPrice() { return sellPrice; }
    public void setSellPrice(Double sellPrice) { this.sellPrice = sellPrice; }

    public String getVatType() { return vatType; }
    public void setVatType(String vatType) { this.vatType = vatType; }

    public Double getVat() { return vat; }
    public void setVat(Double vat) { this.vat = vat; }

    public Double getSd() { return sd; }
    public void setSd(Double sd) { this.sd = sd; }

    public Double getAt() { return at; }
    public void setAt(Double at) { this.at = at; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getEngineNo() { return engineNo; }
    public void setEngineNo(String engineNo) { this.engineNo = engineNo; }

    public String getChassisOrDescription() { return chassisOrDescription; }
    public void setChassisOrDescription(String chassisOrDescription) { this.chassisOrDescription = chassisOrDescription; }

    public String getSeller() { return seller; }
    public void setSeller(String seller) { this.seller = seller; }

    public String getSellerAddress() { return sellerAddress; }
    public void setSellerAddress(String sellerAddress) { this.sellerAddress = sellerAddress; }

    public String getSellerPhone() { return sellerPhone; }
    public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }

    public String getCountryRegion() { return countryRegion; }
    public void setCountryRegion(String countryRegion) { this.countryRegion = countryRegion; }

    public LocalDate getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(LocalDate assessmentDate) { this.assessmentDate = assessmentDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Boolean getIsDraft() { return isDraft; }
    public void setIsDraft(Boolean draft) { isDraft = draft; }

    public Boolean getAddInto91() { return addInto91; }
    public void setAddInto91(Boolean addInto91) { this.addInto91 = addInto91; }

    public Boolean getIsSale() { return isSale; }
    public void setIsSale(Boolean sale) { isSale = sale; }

    public Boolean getIsShow() { return isShow; }
    public void setIsShow(Boolean show) { isShow = show; }

    public Boolean getPersonalUse() { return personalUse; }
    public void setPersonalUse(Boolean personalUse) { this.personalUse = personalUse; }

    public Boolean getVds() { return vds; }
    public void setVds(Boolean vds) { this.vds = vds; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public Double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(Double grandTotal) { this.grandTotal = grandTotal; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Long getCompanyBranchId() { return companyBranchId; }
    public void setCompanyBranchId(Long companyBranchId) { this.companyBranchId = companyBranchId; }

    public CompanyBranch getCompanyBranch() { return companyBranch; }
    public void setCompanyBranch(CompanyBranch companyBranch) { this.companyBranch = companyBranch; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public Long getPortId() { return portId; }
    public void setPortId(Long portId) { this.portId = portId; }

    public Port getPort() { return port; }
    public void setPort(Port port) { this.port = port; }

    public Long getUnitOfSupplyId() { return unitOfSupplyId; }
    public void setUnitOfSupplyId(Long unitOfSupplyId) { this.unitOfSupplyId = unitOfSupplyId; }

    public UnitOfSupply getUnitOfSupply() { return unitOfSupply; }
    public void setUnitOfSupply(UnitOfSupply unitOfSupply) { this.unitOfSupply = unitOfSupply; }
}
