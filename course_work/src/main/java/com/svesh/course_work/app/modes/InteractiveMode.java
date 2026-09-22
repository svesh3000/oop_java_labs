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
    private final Scanner scanner;

    private record ViewTarget(Path path, OutputFormat format) {
    }

    private record ExportTarget(Path path, WriteMode mode) {
    }

    private enum FileAction {
        OVERWRITE,
        APPEND,
        CHANGE_PATH,
        CANCEL
    }

    public InteractiveMode(AppRunner runner, ApiRegistry registry, Scanner scanner) {
        this.runner = runner;
        this.registry = registry;
        this.requestBuilder = new RequestBuilder(registry);
        this.pathResolver = new OutputPathResolver();
        this.scanner = scanner;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (runner.isPollingRunning()) {
                runner.stopPolling();
            }
        }, "interactive-shutdown"));
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
                    4. Start polling
                    5. Stop polling
                    0. Exit
                    """);
            System.out.print("> ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> exportFlow();
                case "2" -> viewAllFlow();
                case "3" -> viewBySourceFlow();
                case "4" -> startPollingFlow();
                case "5" -> stopPollingFlow();
                case "0" -> {
                    stopPollingIfRunning();
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

        List<String> apiNames = selectApiNames(false);
        if (apiNames == null) {
            sayCancelled();
            return;
        }

        String apiName = apiNames.get(0);
        ApiDefinition api = registry.get(apiName);

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

        ExportTarget target = askExportTarget(format);
        if (target == null) {
            sayCancelled();
            return;
        }

        List<ApiRequest> requests = requestBuilder.build(
                apiNames,
                Map.of(apiName, params)
        );

        try {
            runner.export(requests, format, target.path(), target.mode());
            System.out.println("EXPORT COMPLETED -> " + target.path());
        } catch (IOException e) {
            System.out.println("EXPORT FAILED: " + e.getMessage());
        }
    }

    private List<String> selectApiNames(boolean allowMultiple) {
        List<ApiDefinition> apis = registry.getAll();
        System.out.println("Available APIs: "
                + apis.stream().map(ApiDefinition::getApiName).toList());

        if (allowMultiple) {
            System.out.println("Enter API names (space-separated).");
        } else {
            System.out.println("Enter API name (Press Enter to cancel).");
        }

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return null;

            String[] parts = input.split("\\s+");
            if (!allowMultiple && parts.length > 1) {
                System.out.println("Only one API is expected here. Please enter a single name.");
                continue;
            }

            List<String> names = new ArrayList<>();
            boolean ok = true;
            for (String name : parts) {
                if (registry.get(name) == null) {
                    System.out.println("Unknown API: " + name);
                    ok = false;
                    break;
                }
                names.add(name);
            }
            if (ok && !names.isEmpty()) {
                return names;
            }
        }
    }

    private ExportTarget askExportTarget(OutputFormat format) {
        while (true) {
            Path path = askPath(format);
            if (path == null) {
                return null;
            }

            if (!Files.exists(path)) {
                return new ExportTarget(path, WriteMode.CREATE);
            }

            FileAction action = askFileAction(path);
            switch (action) {
                case OVERWRITE -> {
                    return new ExportTarget(path, WriteMode.CREATE);
                }
                case APPEND -> {
                    return new ExportTarget(path, WriteMode.APPEND);
                }
                case CHANGE_PATH -> {
                }
                case CANCEL -> {
                    return null;
                }
            }
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

    private ViewTarget askViewPath() {
        while (true) {
            System.out.print("File path (Enter to cancel): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            if (input.endsWith("/") || input.endsWith("\\")) {
                System.out.println("ERROR: Path is a directory. Specify a file name at the end.");
                continue;
            }

            Path path;
            try {
                path = Path.of(input);
            } catch (InvalidPathException e) {
                System.out.println("ERROR: " + e.getMessage());
                continue;
            }

            OutputFormat detected = OutputFormat.fromPath(path);
            if (detected != null) {
                return new ViewTarget(path, detected);
            }

            System.out.println("Unknown format. Choose the format to use:");
            OutputFormat chosen = selectFormat();
            if (chosen == null) {
                return null;
            }

            try {
                Path resolved = pathResolver.resolve(path, chosen);
                return new ViewTarget(resolved, chosen);
            } catch (CliError e) {
                System.out.println("ERROR: " + e.getMessage());
            }
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
            if (input.isEmpty()) {
                return null;
            }
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
            if (input.isEmpty()) {
                return null;
            }
            if (input.endsWith("/") || input.endsWith("\\")) {
                System.out.println("ERROR: Path is a directory. Specify a file name at the end.");
                continue;
            }
            try {
                Path path = Path.of(input);
                return pathResolver.resolve(path, format);
            } catch (CliError | InvalidPathException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private int askNonNegativeInt(String prompt, int min) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return -1;
            }
            try {
                int val = Integer.parseInt(input);
                if (val < min) {
                    System.out.println("Value must be >= " + min);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer.");
            }
        }
    }

    private void sayCancelled() {
        System.out.println("OPERATION CANCELLED.");
    }

    private void startPollingFlow() {
        System.out.println("\n--- START POLLING ---");

        if (runner.isPollingRunning()) {
            System.out.println("Polling is already running. Stop it first (menu item 5).");
            return;
        }

        List<String> apiNames = selectApiNames(true);
        if (apiNames == null) {
            sayCancelled();
            return;
        }

        OutputFormat format = selectFormat();
        if (format == null) {
            sayCancelled();
            return;
        }

        ExportTarget target = askExportTarget(format);
        if (target == null) {
            sayCancelled();
            return;
        }

        int maxThreads = askNonNegativeInt(
                "Max concurrent tasks n (>= 1) (Enter to cancel): ", 1);
        if (maxThreads < 0) {
            sayCancelled();
            return;
        }

        int interval = askNonNegativeInt(
                "Interval t in seconds (>= 0) (Enter to cancel): ", 0);
        if (interval < 0) {
            sayCancelled();
            return;
        }

        List<ApiRequest> requests = requestBuilder.build(apiNames, Map.of());
        try {
            runner.startPolling(requests, format, target.path(),
                    maxThreads, interval, target.mode());
            System.out.println("Polling started. Use menu item 5 to stop.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void stopPollingFlow() {
        if (!runner.isPollingRunning()) {
            System.out.println("Polling is not running.");
            return;
        }
        System.out.println("Stopping polling...");
        runner.stopPolling();
        System.out.println("Polling stopped.");
    }

    private void stopPollingIfRunning() {
        if (runner.isPollingRunning()) {
            runner.stopPolling();
        }
    }
}
