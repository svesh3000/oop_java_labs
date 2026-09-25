package com.svesh.course_work.app.requests;

import com.svesh.course_work.api.ApiRegistry;
import com.svesh.course_work.api.ApiRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("RequestBuilder: builds ApiRequests from names and params")
class RequestBuilderTest {
    private final RequestBuilder builder = new RequestBuilder(new ApiRegistry());

    @Test
    @DisplayName("Uses default params when none provided")
    void shouldUseDefaults() {
        List<ApiRequest> requests = builder.build(List.of("open-meteo"), Map.of());

        assertEquals(1, requests.size());
        assertEquals("open-meteo", requests.get(0).api().getApiName());
        assertEquals("52.97", requests.get(0).queryParams().get("latitude"));
    }

    @Test
    @DisplayName("Uses provided params instead of defaults")
    void shouldUseProvidedParams() {
        Map<String, String> customParam = Map.of("latitude", "50.0");
        List<ApiRequest> requests = builder.build(
                List.of("open-meteo"),
                Map.of("open-meteo", customParam)
        );

        assertEquals("50.0", requests.get(0).queryParams().get("latitude"));
    }

    @Test
    @DisplayName("Throws on unknown API")
    void shouldThrowOnUnknownApi() {
        assertThrows(IllegalArgumentException.class,
                () -> builder.build(List.of("unknown"), Map.of()));
    }
}
