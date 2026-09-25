package com.svesh.course_work.ingest;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("JsonParser: parses JSON strings")
class JsonParserTest {
    private final JsonParser parser = new JsonParser();

    @Test
    @DisplayName("Parses valid JSON")
    void shouldParseValidJson() {
        JsonNode node = parser.parse("{\"name\":\"Petr\",\"age\":30}");

        assertEquals("Petr", node.get("name").asText());
        assertEquals(30, node.get("age").asInt());
    }

    @Test
    @DisplayName("Throws RuntimeException on invalid JSON")
    void shouldThrowOnInvalidJson() {
        assertThrows(RuntimeException.class, () -> parser.parse("not json at all"));
    }
}
