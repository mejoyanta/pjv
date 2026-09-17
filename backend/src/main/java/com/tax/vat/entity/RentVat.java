package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rent_vats")
public class RentVat extends BaseEntity {

    private String slug;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Company company;

    @Column(name = "company_branch_id")
    private Long companyBranchId;

    @Column(name = "challan_no")
    private String challanNo;

    private String date;

    @Column(name = "show_date")
    private String showDate;

    private String amount;

    private String vat;

    private String note;

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

    public String getChallanNo() { return challanNo; }
    public void setChallanNo(String challanNo) { this.challanNo = challanNo; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getShowDate() { return showDate; }
    public void setShowDate(String showDate) { this.showDate = showDate; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getVat() { return vat; }
    public void setVat(String vat) { this.vat = vat; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
