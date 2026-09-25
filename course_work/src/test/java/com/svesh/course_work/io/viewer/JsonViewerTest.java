package com.svesh.course_work.io.viewer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svesh.course_work.models.DataRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("JsonViewer: prints records as pretty JSON")
class JsonViewerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonViewer viewer = new JsonViewer();

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void redirectStdout() {
        originalOut = System.out;
        System.setOut(new PrintStream(out, true));
    }

    @AfterEach
    void restoreStdout() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Prints NO DATA for empty list")
    void shouldPrintNoDataForEmptyList() {
        viewer.show(List.of());

        assertEquals("NO DATA", out.toString().trim());
    }

    @Test
    @DisplayName("Prints records with their fields")
    void shouldPrintRecords() throws JsonProcessingException {
        DataRecord record = new DataRecord(1, "joke", Instant.EPOCH, mapper.readTree("""
                {
                  "type": "single"
                }
                """));

        viewer.show(List.of(record));

        String output = out.toString();
        assertTrue(output.contains("\"id\""));
        assertTrue(output.contains("\"source\""));
        assertTrue(output.contains("\"joke\""));
        assertTrue(output.contains("\"type\""));
        assertTrue(output.contains("\"single\""));
    }
}
