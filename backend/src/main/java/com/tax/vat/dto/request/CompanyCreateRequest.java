package com.tax.vat.dto.request;

import com.tax.vat.enums.CompanyLevel;
import com.tax.vat.enums.CompanyStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class CompanyCreateRequest {

    @NotBlank(message = "Company Name is required")
    private String name;

    private String nameOfEntry;

    @NotNull(message = "Company Category is required")
    private Long categoryId;

    private CompanyLevel companyLevel = CompanyLevel.SMALL;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Company ID / Username is required")
    private String username;

    private String password;

    @NotBlank(message = "BIN is required")
    private String bin;

    private String tin;
    private String phone;
    private String altPhone;
    private String trustCode;
    private LocalDate effectiveDate;
    private LocalDate subscriptionExpireDate;

    private CompanyStatus status = CompanyStatus.ACTIVE;

    // Contact Person Info
    private String contactPersonName;
    private String contactPersonPhone;
    private String contactPersonDesignation;

    // Owner Info
    private String ownerName;
    private String ownerPhone;
    private String ownerAddress;
    private String ownershipType;
    private String ownerFatherName;
    private String ownerMotherName;
    private LocalDate ownerDob;
    private Integer ownerAge;

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactNid;
    private String emergencyContactPassport;
    private String emergencyContactPhone;
    private String emergencyContactRelation;

    // Addresses
    private String address;
    private String presentAddress;
    private String permanentAddress;
    private String vatOfficeAddress;
    private String divisionAddress;
    private String circleAddress;

    // Charges
    private Double serviceCharge;
    private Double rentVatCharge;
    private Double consultancyCharge;

    // Colors
    private String padHeadingColor;
    private String debitInvoiceHeadingColor;
    private String creditInvoiceHeadingColor;
    private String commercialInvoiceHeadingColor;
    private String proformaInvoiceHeadingColor;
    private String billOfLadingInvoiceHeadingColor;
    private String packageListHeadingColor;
    private String purchaseOrderHeadingColor;

    // Flags
    private Boolean isSales = false;
    private Boolean isNewDashboard = false;
    private Boolean companyIsGold = false;
    private Boolean companyIsTobacco = false;
    private Boolean allow63PriceType = false;
    private Boolean allowRevenueShare = false;
    private Boolean bkashPayBillEnabled = false;
    private String bkashPayBillReferenceId;
    private LocalDate stockUpdateDate;
    private String notes;

    // File Uploads
    private MultipartFile logoFile;
    private MultipartFile companySealFile;
    private MultipartFile companyPadFile;

    public CompanyCreateRequest() {
    }

    // Getters and Setters
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public CompanyLevel getCompanyLevel() {
        return companyLevel;
    }

    public void setCompanyLevel(CompanyLevel companyLevel) {
        this.companyLevel = companyLevel;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAltPhone() {
        return altPhone;
    }

    public void setAltPhone(String altPhone) {
        this.altPhone = altPhone;
    }

    public String getTrustCode() {
        return trustCode;
    }

    public void setTrustCode(String trustCode) {
        this.trustCode = trustCode;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getSubscriptionExpireDate() {
        return subscriptionExpireDate;
    }

    public void setSubscriptionExpireDate(LocalDate subscriptionExpireDate) {
        this.subscriptionExpireDate = subscriptionExpireDate;
    }

    public CompanyStatus getStatus() {
        return status;
    }

    public void setStatus(CompanyStatus status) {
        this.status = status;
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
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public String getOwnershipType() {
        return ownershipType;
    }

    public void setOwnershipType(String ownershipType) {
        this.ownershipType = ownershipType;
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

    public Integer getOwnerAge() {
        return ownerAge;
    }

    public void setOwnerAge(Integer ownerAge) {
        this.ownerAge = ownerAge;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getVatOfficeAddress() {
        return vatOfficeAddress;
    }

    public void setVatOfficeAddress(String vatOfficeAddress) {
        this.vatOfficeAddress = vatOfficeAddress;
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

    public String getPadHeadingColor() {
        return padHeadingColor;
    }

    public void setPadHeadingColor(String padHeadingColor) {
        this.padHeadingColor = padHeadingColor;
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

    public LocalDate getStockUpdateDate() {
        return stockUpdateDate;
    }

    public void setStockUpdateDate(LocalDate stockUpdateDate) {
        this.stockUpdateDate = stockUpdateDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public MultipartFile getLogoFile() {
        return logoFile;
    }

    public void setLogoFile(MultipartFile logoFile) {
        this.logoFile = logoFile;
    }

    public MultipartFile getCompanySealFile() {
        return companySealFile;
    }

    public void setCompanySealFile(MultipartFile companySealFile) {
        this.companySealFile = companySealFile;
    }

    public MultipartFile getCompanyPadFile() {
        return companyPadFile;
    }

    public void setCompanyPadFile(MultipartFile companyPadFile) {
        this.companyPadFile = companyPadFile;
    }
}
