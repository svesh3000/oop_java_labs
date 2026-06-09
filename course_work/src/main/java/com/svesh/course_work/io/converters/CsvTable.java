package com.svesh.course_work.io.converters;

import java.util.List;
import java.util.Map;

public record CsvTable(
        List<String> headers,
        List<Map<String, String>> rows
) {
}
