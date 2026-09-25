package com.svesh.course_work.ingest;

import com.svesh.course_work.api.ApiDefinition;
import com.svesh.course_work.api.ApiRequest;
import com.svesh.course_work.api.ParamSpec;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HttpService: builds URL and handles responses")
class HttpServiceTest {
    private MockWebServer server;
    private HttpService service;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        service = new HttpService();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    private ApiDefinition apiAt(String url) {
        return new ApiDefinition() {
            @Override
            public String getApiName() {
                return "test";
            }

            @Override
            public String getApiUrl() {
                return url;
            }

            @Override
            public Map<String, ParamSpec> getParamSpecs() {
                return Map.of();
            }

            @Override
            public Map<String, String> getDefaultQueryParams() {
                return Map.of();
            }

            @Override
            public String getInstruction() {
                return "";
            }
        };
    }

    @Test
    @DisplayName("fetch returns body and includes query params in URL")
    void shouldFetchSuccessfully() throws InterruptedException, IOException {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"ok\":true}")
                .addHeader("Content-Type", "application/json"));

        ApiRequest req = new ApiRequest(
                apiAt(server.url("/joke/Any").toString()),
                Map.of("type", "single")
        );

        String body = service.fetch(req);
        assertEquals("{\"ok\":true}", body);

        RecordedRequest rec = server.takeRequest();
        assertNotNull(rec.getPath());
        assertTrue(rec.getPath().contains("type=single"),
                "Query param must be in URL: " + rec.getPath());
    }

    @Test
    @DisplayName("fetch throws IOException on non-2xx status")
    void shouldThrowOnHttpError() {
        server.enqueue(new MockResponse().setResponseCode(500));

        ApiRequest req = new ApiRequest(
                apiAt(server.url("/joke/Any").toString()),
                Map.of()
        );

        IOException error = assertThrows(IOException.class, () -> service.fetch(req));
        assertTrue(error.getMessage().contains("500"));
    }
}
