package com.svesh.course_work.io.storage;

import com.svesh.course_work.models.DataRecord;

import java.nio.file.Path;
import java.util.List;

public interface Storage<T> {
    void write(List<DataRecord> records, Path path, WriteMode mode);
    T read(Path path);
    int getMaxId(Path path);
}
