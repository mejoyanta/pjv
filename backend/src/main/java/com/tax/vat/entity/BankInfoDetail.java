package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_info_details")
public class BankInfoDetail extends BaseEntity {

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

    @Column(name = "bank_branch_id")
    private Long bankBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_branch_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private BankBranch bankBranch;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "acc_name")
    private String accName;

    @Column(name = "acc_number")
    private String accNumber;

    @Column(name = "routing_no")
    private String routingNo;

    @Column(name = "swift_code")
    private String swiftCode;

    private LocalDate date;

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

    public Long getBankBranchId() { return bankBranchId; }
    public void setBankBranchId(Long bankBranchId) { this.bankBranchId = bankBranchId; }

    public BankBranch getBankBranch() { return bankBranch; }
    public void setBankBranch(BankBranch bankBranch) { this.bankBranch = bankBranch; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAccName() { return accName; }
    public void setAccName(String accName) { this.accName = accName; }

    public String getAccNumber() { return accNumber; }
    public void setAccNumber(String accNumber) { this.accNumber = accNumber; }

    public String getRoutingNo() { return routingNo; }
    public void setRoutingNo(String routingNo) { this.routingNo = routingNo; }

    public String getSwiftCode() { return swiftCode; }
    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
