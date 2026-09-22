package com.svesh.course_work.api;

import com.svesh.course_work.api.impl.JokeApi;
import com.svesh.course_work.api.impl.OpenMeteoApi;
import com.svesh.course_work.api.impl.ScryfallApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ParamResolver: merges params with defaults and validates parameters")
class ParamResolverTest {
    private final ApiDefinition openMeteo = new OpenMeteoApi();
    private final ApiDefinition joke = new JokeApi();
    private final ApiDefinition scryfall = new ScryfallApi();

    @Test
    @DisplayName("Empty input -> all default parameters")
    void shouldReturnDefaultsWhenNoOverrides() {
        Map<String, String> overrides = Map.of();

        ParamResolver.Result result = ParamResolver.resolve(openMeteo, overrides);

        assertTrue(result.isSuccess());
        assertEquals("52.97", result.params().get("latitude"));
        assertEquals("35.91", result.params().get("longitude"));
    }

    @Test
    @DisplayName("User parameter overrides default value")
    void shouldOverrideDefaultValue() {
        Map<String, String> overrides = Map.of("latitude", "50.03");

        ParamResolver.Result result = ParamResolver.resolve(openMeteo, overrides);

        assertTrue(result.isSuccess());
        assertEquals("50.03", result.params().get("latitude"));
        assertEquals("35.91", result.params().get("longitude"));
    }

    @Test
    @DisplayName("Unknown parameter is rejected")
    void shouldRejectUnknownParameter() {
        Map<String, String> overrides = Map.of("number", "one");

        ParamResolver.Result result = ParamResolver.resolve(openMeteo, overrides);

        assertFalse(result.isSuccess());
        assertNull(result.params());
        assertTrue(result.error().contains("Unknown parameter"));
        assertTrue(result.error().contains("number"));
    }

    @Test
    @DisplayName("Valid single value is accepted")
    void shouldAcceptValidSingleValue() {
        Map<String, String> overrides = Map.of("type", "single");

        ParamResolver.Result result = ParamResolver.resolve(joke, overrides);

        assertTrue(result.isSuccess());
        assertEquals("single", result.params().get("type"));
    }

    @Test
    @DisplayName("Invalid single value is rejected")
    void shouldRejectInvalidSingleValue() {
        Map<String, String> overrides = Map.of("type", "inyfg");

        ParamResolver.Result result = ParamResolver.resolve(joke, overrides);

        assertFalse(result.isSuccess());
        assertTrue(result.error().contains("Unknown value"));
        assertTrue(result.error().contains("inyfg"));
    }

    @Test
    @DisplayName("List of valid values is accepted")
    void shouldAcceptValidMultiValue() {
        Map<String, String> overrides = Map.of("blacklistFlags", "racist, nsfw");

        ParamResolver.Result result = ParamResolver.resolve(joke, overrides);

        assertTrue(result.isSuccess());
        assertEquals("racist, nsfw", result.params().get("blacklistFlags"));
    }

    @Test
    @DisplayName("Empty element in list is rejected")
    void shouldRejectEmptyElementInMultiValue() {
        Map<String, String> overrides = Map.of("blacklistFlags", "racist,,nsfw");

        ParamResolver.Result result = ParamResolver.resolve(joke, overrides);

        assertFalse(result.isSuccess());
        assertTrue(result.error().contains("Empty value"));
    }

    @Test
    @DisplayName("Invalid element in list is rejected")
    void shouldRejectInvalidElementInMultiValue() {
        Map<String, String> overrides = Map.of("blacklistFlags", "racist,blah");

        ParamResolver.Result result = ParamResolver.resolve(joke, overrides);

        assertFalse(result.isSuccess());
        assertTrue(result.error().contains("blah"));
    }

    @Test
    @DisplayName("Free-form parameter accepts any value")
    void shouldAcceptAnyValueForFreeFormParam() {
        Map<String, String> overrides = Map.of("q", "dwarf");

        ParamResolver.Result result = ParamResolver.resolve(scryfall, overrides);

        assertTrue(result.isSuccess());
        assertEquals("dwarf", result.params().get("q"));
    }
}
