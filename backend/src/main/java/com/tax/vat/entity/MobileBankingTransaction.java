package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mobile_banking_transactions")
public class MobileBankingTransaction extends BaseEntity {

    private String slug;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Company company;

    @Column(name = "mobile_banking_provider_id")
    private Long mobileBankingProviderId;

    @Column(name = "mobile_banking_account_id")
    private Long mobileBankingAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mobile_banking_account_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private MobileBankingAccount mobileBankingAccount;

    private LocalDate date;

    @Column(name = "opening_balance")
    private Double openingBalance = 0.0;

    private Double deposit = 0.0;

    private Double withdraw = 0.0;

    @Column(name = "send_money")
    private Double sendMoney = 0.0;

    @Column(name = "receive_money")
    private Double receiveMoney = 0.0;

    private Double payment = 0.0;

    @Column(name = "closing_balance")
    private Double closingBalance = 0.0;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "purpose_of")
    private String purposeOf;

    private String status = "active";

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Long getMobileBankingProviderId() { return mobileBankingProviderId; }
    public void setMobileBankingProviderId(Long mobileBankingProviderId) { this.mobileBankingProviderId = mobileBankingProviderId; }

    public Long getMobileBankingAccountId() { return mobileBankingAccountId; }
    public void setMobileBankingAccountId(Long mobileBankingAccountId) { this.mobileBankingAccountId = mobileBankingAccountId; }

    public MobileBankingAccount getMobileBankingAccount() { return mobileBankingAccount; }
    public void setMobileBankingAccount(MobileBankingAccount mobileBankingAccount) { this.mobileBankingAccount = mobileBankingAccount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(Double openingBalance) { this.openingBalance = openingBalance; }

    public Double getDeposit() { return deposit; }
    public void setDeposit(Double deposit) { this.deposit = deposit; }

    public Double getWithdraw() { return withdraw; }
    public void setWithdraw(Double withdraw) { this.withdraw = withdraw; }

    public Double getSendMoney() { return sendMoney; }
    public void setSendMoney(Double sendMoney) { this.sendMoney = sendMoney; }

    public Double getReceiveMoney() { return receiveMoney; }
    public void setReceiveMoney(Double receiveMoney) { this.receiveMoney = receiveMoney; }

    public Double getPayment() { return payment; }
    public void setPayment(Double payment) { this.payment = payment; }

    public Double getClosingBalance() { return closingBalance; }
    public void setClosingBalance(Double closingBalance) { this.closingBalance = closingBalance; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getPurposeOf() { return purposeOf; }
    public void setPurposeOf(String purposeOf) { this.purposeOf = purposeOf; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
