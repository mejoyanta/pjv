package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "packages")
public class PackageEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private Double price = 0.0;

    @Column(name = "validaty")
    private Double validity = 0.0;

    @Column(name = "validaty_type")
    private String validityType = "days";

    @Column(name = "publication_status")
    private Boolean publicationStatus = true;

    @Column(name = "package_type")
    private String packageType;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getValidity() { return validity; }
    public void setValidity(Double validity) { this.validity = validity; }

    public String getValidityType() { return validityType; }
    public void setValidityType(String validityType) { this.validityType = validityType; }

    public Boolean getPublicationStatus() { return publicationStatus; }
    public void setPublicationStatus(Boolean publicationStatus) { this.publicationStatus = publicationStatus; }

    public String getPackageType() { return packageType; }
    public void setPackageType(String packageType) { this.packageType = packageType; }
}
