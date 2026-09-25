package com.svesh.course_work.io.filters;

import com.svesh.course_work.models.DataRecord;

import java.util.List;

public class JsonFilter implements Filter<List<DataRecord>> {
    @Override
    public List<DataRecord> filterBySource(List<DataRecord> data, String source) {
        return data.stream()
                .filter(record -> record.source().equals(source))
                .toList();
    }
}
