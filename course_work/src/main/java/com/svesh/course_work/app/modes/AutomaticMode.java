package com.svesh.course_work.app.modes;

import com.svesh.course_work.api.ApiRegistry;
import com.svesh.course_work.api.ApiRequest;
import com.svesh.course_work.app.cli.CliConfig;
import com.svesh.course_work.app.cli.CliError;
import com.svesh.course_work.app.cli.CliParser;
import com.svesh.course_work.app.io.AppRunner;
import com.svesh.course_work.app.io.OutputPathResolver;
import com.svesh.course_work.app.requests.RequestBuilder;
import com.svesh.course_work.io.storage.WriteMode;

import java.util.List;
import java.util.Map;

public class AutomaticMode {
    private final AppRunner runner;
    private final RequestBuilder requestBuilder;
    private final CliParser parser;

    public AutomaticMode(AppRunner runner, ApiRegistry registry) {
        this.runner = runner;
        this.requestBuilder = new RequestBuilder(registry);
        this.parser = new CliParser(registry, new OutputPathResolver());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (runner.isPollingRunning()) {
                System.out.println("\nShutting down polling...");
                try {
                    runner.stopPolling();
                } catch (RuntimeException e) {
                    System.err.println("Shutdown error: " + e.getMessage());
                }
                System.out.println("EXIT");
            }
        }, "automatic-shutdown"));
    }

    public int run(String[] args) {
        CliConfig config;
        try {
            config = parser.parse(args);
        } catch (CliError e) {
            System.err.println("ERROR [" + e.getCode() + "] " + e.getMessage());
            return 2;
        }

        List<ApiRequest> requests;
        try {
            requests = requestBuilder.build(config.apiNames(), Map.of());
        } catch (IllegalArgumentException e) {
            System.err.println("REQUEST ERROR: " + e.getMessage());
            return 1;
        }

        try {
            runner.startPolling(requests, config.format(), config.path(),
                    config.maxThreads(), config.intervalSeconds(), WriteMode.APPEND);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("ERROR: " + e.getMessage());
            return 1;
        }

        System.out.println("Polling started: threads=" + config.maxThreads()
                + ", interval=" + config.intervalSeconds() + "s.");
        System.out.println("Output file: " + config.path());
        System.out.println("Press Ctrl+C to stop.");

        try {
            runner.awaitPollingShutdown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return 0;
    }
}
