package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bank_treasuries")
public class BankTreasury extends BaseEntity {

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

    @Column(name = "branch_id")
    private Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private BankBranch branch;

    private LocalDate date;

    @Column(name = "challan_no")
    private String challanNo;

    private Double amount = 0.0;

    @Column(name = "account_code")
    private String accountCode;

    @Column(name = "add_into_9_1")
    private Boolean addInto91 = false;

    @Column(columnDefinition = "TEXT")
    private String comments;

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Long getBankId() { return bankId; }
    public void setBankId(Long bankId) { this.bankId = bankId; }

    public Bank getBank() { return bank; }
    public void setBank(Bank bank) { this.bank = bank; }

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }

    public BankBranch getBranch() { return branch; }
    public void setBranch(BankBranch branch) { this.branch = branch; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getChallanNo() { return challanNo; }
    public void setChallanNo(String challanNo) { this.challanNo = challanNo; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String accountCode) { this.accountCode = accountCode; }

    public Boolean getAddInto91() { return addInto91; }
    public void setAddInto91(Boolean addInto91) { this.addInto91 = addInto91; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
