package com.svesh.course_work.io.viewer;

import com.svesh.course_work.io.OutputFormat;

public class ViewerFactory {
    public Viewer create(OutputFormat format) {
        return switch (format) {
            case JSON -> new JsonViewer();
            case CSV -> new CsvViewer();
        };
    }
}
