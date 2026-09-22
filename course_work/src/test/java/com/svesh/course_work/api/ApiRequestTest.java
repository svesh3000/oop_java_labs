package com.svesh.course_work.api;

import com.svesh.course_work.api.impl.JokeApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ApiRequest: stores api and query params")
class ApiRequestTest {
    @Test
    @DisplayName("ApiRequest holds api and params")
    void shouldStoreApiAndParams() {
        ApiRequest request = new ApiRequest(new JokeApi(), Map.of("type", "single"));

        assertEquals("joke", request.api().getApiName());
        assertEquals("single", request.queryParams().get("type"));
    }
}
