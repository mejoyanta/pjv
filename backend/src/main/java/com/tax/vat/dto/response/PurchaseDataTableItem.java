package com.tax.vat.dto.response;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PurchaseDataTableItem implements Serializable {
    private Long id;
    private String slug;
    private Long sn;
    private String purchaseType;
    private LocalDate date;
    private LocalDate boeDate;
    private Long companyId;
    private String companyName;
    private String branchName;
    private String bin;
    private String tin;
    private String hsCode;
    private String productDescription;
    private String billOfEntry;
    private String supplierName;
    private String supplierAddress;
    private String supplyBinNid;
    private Double assessable;
    private Double vatAmount;
    private Double sdAmount;
    private Double atAmount;
    private Double totalPurchaseAmount;
    private Double wholesaleRate;
    private Double retailerRate;
    private Double quantity;
    private Double basePrice;
    private Boolean isDraft;
    private LocalDateTime createdAt;

    public PurchaseDataTableItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Long getSn() { return sn; }
    public void setSn(Long sn) { this.sn = sn; }

    public String getPurchaseType() { return purchaseType; }
    public void setPurchaseType(String purchaseType) { this.purchaseType = purchaseType; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDate getBoeDate() { return boeDate; }
    public void setBoeDate(LocalDate boeDate) { this.boeDate = boeDate; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getBin() { return bin; }
    public void setBin(String bin) { this.bin = bin; }

    public String getTin() { return tin; }
    public void setTin(String tin) { this.tin = tin; }

    public String getHsCode() { return hsCode; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }

    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }

    public String getBillOfEntry() { return billOfEntry; }
    public void setBillOfEntry(String billOfEntry) { this.billOfEntry = billOfEntry; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getSupplierAddress() { return supplierAddress; }
    public void setSupplierAddress(String supplierAddress) { this.supplierAddress = supplierAddress; }

    public String getSupplyBinNid() { return supplyBinNid; }
    public void setSupplyBinNid(String supplyBinNid) { this.supplyBinNid = supplyBinNid; }

    public Double getAssessable() { return assessable; }
    public void setAssessable(Double assessable) { this.assessable = assessable; }

    public Double getVatAmount() { return vatAmount; }
    public void setVatAmount(Double vatAmount) { this.vatAmount = vatAmount; }

    public Double getSdAmount() { return sdAmount; }
    public void setSdAmount(Double sdAmount) { this.sdAmount = sdAmount; }

    public Double getAtAmount() { return atAmount; }
    public void setAtAmount(Double atAmount) { this.atAmount = atAmount; }

    public Double getTotalPurchaseAmount() { return totalPurchaseAmount; }
    public void setTotalPurchaseAmount(Double totalPurchaseAmount) { this.totalPurchaseAmount = totalPurchaseAmount; }

    public Double getWholesaleRate() { return wholesaleRate; }
    public void setWholesaleRate(Double wholesaleRate) { this.wholesaleRate = wholesaleRate; }

    public Double getRetailerRate() { return retailerRate; }
    public void setRetailerRate(Double retailerRate) { this.retailerRate = retailerRate; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

    public Boolean getIsDraft() { return isDraft; }
    public void setIsDraft(Boolean draft) { isDraft = draft; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
