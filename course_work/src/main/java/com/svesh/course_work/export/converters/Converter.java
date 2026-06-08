package com.svesh.course_work.export.converters;

import com.svesh.course_work.records.DataRecord;

import java.util.List;

public interface Converter {
    String convert(List<DataRecord> records);
}
