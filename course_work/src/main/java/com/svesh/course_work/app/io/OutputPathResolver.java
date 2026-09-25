package com.svesh.course_work.app.io;

import com.svesh.course_work.app.cli.CliError;
import com.svesh.course_work.io.OutputFormat;

import java.nio.file.Path;

public class OutputPathResolver {
    public Path resolve(Path input, OutputFormat format) {
        String extension = format.extension();

        Path fileName = input.getFileName();
        if (fileName == null) {
            throw new CliError(CliError.Code.ERR_INVALID_OUTPUT_PATH, "Path must point to a file, not a root: " + input);
        }

        String name = fileName.toString();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == name.length() - 1) {
            String base = dotIndex == -1 ? name : name.substring(0, dotIndex);
            return input.resolveSibling(base + "." + extension);
        }

        String actualExtension = name.substring(dotIndex + 1);
        if (!actualExtension.equalsIgnoreCase(extension)) {
            throw new CliError(
                    CliError.Code.ERR_FORMAT_PATH_CONFLICT,
                    "File extension '" + actualExtension + "' does not match format '" + extension + "'"
            );
        }
        return input;
    }
}
