package com.tax.vat.dto.response;

import java.time.LocalDate;

public class Musak621DataTableItem {
    private Long id;
    private String slug;
    private Integer sn;
    private LocalDate purchaseDate;
    private String billOfEntry;
    private LocalDate saleDate;
    private Long viNo;
    private String companyName;
    private String buyer;
    private Double vatAmount;
    private Double sale;
    private String productName;
    private Double openingStock;
    private Double closingStock;

    public Musak621DataTableItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Integer getSn() { return sn; }
    public void setSn(Integer sn) { this.sn = sn; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getBillOfEntry() { return billOfEntry; }
    public void setBillOfEntry(String billOfEntry) { this.billOfEntry = billOfEntry; }

    public LocalDate getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }

    public Long getViNo() { return viNo; }
    public void setViNo(Long viNo) { this.viNo = viNo; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getBuyer() { return buyer; }
    public void setBuyer(String buyer) { this.buyer = buyer; }

    public Double getVatAmount() { return vatAmount; }
    public void setVatAmount(Double vatAmount) { this.vatAmount = vatAmount; }

    public Double getSale() { return sale; }
    public void setSale(Double sale) { this.sale = sale; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Double getOpeningStock() { return openingStock; }
    public void setOpeningStock(Double openingStock) { this.openingStock = openingStock; }

    public Double getClosingStock() { return closingStock; }
    public void setClosingStock(Double closingStock) { this.closingStock = closingStock; }
}
