package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "banks")
public class Bank extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "routing_no")
    private String routingNo;

    @Column(name = "swift_code")
    private String swiftCode;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRoutingNo() { return routingNo; }
    public void setRoutingNo(String routingNo) { this.routingNo = routingNo; }

    public String getSwiftCode() { return swiftCode; }
    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }
}
