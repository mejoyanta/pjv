package com.tax.vat.helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateHelper {

    private static final DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy")
    };

    public static LocalDate parse(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        String clean = str.trim();
        for (DateTimeFormatter dtf : FORMATTERS) {
            try {
                return LocalDate.parse(clean, dtf);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    public static String format(LocalDate date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
