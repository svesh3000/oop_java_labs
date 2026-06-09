package com.svesh.course_work.io.storage;

import com.svesh.course_work.models.DataRecord;

import java.nio.file.Path;
import java.util.List;

public interface Storage {
    void write(List<DataRecord> records, Path path, WriteMode mode);
}
