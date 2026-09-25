package com.svesh.course_work.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("OutputFormat: extension, fromString, fromPath")
class OutputFormatTest {
    @Test
    @DisplayName("extension returns the format's file extension")
    void shouldReturnExtension() {
        assertEquals("json", OutputFormat.JSON.extension());
        assertEquals("csv", OutputFormat.CSV.extension());
    }

    @Test
    @DisplayName("fromString parses name")
    void shouldParseFromString() {
        assertEquals(OutputFormat.JSON, OutputFormat.fromString("json"));
        assertEquals(OutputFormat.JSON, OutputFormat.fromString("JSON"));
        assertEquals(OutputFormat.CSV, OutputFormat.fromString("csv"));
        assertEquals(OutputFormat.CSV, OutputFormat.fromString("CsV"));
        assertNull(OutputFormat.fromString("xml"));
        assertNull(OutputFormat.fromString(null));
    }

    @Test
    @DisplayName("fromPath detects format from file extension")
    void shouldParseFromPath() {
        assertEquals(OutputFormat.JSON, OutputFormat.fromPath(Path.of("res.json")));
        assertEquals(OutputFormat.CSV, OutputFormat.fromPath(Path.of("/data/out.csv")));
        assertNull(OutputFormat.fromPath(Path.of("res")));
        assertNull(OutputFormat.fromPath(Path.of("res.")));
        assertNull(OutputFormat.fromPath(Path.of("res.txt")));
        assertNull(OutputFormat.fromPath(null));
    }
}
