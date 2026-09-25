package com.svesh.course_work.io.filters;

import com.svesh.course_work.io.converters.CsvTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CsvFilter: filter table rows by source")
class CsvFilterTest {
    private final CsvFilter filter = new CsvFilter();

    @Test
    @DisplayName("Keeps only rows with matching source")
    void shouldKeepOnlyMatchingSource() {
        CsvTable table = new CsvTable(
                List.of("id", "source"),
                List.of(
                        Map.of("id", "1", "source", "joke"),
                        Map.of("id", "2", "source", "open-meteo"),
                        Map.of("id", "3", "source", "joke")
                )
        );

        CsvTable result = filter.filterBySource(table, "joke");

        assertEquals(2, result.rows().size());
        assertEquals("1", result.rows().get(0).get("id"));
        assertEquals("3", result.rows().get(1).get("id"));
        assertEquals(table.headers(), result.headers());
    }
}
