package com.svesh.course_work.export.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.svesh.course_work.records.DataRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class CsvConverter implements Converter {
    @Override
    public String convert(List<DataRecord> records) {
        List<Map<String, String>> rows = new ArrayList<>(records.size());
        for (DataRecord record : records) {
            rows.addAll(flattenRecord(record));
        }

        Set<String> headers = new LinkedHashSet<>();
        for (Map<String, String> row : rows) {
            headers.addAll(row.keySet());
        }

        StringWriter writer = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            printer.printRecord(headers);
            for (Map<String, String> row : rows) {
                List<String> values = new ArrayList<>();
                for (String key : headers) {
                    values.add(row.getOrDefault(key, ""));
                }
                printer.printRecord(values);
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV conversion failed", e);
        }
        return writer.toString();
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
            if (!node.isEmpty()) {
                rows.remove(row);
            }
            for (JsonNode item : node) {
                Map<String, String> new_row = new LinkedHashMap<>(row);
                rows.add(new_row);
                flatten(prefix, item, new_row, rows);
            }
        } else {
            row.put(prefix, node.asText());
        }
    }
}
