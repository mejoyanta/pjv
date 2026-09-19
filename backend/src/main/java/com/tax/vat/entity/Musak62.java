package com.tax.vat.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "musak_6_2s")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Musak62 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", length = 255)
    private String slug;

    @Column(name = "company_id")
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    @Column(name = "company_branch_id")
    private Long companyBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_branch_id", insertable = false, updatable = false)
    private CompanyBranch companyBranch;

    @Column(name = "purchase_id")
    private Long purchaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", insertable = false, updatable = false)
    private Purchase purchase;

    @Column(name = "sale_id")
    private Long saleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", insertable = false, updatable = false)
    private Sale sale;

    @Column(name = "musak_6_3_id")
    private Long musak63Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musak_6_3_id", insertable = false, updatable = false)
    private Musak63 musak63;

    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @Column(name = "supplyment_unit_id")
    private Long supplymentUnitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplyment_unit_id", insertable = false, updatable = false)
    private UnitOfSupply unit;

    @Column(name = "supplier_id")
    private Long supplierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", insertable = false, updatable = false)
    private Supplier supplier;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "sale_date")
    private LocalDate saleDate;

    @Column(name = "opening_stock", length = 255)
    private String openingStock;

    @Column(name = "closing_stock")
    private Double closingStock;

    @Column(name = "manufacturer_id")
    private Long manufacturerId;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Musak62() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Long getCompanyBranchId() { return companyBranchId; }
    public void setCompanyBranchId(Long companyBranchId) { this.companyBranchId = companyBranchId; }

    public CompanyBranch getCompanyBranch() { return companyBranch; }
    public void setCompanyBranch(CompanyBranch companyBranch) { this.companyBranch = companyBranch; }

    public Long getPurchaseId() { return purchaseId; }
    public void setPurchaseId(Long purchaseId) { this.purchaseId = purchaseId; }

    public Purchase getPurchase() { return purchase; }
    public void setPurchase(Purchase purchase) { this.purchase = purchase; }

    public Long getSaleId() { return saleId; }
    public void setSaleId(Long saleId) { this.saleId = saleId; }

    public Long getMusak63Id() { return musak63Id; }
    public void setMusak63Id(Long musak63Id) { this.musak63Id = musak63Id; }

    public Musak63 getMusak63() { return musak63; }
    public void setMusak63(Musak63 musak63) { this.musak63 = musak63; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Long getSupplymentUnitId() { return supplymentUnitId; }
    public void setSupplymentUnitId(Long supplymentUnitId) { this.supplymentUnitId = supplymentUnitId; }

    public UnitOfSupply getUnit() { return unit; }
    public void setUnit(UnitOfSupply unit) { this.unit = unit; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public LocalDate getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }

    public String getOpeningStock() { return openingStock; }
    public void setOpeningStock(String openingStock) { this.openingStock = openingStock; }

    public Double getClosingStock() { return closingStock; }
    public void setClosingStock(Double closingStock) { this.closingStock = closingStock; }

    public Long getManufacturerId() { return manufacturerId; }
    public void setManufacturerId(Long manufacturerId) { this.manufacturerId = manufacturerId; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
