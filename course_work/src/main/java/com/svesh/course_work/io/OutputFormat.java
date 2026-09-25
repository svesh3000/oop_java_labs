package com.svesh.course_work.io;

import java.nio.file.Path;

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

    public static OutputFormat fromPath(Path path) {
        if (path == null) {
            return null;
        }
        Path fileName = path.getFileName();
        if (fileName == null) {
            return null;
        }

        String name = fileName.toString();
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return null;
        }

        return fromString(name.substring(dot + 1));
    }
}
