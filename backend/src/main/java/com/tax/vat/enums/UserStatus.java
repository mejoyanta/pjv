package com.tax.vat.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
    ACTIVE("active"),
    SUSPENDED("suspended"),
    ARCHIVED("archived");

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static UserStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) return ACTIVE;
        String val = value.trim();
        for (UserStatus status : UserStatus.values()) {
            if (status.value.equalsIgnoreCase(val) || status.name().equalsIgnoreCase(val)) {
                return status;
            }
        }
        return ACTIVE;
    }
}
