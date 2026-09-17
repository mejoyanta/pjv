package com.tax.vat.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CategoryType {
    MANUFACTURER("manufacturer"),
    IMPORTER("importer"),
    TRADERS("traders"),
    SERVICE("service"),
    OTHERS("others");

    private final String value;

    CategoryType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CategoryType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        String val = value.trim();
        for (CategoryType type : CategoryType.values()) {
            if (type.value.equalsIgnoreCase(val) || type.name().equalsIgnoreCase(val)) {
                return type;
            }
        }
        return OTHERS;
    }
}
