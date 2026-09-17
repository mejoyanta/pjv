package com.tax.vat.helper;

public class NIDValidator {

    public static boolean isValid(String nid) {
        if (nid == null) return false;
        String clean = nid.trim();
        int len = clean.length();
        if (len != 10 && len != 13 && len != 17) {
            return false;
        }
        return clean.matches("\\d+");
    }

    public static String getErrorMessage() {
        return "NID must be 10, 13, or 17 digits only.";
    }
}
