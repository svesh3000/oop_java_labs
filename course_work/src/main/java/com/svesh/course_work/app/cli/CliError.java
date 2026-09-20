package com.svesh.course_work.app.cli;

public final class CliError extends RuntimeException {
    public enum Code {
        ERR_INVALID_ARG,
        ERR_EMPTY_API_LIST,
        ERR_CONFLICT_FORMAT,
        ERR_NO_FORMAT,
        ERR_CONFLICT_OUTPUT,
        ERR_INVALID_OUTPUT,
        ERR_UNKNOWN_FLAG,
        ERR_NO_APIS,
        ERR_UNKNOWN_FORMAT,
        ERR_UNKNOWN_API,
        ERR_FORMAT_PATH_CONFLICT
    }

    private final Code code;

    public CliError(Code code, String message) {
        super(message);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }
}
