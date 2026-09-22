package com.svesh.course_work.io.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svesh.course_work.models.DataRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("JsonFilter: filter records by source")
class JsonFilterTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonFilter filter = new JsonFilter();

    @Test
    @DisplayName("Keeps only records with matching source")
    void shouldKeepOnlyMatchingSource() throws Exception {
        DataRecord joke = new DataRecord(1, "joke", Instant.EPOCH, mapper.readTree("{}"));
        DataRecord meteo = new DataRecord(2, "open-meteo", Instant.EPOCH, mapper.readTree("{}"));
        DataRecord joke2 = new DataRecord(3, "joke", Instant.EPOCH, mapper.readTree("{}"));

        List<DataRecord> result = filter.filterBySource(List.of(joke, meteo, joke2), "joke");

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).id());
        assertEquals(3, result.get(1).id());
    }

    @Test
    @DisplayName("Returns empty list when no records match")
    void shouldReturnEmptyWhenNoMatch() throws Exception {
        DataRecord joke = new DataRecord(1, "joke", Instant.EPOCH, mapper.readTree("{}"));

        List<DataRecord> result = filter.filterBySource(List.of(joke), "open-meteo");

        assertTrue(result.isEmpty());
    }
}
