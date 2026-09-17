package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "debit_invoices")
public class DebitInvoice extends BaseEntity {

    private String slug;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Company company;

    @Column(name = "company_branch_id")
    private Long companyBranchId;

    private String seller;

    @Column(name = "seller_address")
    private String sellerAddress;

    @Column(name = "purchase_type")
    private String purchaseType;

    private String description;

    @Column(name = "bill_of_entry")
    private String billOfEntry;

    @Column(name = "base_price")
    private Double basePrice = 0.0;

    private Double vat = 0.0;

    @Column(name = "vat_amount")
    private Double vatAmount = 0.0;

    @Column(name = "total_amount")
    private Double totalAmount = 0.0;

    private LocalDate date;

    private String comments;

    @Column(name = "debit_invoice_no")
    private String debitInvoiceNo;

    @Column(name = "paid_amount")
    private Double paidAmount = 0.0;

    @Column(name = "due_amount")
    private Double dueAmount = 0.0;

    @Column(name = "payment_status")
    private String paymentStatus = "pending";

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Long getCompanyBranchId() { return companyBranchId; }
    public void setCompanyBranchId(Long companyBranchId) { this.companyBranchId = companyBranchId; }

    public String getSeller() { return seller; }
    public void setSeller(String seller) { this.seller = seller; }

    public String getSellerAddress() { return sellerAddress; }
    public void setSellerAddress(String sellerAddress) { this.sellerAddress = sellerAddress; }

    public String getPurchaseType() { return purchaseType; }
    public void setPurchaseType(String purchaseType) { this.purchaseType = purchaseType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBillOfEntry() { return billOfEntry; }
    public void setBillOfEntry(String billOfEntry) { this.billOfEntry = billOfEntry; }

    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

    public Double getVat() { return vat; }
    public void setVat(Double vat) { this.vat = vat; }

    public Double getVatAmount() { return vatAmount; }
    public void setVatAmount(Double vatAmount) { this.vatAmount = vatAmount; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getDebitInvoiceNo() { return debitInvoiceNo; }
    public void setDebitInvoiceNo(String debitInvoiceNo) { this.debitInvoiceNo = debitInvoiceNo; }

    public Double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }

    public Double getDueAmount() { return dueAmount; }
    public void setDueAmount(Double dueAmount) { this.dueAmount = dueAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
