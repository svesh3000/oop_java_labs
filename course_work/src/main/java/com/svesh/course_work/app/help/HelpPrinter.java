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
                  --automatic     Run automatic mode
                  --interactive   Run interactive mode
                  --help          Show this help
                
                Flags for automatic mode:
                  --api           One or more API names (required flag)
                  --format        File format           (required flag)
                  --out           Output file path      (optional flag, default: output.<format>)
                
                Examples:
                  --automatic --api joke --format json
                  --automatic --api open-meteo joke --format csv --out result
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
