package com.svesh.course_work.io.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.svesh.course_work.models.DataRecord;

import java.util.*;

public class CsvConverter {
    public CsvTable convert(List<DataRecord> records) {
        List<Map<String, String>> rows = new ArrayList<>(records.size());
        for (DataRecord record : records) {
            rows.addAll(flattenRecord(record));
        }
        LinkedHashSet<String> headers = new LinkedHashSet<>();
        for (Map<String, String> row : rows) {
            headers.addAll(row.keySet());
        }
        return new CsvTable(List.copyOf(headers), rows);
    }

    private List<Map<String, String>> flattenRecord(DataRecord record) {
        List<Map<String, String>> rows = new ArrayList<>();
        Map<String, String> row = new LinkedHashMap<>();
        rows.add(row);
        row.put("id", String.valueOf(record.id()));
        row.put("source", record.source());
        row.put("timestamp", record.timestamp().toString());
        flatten("data", record.data(), row, rows);
        return rows;
    }

    private void flatten(String prefix, JsonNode node, Map<String, String> row, List<Map<String, String>> rows) {
        if (node.isObject()) {
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                String key = entry.getKey();
                JsonNode val = entry.getValue();
                String new_prefix = prefix.isEmpty() ? key : prefix + "." + key;
                flatten(new_prefix, val, row, rows);
            }
        } else if (node.isArray()) {
            if (node.isEmpty()) {
                return;
            }
            boolean first = true;
            for (JsonNode item : node) {
                if (first) {
                    flatten(prefix, item, row, rows);
                    first = false;
                } else {
                    Map<String, String> newRow = new LinkedHashMap<>(row);
                    rows.add(newRow);
                    flatten(prefix, item, newRow, rows);
                }
            }
        } else {
            row.put(prefix, node.asText());
        }
    }
}
