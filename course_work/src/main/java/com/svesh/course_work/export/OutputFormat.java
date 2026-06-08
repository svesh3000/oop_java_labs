package com.svesh.course_work.export;

public enum OutputFormat {
    JSON(".json"),
    CSV(".csv");

    private final String extension;

    OutputFormat(String extension) {
        this.extension = extension;
    }

    public String extension() {
        return extension;
    }
}
