package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "legal_management_cases", indexes = {
        @Index(name = "idx_legal_case_slug", columnList = "slug"),
        @Index(name = "idx_legal_case_company_id", columnList = "company_id"),
        @Index(name = "idx_legal_case_deleted_at", columnList = "deleted_at")
})
public class LegalManagementCase extends BaseEntity {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private String slug;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Company company;

    @Column(name = "company_branch_id")
    private Long companyBranchId;

    @Column(name = "company_person_name")
    private String companyPersonName;

    @Column(name = "bar_id_no")
    private String barIdNo;

    @Column(name = "file_no")
    private String fileNo;

    @Column(name = "file_date")
    private LocalDate fileDate;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "representative_name")
    private String representativeName;

    @Column(name = "respondent_name")
    private String respondentName;

    @Column(name = "filing_lawyer")
    private String filingLawyer;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "case_no")
    private String caseNo;

    @Column(name = "court_name")
    private String courtName;

    @Column(name = "next_hearing_date")
    private LocalDate nextHearingDate;

    @Column(name = "comment_remarks", columnDefinition = "text")
    private String commentRemarks;

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Long getCompanyBranchId() {
        return companyBranchId;
    }

    public void setCompanyBranchId(Long companyBranchId) {
        this.companyBranchId = companyBranchId;
    }

    public String getCompanyPersonName() {
        return companyPersonName;
    }

    public void setCompanyPersonName(String companyPersonName) {
        this.companyPersonName = companyPersonName;
    }

    public String getBarIdNo() {
        return barIdNo;
    }

    public void setBarIdNo(String barIdNo) {
        this.barIdNo = barIdNo;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }

    public LocalDate getFileDate() {
        return fileDate;
    }

    public void setFileDate(LocalDate fileDate) {
        this.fileDate = fileDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRepresentativeName() {
        return representativeName;
    }

    public void setRepresentativeName(String representativeName) {
        this.representativeName = representativeName;
    }

    public String getRespondentName() {
        return respondentName;
    }

    public void setRespondentName(String respondentName) {
        this.respondentName = respondentName;
    }

    public String getFilingLawyer() {
        return filingLawyer;
    }

    public void setFilingLawyer(String filingLawyer) {
        this.filingLawyer = filingLawyer;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public LocalDate getNextHearingDate() {
        return nextHearingDate;
    }

    public void setNextHearingDate(LocalDate nextHearingDate) {
        this.nextHearingDate = nextHearingDate;
    }

    public String getCommentRemarks() {
        return commentRemarks;
    }

    public void setCommentRemarks(String commentRemarks) {
        this.commentRemarks = commentRemarks;
    }
}
