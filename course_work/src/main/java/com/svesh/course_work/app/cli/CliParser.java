package com.svesh.course_work.app.cli;

import com.svesh.course_work.api.ApiRegistry;
import com.svesh.course_work.app.io.OutputPathResolver;
import com.svesh.course_work.io.OutputFormat;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class CliParser {
    private final ApiRegistry registry;
    private final OutputPathResolver pathResolver;

    public CliParser(ApiRegistry registry, OutputPathResolver pathResolver) {
        this.registry = registry;
        this.pathResolver = pathResolver;
    }

    public CliConfig parse(String[] args) {
        OutputFormat format = null;
        Path path = null;
        Set<String> apiNames = new LinkedHashSet<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg == null || arg.isBlank()) {
                throw new CliError(
                        "ERR_INVALID_ARG",
                        "Empty argument is not allowed"
                );
            }

            switch (arg) {
                case "--api" -> {
                    i++;
                    if (i >= args.length || args[i].startsWith("--")) {
                        throw new CliError(
                                "ERR_EMPTY_API_LIST",
                                "No APIs specified after --api"
                        );
                    }
                    while (i < args.length && !args[i].startsWith("--")) {
                        addApi(args[i], apiNames);
                        i++;
                    }
                    i--;
                }
                case "--format" -> {
                    if (format != null) {
                        throw new CliError(
                                "ERR_CONFLICT_FORMAT",
                                "Format specified more than once"
                        );
                    }
                    i++;
                    if (i >= args.length || args[i].startsWith("--")) {
                        throw new CliError(
                                "ERR_NO_FORMAT",
                                "Format value is missing"
                        );
                    }
                    format = parseFormat(args[i]);
                }

                case "--out" -> {
                    if (path != null) {
                        throw new CliError(
                                "ERR_CONFLICT_OUTPUT",
                                "Output file specified more than once"
                        );
                    }
                    i++;
                    if (i >= args.length || args[i].startsWith("--")) {
                        throw new CliError(
                                "ERR_INVALID_OUTPUT",
                                "Output path is missing"
                        );
                    }
                    path = Path.of(args[i].trim());
                }
                default -> {
                    if (arg.startsWith("--")) {
                        throw new CliError(
                                "ERR_UNKNOWN_FLAG",
                                "Unknown flag: " + arg
                        );
                    }
                    throw new CliError(
                            "ERR_INVALID_ARG",
                            "Unexpected argument: " + arg
                    );
                }
            }
        }

        if (apiNames.isEmpty()) {
            throw new CliError(
                    "ERR_NO_APIS",
                    "No APIs specified"
            );
        }
        if (format == null) {
            throw new CliError(
                    "ERR_NO_FORMAT",
                    "Format is required"
            );
        }
        path = pathResolver.resolve(path, format);
        return new CliConfig(format, path, new ArrayList<>(apiNames));
    }

    private OutputFormat parseFormat(String value) {
        OutputFormat requested = OutputFormat.fromString(value);
        if (requested == null) {
            throw new CliError(
                    "ERR_UNKNOWN_FORMAT",
                    "Unsupported format: " + value
            );
        }
        return requested;
    }

    private void addApi(String apiName, Set<String> apiNames) {
        if (registry.get(apiName) == null) {
            StringBuilder available = new StringBuilder();
            registry.getAll().forEach(api -> available.append("\n - ").append(api.getApiName()));

            throw new CliError(
                    "ERR_UNKNOWN_API",
                    "Unknown API: " + apiName + "\nAvailable APIs:" + available
            );
        }
        if (!apiNames.add(apiName)) {
            throw new CliError(
                    "ERR_DUPLICATE_API",
                    "API specified multiple times: " + apiName
            );
        }
    }
}
