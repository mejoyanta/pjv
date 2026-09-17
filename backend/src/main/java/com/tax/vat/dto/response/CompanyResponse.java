package com.tax.vat.dto.response;

import com.tax.vat.entity.Company;
import com.tax.vat.enums.CompanyLevel;
import com.tax.vat.enums.CompanyStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CompanyResponse {

    private Long id;
    private String name;
    private String nameOfEntry;
    private String slug;
    private Long categoryId;
    private String categoryName;
    private CompanyLevel companyLevel;
    private String email;
    private String username;
    private String bin;
    private String tin;
    private String phone;
    private String altPhone;
    private String trustCode;
    private LocalDate effectiveDate;
    private LocalDate subscriptionExpireDate;
    private CompanyStatus status;

    private String contactPersonName;
    private String contactPersonPhone;
    private String contactPersonDesignation;

    private String ownerName;
    private String ownerPhone;
    private String ownerAddress;
    private String ownershipType;
    private String ownerFatherName;
    private String ownerMotherName;
    private LocalDate ownerDob;
    private String ownerAge;

    private String emergencyContactName;
    private String emergencyContactNid;
    private String emergencyContactPassport;
    private String emergencyContactPhone;
    private String emergencyContactRelation;

    private String address;
    private String presentAddress;
    private String permanentAddress;
    private String vatOfficeAddress;
    private String divisionAddress;
    private String circleAddress;

    private Double serviceCharge;
    private Double rentVatCharge;
    private Double consultancyCharge;

    private String padHeadingColor;
    private String debitInvoiceHeadingColor;
    private String creditInvoiceHeadingColor;
    private String commercialInvoiceHeadingColor;
    private String proformaInvoiceHeadingColor;
    private String billOfLadingInvoiceHeadingColor;
    private String packageListHeadingColor;
    private String purchaseOrderHeadingColor;

    private Boolean isSales;
    private Boolean isNewDashboard;
    private Boolean companyIsGold;
    private Boolean companyIsTobacco;
    private Boolean allow63PriceType;
    private Boolean allowRevenueShare;
    private Boolean bkashPayBillEnabled;
    private String bkashPayBillReferenceId;
    private LocalDate stockUpdateDate;
    private String notes;

    private String logo;
    private String companySeal;
    private String companyPad;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CompanyResponse fromEntity(Company company) {
        if (company == null) return null;
        CompanyResponse res = new CompanyResponse();
        res.id = company.getId();
        res.name = company.getName();
        res.nameOfEntry = company.getNameOfEntry();
        res.slug = company.getSlug();
        if (company.getCategory() != null) {
            res.categoryId = company.getCategory().getId();
            res.categoryName = company.getCategory().getName();
        }
        res.companyLevel = company.getCompanyLevel();
        res.email = company.getEmail();
        res.username = company.getUsername();
        res.bin = company.getBin();
        res.tin = company.getTin();
        res.phone = company.getPhone();
        res.altPhone = company.getAltPhone();
        res.trustCode = company.getTrustCode();
        res.effectiveDate = company.getEffectiveDate();
        res.subscriptionExpireDate = company.getSubscriptionExpireDate();
        res.status = company.getStatus();

        res.contactPersonName = company.getContactPersonName();
        res.contactPersonPhone = company.getContactPersonPhone();
        res.contactPersonDesignation = company.getContactPersonDesignation();

        res.ownerName = company.getOwnerName();
        res.ownerPhone = company.getOwnerPhone();
        res.ownerAddress = company.getOwnerAddress();
        res.ownershipType = company.getOwnershipType();
        res.ownerFatherName = company.getOwnerFatherName();
        res.ownerMotherName = company.getOwnerMotherName();
        res.ownerDob = company.getOwnerDob();
        res.ownerAge = company.getOwnerAge();

        res.emergencyContactName = company.getEmergencyContactName();
        res.emergencyContactNid = company.getEmergencyContactNid();
        res.emergencyContactPassport = company.getEmergencyContactPassport();
        res.emergencyContactPhone = company.getEmergencyContactPhone();
        res.emergencyContactRelation = company.getEmergencyContactRelation();

        res.address = company.getAddress();
        res.presentAddress = company.getPresentAddress();
        res.permanentAddress = company.getPermanentAddress();
        res.vatOfficeAddress = company.getVatOfficeAddress();
        res.divisionAddress = company.getDivisionAddress();
        res.circleAddress = company.getCircleAddress();

        res.serviceCharge = company.getServiceCharge();
        res.rentVatCharge = company.getRentVatCharge();
        res.consultancyCharge = company.getConsultancyCharge();

        res.padHeadingColor = company.getPadHeadingColor();
        res.debitInvoiceHeadingColor = company.getDebitInvoiceHeadingColor();
        res.creditInvoiceHeadingColor = company.getCreditInvoiceHeadingColor();
        res.commercialInvoiceHeadingColor = company.getCommercialInvoiceHeadingColor();
        res.proformaInvoiceHeadingColor = company.getProformaInvoiceHeadingColor();
        res.billOfLadingInvoiceHeadingColor = company.getBillOfLadingInvoiceHeadingColor();
        res.packageListHeadingColor = company.getPackageListHeadingColor();
        res.purchaseOrderHeadingColor = company.getPurchaseOrderHeadingColor();

        res.isSales = company.getIsSales();
        res.isNewDashboard = company.getIsNewDashboard();
        res.companyIsGold = company.getCompanyIsGold();
        res.companyIsTobacco = company.getCompanyIsTobacco();
        res.allow63PriceType = company.getAllow63PriceType();
        res.allowRevenueShare = company.getAllowRevenueShare();
        res.bkashPayBillEnabled = company.getBkashPayBillEnabled();
        res.bkashPayBillReferenceId = company.getBkashPayBillReferenceId();
        res.stockUpdateDate = company.getStockUpdateDate();
        res.notes = company.getNotes();

        res.logo = company.getLogo();
        res.companySeal = company.getCompanySeal();
        res.companyPad = company.getCompanyPad();

        res.createdAt = company.getCreatedAt();
        res.updatedAt = company.getUpdatedAt();

        return res;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getNameOfEntry() { return nameOfEntry; }
    public String getSlug() { return slug; }
    public Long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public CompanyLevel getCompanyLevel() { return companyLevel; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getBin() { return bin; }
    public String getTin() { return tin; }
    public String getPhone() { return phone; }
    public String getAltPhone() { return altPhone; }
    public String getTrustCode() { return trustCode; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public LocalDate getSubscriptionExpireDate() { return subscriptionExpireDate; }
    public CompanyStatus getStatus() { return status; }
    public String getContactPersonName() { return contactPersonName; }
    public String getContactPersonPhone() { return contactPersonPhone; }
    public String getContactPersonDesignation() { return contactPersonDesignation; }
    public String getOwnerName() { return ownerName; }
    public String getOwnerPhone() { return ownerPhone; }
    public String getOwnerAddress() { return ownerAddress; }
    public String getOwnershipType() { return ownershipType; }
    public String getOwnerFatherName() { return ownerFatherName; }
    public String getOwnerMotherName() { return ownerMotherName; }
    public LocalDate getOwnerDob() { return ownerDob; }
    public String getOwnerAge() { return ownerAge; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public String getEmergencyContactNid() { return emergencyContactNid; }
    public String getEmergencyContactPassport() { return emergencyContactPassport; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public String getEmergencyContactRelation() { return emergencyContactRelation; }
    public String getAddress() { return address; }
    public String getPresentAddress() { return presentAddress; }
    public String getPermanentAddress() { return permanentAddress; }
    public String getVatOfficeAddress() { return vatOfficeAddress; }
    public String getDivisionAddress() { return divisionAddress; }
    public String getCircleAddress() { return circleAddress; }
    public Double getServiceCharge() { return serviceCharge; }
    public Double getRentVatCharge() { return rentVatCharge; }
    public Double getConsultancyCharge() { return consultancyCharge; }
    public String getPadHeadingColor() { return padHeadingColor; }
    public String getDebitInvoiceHeadingColor() { return debitInvoiceHeadingColor; }
    public String getCreditInvoiceHeadingColor() { return creditInvoiceHeadingColor; }
    public String getCommercialInvoiceHeadingColor() { return commercialInvoiceHeadingColor; }
    public String getProformaInvoiceHeadingColor() { return proformaInvoiceHeadingColor; }
    public String getBillOfLadingInvoiceHeadingColor() { return billOfLadingInvoiceHeadingColor; }
    public String getPackageListHeadingColor() { return packageListHeadingColor; }
    public String getPurchaseOrderHeadingColor() { return purchaseOrderHeadingColor; }
    public Boolean getIsSales() { return isSales; }
    public Boolean getIsNewDashboard() { return isNewDashboard; }
    public Boolean getCompanyIsGold() { return companyIsGold; }
    public Boolean getCompanyIsTobacco() { return companyIsTobacco; }
    public Boolean getAllow63PriceType() { return allow63PriceType; }
    public Boolean getAllowRevenueShare() { return allowRevenueShare; }
    public Boolean getBkashPayBillEnabled() { return bkashPayBillEnabled; }
    public String getBkashPayBillReferenceId() { return bkashPayBillReferenceId; }
    public LocalDate getStockUpdateDate() { return stockUpdateDate; }
    public String getNotes() { return notes; }
    public String getLogo() { return logo; }
    public String getCompanySeal() { return companySeal; }
    public String getCompanyPad() { return companyPad; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
