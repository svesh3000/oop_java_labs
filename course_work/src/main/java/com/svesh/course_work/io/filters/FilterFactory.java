package com.svesh.course_work.io.filters;

import com.svesh.course_work.io.OutputFormat;

public class FilterFactory {
    public Filter create(OutputFormat format) {
        return switch (format) {
            case JSON -> new JsonFilter();
            case CSV -> new CsvFilter();
        };
    }
}
