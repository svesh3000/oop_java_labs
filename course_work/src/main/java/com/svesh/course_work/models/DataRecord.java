package com.svesh.course_work.models;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

public record DataRecord(
        int id,
        String source,
        Instant timestamp,
        JsonNode data
) {
}
