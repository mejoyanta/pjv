package com.tax.vat.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CompanyLevelConverter implements AttributeConverter<CompanyLevel, String> {

    @Override
    public String convertToDatabaseColumn(CompanyLevel attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public CompanyLevel convertToEntityAttribute(String dbData) {
        return dbData != null ? CompanyLevel.fromValue(dbData) : CompanyLevel.SMALL;
    }
}
