package com.svesh.course_work.app.cli;

import com.svesh.course_work.api.ApiRegistry;
import com.svesh.course_work.app.io.OutputPathResolver;
import com.svesh.course_work.io.OutputFormat;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CliParser {
    private static final int DEFAULT_MAX_THREADS = 1;
    private static final int DEFAULT_INTERVAL_SECONDS = 5;

    private final ApiRegistry registry;
    private final OutputPathResolver pathResolver;

    public CliParser(ApiRegistry registry, OutputPathResolver pathResolver) {
        this.registry = registry;
        this.pathResolver = pathResolver;
    }

    public CliConfig parse(String[] args) {
        OutputFormat format = null;
        Path path = null;
        List<String> apiNames = new ArrayList<>();
        int maxThreads = DEFAULT_MAX_THREADS;
        int intervalSeconds = DEFAULT_INTERVAL_SECONDS;
        boolean threadsSet = false;
        boolean intervalSet = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg == null || arg.isBlank()) {
                throw new CliError(
                        CliError.Code.ERR_INVALID_ARG,
                        "Empty argument is not allowed"
                );
            }

            switch (arg) {
                case "--api" -> {
                    i++;
                    if (i >= args.length || args[i].startsWith("--")) {
                        throw new CliError(
                                CliError.Code.ERR_EMPTY_API_LIST,
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
                                CliError.Code.ERR_CONFLICT_FORMAT,
                                "Format specified more than once"
                        );
                    }
                    i++;
                    if (i >= args.length || args[i].startsWith("--")) {
                        throw new CliError(
                                CliError.Code.ERR_NO_FORMAT,
                                "Format value is missing"
                        );
                    }
                    format = parseFormat(args[i]);
                }
                case "--out" -> {
                    if (path != null) {
                        throw new CliError(
                                CliError.Code.ERR_CONFLICT_OUTPUT_PATH,
                                "Output file specified more than once"
                        );
                    }
                    i++;
                    if (i >= args.length || args[i].startsWith("--")) {
                        throw new CliError(
                                CliError.Code.ERR_INVALID_OUTPUT_PATH,
                                "Output path is missing"
                        );
                    }
                    try {
                        String input = args[i].trim();
                        if (input.endsWith("/") || input.endsWith("\\")) {
                            throw new CliError(
                                    CliError.Code.ERR_INVALID_OUTPUT_PATH,
                                    "Path is a directory. Specify a file name at the end"
                            );
                        }
                        path = Path.of(args[i].trim());
                    } catch (InvalidPathException e) {
                        throw new CliError(
                                CliError.Code.ERR_INVALID_OUTPUT_PATH,
                                "Invalid output path: " + args[i] + " (" + e.getMessage() + ")"
                        );
                    }
                }
                case "--n" -> {
                    if (threadsSet) {
                        throw new CliError(
                                CliError.Code.ERR_CONFLICT_NUMBER_OF_THREADS,
                                "Parameter --n specified more than once"
                        );
                    }
                    i++;
                    if (i >= args.length || args[i].startsWith("--")) {
                        throw new CliError(
                                CliError.Code.ERR_INVALID_ARG,
                                "Value for --n is missing"
                        );
                    }
                    maxThreads = parseNonNegativeInt(args[i], "n", 1);
                    threadsSet = true;
                }
                case "--t" -> {
                    if (intervalSet) {
                        throw new CliError(
                                CliError.Code.ERR_CONFLICT_INTERVAL,
                                "Parameter --t specified more than once"
                        );
                    }
                    i++;
                    if (i >= args.length || args[i].startsWith("--")) {
                        throw new CliError(
                                CliError.Code.ERR_INVALID_ARG,
                                "Value for --t is missing"
                        );
                    }
                    intervalSeconds = parseNonNegativeInt(args[i], "t", 0);
                    intervalSet = true;
                }
                default -> {
                    if (arg.startsWith("--")) {
                        throw new CliError(
                                CliError.Code.ERR_UNKNOWN_FLAG,
                                "Unknown flag: " + arg
                        );
                    }
                    throw new CliError(
                            CliError.Code.ERR_INVALID_ARG,
                            "Unexpected argument: " + arg
                    );
                }
            }
        }

        if (apiNames.isEmpty()) {
            throw new CliError(
                    CliError.Code.ERR_NO_APIS,
                    "No APIs specified"
            );
        }
        if (format == null) {
            throw new CliError(
                    CliError.Code.ERR_NO_FORMAT,
                    "Format is required"
            );
        }
        path = (path == null) ? Path.of("output") : path;
        path = pathResolver.resolve(path, format);
        return new CliConfig(format, path, apiNames, maxThreads, intervalSeconds);
    }

    private int parseNonNegativeInt(String raw, String name, int min) {
        try {
            int v = Integer.parseInt(raw.trim());
            if (v < min) {
                throw new CliError(
                        CliError.Code.ERR_INVALID_ARG,
                        "Value for --" + name + " must be >= " + min
                );
            }
            return v;
        } catch (NumberFormatException e) {
            throw new CliError(
                    CliError.Code.ERR_INVALID_ARG,
                    "Value for --" + name + " must be an integer, got: " + raw
            );
        }
    }

    private OutputFormat parseFormat(String value) {
        OutputFormat requested = OutputFormat.fromString(value);
        if (requested == null) {
            StringBuilder available = new StringBuilder();
            for (OutputFormat format : OutputFormat.values()) {
                available.append("\n - ").append(format.extension());
            }
            throw new CliError(
                    CliError.Code.ERR_UNKNOWN_FORMAT,
                    "Unsupported format: " + value + "\nAvailable formats:" + available
            );
        }
        return requested;
    }

    private void addApi(String apiName, List<String> apiNames) {
        if (registry.get(apiName) == null) {
            StringBuilder available = new StringBuilder();
            registry.getAll().forEach(api -> available.append("\n - ").append(api.getApiName()));

            throw new CliError(
                    CliError.Code.ERR_UNKNOWN_API,
                    "Unknown API: " + apiName + "\nAvailable APIs:" + available
            );
        }
        apiNames.add(apiName);
    }
}
