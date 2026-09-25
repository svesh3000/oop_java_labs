package com.svesh.course_work.io.viewer;

import com.svesh.course_work.io.converters.CsvTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsvViewer implements Viewer<CsvTable> {
    @Override
    public void show(CsvTable table) {
        if (table.rows().isEmpty()) {
            System.out.println("NO DATA");
            return;
        }

        System.out.println(String.join(",", table.headers()));
        for (Map<String, String> row : table.rows()) {
            List<String> values = new ArrayList<>();
            for (String header : table.headers()) {
                values.add(row.getOrDefault(header, ""));
            }
            System.out.println(String.join(",", values));
        }
    }
}
