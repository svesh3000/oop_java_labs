package com.svesh.course_work.io.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svesh.course_work.models.DataRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage implements Storage {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void write(List<DataRecord> records, Path path, WriteMode mode) {
        switch (mode) {
            case CREATE -> writeCreate(records, path);
            case APPEND -> writeAppend(records, path);
        }
    }

    private void writeCreate(List<DataRecord> records, Path path) {
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), records);
        } catch (IOException e) {
            throw new RuntimeException("JSON write failed", e);
        }
    }

    private void writeAppend(List<DataRecord> records, Path path) {
        try {
            List<DataRecord> allRecords = read(path);
            allRecords.addAll(records);
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), allRecords);
        } catch (IOException e) {
            throw new RuntimeException("JSON append failed", e);
        }
    }

    public List<DataRecord> read(Path path) {
        try {
            if (!Files.exists(path) || Files.size(path) == 0) {
                return new ArrayList<>();
            }
            DataRecord[] records = MAPPER.readValue(path.toFile(), DataRecord[].class);
            return new ArrayList<>(List.of(records));
        } catch (IOException e) {
            throw new RuntimeException("JSON read failed", e);
        }
    }
}
