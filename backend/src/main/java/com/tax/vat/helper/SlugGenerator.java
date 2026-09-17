package com.tax.vat.helper;

import org.apache.commons.lang3.RandomStringUtils;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugGenerator {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s/]+");

    public static String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            input = LocalDate.now().toString();
        }

        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = slug.replaceAll("[,.;~]", "");
        slug = slug.toLowerCase(Locale.ENGLISH);
        slug = slug.replaceAll("-{2,}", "-");
        slug = slug.replaceAll("^-|-$", "");

        String suffix = RandomStringUtils.randomAlphanumeric(6).toUpperCase() + "-" + System.currentTimeMillis() / 1000;
        String finalSlug = slug + "-" + suffix;
        if (finalSlug.length() > 100) {
            finalSlug = finalSlug.substring(0, 80) + "-" + System.currentTimeMillis() / 1000;
        }
        return finalSlug;
    }
}
