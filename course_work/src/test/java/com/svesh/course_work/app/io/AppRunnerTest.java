package com.svesh.course_work.app.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svesh.course_work.api.ApiRequest;
import com.svesh.course_work.api.impl.JokeApi;
import com.svesh.course_work.ingest.IngestService;
import com.svesh.course_work.io.OutputFormat;
import com.svesh.course_work.io.filters.FilterFactory;
import com.svesh.course_work.io.storage.JsonStorage;
import com.svesh.course_work.io.storage.StorageFactory;
import com.svesh.course_work.io.storage.WriteMode;
import com.svesh.course_work.io.viewer.ViewerFactory;
import com.svesh.course_work.models.DataRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppRunner: export, view, polling")
class AppRunnerTest {
    @Mock
    private IngestService ingestService;

    private AppRunner runner;
    private final ObjectMapper mapper = new ObjectMapper();

    private PrintStream originalOut;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        runner = new AppRunner(
                ingestService,
                new StorageFactory(),
                new ViewerFactory(),
                new FilterFactory()
        );
        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true));
    }

    @AfterEach
    void restoreStdout() {
        System.setOut(originalOut);
    }

    private DataRecord record(int id, String source) throws IOException {
        return new DataRecord(id, source, Instant.EPOCH, mapper.readTree("{\"name\":\"Petr\"}"));
    }

    private ApiRequest request() {
        return new ApiRequest(new JokeApi(), Map.of());
    }

    @Test
    @DisplayName("Export with CREATE writes fresh file")
    void shouldExportWithCreate(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out.json");
        when(ingestService.aggregate(anyList(), eq(1))).thenReturn(List.of(record(1, "joke")));

        runner.export(List.of(request()), OutputFormat.JSON, file, WriteMode.CREATE);

        assertTrue(Files.exists(file));
        List<DataRecord> read = new JsonStorage().read(file);
        assertEquals(1, read.size());
        assertEquals(1, read.get(0).id());
        assertEquals("joke", read.get(0).source());
    }

    @Test
    @DisplayName("Export with APPEND continues id numbering")
    void shouldExportWithAppend(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out.json");
        new JsonStorage().write(List.of(record(5, "old")), file, WriteMode.CREATE);

        when(ingestService.aggregate(anyList(), eq(6)))
                .thenReturn(List.of(record(6, "new")));

        runner.export(List.of(request()), OutputFormat.JSON, file, WriteMode.APPEND);

        List<DataRecord> all = new JsonStorage().read(file);
        assertEquals(2, all.size());
        assertEquals(5, all.get(0).id());
        assertEquals(6, all.get(1).id());
    }

    @Test
    @DisplayName("viewAll reads the file and prints its content")
    void shouldViewAll(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out.json");
        new JsonStorage().write(List.of(record(1, "joke")), file, WriteMode.CREATE);

        runner.viewAll(OutputFormat.JSON, file);

        String output = out.toString();
        assertTrue(output.contains("\"id\""));
        assertTrue(output.contains("\"source\""));
        assertTrue(output.contains("\"joke\""));
        assertTrue(output.contains("Petr"));
    }

    @Test
    @DisplayName("viewBySource filters by source before printing")
    void shouldViewBySource(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out.json");
        new JsonStorage().write(
                List.of(record(1, "joke"), record(2, "open-meteo")),
                file, WriteMode.CREATE
        );

        runner.viewBySource(OutputFormat.JSON, file, "joke");

        String output = out.toString();
        assertTrue(output.contains("\"id\" : 1"));
        assertTrue(output.contains("\"source\""));
        assertTrue(output.contains("\"joke\""));
        assertTrue(output.contains("\"name\""));
        assertTrue(output.contains("Petr"));
    }

    @Test
    @DisplayName("isPollingRunning returns false before any start")
    void shouldReportNotRunningInitially() {
        assertFalse(runner.isPollingRunning());
    }

    @Test
    @DisplayName("startPolling throws IllegalStateException when polling is already running")
    void shouldThrowWhenPollingAlreadyRunning(@TempDir Path dir) {
        Path file = dir.resolve("out.json");
        List<ApiRequest> requests = List.of(request());

        runner.startPolling(requests, OutputFormat.JSON, file, 1, 1, WriteMode.CREATE);
        try {
            assertTrue(runner.isPollingRunning());
            assertThrows(IllegalStateException.class,
                    () -> runner.startPolling(
                            requests, OutputFormat.JSON,
                            file, 1, 1, WriteMode.CREATE
                    ));
        } finally {
            runner.stopPolling();
        }
    }
}
