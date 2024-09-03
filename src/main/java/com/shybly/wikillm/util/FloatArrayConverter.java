package com.shybly.wikillm.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Converter
public class FloatArrayConverter implements AttributeConverter<Float[], String> {

    @Override
    public String convertToDatabaseColumn(Float[] attribute) {
        if (attribute == null) {
            return null;
        }
        // Convert float array to a comma-separated string
        return Arrays.stream(attribute)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public Float[] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        dbData = dbData.replace("[", "");
        dbData = dbData.replace("]", "");
        // Convert comma-separated string back to a float array
        return Arrays.stream(dbData.split(","))
                .map(Float::valueOf)
                .toArray(size -> new Float[size]);
    }
}

