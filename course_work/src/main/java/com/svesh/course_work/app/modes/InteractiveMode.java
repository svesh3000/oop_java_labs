package com.svesh.course_work.app.modes;

import com.svesh.course_work.api.ApiDefinition;
import com.svesh.course_work.api.ApiRegistry;
import com.svesh.course_work.api.ApiRequest;
import com.svesh.course_work.app.cli.CliError;
import com.svesh.course_work.app.io.AppRunner;
import com.svesh.course_work.app.io.OutputPathResolver;
import com.svesh.course_work.app.requests.RequestBuilder;
import com.svesh.course_work.io.OutputFormat;
import com.svesh.course_work.io.storage.WriteMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class InteractiveMode {
    private final AppRunner runner;
    private final ApiRegistry registry;
    private final RequestBuilder requestBuilder;
    private final OutputPathResolver pathResolver;
    private final Scanner scanner = new Scanner(System.in);

    public InteractiveMode(AppRunner runner, ApiRegistry registry) {
        this.runner = runner;
        this.registry = registry;
        this.requestBuilder = new RequestBuilder(registry);
        this.pathResolver = new OutputPathResolver();
    }

    public void run() {
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
                    return;
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
        }   // явная отмена через 'cancel'

        OutputFormat format = selectFormat();
        if (format == null) {
            sayCancelled();
            return;
        }

        Path path = askPath(format);
        if (path == null) {
            sayCancelled();
            return;
        }

        WriteMode mode = selectWriteMode();
        if (mode == null) {
            sayCancelled();
            return;
        }

        if (mode == WriteMode.CREATE && Files.exists(path)) {
            System.err.println("ERROR: File already exists. Choose Append mode or use a different path.");
            return;
        }

        ApiRequest request = requestBuilder.build(api.getApiName(), params);
        try {
            runner.export(List.of(request), format, path, mode);
            System.out.println("EXPORT COMPLETED -> " + path);
        } catch (IOException e) {
            System.err.println("EXPORT FAILED: " + e.getMessage());
        }
    }

    private ApiDefinition selectApiByName() {
        List<ApiDefinition> apis = registry.getAll();
        System.out.println("Available APIs: "
                + apis.stream().map(ApiDefinition::getApiName).toList());

        while (true) {
            System.out.print("Enter API name (Press Enter to cancel): ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) return null;

            ApiDefinition api = registry.get(name);
            if (api != null) return api;

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
            if (input.isEmpty()) {
                return new HashMap<>(api.getDefaultQueryParams());
            }

            Map<String, String> params = new HashMap<>();
            boolean valid = true;
            for (String pair : input.split("\\s+")) {
                String[] kv = pair.split("=", 2);
                if (kv.length != 2 || kv[0].isBlank() || kv[1].isBlank()) {
                    System.out.println("Invalid format: " + pair + ". Use key=value.");
                    valid = false;
                    break;
                }

                String key = kv[0].trim();
                String value = kv[1].trim();
                if (params.containsKey(key)) {
                    System.out.println("Duplicate parameter: " + key
                            + ". A parameter can only be specified once.");
                    valid = false;
                    break;
                }
                params.put(key, value);
            }

            if (valid) {
                return params;
            }
            System.out.println("Please re-enter parameters, press Enter for defaults, or type 'cancel' to abort.");
        }
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
            System.out.print("Output file path (Enter to cancel): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return null;
            try {
                Path path = Path.of(input);
                return pathResolver.resolve(path, format);
            } catch (CliError e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private WriteMode selectWriteMode() {
        System.out.println("Write mode: 1=Create new, 2=Append (Enter to cancel)");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return null;
            switch (input) {
                case "1":
                    return WriteMode.CREATE;
                case "2":
                    return WriteMode.APPEND;
                default:
                    System.out.println("Please enter 1 or 2.");
            }
        }
    }

    private void viewAllFlow() {
        OutputFormat format = selectFormat();
        if (format == null) {
            sayCancelled();
            return;
        }

        Path path = askPath(format);
        if (path == null) {
            sayCancelled();
            return;
        }

        System.out.println("----- OUTPUT START -----");
        try {
            runner.viewAll(format, path);
        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
        }
        System.out.println("----- OUTPUT END -----");
    }

    private void viewBySourceFlow() {
        OutputFormat format = selectFormat();
        if (format == null) {
            sayCancelled();
            return;
        }

        Path path = askPath(format);
        if (path == null) {
            sayCancelled();
            return;
        }

        String source;
        while (true) {
            System.out.print("Source name (Press Enter to cancel): ");
            source = scanner.nextLine().trim();
            if (source.isEmpty()) {
                sayCancelled();
                return;
            }
            if (!source.isBlank()) {
                break;
            }
        }

        System.out.println("----- OUTPUT START -----");
        try {
            runner.viewBySource(format, path, source);
        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
        }
        System.out.println("----- OUTPUT END -----");
    }

    private void sayCancelled() {
        System.out.println("OPERATION CANCELLED.");
    }
}
