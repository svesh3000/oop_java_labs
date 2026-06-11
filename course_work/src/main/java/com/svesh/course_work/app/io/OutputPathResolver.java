package com.svesh.course_work.app.io;

import com.svesh.course_work.app.cli.CliError;
import com.svesh.course_work.io.OutputFormat;

import java.nio.file.Path;

public class OutputPathResolver {
    public Path resolve(Path input, OutputFormat format) {
        String extension = format.extension();
        if (input == null) {
            return Path.of("output." + extension);
        }

        String fileName = input.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return input.resolveSibling(fileName + "." + extension);
        }

        String actualExtension = fileName.substring(dotIndex + 1);
        if (!actualExtension.equalsIgnoreCase(extension)) {
            throw new CliError(
                    "ERR_FORMAT_PATH_CONFLICT",
                    "File extension '" + actualExtension + "' does not match format '" + extension + "'"
            );
        }
        return input;
    }
}
