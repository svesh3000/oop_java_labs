package com.svesh.course_work.io.storage;

import com.svesh.course_work.io.OutputFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("StorageFactory: create storage by format")
class StorageFactoryTest {
    private final StorageFactory factory = new StorageFactory();

    @Test
    @DisplayName("Creates JsonStorage for JSON and CsvStorage for CSV")
    void shouldCreateCorrectStorage() {
        assertInstanceOf(JsonStorage.class, factory.create(OutputFormat.JSON));
        assertInstanceOf(CsvStorage.class, factory.create(OutputFormat.CSV));
    }
}
