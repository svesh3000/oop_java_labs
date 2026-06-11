package com.svesh.course_work.io;

public enum OutputFormat {
    JSON("json"),
    CSV("csv");

    private final String extension;

    OutputFormat(String extension) {
        this.extension = extension;
    }

    public String extension() {
        return extension;
    }

    public static OutputFormat fromString(String value) {
        for (OutputFormat format : values()) {
            if (format.name().equalsIgnoreCase(value)) {
                return format;
            }
        }
        return null;
    }
}
