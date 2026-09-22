package com.svesh.course_work.io.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@DisplayName("JsonStorage: write, read, getMaxId")
class JsonStorageTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonStorage storage = new JsonStorage();

    private DataRecord buildRecord(int id, String source) throws IOException {
        return new DataRecord(id, source, Instant.EPOCH, mapper.readTree("{\"name\":\"Petr\"}"));
    }

    @Test
    @DisplayName("CREATE writes fresh file and read returns the same records")
    void shouldWriteAndReadRoundTrip(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out.json");
        List<DataRecord> records = List.of(buildRecord(1, "joke"), buildRecord(2, "open-meteo"));

        storage.write(records, file, WriteMode.CREATE);

        List<DataRecord> read = storage.read(file);
        assertEquals(2, read.size());
        assertEquals(1, read.get(0).id());
        assertEquals("joke", read.get(0).source());
        assertEquals(2, read.get(1).id());
        assertEquals("open-meteo", read.get(1).source());
    }

    @Test
    @DisplayName("APPEND keeps existing records and getMaxId returns the highest id")
    void shouldAppendAndReturnMaxId(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out.json");

        assertEquals(0, storage.getMaxId(file));

        storage.write(List.of(buildRecord(1, "a")), file, WriteMode.CREATE);
        storage.write(List.of(buildRecord(5, "b")), file, WriteMode.APPEND);

        List<DataRecord> read = storage.read(file);
        assertEquals(2, read.size());
        assertEquals(1, read.get(0).id());
        assertEquals(5, read.get(1).id());

        assertEquals(5, storage.getMaxId(file));
    }

    @Test
    @DisplayName("read returns empty list for missing or empty file")
    void shouldReturnEmptyForMissingOrEmptyFile(@TempDir Path dir) throws IOException {
        Path missing = dir.resolve("missing.json");
        assertTrue(storage.read(missing).isEmpty());

        Path empty = dir.resolve("empty.json");
        Files.createFile(empty);
        assertTrue(storage.read(empty).isEmpty());
    }
}
