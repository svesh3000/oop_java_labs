package com.svesh.course_work.io.storage;

import com.svesh.course_work.io.OutputFormat;

public class StorageFactory {
    public Storage create(OutputFormat format) {
        return switch (format) {
            case JSON -> new JsonStorage();
            case CSV -> new CsvStorage();
        };
    }
}
