package com.svesh.course_work.io.filters;

import com.svesh.course_work.io.OutputFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("FilterFactory: create filter by format")
class FilterFactoryTest {
    private final FilterFactory factory = new FilterFactory();

    @Test
    @DisplayName("Creates JsonFilter for JSON and CsvFilter for CSV")
    void shouldCreateCorrectFilter() {
        assertInstanceOf(JsonFilter.class, factory.create(OutputFormat.JSON));
        assertInstanceOf(CsvFilter.class, factory.create(OutputFormat.CSV));
    }
}
