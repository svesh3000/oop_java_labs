package com.svesh.course_work.export.converters;

import com.svesh.course_work.export.OutputFormat;

public class ConverterFactory {
    public Converter create(OutputFormat format) {
        return switch (format) {
            case JSON -> new JsonConverter();
            case CSV -> new CsvConverter();
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }
}
