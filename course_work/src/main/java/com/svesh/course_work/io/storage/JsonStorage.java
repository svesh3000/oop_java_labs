package com.svesh.course_work.io.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.svesh.course_work.models.DataRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage implements Storage<List<DataRecord>> {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void write(List<DataRecord> records, Path path, WriteMode mode) {
        switch (mode) {
            case CREATE -> writeCreate(records, path);
            case APPEND -> writeAppend(records, path);
        }
    }

    private void writeCreate(List<DataRecord> records, Path path) {
        try {
            AtomicFileWriter.write(path, tmp ->
                    MAPPER.writerWithDefaultPrettyPrinter().writeValue(tmp.toFile(), records));
        } catch (IOException e) {
            throw new RuntimeException("JSON write failed", e);
        }
    }

    private void writeAppend(List<DataRecord> records, Path path) {
        List<DataRecord> allRecords = read(path);
        allRecords.addAll(records);
        writeCreate(allRecords, path);
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

    @Override
    public int getMaxId(Path path) {
        if (!Files.exists(path)) {
            return 0;
        }
        List<DataRecord> records = read(path);
        return records.stream()
                .mapToInt(DataRecord::id)
                .max()
                .orElse(0);

    }
}
