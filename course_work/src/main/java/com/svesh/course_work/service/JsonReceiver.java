package com.svesh.course_work.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReceiver {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public JsonNode parse(String response) {
        try {
            return MAPPER.readTree(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid JSON response", e);
        }
    }
}
