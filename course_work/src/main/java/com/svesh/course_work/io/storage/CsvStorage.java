package com.svesh.course_work.io.storage;

import com.svesh.course_work.io.converters.CsvConverter;
import com.svesh.course_work.io.converters.CsvTable;
import com.svesh.course_work.models.DataRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CsvStorage implements Storage<CsvTable> {
    private static final Object LOCK = new Object();
    private final CsvConverter converter = new CsvConverter();

    @Override
    public void write(List<DataRecord> records, Path path, WriteMode mode) {
        synchronized (LOCK) {
            CsvTable table = converter.convert(records);
            switch (mode) {
                case CREATE -> writeCreate(table, path);
                case APPEND -> writeAppend(table, path);
            }
        }
    }

    private void writeCreate(CsvTable table, Path path) {
        try {
            AtomicFileWriter.write(path, tmp -> {
                try (BufferedWriter writer = Files.newBufferedWriter(tmp);
                     CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
                    printer.printRecord(table.headers());
                    for (Map<String, String> row : table.rows()) {
                        List<String> values = new ArrayList<>();
                        for (String header : table.headers()) {
                            values.add(row.getOrDefault(header, ""));
                        }
                        printer.printRecord(values);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("CSV write failed", e);
        }
    }

    private void writeAppend(CsvTable table, Path path) {
        if (!Files.exists(path)) {
            writeCreate(table, path);
            return;
        }
        try {
            if (Files.size(path) == 0) {
                writeCreate(table, path);
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV io-error in storage", e);
        }

        CsvTable oldTable = read(path);

        List<String> mergedHeaders = new ArrayList<>(oldTable.headers());
        for (String header : table.headers()) {
            if (!mergedHeaders.contains(header)) {
                mergedHeaders.add(header);
            }
        }

        List<Map<String, String>> mergedRows = new ArrayList<>(oldTable.rows());
        mergedRows.addAll(table.rows());
        writeCreate(new CsvTable(mergedHeaders, mergedRows), path);
    }

    public CsvTable read(Path path) {
        try (
                Reader reader = Files.newBufferedReader(path);
                CSVParser parser = CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .parse(reader)
        ) {
            List<String> headers = new ArrayList<>(parser.getHeaderMap().keySet());
            List<Map<String, String>> rows = new ArrayList<>();
            for (CSVRecord record : parser) {
                rows.add(record.toMap());
            }
            return new CsvTable(headers, rows);
        } catch (IOException e) {
            throw new RuntimeException("CSV read failed", e);
        }
    }

    @Override
    public int getMaxId(Path path) {
        if (!Files.exists(path)) {
            return 0;
        }
        CsvTable table = read(path);
        return table.rows().stream()
                .map(row -> row.get("id"))
                .filter(Objects::nonNull)
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
    }
}
