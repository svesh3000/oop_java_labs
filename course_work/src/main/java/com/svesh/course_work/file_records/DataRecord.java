package com.svesh.course_work.file_records;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

public record DataRecord(
        String source,
        Instant timestamp,
        JsonNode data
) {
}
