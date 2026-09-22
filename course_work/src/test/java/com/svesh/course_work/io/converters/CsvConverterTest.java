package com.svesh.course_work.io.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svesh.course_work.models.DataRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CsvConverter: convert DataRecords into CsvTable")
class CsvConverterTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Converts nested object and array into multiple rows")
    void shouldConvertNestedRecordIntoRows() throws JsonProcessingException {
        JsonNode data = mapper.readTree("""
                {
                  "customer": {"name": "Petr"},
                  "items": [
                    {"id": 1, "name": "Milk"},
                    {"id": 2, "name": "Bread"}
                  ]
                }
                """);
        DataRecord record = new DataRecord(1, "test", Instant.EPOCH, data);

        CsvTable table = new CsvConverter().convert(List.of(record));

        assertEquals(2, table.rows().size());

        for (Map<String, String> row : table.rows()) {
            assertEquals("1", row.get("id"));
            assertEquals("test", row.get("source"));
            assertEquals("1970-01-01T00:00:00Z", row.get("timestamp"));
            assertEquals("Petr", row.get("data.customer.name"));
        }

        assertEquals("1", table.rows().get(0).get("data.items.id"));
        assertEquals("Milk", table.rows().get(0).get("data.items.name"));
        assertEquals("2", table.rows().get(1).get("data.items.id"));
        assertEquals("Bread", table.rows().get(1).get("data.items.name"));

        assertTrue(table.headers().contains("id"));
        assertTrue(table.headers().contains("data.customer.name"));
        assertTrue(table.headers().contains("data.items.id"));
        assertTrue(table.headers().contains("data.items.name"));
    }

    @Test
    @DisplayName("Empty array does not add columns or rows")
    void shouldHandleEmptyArray() throws Exception {
        JsonNode data = mapper.readTree("""
                {
                  "customer": {"name": "Petr"},
                  "tags": []
                }
                """);
        DataRecord record = new DataRecord(1, "test", Instant.EPOCH, data);

        CsvTable table = new CsvConverter().convert(List.of(record));

        assertEquals(1, table.rows().size());
        assertFalse(table.headers().contains("data.tags"));
        assertEquals("Petr", table.rows().get(0).get("data.customer.name"));
    }
}
