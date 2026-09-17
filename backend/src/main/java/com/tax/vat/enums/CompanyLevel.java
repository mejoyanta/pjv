package com.tax.vat.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CompanyLevel {
    SMALL("small"),
    MEDIUM("medium"),
    LARGE("large");

    private final String value;

    CompanyLevel(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompanyLevel fromValue(String value) {
        if (value == null || value.trim().isEmpty()) return SMALL;
        String val = value.trim();
        for (CompanyLevel level : CompanyLevel.values()) {
            if (level.value.equalsIgnoreCase(val) || level.name().equalsIgnoreCase(val)) {
                return level;
            }
        }
        return SMALL;
    }
}
