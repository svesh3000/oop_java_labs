package com.svesh.course_work.app.modes;

import com.svesh.course_work.api.*;
import com.svesh.course_work.app.cli.CliError;
import com.svesh.course_work.app.io.AppRunner;
import com.svesh.course_work.app.io.OutputPathResolver;
import com.svesh.course_work.app.requests.RequestBuilder;
import com.svesh.course_work.io.OutputFormat;
import com.svesh.course_work.io.storage.WriteMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;

public class InteractiveMode {
    private final AppRunner runner;
    private final ApiRegistry registry;
    private final RequestBuilder requestBuilder;
    private final OutputPathResolver pathResolver;
    private final Scanner scanner = new Scanner(System.in);

    private record ViewTarget(Path path, OutputFormat format) {
    }

    private enum FileAction {OVERWRITE, APPEND, CHANGE_PATH, CANCEL}

    public InteractiveMode(AppRunner runner, ApiRegistry registry) {
        this.runner = runner;
        this.registry = registry;
        this.requestBuilder = new RequestBuilder(registry);
        this.pathResolver = new OutputPathResolver();
    }

    public int run() {
        System.out.println("""
                ===== DATA AGGREGATOR =====
                Interactive mode.
                """);
        while (true) {
            System.out.println("""
                    ===== MAIN MENU =====
                    1. Export data from API
                    2. View all records
                    3. View records by source
                    0. Exit
                    """);
            System.out.print("> ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> exportFlow();
                case "2" -> viewAllFlow();
                case "3" -> viewBySourceFlow();
                case "0" -> {
                    System.out.println("EXIT");
                    return 0;
                }
                default -> System.out.println(
                        "Invalid option. Please enter numeric indexes from the list.");
            }
        }
    }

    private void exportFlow() {
        System.out.println("\n--- EXPORT MODE ---");

        ApiDefinition api = selectApiByName();
        if (api == null) {
            sayCancelled();
            return;
        }

        Map<String, String> params = collectParams(api);
        if (params == null) {
            sayCancelled();
            return;
        }

        OutputFormat format = selectFormat();
        if (format == null) {
            sayCancelled();
            return;
        }

        Path path = null;
        WriteMode mode = null;

        while (mode == null) {
            path = askPath(format);
            if (path == null) {
                sayCancelled();
                return;
            }

            if (!Files.exists(path)) {
                mode = WriteMode.CREATE;
            } else {
                FileAction action = askFileAction(path);
                switch (action) {
                    case OVERWRITE -> mode = WriteMode.CREATE;
                    case APPEND -> mode = WriteMode.APPEND;
                    case CHANGE_PATH -> {
                    }
                    case CANCEL -> {
                        sayCancelled();
                        return;
                    }
                }
            }
        }

        List<ApiRequest> requests = requestBuilder.build(
                List.of(api.getApiName()),
                Map.of(api.getApiName(), params)
        );
        try {
            runner.export(requests, format, path, mode);
            System.out.println("EXPORT COMPLETED -> " + path);
        } catch (IOException e) {
            System.out.println("EXPORT FAILED: " + e.getMessage());
        }
    }

    private FileAction askFileAction(Path path) {
        System.out.println("File " + path + " already exists.");
        System.out.println("  1 = Overwrite");
        System.out.println("  2 = Append");
        System.out.println("  3 = Choose another path");
        System.out.println("  Enter = Cancel");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    return FileAction.OVERWRITE;
                case "2":
                    return FileAction.APPEND;
                case "3":
                    return FileAction.CHANGE_PATH;
                case "":
                    return FileAction.CANCEL;
                default:
                    System.out.println("Please enter 1, 2, 3, or Enter.");
            }
        }
    }

    private ApiDefinition selectApiByName() {
        List<ApiDefinition> apis = registry.getAll();
        System.out.println("Available APIs: "
                + apis.stream().map(ApiDefinition::getApiName).toList());

        while (true) {
            System.out.print("Enter API name (Press Enter to cancel): ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                return null;
            }
            ApiDefinition api = registry.get(name);
            if (api != null) {
                return api;
            }
            System.out.println("Unknown API. Available: " +
                    apis.stream().map(ApiDefinition::getApiName).toList());
        }
    }

    private Map<String, String> collectParams(ApiDefinition api) {
        System.out.println(api.getInstruction());
        System.out.println("Enter parameters as key=value pairs separated by spaces.");
        System.out.println("Press Enter to use default parameters, or type 'cancel' to abort.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("cancel")) {
                return null;
            }

            Map<String, String> overrides = new HashMap<>();
            if (!input.isEmpty()) {
                String parseError = parseInto(overrides, input);
                if (parseError != null) {
                    System.out.println(parseError);
                    System.out.println("Please re-enter parameters, press Enter for defaults, "
                            + "or type 'cancel' to abort.");
                    continue;
                }
            }

            ParamResolver.Result result = ParamResolver.resolve(api, overrides);
            if (!result.isSuccess()) {
                System.out.println(result.error());
                System.out.println("Please re-enter parameters, press Enter for defaults, "
                        + "or type 'cancel' to abort.");
                continue;
            }

            System.out.println("Sending query params: " + result.params());
            return result.params();
        }
    }

    private String parseInto(Map<String, String> params, String input) {
        Set<String> seen = new HashSet<>();
        for (String pair : input.split("\\s+")) {
            String[] kv = pair.split("=", 2);
            if (kv.length != 2 || kv[0].isBlank() || kv[1].isBlank()) {
                return "Invalid format: " + pair + ". Use key=value.";
            }
            String key = kv[0].trim();
            String value = kv[1].trim();
            if (!seen.add(key)) {
                return "Duplicate parameter: " + key
                        + ". A parameter can only be specified once.";
            }
            params.put(key, value);
        }
        return null;
    }

    private OutputFormat selectFormat() {
        System.out.println("Format: 1=JSON, 2=CSV (Enter to cancel)");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return null;
            switch (input) {
                case "1":
                    return OutputFormat.JSON;
                case "2":
                    return OutputFormat.CSV;
                default:
                    System.out.println("Please enter 1 or 2.");
            }
        }
    }

    private Path askPath(OutputFormat format) {
        while (true) {
            System.out.print("File path (Enter to cancel): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return null;
            try {
                Path path = Path.of(input);
                return pathResolver.resolve(path, format);
            } catch (CliError | InvalidPathException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private ViewTarget askViewPath() {
        while (true) {
            System.out.print("File path (Enter to cancel): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return null;

            try {
                Path path = Path.of(input);
                OutputFormat detected = OutputFormat.fromPath(path);
                if (detected != null) {
                    return new ViewTarget(path, detected);
                }
                System.out.println("Unknown format.");
                OutputFormat chosen = selectFormat();
                if (chosen == null) return null;
                String name = path.getFileName().toString();
                int dot = name.lastIndexOf('.');
                String base = dot >= 0 ? name.substring(0, dot) : name;
                path = path.resolveSibling(base + "." + chosen.extension());
                return new ViewTarget(path, chosen);
            } catch (InvalidPathException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private void viewAllFlow() {
        ViewTarget target = askViewPath();
        if (target == null) {
            sayCancelled();
            return;
        }

        if (!Files.exists(target.path())) {
            System.out.println("File not found: " + target.path());
            return;
        }

        System.out.println("----- OUTPUT START -----");
        try {
            runner.viewAll(target.format(), target.path());
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("----- OUTPUT END -----");
    }

    private void viewBySourceFlow() {
        ViewTarget target = askViewPath();
        if (target == null) {
            sayCancelled();
            return;
        }

        if (!Files.exists(target.path())) {
            System.out.println("File not found: " + target.path());
            return;
        }

        System.out.print("Source name (Press Enter to cancel): ");
        String source = scanner.nextLine().trim();
        if (source.isEmpty()) {
            sayCancelled();
            return;
        }

        System.out.println("----- OUTPUT START -----");
        try {
            runner.viewBySource(target.format(), target.path(), source);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("----- OUTPUT END -----");
    }

    private void sayCancelled() {
        System.out.println("OPERATION CANCELLED.");
    }
}
