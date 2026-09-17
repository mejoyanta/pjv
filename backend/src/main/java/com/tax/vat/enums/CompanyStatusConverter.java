package com.tax.vat.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CompanyStatusConverter implements AttributeConverter<CompanyStatus, String> {

    @Override
    public String convertToDatabaseColumn(CompanyStatus attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public CompanyStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? CompanyStatus.fromValue(dbData) : CompanyStatus.ACTIVE;
    }
}
