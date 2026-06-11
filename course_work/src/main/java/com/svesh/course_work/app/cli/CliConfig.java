package com.svesh.course_work.app.cli;

import com.svesh.course_work.io.OutputFormat;

import java.nio.file.Path;
import java.util.List;

public record CliConfig(
        OutputFormat format,
        Path path,
        List<String> apiNames
) {
}
