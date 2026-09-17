package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_invoices")
public class CreditInvoice extends BaseEntity {

    private String slug;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Company company;

    @Column(name = "company_branch_id")
    private Long companyBranchId;

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "buyer_address")
    private String buyerAddress;

    @Column(name = "vi_no")
    private String viNo;

    @Column(name = "total_price")
    private Double totalPrice = 0.0;

    @Column(name = "vat_amount")
    private Double vatAmount = 0.0;

    @Column(name = "total_sale_amount")
    private Double totalSaleAmount = 0.0;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "payment_details")
    private String paymentDetails;

    private LocalDate date;

    private String comments;

    @Column(name = "credit_invoice_no")
    private String creditInvoiceNo;

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

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getBuyerAddress() { return buyerAddress; }
    public void setBuyerAddress(String buyerAddress) { this.buyerAddress = buyerAddress; }

    public String getViNo() { return viNo; }
    public void setViNo(String viNo) { this.viNo = viNo; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public Double getVatAmount() { return vatAmount; }
    public void setVatAmount(Double vatAmount) { this.vatAmount = vatAmount; }

    public Double getTotalSaleAmount() { return totalSaleAmount; }
    public void setTotalSaleAmount(Double totalSaleAmount) { this.totalSaleAmount = totalSaleAmount; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getPaymentDetails() { return paymentDetails; }
    public void setPaymentDetails(String paymentDetails) { this.paymentDetails = paymentDetails; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getCreditInvoiceNo() { return creditInvoiceNo; }
    public void setCreditInvoiceNo(String creditInvoiceNo) { this.creditInvoiceNo = creditInvoiceNo; }

    public Double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }

    public Double getDueAmount() { return dueAmount; }
    public void setDueAmount(Double dueAmount) { this.dueAmount = dueAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
