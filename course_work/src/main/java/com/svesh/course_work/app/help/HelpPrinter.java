package com.svesh.course_work.app.help;

import com.svesh.course_work.api.ApiDefinition;
import com.svesh.course_work.api.ApiRegistry;
import com.svesh.course_work.io.OutputFormat;

import java.util.Arrays;

public final class HelpPrinter {
    private HelpPrinter() {
    }

    public static void printGeneralHelp(ApiRegistry registry) {
        System.out.println("""
                ==================================================
                DATA AGGREGATOR
                ==================================================
                
                Usage:
                  --automatic     Run automatic mode (polling)
                  --interactive   Run interactive mode
                  --help          Show this help
                
                Flags for automatic mode:
                  --api           One or more API names (required)
                  --format        File format (required)
                  --out           Output file path (optional, default: output.<format>)
                  --n             Max concurrent polling tasks (optional, default: 1)
                  --t             Seconds between re-polls of the same API (optional, default: 5)
                
                Examples:
                  --automatic --api joke --format json
                  --automatic --api joke scryfall --format json --n 2 --t 10
                  --automatic --api joke joke joke --format csv --n 3 --t 0
                  --interactive
                """);

        printSupportedApis(registry);
        System.out.println();
        printSupportedFormats();
    }

    private static void printSupportedApis(ApiRegistry registry) {
        System.out.println("Supported APIs:");

        registry.getAll()
                .stream()
                .map(ApiDefinition::getApiName)
                .forEach(api -> System.out.println("  - " + api));
    }

    private static void printSupportedFormats() {
        System.out.println("Supported formats:");

        Arrays.stream(OutputFormat.values())
                .map(OutputFormat::name)
                .map(String::toLowerCase)
                .forEach(format -> System.out.println("  - " + format));
    }
}
