package com.tax.vat.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CompanyStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String value;

    CompanyStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompanyStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) return ACTIVE;
        String val = value.trim();
        for (CompanyStatus status : CompanyStatus.values()) {
            if (status.value.equalsIgnoreCase(val) || status.name().equalsIgnoreCase(val)) {
                return status;
            }
        }
        return ACTIVE;
    }
}
