package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "bank_branches")
public class BankBranch extends BaseEntity {

    @Column(name = "bank_id", nullable = false)
    private Long bankId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Bank bank;

    @Column(nullable = false)
    private String name;

    private String district;

    private String address;

    public Long getBankId() { return bankId; }
    public void setBankId(Long bankId) { this.bankId = bankId; }

    public Bank getBank() { return bank; }
    public void setBank(Bank bank) { this.bank = bank; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
