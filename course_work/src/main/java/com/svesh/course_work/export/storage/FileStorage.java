package com.svesh.course_work.export.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileStorage {
    public void save(String data, Path path, FileMode mode) throws IOException {
        switch (mode) {
            case CREATE -> Files.writeString(path, data,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            case APPEND -> Files.writeString(path, data,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
            default -> throw new IllegalArgumentException("Unknown mode: " + mode);
        }

    }
}
