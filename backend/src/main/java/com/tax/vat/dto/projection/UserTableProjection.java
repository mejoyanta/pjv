package com.tax.vat.dto.projection;

/**
 * High-performance projection for User DataTable queries.
 */
public interface UserTableProjection {
    Long getId();
    String getName();
    String getUsername();
    String getEmail();
    String getContact();
    String getCompanyName();
    String getCompanyBranchName();
    String getDesignationName();
    String getDepartmentName();
    String getGroupName();
    Boolean getIsOnline();
    Object getStatus();
    Object getCreatedAt();
    Object getDeletedAt();
}
