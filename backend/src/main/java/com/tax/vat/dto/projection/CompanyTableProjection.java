package com.tax.vat.dto.projection;

/**
 * High-performance projection for 100k - 500k DataTable queries.
 * Selects only the needed columns with flexible return types.
 */
public interface CompanyTableProjection {
    Long getId();
    String getName();
    String getSlug();
    String getUsername();
    String getEmail();
    String getPhone();
    String getBin();
    String getCategoryName();
    Object getStatus();
    Object getSubscriptionExpireDate();
    Object getCreatedAt();
    Object getDeletedAt();
}
