package com.tax.vat.dto.response;

import java.time.LocalDate;

public class Musak63DataTableItem {
    private Long id;
    private String slug;
    private Integer sn;
    private LocalDate date;
    private Long viNo;
    private String companyName;
    private String branchName;
    private String buyer;
    private String buyerBin;
    private String buyerAddress;
    private Double vatAmount;
    private Double subTotal;
    private Double totalAmount;
    private Boolean isThisCreditNote;
    private String saleTypeIdentify;
    private String paymentStatus;

    public Musak63DataTableItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Integer getSn() { return sn; }
    public void setSn(Integer sn) { this.sn = sn; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Long getViNo() { return viNo; }
    public void setViNo(Long viNo) { this.viNo = viNo; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getBuyer() { return buyer; }
    public void setBuyer(String buyer) { this.buyer = buyer; }

    public String getBuyerBin() { return buyerBin; }
    public void setBuyerBin(String buyerBin) { this.buyerBin = buyerBin; }

    public String getBuyerAddress() { return buyerAddress; }
    public void setBuyerAddress(String buyerAddress) { this.buyerAddress = buyerAddress; }

    public Double getVatAmount() { return vatAmount; }
    public void setVatAmount(Double vatAmount) { this.vatAmount = vatAmount; }

    public Double getSubTotal() { return subTotal; }
    public void setSubTotal(Double subTotal) { this.subTotal = subTotal; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Boolean getIsThisCreditNote() { return isThisCreditNote; }
    public void setIsThisCreditNote(Boolean isThisCreditNote) { this.isThisCreditNote = isThisCreditNote; }

    public String getSaleTypeIdentify() { return saleTypeIdentify; }
    public void setSaleTypeIdentify(String saleTypeIdentify) { this.saleTypeIdentify = saleTypeIdentify; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
