package com.svesh.course_work.io.filters;

import com.svesh.course_work.io.converters.CsvTable;

import java.util.List;
import java.util.Map;

public class CsvFilter implements Filter<CsvTable> {
    @Override
    public CsvTable filterBySource(CsvTable table, String source) {
        List<Map<String,String>> filteredRows = table.rows().stream()
                        .filter(row -> source.equals(row.get("source")))
                        .toList();
        return new CsvTable(table.headers(), filteredRows);
    }
}
