package com.tax.vat.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class FlexibleBooleanDeserializer extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentToken() == JsonToken.VALUE_NULL) {
            return false;
        }
        if (p.currentToken() == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE;
        }
        if (p.currentToken() == JsonToken.VALUE_FALSE) {
            return Boolean.FALSE;
        }
        if (p.currentToken() == JsonToken.VALUE_NUMBER_INT) {
            return p.getIntValue() == 1;
        }
        String text = p.getText();
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        text = text.trim();
        return "true".equalsIgnoreCase(text) || "1".equals(text) || "yes".equalsIgnoreCase(text) || "t".equalsIgnoreCase(text);
    }
}
