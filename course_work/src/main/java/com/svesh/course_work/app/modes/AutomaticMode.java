package com.svesh.course_work.app.modes;

import com.svesh.course_work.api.ApiRegistry;
import com.svesh.course_work.app.cli.CliConfig;
import com.svesh.course_work.app.cli.CliError;
import com.svesh.course_work.app.cli.CliParser;
import com.svesh.course_work.app.io.AppRunner;
import com.svesh.course_work.app.io.OutputPathResolver;
import com.svesh.course_work.app.requests.RequestBuilder;
import com.svesh.course_work.io.storage.WriteMode;
import com.svesh.course_work.api.ApiRequest;

import java.io.IOException;
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
            runner.export(requests, config.format(), config.path(), WriteMode.APPEND);
            System.out.println("EXPORT COMPLETED\nOUTPUT-FILE: " + config.path());
            return 0;
        } catch (IOException e) {
            System.err.println("I/O ERROR: " + e.getMessage());
            return 1;
        } catch (RuntimeException e) {
            System.err.println("ERROR: " + e.getMessage());
            return 1;
        }
    }
}
