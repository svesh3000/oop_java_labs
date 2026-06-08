package com.svesh.course_work.export.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svesh.course_work.file_records.DataRecord;

import java.util.List;

public class JsonConverter implements Converter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convert(List<DataRecord> records) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(records);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON conversion failed", e);
        }
    }
}
