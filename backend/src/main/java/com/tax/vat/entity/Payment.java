package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Column(name = "company_id")
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Company company;

    @Column(name = "package_id")
    private Long packageId;

    @Column(name = "tran_id")
    private String tranId;

    @Column(name = "val_id")
    private String valId;

    private Double amount = 0.0;

    private String status = "pending";

    @Column(name = "tran_date")
    private LocalDateTime tranDate;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "payment_details", columnDefinition = "TEXT")
    private String paymentDetails;

    @Column(name = "payment_month")
    private String paymentMonth;

    @Column(name = "paid_amount")
    private Double paidAmount = 0.0;

    @Column(name = "due_amount")
    private Double dueAmount = 0.0;

    @Column(name = "sending_number")
    private String sendingNumber;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "is_approved")
    private Boolean isApproved = false;

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Long getPackageId() { return packageId; }
    public void setPackageId(Long packageId) { this.packageId = packageId; }

    public String getTranId() { return tranId; }
    public void setTranId(String tranId) { this.tranId = tranId; }

    public String getValId() { return valId; }
    public void setValId(String valId) { this.valId = valId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTranDate() { return tranDate; }
    public void setTranDate(LocalDateTime tranDate) { this.tranDate = tranDate; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getPaymentDetails() { return paymentDetails; }
    public void setPaymentDetails(String paymentDetails) { this.paymentDetails = paymentDetails; }

    public String getPaymentMonth() { return paymentMonth; }
    public void setPaymentMonth(String paymentMonth) { this.paymentMonth = paymentMonth; }

    public Double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }

    public Double getDueAmount() { return dueAmount; }
    public void setDueAmount(Double dueAmount) { this.dueAmount = dueAmount; }

    public String getSendingNumber() { return sendingNumber; }
    public void setSendingNumber(String sendingNumber) { this.sendingNumber = sendingNumber; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }
}
