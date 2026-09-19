package com.tax.vat.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "musak_9_1s")
public class Musak91 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "submision_date")
    private LocalDate submisionDate;

    @Column(name = "return_type")
    private String returnType;

    @Column(name = "tax_period_activity")
    private Boolean taxPeriodActivity = true;

    @Column(name = "get_refund")
    private Boolean getRefund = false;

    @Column(name = "declaration_name")
    private String declarationName;

    @Column(name = "declaration_email")
    private String declarationEmail;

    @Column(name = "declaration_phone")
    private String declarationPhone;

    @Column(name = "declaration_designation")
    private String declarationDesignation;

    @Column(name = "declaration_nid_passport")
    private String declarationNidPassport;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    @Column(name = "purchase_ids", columnDefinition = "TEXT")
    private String purchaseIds;

    @Column(name = "material_purchase_ids", columnDefinition = "TEXT")
    private String materialPurchaseIds;

    @Column(name = "sales_ids", columnDefinition = "TEXT")
    private String salesIds;

    @Column(name = "debit_note_ids", columnDefinition = "TEXT")
    private String debitNoteIds;

    @Column(name = "credit_note_ids", columnDefinition = "TEXT")
    private String creditNoteIds;

    @Column(name = "musak_6_6_ids", columnDefinition = "TEXT")
    private String musak66Ids;

    @Column(name = "treasury_ids", columnDefinition = "TEXT")
    private String treasuryIds;

    @Column(name = "input_11")
    private Boolean input11 = true;

    @Column(name = "input_24")
    private String input24;

    @Column(name = "input_25")
    private String input25;

    @Column(name = "input_26")
    private String input26;

    @Column(name = "input_27")
    private String input27;

    @Column(name = "input_28")
    private String input28;

    @Column(name = "input_29")
    private String input29;

    @Column(name = "input_30")
    private String input30;

    @Column(name = "input_31")
    private String input31;

    @Column(name = "input_32")
    private String input32;

    @Column(name = "input_52")
    private String input52;

    @Column(name = "input_53")
    private String input53;

    @Column(name = "input_54")
    private String input54;

    @Column(name = "input_55")
    private String input55;

    @Column(name = "input_56")
    private String input56;

    @Column(name = "input_57")
    private String input57;

    @Column(name = "input_58")
    private String input58;

    @Column(name = "input_59")
    private String input59;

    @Column(name = "input_60")
    private String input60;

    @Column(name = "input_61")
    private String input61;

    @Column(name = "input_62")
    private String input62;

    @Column(name = "input_63")
    private String input63;

    @Column(name = "input_64")
    private String input64;

    @Column(name = "input_65")
    private String input65;

    @Column(name = "field_66")
    private Double field66 = 0.0;

    @Column(name = "input_67")
    private String input67;

    @Column(name = "input_68")
    private String input68;

    @Column(name = "input_65_adjustment")
    private String input65Adjustment;

    @Column(name = "extra_input", columnDefinition = "TEXT")
    private String extraInput;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Musak91() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDate getSubmisionDate() { return submisionDate; }
    public void setSubmisionDate(LocalDate submisionDate) { this.submisionDate = submisionDate; }

    public String getReturnType() { return returnType; }
    public void setReturnType(String returnType) { this.returnType = returnType; }

    public Boolean getTaxPeriodActivity() { return taxPeriodActivity; }
    public void setTaxPeriodActivity(Boolean taxPeriodActivity) { this.taxPeriodActivity = taxPeriodActivity; }

    public Boolean getGetRefund() { return getRefund; }
    public void setGetRefund(Boolean getRefund) { this.getRefund = getRefund; }

    public String getDeclarationName() { return declarationName; }
    public void setDeclarationName(String declarationName) { this.declarationName = declarationName; }

    public String getDeclarationEmail() { return declarationEmail; }
    public void setDeclarationEmail(String declarationEmail) { this.declarationEmail = declarationEmail; }

    public String getDeclarationPhone() { return declarationPhone; }
    public void setDeclarationPhone(String declarationPhone) { this.declarationPhone = declarationPhone; }

    public String getDeclarationDesignation() { return declarationDesignation; }
    public void setDeclarationDesignation(String declarationDesignation) { this.declarationDesignation = declarationDesignation; }

    public String getDeclarationNidPassport() { return declarationNidPassport; }
    public void setDeclarationNidPassport(String declarationNidPassport) { this.declarationNidPassport = declarationNidPassport; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getPurchaseIds() { return purchaseIds; }
    public void setPurchaseIds(String purchaseIds) { this.purchaseIds = purchaseIds; }

    public String getMaterialPurchaseIds() { return materialPurchaseIds; }
    public void setMaterialPurchaseIds(String materialPurchaseIds) { this.materialPurchaseIds = materialPurchaseIds; }

    public String getSalesIds() { return salesIds; }
    public void setSalesIds(String salesIds) { this.salesIds = salesIds; }

    public String getDebitNoteIds() { return debitNoteIds; }
    public void setDebitNoteIds(String debitNoteIds) { this.debitNoteIds = debitNoteIds; }

    public String getCreditNoteIds() { return creditNoteIds; }
    public void setCreditNoteIds(String creditNoteIds) { this.creditNoteIds = creditNoteIds; }

    public String getMusak66Ids() { return musak66Ids; }
    public void setMusak66Ids(String musak66Ids) { this.musak66Ids = musak66Ids; }

    public String getTreasuryIds() { return treasuryIds; }
    public void setTreasuryIds(String treasuryIds) { this.treasuryIds = treasuryIds; }

    public Boolean getInput11() { return input11; }
    public void setInput11(Boolean input11) { this.input11 = input11; }

    public String getInput24() { return input24; }
    public void setInput24(String input24) { this.input24 = input24; }

    public String getInput25() { return input25; }
    public void setInput25(String input25) { this.input25 = input25; }

    public String getInput26() { return input26; }
    public void setInput26(String input26) { this.input26 = input26; }

    public String getInput27() { return input27; }
    public void setInput27(String input27) { this.input27 = input27; }

    public String getInput28() { return input28; }
    public void setInput28(String input28) { this.input28 = input28; }

    public String getInput29() { return input29; }
    public void setInput29(String input29) { this.input29 = input29; }

    public String getInput30() { return input30; }
    public void setInput30(String input30) { this.input30 = input30; }

    public String getInput31() { return input31; }
    public void setInput31(String input31) { this.input31 = input31; }

    public String getInput32() { return input32; }
    public void setInput32(String input32) { this.input32 = input32; }

    public String getInput52() { return input52; }
    public void setInput52(String input52) { this.input52 = input52; }

    public String getInput53() { return input53; }
    public void setInput53(String input53) { this.input53 = input53; }

    public String getInput54() { return input54; }
    public void setInput54(String input54) { this.input54 = input54; }

    public String getInput55() { return input55; }
    public void setInput55(String input55) { this.input55 = input55; }

    public String getInput56() { return input56; }
    public void setInput56(String input56) { this.input56 = input56; }

    public String getInput57() { return input57; }
    public void setInput57(String input57) { this.input57 = input57; }

    public String getInput58() { return input58; }
    public void setInput58(String input58) { this.input58 = input58; }

    public String getInput59() { return input59; }
    public void setInput59(String input59) { this.input59 = input59; }

    public String getInput60() { return input60; }
    public void setInput60(String input60) { this.input60 = input60; }

    public String getInput61() { return input61; }
    public void setInput61(String input61) { this.input61 = input61; }

    public String getInput62() { return input62; }
    public void setInput62(String input62) { this.input62 = input62; }

    public String getInput63() { return input63; }
    public void setInput63(String input63) { this.input63 = input63; }

    public String getInput64() { return input64; }
    public void setInput64(String input64) { this.input64 = input64; }

    public String getInput65() { return input65; }
    public void setInput65(String input65) { this.input65 = input65; }

    public Double getField66() { return field66; }
    public void setField66(Double field66) { this.field66 = field66; }

    public String getInput67() { return input67; }
    public void setInput67(String input67) { this.input67 = input67; }

    public String getInput68() { return input68; }
    public void setInput68(String input68) { this.input68 = input68; }

    public String getInput65Adjustment() { return input65Adjustment; }
    public void setInput65Adjustment(String input65Adjustment) { this.input65Adjustment = input65Adjustment; }

    public String getExtraInput() { return extraInput; }
    public void setExtraInput(String extraInput) { this.extraInput = extraInput; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
