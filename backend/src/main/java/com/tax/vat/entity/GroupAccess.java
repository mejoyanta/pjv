package com.tax.vat.entity;

import com.tax.vat.entity.base.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "group_accesses")
public class GroupAccess extends BaseEntity {

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "group_access", columnDefinition = "TEXT")
    private String groupAccess; // JSON string of granted permission keys

    public GroupAccess() {
    }

    public GroupAccess(Long groupId, String groupAccess) {
        this.groupId = groupId;
        this.groupAccess = groupAccess;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupAccess() {
        return groupAccess;
    }

    public void setGroupAccess(String groupAccess) {
        this.groupAccess = groupAccess;
    }
}
