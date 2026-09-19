package com.tax.vat.dto.response;

import java.time.LocalDate;

public class Musak43DataTableItem {
    private Long id;
    private String slug;
    private Integer sn;
    private String submissionId;
    private LocalDate submissionDate;
    private LocalDate purchaseShowDate;
    private LocalDate boeDate;
    private LocalDate assessmentDate;
    private String billOfEntry;
    private String companyName;
    private String branchName;
    private String boeCpc;
    private String port;
    private String productServiceDetails;
    private String hsCode;
    private String unit;
    private Double purchasePrice;
    private Double qtyCost;
    private Double additionalCost;
    private Double profit;
    private Double salePrice;
    private Double wholesalePrice;
    private Double retailerAmount;

    public Musak43DataTableItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Integer getSn() { return sn; }
    public void setSn(Integer sn) { this.sn = sn; }

    public String getSubmissionId() { return submissionId; }
    public void setSubmissionId(String submissionId) { this.submissionId = submissionId; }

    public LocalDate getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDate submissionDate) { this.submissionDate = submissionDate; }

    public LocalDate getPurchaseShowDate() { return purchaseShowDate; }
    public void setPurchaseShowDate(LocalDate purchaseShowDate) { this.purchaseShowDate = purchaseShowDate; }

    public LocalDate getBoeDate() { return boeDate; }
    public void setBoeDate(LocalDate boeDate) { this.boeDate = boeDate; }

    public LocalDate getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(LocalDate assessmentDate) { this.assessmentDate = assessmentDate; }

    public String getBillOfEntry() { return billOfEntry; }
    public void setBillOfEntry(String billOfEntry) { this.billOfEntry = billOfEntry; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getBoeCpc() { return boeCpc; }
    public void setBoeCpc(String boeCpc) { this.boeCpc = boeCpc; }

    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }

    public String getProductServiceDetails() { return productServiceDetails; }
    public void setProductServiceDetails(String productServiceDetails) { this.productServiceDetails = productServiceDetails; }

    public String getHsCode() { return hsCode; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(Double purchasePrice) { this.purchasePrice = purchasePrice; }

    public Double getQtyCost() { return qtyCost; }
    public void setQtyCost(Double qtyCost) { this.qtyCost = qtyCost; }

    public Double getAdditionalCost() { return additionalCost; }
    public void setAdditionalCost(Double additionalCost) { this.additionalCost = additionalCost; }

    public Double getProfit() { return profit; }
    public void setProfit(Double profit) { this.profit = profit; }

    public Double getSalePrice() { return salePrice; }
    public void setSalePrice(Double salePrice) { this.salePrice = salePrice; }

    public Double getWholesalePrice() { return wholesalePrice; }
    public void setWholesalePrice(Double wholesalePrice) { this.wholesalePrice = wholesalePrice; }

    public Double getRetailerAmount() { return retailerAmount; }
    public void setRetailerAmount(Double retailerAmount) { this.retailerAmount = retailerAmount; }
}
