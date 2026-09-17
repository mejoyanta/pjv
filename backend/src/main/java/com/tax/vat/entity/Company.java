package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import com.tax.vat.enums.CompanyLevel;
import com.tax.vat.enums.CompanyStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies", indexes = {
        @Index(name = "idx_companies_slug", columnList = "slug"),
        @Index(name = "idx_companies_username", columnList = "username"),
        @Index(name = "idx_companies_bin", columnList = "bin"),
        @Index(name = "idx_companies_category_id", columnList = "category_id"),
        @Index(name = "idx_companies_status", columnList = "status"),
        @Index(name = "idx_companies_deleted_at_id", columnList = "deleted_at, id")
})
public class Company extends BaseEntity {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private CompanyCategory category;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(name = "name_of_entry")
    private String nameOfEntry;

    @Column(length = 150)
    private String email;

    @Column(length = 100, unique = true)
    private String username;

    @Column(name = "trust_code", length = 100)
    private String trustCode;

    @Column(length = 50)
    private String phone;

    @Column(length = 50, unique = true)
    private String bin;

    @Column(length = 50)
    private String tin;

    @Column(name = "contact_person_name")
    private String contactPersonName;

    @Column(name = "contact_person_phone", length = 50)
    private String contactPersonPhone;

    @Column(name = "contact_person_designation")
    private String contactPersonDesignation;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "owner_phone", length = 50)
    private String ownerPhone;

    @Column(name = "ownership_type", length = 100)
    private String ownershipType;

    @Column(name = "vat_office_address", columnDefinition = "TEXT")
    private String vatOfficeAddress;

    @Column(name = "alt_phone", length = 50)
    private String altPhone;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(length = 20)
    private CompanyStatus status = CompanyStatus.ACTIVE;

    @Column(name = "pad_heading_color", length = 30)
    private String padHeadingColor;

    @Column(name = "pad_attachment")
    private String padAttachment;

    @Column(length = 255)
    private String password;

    @Column(length = 255)
    private String logo;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "view_own_product_material")
    private Boolean viewOwnProductMaterial = false;

    @Column(name = "subscription_expire_date")
    private LocalDate subscriptionExpireDate;

    @Column(name = "active_package")
    private Integer activePackage;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "document", columnDefinition = "TEXT")
    private String document; // JSON string in DB

    @Column(name = "company_level", length = 20)
    private CompanyLevel companyLevel = CompanyLevel.SMALL;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "company_seal")
    private String companySeal;

    @Column(name = "company_pad")
    private String companyPad;

    @Column(name = "service_charge")
    private Double serviceCharge = 0.0;

    @Column(name = "rent_vat_charge")
    private Double rentVatCharge = 0.0;

    @Column(name = "consultancy_charge")
    private Double consultancyCharge = 0.0;

    @Column(name = "is_sales")
    private Boolean isSales = false;

    @Column(name = "is_new_dashboard")
    private Boolean isNewDashboard = false;

    @Column(name = "stock_update_date")
    private LocalDate stockUpdateDate;

    @Column(name = "company_is_gold")
    private Boolean companyIsGold = false;

    @Column(name = "company_is_tobacco")
    private Boolean companyIsTobacco = false;

    @Column(name = "allow_63_price_type")
    private Boolean allow63PriceType = false;

    @Column(name = "allow_revenue_share")
    private Boolean allowRevenueShare = false;

    @Column(name = "bkash_pay_bill_enabled")
    private Boolean bkashPayBillEnabled = false;

    @Column(name = "bkash_pay_bill_reference_id", length = 100)
    private String bkashPayBillReferenceId;

    @Column(name = "otp_no", length = 50)
    private String otpNo;

    @Column(name = "division_address", columnDefinition = "TEXT")
    private String divisionAddress;

    @Column(name = "circle_address", columnDefinition = "TEXT")
    private String circleAddress;

    @Column(name = "debit_invoice_heading_color", length = 30)
    private String debitInvoiceHeadingColor;

    @Column(name = "credit_invoice_heading_color", length = 30)
    private String creditInvoiceHeadingColor;

    @Column(name = "commercial_invoice_heading_color", length = 30)
    private String commercialInvoiceHeadingColor;

    @Column(name = "proforma_invoice_heading_color", length = 30)
    private String proformaInvoiceHeadingColor;

    @Column(name = "bill_of_lading_invoice_heading_color", length = 30)
    private String billOfLadingInvoiceHeadingColor;

    @Column(name = "package_list_heading_color", length = 30)
    private String packageListHeadingColor;

    @Column(name = "purchase_order_heading_color", length = 30)
    private String purchaseOrderHeadingColor;

    @Column(name = "owner_father_name")
    private String ownerFatherName;

    @Column(name = "owner_mother_name")
    private String ownerMotherName;

    @Column(name = "owner_dob")
    private LocalDate ownerDob;

    @Column(name = "owner_age")
    private String ownerAge;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_nid", length = 50)
    private String emergencyContactNid;

    @Column(name = "emergency_contact_passport", length = 50)
    private String emergencyContactPassport;

    @Column(name = "emergency_contact_phone", length = 50)
    private String emergencyContactPhone;

    @Column(name = "emergency_contact_relation", length = 100)
    private String emergencyContactRelation;

    @Column(name = "present_address", columnDefinition = "TEXT")
    private String presentAddress;

    @Column(name = "permanent_address", columnDefinition = "TEXT")
    private String permanentAddress;

    @Column(name = "username_change_reason", columnDefinition = "TEXT")
    private String usernameChangeReason;

    public Company() {
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // Getters and Setters
    public CompanyCategory getCategory() {
        return category;
    }

    public void setCategory(CompanyCategory category) {
        this.category = category;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameOfEntry() {
        return nameOfEntry;
    }

    public void setNameOfEntry(String nameOfEntry) {
        this.nameOfEntry = nameOfEntry;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTrustCode() {
        return trustCode;
    }

    public void setTrustCode(String trustCode) {
        this.trustCode = trustCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public String getContactPersonDesignation() {
        return contactPersonDesignation;
    }

    public void setContactPersonDesignation(String contactPersonDesignation) {
        this.contactPersonDesignation = contactPersonDesignation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerAddress() {
        return permanentAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.permanentAddress = ownerAddress;
    }

    public String getOwnershipType() {
        return ownershipType;
    }

    public void setOwnershipType(String ownershipType) {
        this.ownershipType = ownershipType;
    }

    public String getVatOfficeAddress() {
        return vatOfficeAddress;
    }

    public void setVatOfficeAddress(String vatOfficeAddress) {
        this.vatOfficeAddress = vatOfficeAddress;
    }

    public String getAltPhone() {
        return altPhone;
    }

    public void setAltPhone(String altPhone) {
        this.altPhone = altPhone;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public CompanyStatus getStatus() {
        return status;
    }

    public void setStatus(CompanyStatus status) {
        this.status = status;
    }

    public String getPadHeadingColor() {
        return padHeadingColor;
    }

    public void setPadHeadingColor(String padHeadingColor) {
        this.padHeadingColor = padHeadingColor;
    }

    public String getPadAttachment() {
        return padAttachment;
    }

    public void setPadAttachment(String padAttachment) {
        this.padAttachment = padAttachment;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Boolean getViewOwnProductMaterial() {
        return viewOwnProductMaterial;
    }

    public void setViewOwnProductMaterial(Boolean viewOwnProductMaterial) {
        this.viewOwnProductMaterial = viewOwnProductMaterial;
    }

    public LocalDate getSubscriptionExpireDate() {
        return subscriptionExpireDate;
    }

    public void setSubscriptionExpireDate(LocalDate subscriptionExpireDate) {
        this.subscriptionExpireDate = subscriptionExpireDate;
    }

    public Integer getActivePackage() {
        return activePackage;
    }

    public void setActivePackage(Integer activePackage) {
        this.activePackage = activePackage;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public CompanyLevel getCompanyLevel() {
        return companyLevel;
    }

    public void setCompanyLevel(CompanyLevel companyLevel) {
        this.companyLevel = companyLevel;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCompanySeal() {
        return companySeal;
    }

    public void setCompanySeal(String companySeal) {
        this.companySeal = companySeal;
    }

    public String getCompanyPad() {
        return companyPad;
    }

    public void setCompanyPad(String companyPad) {
        this.companyPad = companyPad;
    }

    public Double getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(Double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public Double getRentVatCharge() {
        return rentVatCharge;
    }

    public void setRentVatCharge(Double rentVatCharge) {
        this.rentVatCharge = rentVatCharge;
    }

    public Double getConsultancyCharge() {
        return consultancyCharge;
    }

    public void setConsultancyCharge(Double consultancyCharge) {
        this.consultancyCharge = consultancyCharge;
    }

    public Boolean getIsSales() {
        return isSales;
    }

    public void setIsSales(Boolean isSales) {
        this.isSales = isSales;
    }

    public Boolean getIsNewDashboard() {
        return isNewDashboard;
    }

    public void setIsNewDashboard(Boolean isNewDashboard) {
        this.isNewDashboard = isNewDashboard;
    }

    public LocalDate getStockUpdateDate() {
        return stockUpdateDate;
    }

    public void setStockUpdateDate(LocalDate stockUpdateDate) {
        this.stockUpdateDate = stockUpdateDate;
    }

    public Boolean getCompanyIsGold() {
        return companyIsGold;
    }

    public void setCompanyIsGold(Boolean companyIsGold) {
        this.companyIsGold = companyIsGold;
    }

    public Boolean getCompanyIsTobacco() {
        return companyIsTobacco;
    }

    public void setCompanyIsTobacco(Boolean companyIsTobacco) {
        this.companyIsTobacco = companyIsTobacco;
    }

    public Boolean getAllow63PriceType() {
        return allow63PriceType;
    }

    public void setAllow63PriceType(Boolean allow63PriceType) {
        this.allow63PriceType = allow63PriceType;
    }

    public Boolean getAllowRevenueShare() {
        return allowRevenueShare;
    }

    public void setAllowRevenueShare(Boolean allowRevenueShare) {
        this.allowRevenueShare = allowRevenueShare;
    }

    public Boolean getBkashPayBillEnabled() {
        return bkashPayBillEnabled;
    }

    public void setBkashPayBillEnabled(Boolean bkashPayBillEnabled) {
        this.bkashPayBillEnabled = bkashPayBillEnabled;
    }

    public String getBkashPayBillReferenceId() {
        return bkashPayBillReferenceId;
    }

    public void setBkashPayBillReferenceId(String bkashPayBillReferenceId) {
        this.bkashPayBillReferenceId = bkashPayBillReferenceId;
    }

    public String getOtpNo() {
        return otpNo;
    }

    public void setOtpNo(String otpNo) {
        this.otpNo = otpNo;
    }

    public String getDivisionAddress() {
        return divisionAddress;
    }

    public void setDivisionAddress(String divisionAddress) {
        this.divisionAddress = divisionAddress;
    }

    public String getCircleAddress() {
        return circleAddress;
    }

    public void setCircleAddress(String circleAddress) {
        this.circleAddress = circleAddress;
    }

    public String getDebitInvoiceHeadingColor() {
        return debitInvoiceHeadingColor;
    }

    public void setDebitInvoiceHeadingColor(String debitInvoiceHeadingColor) {
        this.debitInvoiceHeadingColor = debitInvoiceHeadingColor;
    }

    public String getCreditInvoiceHeadingColor() {
        return creditInvoiceHeadingColor;
    }

    public void setCreditInvoiceHeadingColor(String creditInvoiceHeadingColor) {
        this.creditInvoiceHeadingColor = creditInvoiceHeadingColor;
    }

    public String getCommercialInvoiceHeadingColor() {
        return commercialInvoiceHeadingColor;
    }

    public void setCommercialInvoiceHeadingColor(String commercialInvoiceHeadingColor) {
        this.commercialInvoiceHeadingColor = commercialInvoiceHeadingColor;
    }

    public String getProformaInvoiceHeadingColor() {
        return proformaInvoiceHeadingColor;
    }

    public void setProformaInvoiceHeadingColor(String proformaInvoiceHeadingColor) {
        this.proformaInvoiceHeadingColor = proformaInvoiceHeadingColor;
    }

    public String getBillOfLadingInvoiceHeadingColor() {
        return billOfLadingInvoiceHeadingColor;
    }

    public void setBillOfLadingInvoiceHeadingColor(String billOfLadingInvoiceHeadingColor) {
        this.billOfLadingInvoiceHeadingColor = billOfLadingInvoiceHeadingColor;
    }

    public String getPackageListHeadingColor() {
        return packageListHeadingColor;
    }

    public void setPackageListHeadingColor(String packageListHeadingColor) {
        this.packageListHeadingColor = packageListHeadingColor;
    }

    public String getPurchaseOrderHeadingColor() {
        return purchaseOrderHeadingColor;
    }

    public void setPurchaseOrderHeadingColor(String purchaseOrderHeadingColor) {
        this.purchaseOrderHeadingColor = purchaseOrderHeadingColor;
    }

    public String getOwnerFatherName() {
        return ownerFatherName;
    }

    public void setOwnerFatherName(String ownerFatherName) {
        this.ownerFatherName = ownerFatherName;
    }

    public String getOwnerMotherName() {
        return ownerMotherName;
    }

    public void setOwnerMotherName(String ownerMotherName) {
        this.ownerMotherName = ownerMotherName;
    }

    public LocalDate getOwnerDob() {
        return ownerDob;
    }

    public void setOwnerDob(LocalDate ownerDob) {
        this.ownerDob = ownerDob;
    }

    public String getOwnerAge() {
        return ownerAge;
    }

    public void setOwnerAge(String ownerAge) {
        this.ownerAge = ownerAge;
    }

    public void setOwnerAge(Integer ownerAge) {
        this.ownerAge = ownerAge != null ? ownerAge.toString() : null;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactNid() {
        return emergencyContactNid;
    }

    public void setEmergencyContactNid(String emergencyContactNid) {
        this.emergencyContactNid = emergencyContactNid;
    }

    public String getEmergencyContactPassport() {
        return emergencyContactPassport;
    }

    public void setEmergencyContactPassport(String emergencyContactPassport) {
        this.emergencyContactPassport = emergencyContactPassport;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public String getEmergencyContactRelation() {
        return emergencyContactRelation;
    }

    public void setEmergencyContactRelation(String emergencyContactRelation) {
        this.emergencyContactRelation = emergencyContactRelation;
    }

    public String getPresentAddress() {
        return presentAddress;
    }

    public void setPresentAddress(String presentAddress) {
        this.presentAddress = presentAddress;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getUsernameChangeReason() {
        return usernameChangeReason;
    }

    public void setUsernameChangeReason(String usernameChangeReason) {
        this.usernameChangeReason = usernameChangeReason;
    }
}
