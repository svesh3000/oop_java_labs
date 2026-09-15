package com.svesh.course_work.io.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

final class AtomicFileWriter {
    private AtomicFileWriter() {
    }

    @FunctionalInterface
    interface ContentWriter {
        void writeTo(Path tmp) throws IOException;
    }

    static void write(Path target, ContentWriter writer) throws IOException {
        Path abs = target.toAbsolutePath();
        Path dir = abs.getParent();
        if (dir == null) {
            dir = Path.of(".");
        }

        Files.createDirectories(dir);

        Path tmp = Files.createTempFile(dir, ".export-", ".tmp");
        try {
            writer.writeTo(tmp);
            try {
                Files.move(tmp, abs,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, abs, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            Files.deleteIfExists(tmp);
            throw e;
        }
    }
}
