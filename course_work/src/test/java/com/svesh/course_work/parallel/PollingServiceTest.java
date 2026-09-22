package com.svesh.course_work.parallel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svesh.course_work.api.ApiRequest;
import com.svesh.course_work.api.impl.JokeApi;
import com.svesh.course_work.ingest.IngestService;
import com.svesh.course_work.io.OutputFormat;
import com.svesh.course_work.io.storage.JsonStorage;
import com.svesh.course_work.io.storage.StorageFactory;
import com.svesh.course_work.io.storage.WriteMode;
import com.svesh.course_work.models.DataRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PollingService: polling")
class PollingServiceTest {
    @Mock
    private IngestService ingestService;

    private final StorageFactory storageFactory = new StorageFactory();
    private final ObjectMapper mapper = new ObjectMapper();

    private PollingService newService(Path file) {
        return new PollingService(ingestService, storageFactory, OutputFormat.JSON, file);
    }

    private List<ApiRequest> getOneRequest() {
        return List.of(new ApiRequest(new JokeApi(), Map.of()));
    }

    private void putStubFetch() throws IOException {
        when(ingestService.fetchOne(any(), anyInt())).thenAnswer(inv -> {
            ApiRequest req = inv.getArgument(0);
            int id = inv.getArgument(1);
            return new DataRecord(id, req.api().getApiName(), Instant.EPOCH, mapper.readTree("{}"));
        });
    }

    @Test
    @DisplayName("isRunning returns false before start")
    void shouldReportNotRunningInitially(@TempDir Path dir) {
        assertFalse(newService(dir.resolve("out.json")).isRunning());
    }

    @Test
    @DisplayName("start rejects maxThreads < 1")
    void shouldRejectZeroThreads(@TempDir Path dir) {
        PollingService service = newService(dir.resolve("out.json"));

        assertThrows(IllegalArgumentException.class,
                () -> service.start(getOneRequest(), 0, 1, WriteMode.CREATE));
    }

    @Test
    @DisplayName("start rejects negative interval")
    void shouldRejectNegativeInterval(@TempDir Path dir) {
        PollingService service = newService(dir.resolve("out.json"));

        assertThrows(IllegalArgumentException.class,
                () -> service.start(getOneRequest(), 1, -1, WriteMode.CREATE));
    }

    @Test
    @DisplayName("start twice throws IllegalStateException")
    void shouldRejectSecondStart(@TempDir Path dir) {
        PollingService service = newService(dir.resolve("out.json"));
        service.start(getOneRequest(), 1, 1, WriteMode.CREATE);
        try {
            assertTrue(service.isRunning());
            assertThrows(IllegalStateException.class,
                    () -> service.start(getOneRequest(), 1, 1, WriteMode.CREATE));
        } finally {
            service.stop();
        }
    }

    @Test
    @DisplayName("stop is a no-op before start")
    void shouldIgnoreStopBeforeStart(@TempDir Path dir) {
        PollingService service = newService(dir.resolve("out.json"));

        assertDoesNotThrow(service::stop);
        assertFalse(service.isRunning());
    }

    @Test
    @DisplayName("start fetches records and writes them to file")
    void shouldWriteFetchedRecordsToFile(@TempDir Path dir) throws InterruptedException, IOException {
        putStubFetch();
        Path file = dir.resolve("out.json");
        PollingService service = newService(file);

        service.start(getOneRequest(), 1, 1, WriteMode.CREATE);
        Thread.sleep(300);
        service.stop();

        assertTrue(Files.exists(file));
        List<DataRecord> records = new JsonStorage().read(file);
        assertFalse(records.isEmpty());
        assertEquals("joke", records.get(0).source());
    }

    @Test
    @DisplayName("start continues id numbering when APPEND")
    void shouldContinueIdsOnAppend(@TempDir Path dir) throws IOException, InterruptedException {
        Path file = dir.resolve("out.json");
        new JsonStorage().write(
                List.of(new DataRecord(5, "old", Instant.EPOCH, mapper.readTree("{}"))),
                file, WriteMode.CREATE
        );
        putStubFetch();

        PollingService service = newService(file);
        service.start(getOneRequest(), 1, 1, WriteMode.APPEND);
        Thread.sleep(300);
        service.stop();

        List<DataRecord> records = new JsonStorage().read(file);
        assertTrue(records.size() >= 2);
        assertEquals(5, records.get(0).id());
        assertTrue(records.get(1).id() >= 6);
    }

    @Test
    @DisplayName("pollOnce survives IOException and keeps polling")
    void shouldSurviveFetchError(@TempDir Path dir) throws IOException, InterruptedException {
        when(ingestService.fetchOne(any(), anyInt()))
                .thenThrow(new IOException("network down"))
                .thenAnswer(inv -> new DataRecord(
                        inv.getArgument(1),
                        inv.<ApiRequest>getArgument(0).api().getApiName(),
                        Instant.EPOCH,
                        mapper.readTree("{}")
                ));

        Path file = dir.resolve("out.json");
        PollingService service = newService(file);
        service.start(getOneRequest(), 1, 1, WriteMode.CREATE);
        Thread.sleep(1500);
        service.stop();

        assertTrue(Files.exists(file));
        assertFalse(new JsonStorage().read(file).isEmpty());
    }

    @Test
    @DisplayName("awaitShutdown returns immediately after stop")
    void shouldReturnFromAwaitAfterStop(@TempDir Path dir) {
        PollingService service = newService(dir.resolve("out.json"));
        service.start(getOneRequest(), 1, 1, WriteMode.CREATE);
        service.stop();

        assertTimeoutPreemptively(Duration.ofSeconds(2), service::awaitShutdown);
    }

    @Test
    @DisplayName("awaitShutdown blocks until stop is called")
    void shouldBlockUntilStop(@TempDir Path dir) throws InterruptedException {
        PollingService service = newService(dir.resolve("out.json"));
        service.start(getOneRequest(), 1, 1, WriteMode.CREATE);

        AtomicBoolean awaitReturned = new AtomicBoolean(false);
        Thread awaiter = new Thread(() -> {
            try {
                service.awaitShutdown();
                awaitReturned.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        awaiter.start();

        Thread.sleep(150);
        assertFalse(awaitReturned.get(), "awaitShutdown must still be blocked");

        service.stop();
        awaiter.join(2000);
        assertTrue(awaitReturned.get(), "awaitShutdown must return after stop");
    }
}
