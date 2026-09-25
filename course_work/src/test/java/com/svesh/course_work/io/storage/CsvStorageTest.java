package com.svesh.course_work.io.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svesh.course_work.io.converters.CsvTable;
import com.svesh.course_work.models.DataRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CsvStorage: write, read, getMaxId")
class CsvStorageTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final CsvStorage storage = new CsvStorage();

    private DataRecord record(int id, String source, String dataJson) throws IOException {
        return new DataRecord(id, source, Instant.EPOCH, mapper.readTree(dataJson));
    }

    @Test
    @DisplayName("CREATE writes header and rows, read returns them")
    void shouldWriteAndReadTheSame(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out.csv");

        storage.write(List.of(record(1, "joke", "{\"name\":\"Petr\"}")), file, WriteMode.CREATE);
        CsvTable table = storage.read(file);

        assertEquals(1, table.rows().size());
        assertTrue(table.headers().contains("id"));
        assertTrue(table.headers().contains("source"));
        assertTrue(table.headers().contains("data.name"));

        assertEquals("1", table.rows().get(0).get("id"));
        assertEquals("joke", table.rows().get(0).get("source"));
        assertEquals("Petr", table.rows().get(0).get("data.name"));
    }

    @Test
    @DisplayName("APPEND merges headers and preserves existing rows")
    void shouldAppendAndMergeHeaders(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out.csv");

        storage.write(List.of(record(1, "a", "{\"name\":\"Petr\"}")), file, WriteMode.CREATE);
        storage.write(List.of(record(2, "b", "{\"age\":30}")), file, WriteMode.APPEND);
        CsvTable table = storage.read(file);

        assertEquals(2, table.rows().size());
        assertTrue(table.headers().contains("data.name"));
        assertTrue(table.headers().contains("data.age"));
        assertEquals("Petr", table.rows().get(0).get("data.name"));
        assertEquals("30", table.rows().get(1).get("data.age"));
        assertEquals(2, storage.getMaxId(file));
    }

    @Test
    @DisplayName("APPEND on missing or empty file falls back to CREATE")
    void shouldFallbackToCreateOnMissingOrEmptyFile(@TempDir Path dir) throws IOException {
        Path missing = dir.resolve("missing.csv");
        storage.write(List.of(record(1, "joke", "{\"name\":\"A\"}")), missing, WriteMode.APPEND);

        CsvTable fromMissing = storage.read(missing);
        assertEquals(1, fromMissing.rows().size());

        Path empty = dir.resolve("empty.csv");
        Files.createFile(empty);
        storage.write(List.of(record(2, "joke", "{\"name\":\"B\"}")), empty, WriteMode.APPEND);

        CsvTable fromEmpty = storage.read(empty);
        assertEquals(1, fromEmpty.rows().size());
        assertEquals("B", fromEmpty.rows().get(0).get("data.name"));
    }
}
