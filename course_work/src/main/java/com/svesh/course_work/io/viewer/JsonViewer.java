package com.svesh.course_work.io.viewer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.svesh.course_work.models.DataRecord;

import java.util.List;

public class JsonViewer implements Viewer<List<DataRecord>> {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());;

    @Override
    public void show(List<DataRecord> data) {
        if (data.isEmpty()) {
            System.out.println("NO DATA");
            return;
        }

        try {
            System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(data));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON view failed", e);
        }
    }
}
