package com.svesh.course_work.io.viewer;

import com.svesh.course_work.io.OutputFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("ViewerFactory: create viewer by format")
class ViewerFactoryTest {
    private final ViewerFactory factory = new ViewerFactory();

    @Test
    @DisplayName("Creates JsonViewer for JSON and CsvViewer for CSV")
    void shouldCreateCorrectViewer() {
        assertInstanceOf(JsonViewer.class, factory.create(OutputFormat.JSON));
        assertInstanceOf(CsvViewer.class, factory.create(OutputFormat.CSV));
    }
}
