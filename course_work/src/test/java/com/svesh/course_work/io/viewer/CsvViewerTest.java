package com.svesh.course_work.io.viewer;

import com.svesh.course_work.io.converters.CsvTable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CsvViewer: prints table as CSV")
class CsvViewerTest {
    private final CsvViewer viewer = new CsvViewer();

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
    @DisplayName("Prints NO DATA for empty table")
    void shouldPrintNoDataForEmptyTable() {
        CsvTable table = new CsvTable(List.of("id", "source"), List.of());

        viewer.show(table);

        assertEquals("NO DATA", out.toString().trim());
    }

    @Test
    @DisplayName("Prints header and rows")
    void shouldPrintHeaderAndRows() {
        CsvTable table = new CsvTable(
                List.of("id", "source", "data.name"),
                List.of(
                        Map.of("id", "1", "source", "joke", "data.name", "Petr"),
                        Map.of("id", "2", "source", "open-meteo", "data.name", "")
                )
        );

        viewer.show(table);

        String output = out.toString();
        String[] lines = output.strip().split("\\R");

        assertEquals("id,source,data.name", lines[0]);
        assertEquals("1,joke,Petr", lines[1]);
        assertEquals("2,open-meteo,", lines[2]);
    }
}
