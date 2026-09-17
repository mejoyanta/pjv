package com.tax.vat.helper;

import java.util.regex.Pattern;

public class CompanyIdFormat {

    public static final String DEFAULT_PREFIX = "BARA49";
    private static final Pattern PATTERN = Pattern.compile("^" + Pattern.quote(DEFAULT_PREFIX) + "-?[A-Za-z0-9]+$");

    public static String prefix() {
        return DEFAULT_PREFIX;
    }

    public static boolean matches(String value) {
        String normalized = normalize(value);
        if (normalized.isEmpty()) {
            return false;
        }
        return PATTERN.matcher(normalized).matches();
    }

    public static String normalize(String value) {
        if (value == null) return "";
        return value.replaceAll("\\s+", "").toUpperCase().trim();
    }

    public static String validationMessage() {
        return "Company ID must start with " + DEFAULT_PREFIX + " followed by letters or numbers, e.g. " + DEFAULT_PREFIX + "-CITY or " + DEFAULT_PREFIX + "CITY.";
    }
}
