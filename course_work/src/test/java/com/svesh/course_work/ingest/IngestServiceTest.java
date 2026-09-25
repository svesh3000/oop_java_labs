package com.svesh.course_work.ingest;

import com.svesh.course_work.api.ApiRequest;
import com.svesh.course_work.api.impl.JokeApi;
import com.svesh.course_work.api.impl.OpenMeteoApi;
import com.svesh.course_work.models.DataRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("IngestService: fetch and aggregate")
class IngestServiceTest {
    @Mock
    private HttpService httpService;

    private IngestService ingestService;

    @BeforeEach
    void setUp() {
        ingestService = new IngestService(httpService, new JsonParser());
    }

    @Test
    @DisplayName("fetchOne returns DataRecord with given id and source")
    void shouldFetchOne() throws IOException {
        when(httpService.fetch(any())).thenReturn("{\"joke\":\"ha\"}");

        ApiRequest req = new ApiRequest(new JokeApi(), Map.of());
        DataRecord record = ingestService.fetchOne(req, 42);

        assertEquals(42, record.id());
        assertEquals("joke", record.source());
        assertEquals("ha", record.data().get("joke").asText());
        assertNotNull(record.timestamp());
    }

    @Test
    @DisplayName("fetchOne propagates IOException from HttpService")
    void shouldPropagateIoException() throws IOException {
        when(httpService.fetch(any())).thenThrow(new IOException("network down"));

        ApiRequest req = new ApiRequest(new JokeApi(), Map.of());
        assertThrows(IOException.class, () -> ingestService.fetchOne(req, 1));
    }

    @Test
    @DisplayName("aggregate assigns consecutive ids starting from startId")
    void shouldAggregateWithConsecutiveIds() throws IOException {
        when(httpService.fetch(any())).thenReturn("{}");

        List<ApiRequest> requests = List.of(
                new ApiRequest(new JokeApi(), Map.of()),
                new ApiRequest(new OpenMeteoApi(), Map.of())
        );
        List<DataRecord> records = ingestService.aggregate(requests, 10);

        assertEquals(2, records.size());
        assertEquals(10, records.get(0).id());
        assertEquals(11, records.get(1).id());
        assertEquals("joke", records.get(0).source());
        assertEquals("open-meteo", records.get(1).source());
    }
}
