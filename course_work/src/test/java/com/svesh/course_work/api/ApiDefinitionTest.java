package com.svesh.course_work.api;

import com.svesh.course_work.api.impl.JokeApi;
import com.svesh.course_work.api.impl.OpenMeteoApi;
import com.svesh.course_work.api.impl.ScryfallApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ApiDefinition implementations: meta information")
class ApiDefinitionTest {
    static Stream<ApiDefinition> apis() {
        return Stream.of(new JokeApi(), new OpenMeteoApi(), new ScryfallApi());
    }

    @ParameterizedTest
    @MethodSource("apis")
    @DisplayName("Every API provides non-blank url and instruction")
    void shouldProvideUrlAndInstruction(ApiDefinition api) {
        assertNotNull(api.getApiUrl());
        assertFalse(api.getApiUrl().isBlank());

        assertNotNull(api.getInstruction());
        assertFalse(api.getInstruction().isBlank());
    }
}
