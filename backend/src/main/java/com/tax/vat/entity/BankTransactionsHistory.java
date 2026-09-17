package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_transactions_histories")
public class BankTransactionsHistory extends BaseEntity {

    private String slug;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Company company;

    @Column(name = "bank_id")
    private Long bankId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Bank bank;

    @Column(name = "bank_info_detail_id")
    private Long bankInfoDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_info_detail_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private BankInfoDetail bankInfoDetail;

    private LocalDate date;

    @Column(name = "opening_balance")
    private Double openingBalance = 0.0;

    private Double deposit = 0.0;

    private Double debit = 0.0;

    private Double withdraw = 0.0;

    private Double credit = 0.0;

    @Column(name = "closing_balance")
    private Double closingBalance = 0.0;

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

    public Long getBankId() { return bankId; }
    public void setBankId(Long bankId) { this.bankId = bankId; }

    public Bank getBank() { return bank; }
    public void setBank(Bank bank) { this.bank = bank; }

    public Long getBankInfoDetailId() { return bankInfoDetailId; }
    public void setBankInfoDetailId(Long bankInfoDetailId) { this.bankInfoDetailId = bankInfoDetailId; }

    public BankInfoDetail getBankInfoDetail() { return bankInfoDetail; }
    public void setBankInfoDetail(BankInfoDetail bankInfoDetail) { this.bankInfoDetail = bankInfoDetail; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(Double openingBalance) { this.openingBalance = openingBalance; }

    public Double getDeposit() { return deposit; }
    public void setDeposit(Double deposit) { this.deposit = deposit; }

    public Double getDebit() { return debit; }
    public void setDebit(Double debit) { this.debit = debit; }

    public Double getWithdraw() { return withdraw; }
    public void setWithdraw(Double withdraw) { this.withdraw = withdraw; }

    public Double getCredit() { return credit; }
    public void setCredit(Double credit) { this.credit = credit; }

    public Double getClosingBalance() { return closingBalance; }
    public void setClosingBalance(Double closingBalance) { this.closingBalance = closingBalance; }

    public String getPurposeOf() { return purposeOf; }
    public void setPurposeOf(String purposeOf) { this.purposeOf = purposeOf; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
