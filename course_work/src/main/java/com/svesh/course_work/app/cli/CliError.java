package com.svesh.course_work.app.cli;

public class CliError extends RuntimeException {
    private final String code;

    public CliError(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
