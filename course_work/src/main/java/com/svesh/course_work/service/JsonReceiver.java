package com.svesh.course_work.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReceiver {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public JsonNode parse(String response) throws JsonProcessingException {
        return MAPPER.readTree(response);
    }
}
