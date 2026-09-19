package com.tax.vat.dto.response;

import java.time.LocalDate;

public class Musak610DataTableItem {
    private Long id;
    private String slug;
    private Integer sn;
    private String company;
    private String sellerName;
    private String sellerAddress;
    private String sellerBinNid;
    private String sellerChallanNo;
    private String issueDate;
    private Double price;
    private String buyerName;
    private String buyerAddress;
    private String buyerBinNid;
    private String buyerIdentification;
    private String buyer;
    private LocalDate date;
    private Long viNo;
    private String saleType;
    private Double sale;

    public Musak610DataTableItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Integer getSn() { return sn; }
    public void setSn(Integer sn) { this.sn = sn; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getSellerAddress() { return sellerAddress; }
    public void setSellerAddress(String sellerAddress) { this.sellerAddress = sellerAddress; }

    public String getSellerBinNid() { return sellerBinNid; }
    public void setSellerBinNid(String sellerBinNid) { this.sellerBinNid = sellerBinNid; }

    public String getSellerChallanNo() { return sellerChallanNo; }
    public void setSellerChallanNo(String sellerChallanNo) { this.sellerChallanNo = sellerChallanNo; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getBuyerAddress() { return buyerAddress; }
    public void setBuyerAddress(String buyerAddress) { this.buyerAddress = buyerAddress; }

    public String getBuyerBinNid() { return buyerBinNid; }
    public void setBuyerBinNid(String buyerBinNid) { this.buyerBinNid = buyerBinNid; }

    public String getBuyerIdentification() { return buyerIdentification; }
    public void setBuyerIdentification(String buyerIdentification) { this.buyerIdentification = buyerIdentification; }

    public String getBuyer() { return buyer; }
    public void setBuyer(String buyer) { this.buyer = buyer; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Long getViNo() { return viNo; }
    public void setViNo(Long viNo) { this.viNo = viNo; }

    public String getSaleType() { return saleType; }
    public void setSaleType(String saleType) { this.saleType = saleType; }

    public Double getSale() { return sale; }
    public void setSale(Double sale) { this.sale = sale; }
}
